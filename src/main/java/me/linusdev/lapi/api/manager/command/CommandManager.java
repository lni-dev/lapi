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
import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.manager.command.refactor.Refactor;
import me.linusdev.lapi.api.manager.command.refactor.RefactorType;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class CommandManager implements Manager, EventListener {

    private final @NotNull LApiImpl lApi;
    private final @NotNull LogInstance log = Logger.getLogger(this);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final ArrayList<BaseCommand> globalCommands;
    private final ArrayList<BaseCommand> guildCommands;

    private final HashMap<String, BaseCommand> globalCommandsMap;

    public CommandManager(@NotNull LApiImpl lApi) throws IOException {
        this.lApi = lApi;

        this.guildCommands = new ArrayList<>();
        this.globalCommands = new ArrayList<>();

        this.globalCommandsMap = new HashMap<>();

        lApi.getEventTransmitter().addListener(this);
    }

    private @NotNull List<String> readServices() {
        ArrayList<String> list = new ArrayList<>();

        BufferedReader reader = null;
        try(InputStream inputStream = lApi.getCallerClass().getClassLoader().getResourceAsStream("META-INF/services/me.linusdev.lapi.api.manager.command.BaseCommand")){
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

    @Override
    public void init(int initialCapacity) {
        if(isInitialized()) return;

        globalCommands.clear();
        guildCommands.clear();

        Future<ArrayList<ApplicationCommand>> request = this.lApi.getRequestFactory().getGlobalApplicationCommands(true).queue();

        log.debug("loading commands with a ServiceLoader");
        ServiceLoader<BaseCommand> commands = ServiceLoader.load(BaseCommand.class);

        Iterator<BaseCommand> it = commands.iterator();
        while (true){
            try {
                if(!it.hasNext()) break;
                BaseCommand command = it.next();
                command.setlApi(lApi);

                log.debug(String.format("command '%s' loaded: %s %s %s",
                        command.getClass().getCanonicalName(), command.getScope(), command.getType(), command.getName()));

                if(command.getScope() == CommandScope.GLOBAL)
                    globalCommands.add(command);
                else if(command.getScope() == CommandScope.GUILD)
                    guildCommands.add(command);
                else {
                    log.error(String.format("command '%s' has unknown scope: %s", command.getClass().getCanonicalName(), command.getScope()));
                    throw new LApiIllegalStateException("command has unknown scope: " + command.getScope());
                }


            } catch (Throwable t) {
                log.error("Exception while trying to load a command.");
                log.error(t);
                //break the loop, so that we do not have an infinite loop
                return;
            }
        }

        //Sort the array, so that we will first iterate over commands, that specified an id.
        //Then commands that specified a template.
        //And commands that specified scope, type and name last.
        this.globalCommands.sort((o1, o2) -> {
            if(o1.getId() != null && o2.getId() != null) return 0;
            if(o1.getId() != null && o2.getId() == null) return 2;
            if(o2.getId() != null) return -2;
            if(o1.getTemplate() != null && o2.getTemplate() != null) return 0;
            if(o1.getTemplate() != null && o2.getTemplate() == null) return 1;
            if(o2.getTemplate() != null) return -1;
            return 0;
        });

        try {
            ArrayList<ApplicationCommand> discordCommands = request.get();

            for(BaseCommand command : this.globalCommands) {
                try {
                    ApplicationCommand match = null;

                    if(command.getId() != null) {
                        //match by id
                        match = match(discordCommands, applicationCommand -> {
                            return applicationCommand.getId().equals(command.getId());
                        });

                    } else if(command.getTemplate() != null) {
                        //match by template
                        match = match(discordCommands, applicationCommand -> {
                           return applicationCommand.getType() == command.getTemplate().getType()
                                   && applicationCommand.getName().equals(command.getTemplate().getName());
                        });

                    } else if(command.getType() != null && command.getName() != null) {
                        //match by scope, type and name
                        match = match(discordCommands, applicationCommand -> {
                            return applicationCommand.getType() == command.getType()
                                    && applicationCommand.getName().equals(command.getName());
                        });

                    } else {
                        //cannot match
                        log.error(String.format("command '%s' does not meet the requirements.", command.getClass().getCanonicalName()));
                        command.onError(new LApiIllegalStateException("Your command does not meet the requirements."));
                        continue;

                    }

                    if(match == null) {
                        //This is a new command

                        if(command.getTemplate() == null) {
                            log.error(String.format("command '%s' cannot be found on discord and template returns null." +
                                    " Cannot create command.", command.getClass().getCanonicalName()));
                            command.onError(new LApiIllegalStateException("Your command cannot be found on discord" +
                                    " and that's why needs a template to be created."));
                            continue;
                        }

                        try {
                            ApplicationCommand created = lApi.getRequestFactory()
                                    .createGlobalApplicationCommand(command.getTemplate()).queueAndWait();
                            command.setConnected(created);
                            globalCommandsMap.put(created.getId(), command);

                        } catch (InterruptedException | ExecutionException e) {
                            log.error(String.format("Could not create command %s.", command.getClass().getCanonicalName()));
                            command.onError(e);
                            continue;

                        }

                    } else {
                        command.setConnected(match);
                        globalCommandsMap.put(match.getId(), command);

                    }

                } catch (Throwable t) {
                    log.error("Exception while trying to match command");
                    log.error(t);
                    command.onError(t);
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize!");
            log.error(e);
            return;
        }

        initialized.set(true);
    }

    @Override
    public void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        lApi.runSupervised(() -> init(0));
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        if(!event.hasCommandId()) return;
        //noinspection ConstantConditions: checked by above if
        @NotNull String commandId = event.getCommandId();

        //check global commands
        for(Map.Entry<String, BaseCommand> command : globalCommandsMap.entrySet()) {
            if(command.getKey().equals(commandId)) {
                try {
                    command.getValue().onInteract(event);
                } catch (Throwable t) {
                    command.getValue().onError(t);
                }
                return;
            }
        }

        //check guild commands
        if(event.getGuildId() != null) {
            //TODO: error
            return;
        }
        @NotNull String guild = event.getGuildId();
        //TODO: check guild commands
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    public static @Nullable ApplicationCommand match(@NotNull ArrayList<ApplicationCommand> list, @NotNull Predicate<ApplicationCommand> matchFunction) {
        Iterator<ApplicationCommand> iterator = list.iterator();
        if(list.isEmpty()) return null;

        for(ApplicationCommand command = iterator.next(); iterator.hasNext(); command=iterator.next()){
            if(matchFunction.test(command)) {
                iterator.remove();
                return command;
            }
        }

        return null;
    }

}
