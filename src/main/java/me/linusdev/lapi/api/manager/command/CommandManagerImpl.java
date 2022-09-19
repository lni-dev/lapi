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
import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.Nothing;
import me.linusdev.lapi.api.async.Task;
import me.linusdev.lapi.api.async.error.StandardErrorTypes;
import me.linusdev.lapi.api.async.error.MessageError;
import me.linusdev.lapi.api.async.conditioned.Condition;
import me.linusdev.lapi.api.async.error.ThrowableError;
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.async.tasks.SupervisedAsyncTask;
import me.linusdev.lapi.api.cache.CacheReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.event.EventAwaiter;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.manager.command.autocomplete.SelectedOptions;
import me.linusdev.lapi.api.manager.command.event.CommandManagerInitializedEvent;
import me.linusdev.lapi.api.manager.command.event.CommandManagerReadyEvent;
import me.linusdev.lapi.api.manager.command.guild.GuildCommands;
import me.linusdev.lapi.api.manager.command.provider.CommandProvider;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.guild.UnavailableGuild;
import me.linusdev.lapi.api.objects.interaction.InteractionType;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponseBuilder;
import me.linusdev.lapi.api.objects.interaction.response.data.AutocompleteBuilder;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.linusdev.lapi.api.manager.command.CommandUtils.*;

public class CommandManagerImpl implements CommandManager, Manager, EventListener {

    private final @NotNull LApiImpl lApi;
    private final @NotNull CommandProvider provider;
    private final @NotNull LogInstance log = Logger.getLogger(this);

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final EventAwaiter commandManagerInitEvent;
    private final EventAwaiter commandManagerReadyEvent;

    private final @NotNull List<BaseCommand> localCommands;
    private final @NotNull Map<String, BaseCommand> commandLinks;

    private final @NotNull Map<String, GuildCommands> guildCommandsOnDiscord;
    private final @NotNull AtomicBoolean guildCommandsOnDiscordInitialized = new AtomicBoolean(false);
    private final @NotNull Object guildCommandsOnDiscordWriteLock = new Object();

    private final @NotNull ConcurrentLinkedDeque<Future<?, ?>> responses;

    public CommandManagerImpl(@NotNull LApiImpl lApi, @NotNull CommandProvider provider) throws IOException {
        this.lApi = lApi;
        this.commandManagerInitEvent = lApi.getReadyEventAwaiter().getAwaiter(EventIdentifier.COMMAND_MANAGER_INITIALIZED);
        this.commandManagerReadyEvent = lApi.getReadyEventAwaiter().getAwaiter(EventIdentifier.COMMAND_MANAGER_READY);
        this.provider = provider;

        this.localCommands = new ArrayList<>();
        this.commandLinks = new ConcurrentHashMap<>();
        this.guildCommandsOnDiscord = new ConcurrentHashMap<>();

        responses = new ConcurrentLinkedDeque<>();

        lApi.getEventTransmitter().addSpecifiedListener(this,
                EventIdentifier.READY,
                EventIdentifier.INTERACTION_CREATE,
                EventIdentifier.CACHE_READY);


    }

    @ApiStatus.Internal
    @Override
    public void init(int initialCapacity) {
        if(isInitialized()) return;

        Future<ArrayList<ApplicationCommand>, QResponse> request = this.lApi.getRequestFactory().getGlobalApplicationCommands(true).queue();

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

        ArrayList<ApplicationCommand> globalCommandsOnDiscord = null;
        try {
            globalCommandsOnDiscord = request.getResult();

        } catch (InterruptedException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize!");
            log.error(e);
            return;
        }

        log.debug("starting to match commands.");
        MatchingInformation info = new MatchingInformation(lApi, log, localCommands, globalCommandsOnDiscord, null, commandLinks, responses);
        for(BaseCommand command : localCommands) {
            if(!checkCommand(command, log) || command.getScope() != CommandScope.GLOBAL) continue;
            CommandUtils.matchCommand(info, command);
        }

        //wait until ready event listener is done
        log.debug("Waiting until the ready event listener is done initializing the guild commands on discord");
        synchronized (guildCommandsOnDiscordInitialized) {
            if(!guildCommandsOnDiscordInitialized.get()) {
                try {
                    guildCommandsOnDiscordInitialized.wait();
                } catch (InterruptedException e) {throw new RuntimeException(e);}
            }
        }

        //init done. Transmit event...
        log.debug("Command Manager successfully initialized. Transmitting event...");
        initialized.set(true);
        lApi.transmitEvent().onCommandManagerInitialized(lApi, new CommandManagerInitializedEvent(lApi));

        //wait until all guild commands are retrieved.
        log.debug("Waiting until all GuildCommands are initialized (matched).");
        for(Map.Entry<String, GuildCommands> entry : guildCommandsOnDiscord.entrySet()) {
            try {
                entry.getValue().awaitInit();
            } catch (InterruptedException e) {throw new RuntimeException(e);}
        }

        //wait until all Futures have finished
        log.debug("Waiting until all Futures have finished.");
        while (responses.peek() != null) {
            try {
                responses.poll().get();
            } catch (InterruptedException ignored) {}
        }

        //command manager is now ready.
        log.debug("Transmitting command manager ready event.");
        lApi.transmitEvent().onCommandManagerReady(lApi, new CommandManagerReadyEvent(lApi));
    }

