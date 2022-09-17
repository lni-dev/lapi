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

import me.linusdev.lapi.api.async.ComputationResult;
import me.linusdev.lapi.api.async.Nothing;
import me.linusdev.lapi.api.async.ResultAndErrorConsumer;
import me.linusdev.lapi.api.async.Task;
import me.linusdev.lapi.api.async.error.Error;
import me.linusdev.lapi.api.async.error.StandardErrorTypes;
import me.linusdev.lapi.api.async.error.MessageError;
import me.linusdev.lapi.api.async.conditioned.Condition;
import me.linusdev.lapi.api.async.tasks.SupervisedAsyncTask;
import me.linusdev.lapi.api.cache.CacheReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.GuildCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.manager.command.autocomplete.SelectedOptions;
import me.linusdev.lapi.api.manager.command.event.LocalCommandsInitializedEvent;
import me.linusdev.lapi.api.manager.command.guild.GuildCommands;
import me.linusdev.lapi.api.manager.command.provider.CommandProvider;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.interaction.InteractionType;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponseBuilder;
import me.linusdev.lapi.api.objects.interaction.response.data.AutocompleteBuilder;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.linusdev.lapi.api.manager.command.CommandUtils.*;

public class CommandManagerImpl implements CommandManager, Manager, EventListener {

    private final @NotNull LApiImpl lApi;
    private final @NotNull CommandProvider provider;
    private final @NotNull LogInstance log = Logger.getLogger(this);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final @NotNull List<BaseCommand> localCommands;
    private final @NotNull Map<String, BaseCommand> commandLinks;

    private final @NotNull Map<String, GuildCommands> guildCommandsOnDiscord;
    private final @NotNull Object guildCommandsOnDiscordWriteLock = new Object();

    public CommandManagerImpl(@NotNull LApiImpl lApi, @NotNull CommandProvider provider) throws IOException {
        this.lApi = lApi;
        this.provider = provider;

        this.localCommands = new ArrayList<>();
        this.commandLinks = new ConcurrentHashMap<>();
        this.guildCommandsOnDiscord = new ConcurrentHashMap<>();

        lApi.getEventTransmitter().addSpecifiedListener(this,
                EventIdentifier.GUILD_CREATE,
                EventIdentifier.INTERACTION_CREATE,
                EventIdentifier.CACHE_READY);

    }

