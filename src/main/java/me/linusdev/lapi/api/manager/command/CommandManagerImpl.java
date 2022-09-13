/*
 * Copyright (c) 2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.manager.command;

import me.linusdev.lapi.api.cache.CacheReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.manager.command.autocomplete.SelectedOptions;
import me.linusdev.lapi.api.manager.command.provider.CommandProvider;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.interaction.InteractionType;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponseBuilder;
import me.linusdev.lapi.api.objects.interaction.response.data.AutocompleteBuilder;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.linusdev.lapi.api.manager.command.CommandUtils.*;

public class CommandManagerImpl implements CommandManager, Manager, EventListener {

    private final @NotNull LApiImpl lApi;
    private final @NotNull CommandProvider provider;
    private final @NotNull LogInstance log = Logger.getLogger(this);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final HashMap<String, BaseCommand> commandsMap;

    public CommandManagerImpl(@NotNull LApiImpl lApi, @NotNull CommandProvider provider) throws IOException {
        this.lApi = lApi;
        this.provider = provider;

        this.commandsMap = new HashMap<>();

        //TODO: add as specified listener
        lApi.getEventTransmitter().addListener(this);
    }

    private @NotNull List<String> readServices() {
        ArrayList<String> list = new ArrayList<>();

        BufferedReader reader = null;
        try{
            InputStream inputStream = lApi.getCallerClass().getClassLoader().getResourceAsStream("META-INF/services/me.linusdev.lapi.api.manager.command.BaseCommand");
            if(inputStream == null) return list;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line =reader.readLine()) != null){
                list.add(line);
            }
        }catch (IOException exception) {
            Logger.getLogger(this).error(exception);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }

        return list;
    }

    @ApiStatus.Internal
    @Override
    public void init(int initialCapacity) {
        if(isInitialized()) return;

        ArrayList<BaseCommand> localCommands = new ArrayList<>();

        Future<ArrayList<ApplicationCommand>> request = this.lApi.getRequestFactory().getGlobalApplicationCommands(true).queue();

        log.debug("loading commands with the Provider '" + provider.getClass().getCanonicalName() + "'.");
        Iterator<BaseCommand> it = provider.iterator(lApi);
        while (true){
            try {
                if(!it.hasNext()) break;
                BaseCommand command = it.next();
                command.setlApi(lApi);

                log.debug(String.format("command '%s' loaded: %s %s %s",
                        command.getClass().getCanonicalName(), command.getScope(), command.getType(), command.getName()));

                localCommands.add(command);

            } catch (Throwable t) {
                log.error("Exception while trying to load commands.");
                log.error(t);
                //break the loop, so that we do not have an infinite loop
                return;
            }
        }

        log.debug("successfully loaded " + localCommands.size() + " commands.");

        //Sort the array, so that we will first iterate over commands, that specified an id.
        //Then commands that specified a template.
        //And commands that specified scope, type and name last.
        localCommands.sort((o1, o2) -> {
            if(o1.getId() != null && o2.getId() != null) return 0;
            if(o1.getId() != null && o2.getId() == null) return 2;
            if(o2.getId() != null) return -2;
            if(o1.getTemplate() != null && o2.getTemplate() != null) return 0;
            if(o1.getTemplate() != null && o2.getTemplate() == null) return 1;
            if(o2.getTemplate() != null) return -1;
            return 0;
        });

        log.debug("successfully sorted commands.");

        ArrayList<ApplicationCommand> globalCommandsOnDiscord = null;
        try {
            globalCommandsOnDiscord = request.get();

        } catch (InterruptedException | ExecutionException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize!");
            log.error(e);
            return;
        }

        log.debug("starting to match commands.");
        //TODO: Hashmap must be thread safe! (multiple threads adding and reading)
        MatchingInformation info = new MatchingInformation(lApi, log, localCommands, globalCommandsOnDiscord, new HashMap<>(), commandsMap);
        for(BaseCommand command : localCommands) {
            if(!checkCommand(command, log)) continue;
            CommandUtils.matchCommand(info, command);
        }

        initialized.set(true);
    }

    @Override
    public void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        lApi.runSupervised(() -> init(0));
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        if(event.getType() != InteractionType.APPLICATION_COMMAND && event.getType() != InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE)
            return;
        if(!event.hasCommandId()) return;
        //noinspection ConstantConditions: checked by above if
        @NotNull String commandId = event.getCommandId();

        lApi.runSupervised(() -> {
            //check global commands
            for(Map.Entry<String, BaseCommand> command : commandsMap.entrySet()) {
                if(command.getKey().equals(commandId)) {
                    try {
                        InteractionResponseBuilder builder = new InteractionResponseBuilder(lApi, event.getInteraction());
                        if(event.getType() == InteractionType.APPLICATION_COMMAND) {

                            if(command.getValue().onInteract(event, builder))
                                builder.getQueueable().queueAndWait();

                        } else if (event.getType() == InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE) {
                            AutocompleteBuilder autocomplete = new AutocompleteBuilder();
                            if(command.getValue().onAutocomplete(event, new SelectedOptions(event.getInteraction().getInteractionData()), autocomplete))
                                builder.applicationCommandAutocompleteResult(autocomplete.build(true))
                                        .getQueueable().queueAndWait();

                        }


                    } catch (Throwable t) {
                        command.getValue().onError(t);

                    }
                    return;
                }
            }
        });


    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }



}