    /**
     * This method may only be called if {@link #commandManagerInitEvent} is has been triggered.
     * @param guildId id of the guild to load commands for.
     */
    private void initGuild(@NotNull String guildId) {
        GuildCommands gc;
        synchronized (guildCommandsOnDiscordWriteLock) {
            //make sure that, this guild is not loaded twice
            gc = this.guildCommandsOnDiscord.get(guildId);

            if(gc != null && gc.isStarted()) return;

            //This should never happen
            if(gc == null){
                log.warning("initGuild for an unknown guild id!");
                gc = new GuildCommands();
                this.guildCommandsOnDiscord.put(guildId, gc);
            }
            gc.setStarted(true);
        }

        ArrayList<ApplicationCommand> guildCommandsOnDiscord = null;
        try {
            guildCommandsOnDiscord = this.lApi.getRequestFactory().getGuildApplicationCommands(guildId, true).queueAndWait();
            gc.setCommands(guildCommandsOnDiscord);

        } catch (InterruptedException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize guild with id " + guildId + "!");
            log.error(e);
            return;
        }

        MatchingInformation info = new MatchingInformation(lApi, log, localCommands, null, guildCommandsOnDiscord, commandLinks, responses);
        for(BaseCommand command : localCommands) {
            if(!checkCommand(command, log) || command.getScope() != CommandScope.GUILD) continue;
            CommandUtils.matchCommand(info, command);
        }

        gc.initialized();
    }

    @Override
    public void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        synchronized (guildCommandsOnDiscordWriteLock) {
            for(UnavailableGuild guild : event.getGuilds()) {
                guildCommandsOnDiscord.put(guild.getId(), new GuildCommands());
            }
        }
        synchronized (guildCommandsOnDiscordInitialized) {
            guildCommandsOnDiscordInitialized.set(true);
            guildCommandsOnDiscordInitialized.notifyAll();
        }

        lApi.runSupervised(() -> {
            try {
                commandManagerInitEvent.awaitFirst();
                for(UnavailableGuild guild : event.getGuilds()) {
                    initGuild(guild.getId());
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        lApi.runSupervised(() -> init(0));
        lApi.getEventTransmitter().removeSpecifiedListener(this, EventIdentifier.CACHE_READY);
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        //If the command manager is not ready, we cannot compute interactions.
        if(!commandManagerReadyEvent.hasTriggered()) return;
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

    @Override
    public @NotNull Future<BaseCommand, CommandManager> getCommandByClass(@NotNull Class<? extends BaseCommand> clazz) {

        final @NotNull CommandManager cm = this;

        Task<BaseCommand, CommandManager> task = new SupervisedAsyncTask<>(lApi) {
            @Override
            public @NotNull Condition getCondition() {
                return new Condition() {
                    @Override
                    public boolean check() {
                        return commandManagerInitEvent.hasTriggered();
                    }

                    @Override
                    public void await() throws InterruptedException {
                        commandManagerInitEvent.awaitFirst();
                    }
                };
            }

            @Override
            public @NotNull ComputationResult<BaseCommand, CommandManager> execute() throws InterruptedException {
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
    public @NotNull Future<Nothing, CommandManager> enabledCommandForGuild(@NotNull Class<? extends BaseCommand> clazz, @NotNull String guildId) {

        final @NotNull CommandManager cm = this;

        Task<Nothing, CommandManager> task = new SupervisedAsyncTask<>(lApi) {
            @Override
            public @NotNull Condition getCondition() {
                return new Condition() {
                    @Override
                    public boolean check() {
                        return commandManagerInitEvent.hasTriggered();
                    }

                    @Override
                    public void await() throws InterruptedException {
                        commandManagerInitEvent.awaitFirst();
                    }
                };
            }

            @Override
            public @NotNull ComputationResult<Nothing, CommandManager> execute() throws InterruptedException {
                try {
                    for(BaseCommand command : localCommands) {
                        if(command.getClass().equals(clazz)) {

                            GuildCommands gc = guildCommandsOnDiscord.get(guildId);
                            if(gc == null) {
                                return new ComputationResult<>(null, cm, new MessageError("Unknown guild.", StandardErrorTypes.UNKNOWN_GUILD));
                            }

                            try {
                                gc.awaitInit();
                            } catch (InterruptedException e) {throw new RuntimeException(e);}


                            if(command.forEachLinkedApplicationCommand(acmd -> acmd.getGuildId() != null && acmd.getGuildId().equals(guildId))) {
                                return new ComputationResult<>(null, cm, new MessageError("Command is already enabled for given guild", StandardErrorTypes.COMMAND_ALREADY_ENABLED));
                            }

                            MatchingInformation info = new MatchingInformation(lApi, log, localCommands, null, null, commandLinks, responses);
                            CommandUtils.createCommand(info, command, List.of(guildId), false);

                            return new ComputationResult<>(Nothing.instance, cm, null);
                        }
                    }
                    return new ComputationResult<>(null, cm, new MessageError("Command not found.", StandardErrorTypes.COMMAND_NOT_FOUND));

                } catch (Throwable t) {
                    return new ComputationResult<>(null, cm, new ThrowableError(t));

                }
            }
        };

        return task.queue();
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