    @ApiStatus.Internal
    @Override
    public void init(int initialCapacity) {
        if(isInitialized()) return;

        Future<ArrayList<ApplicationCommand>> request = this.lApi.getRequestFactory().getGlobalApplicationCommands(true).queue();

        log.debug("loading commands with the Provider '" + provider.getClass().getCanonicalName() + "'.");
        Iterator<BaseCommand> it = provider.iterator(lApi);
        while (true){
            try {
                if(!it.hasNext()) break;
                BaseCommand command = it.next();
                command.setlApi(lApi);

                log.debug(String.format("command '%s' loaded: %s %s %s",
                        command.getClass().getCanonicalName(), command.getScope(), command.getType0(), command.getName0()));

                localCommands.add(command);

            } catch (Throwable t) {
                log.error("Exception while trying to load commands.");
                log.error(t);
                //break the loop, so that we do not have an infinite loop
                break;
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

        lApi.transmitEvent().onLocalCommandsInitialized(lApi, new LocalCommandsInitializedEvent(lApi));

        ArrayList<ApplicationCommand> globalCommandsOnDiscord = null;
        try {
            globalCommandsOnDiscord = request.get();

        } catch (InterruptedException | ExecutionException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize!");
            log.error(e);
            return;
        }

        log.debug("starting to match commands.");
        MatchingInformation info = new MatchingInformation(lApi, log, localCommands, globalCommandsOnDiscord, null, commandLinks);
        for(BaseCommand command : localCommands) {
            if(!checkCommand(command, log) || command.getScope() != CommandScope.GLOBAL) continue;
            CommandUtils.matchCommand(info, command);
        }

        synchronized (initialized) {
            initialized.set(true);
            initialized.notifyAll();
        }

    }

    private void initGuild(@NotNull String guildId) {
        GuildCommands gc;
        synchronized (guildCommandsOnDiscordWriteLock) {
            //make sure that, this guild is not loaded twice
            gc = this.guildCommandsOnDiscord.get(guildId);

            if(gc != null && gc.isStarted()) return;

            if(gc == null){
                gc = new GuildCommands();
                this.guildCommandsOnDiscord.put(guildId, gc);
            }
            gc.setStarted(true);
        }

        ArrayList<ApplicationCommand> guildCommandsOnDiscord = null;
        try {
            guildCommandsOnDiscord = this.lApi.getRequestFactory().getGuildApplicationCommands(guildId, true).queueAndWait();
            gc.setCommands(guildCommandsOnDiscord);

        } catch (InterruptedException | ExecutionException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize guild with id " + guildId + "!");
            log.error(e);
            return;
        }

        MatchingInformation info = new MatchingInformation(lApi, log, localCommands, null, guildCommandsOnDiscord, commandLinks);
        for(BaseCommand command : localCommands) {
            if(!checkCommand(command, log) || command.getScope() != CommandScope.GUILD) continue;
            CommandUtils.matchCommand(info, command);
        }

        gc.initialized();
    }

    @Override
    public void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        lApi.runSupervised(() -> init(0));
        lApi.getEventTransmitter().removeSpecifiedListener(this, EventIdentifier.CACHE_READY);
    }

    @Override
    public void onGuildCreate(@NotNull LApi lApi, @NotNull GuildCreateEvent event) {
        lApi.runSupervised(() -> {
            try {
                synchronized (initialized) {
                    if(!initialized.get()) {
                        initialized.wait();
                    }
                }

                initGuild(event.getGuildId());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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
            for(Map.Entry<String, BaseCommand> command : commandLinks.entrySet()) {
                if(command.getKey().equals(commandId)) {
                    try {
                        InteractionResponseBuilder builder = new InteractionResponseBuilder(lApi, event.getInteraction());
                        if(event.getType() == InteractionType.APPLICATION_COMMAND) {

                            if(command.getValue().onInteract(event, new SelectedOptions(event.getInteraction().getInteractionData()), builder))
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

    private void awaitInitialized() throws InterruptedException {
        synchronized (initialized) {
            if(!initialized.get()) {
                lApi.checkQueueThread();
                initialized.wait();
            }
        }
    }

    @Override
    public @NotNull me.linusdev.lapi.api.async.Future<BaseCommand, CommandManager> getCommandByClass(@NotNull Class<? extends BaseCommand> clazz) {

        final @NotNull CommandManager cm = this;

        Task<BaseCommand, CommandManager> task = new SupervisedAsyncTask<>(lApi) {
            @Override
            public @NotNull Condition getCondition() {
                return new Condition() {
                    @Override
                    public boolean check() {
                        return lApi.getReadyEventAwaiter().getAwaiter(EventIdentifier.LOCAL_COMMANDS_INITIALIZED).hasTriggered();
                    }

                    @Override
                    public void await() throws InterruptedException {
                        lApi.getReadyEventAwaiter().getAwaiter(EventIdentifier.LOCAL_COMMANDS_INITIALIZED).awaitFirst();
                    }
                };
            }

            @Override
            public @NotNull ComputationResult<BaseCommand, CommandManager> execute() {
                for (BaseCommand command : localCommands) {
                    if (command.getClass().equals(clazz)) return new ComputationResult<>(command, cm, null);
                }

                return new ComputationResult<>(null, cm, new MessageError("Command not found.", StandardErrorTypes.COMMAND_NOT_FOUND));
            }
        };

        return task.queue();
    }

    /**
     * @param clazz   {@link Class} of your {@link BaseCommand}
     * @param guildId the ids of all guild to enable this command for. The list may not contain the same id twice.
     * @return
     */
    @Override
    public @NotNull me.linusdev.lapi.api.async.Future<Nothing, CommandManager> enabledCommandForGuild(@NotNull Class<? extends BaseCommand> clazz, @NotNull String guildId) {

        final @NotNull CommandManager cm = this;

        Task<Nothing, CommandManager> task = new SupervisedAsyncTask<>(lApi) {
            @Override
            public @NotNull Condition getCondition() {
                return new Condition() {
                    @Override
                    public boolean check() {
                        return isInitialized();
                    }

                    @Override
                    public void await() throws InterruptedException {
                        awaitInitialized();
                    }
                };
            }

            @Override
            public @NotNull ComputationResult<Nothing, CommandManager> execute() {
                for(BaseCommand command : localCommands) {
                    if(command.getClass().equals(clazz)) {

                        GuildCommands gc = guildCommandsOnDiscord.computeIfAbsent(guildId, s -> new GuildCommands());

                        try {
                            gc.awaitInit();
                        } catch (InterruptedException e) {throw new RuntimeException(e);}


                        if(command.forEachLinkedApplicationCommand(acmd -> acmd.getGuildId() != null && acmd.getGuildId().equals(guildId))) {
                            return new ComputationResult<>(null, cm, new MessageError("Command is already enabled for given guild", StandardErrorTypes.COMMAND_ALREADY_ENABLED));
                        }

                        MatchingInformation info = new MatchingInformation(lApi, log, localCommands, null, null, commandLinks);
                        CommandUtils.createCommand(info, command, List.of(guildId), false);

                        return new ComputationResult<>(Nothing.instance, cm, null);
                    }
                }
                return new ComputationResult<>(null, cm, new MessageError("Command not found.", StandardErrorTypes.COMMAND_NOT_FOUND));
            }
        };

        return task.queue();
    }

    @Override
    public synchronized boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }



}
