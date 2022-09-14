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

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.manager.command.autocomplete.SelectedOptions;
import me.linusdev.lapi.api.manager.command.provider.CommandProvider;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.command.ApplicationCommandType;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponseBuilder;
import me.linusdev.lapi.api.objects.interaction.response.data.AutocompleteBuilder;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandBuilder;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * <h2>Register your Command</h2>
 * You can
 * <ul>
 *     <li>
 *         either add the {@link Command} annotation to your class that extends {@link BaseCommand}
 *         and add the following dependency to your build.gradle:
 *         <pre>{@code annotationProcessor 'io.github.lni-dev:lapi-annotation-processor:1.0.1'}</pre>
 *     </li>
 *     <li>
 *         or manually add it to the config. see {@link ConfigBuilder#setCommandProvider(CommandProvider) setCommandProvider(...)}.<br>
 *     </li>
 * </ul>
 *
 * If your command is registered, it will be automatically managed by {@link LApi}.
 *
 * <h2>Requirements</h2>
 * <b>All</b> of the following requirements must always be met:
 * <ul>
 *     <li>
 *         You have overwritten {@link #getScope()} to return the scope of your command
 *         ({@link CommandScope#GUILD GUILD} or {@link CommandScope#GLOBAL GLOBAL})
 *     </li>
 *     <li>
 *         You dont have any overwritten methods that return {@code null}
 *     </li>
 * </ul>
 * <b>One</b> of the following requirements must be met:
 * <ul>
 *     <li>
 *         You have overwritten {@link #create()} to return your commands {@link ApplicationCommandTemplate template}.
 *         Your command can already be uploaded to discord, but does not have to be.
 *     </li>
 *     <li>
 *         Your command is already uploaded to discord and you a have overwritten the {@link #getId()} function to
 *         return your command's id.
 *     </li>
 *     <li>
 *         Your command is already uploaded to discord and you have overwritten the functions
 *         {@link #getType()} and {@link #getName()} to return the values corresponding to your command.
 *     </li>
 *
 * </ul>
 *
 */
@Command
public abstract class BaseCommand implements HasLApi {

    /**
     * Will be available, after the {@link CommandManagerImpl} has called {@link #setlApi(LApi)}.<br><br>
     * Will always be {@code null} in the constructor!<br>
     * Will never be {@code null} in {@link #getId()}, {@link #getName()}, {@link #getScope()}, {@link #getType()},
     * {@link #getTemplate()}, {@link #create()} and {@link #onInteract(InteractionCreateEvent, SelectedOptions, InteractionResponseBuilder)}.
     * (given that these functions are only called by the {@link CommandManagerImpl})
     */
    protected LApi lApi;

    private @Nullable ApplicationCommandTemplate template;
    private final @NotNull List<ApplicationCommand> linkedApplicationCommands = new ArrayList<>();

    @ApiStatus.OverrideOnly
    public @Nullable String getId() {
        return null;
    }

    @ApiStatus.OverrideOnly
    public @Nullable String getName() {

        if(getTemplate() == null) {
            return getNameFromLinkedApplicationCommands();
        }

        return getTemplate().getName();
    }

    @Nullable String getNameFromLinkedApplicationCommands() {
        if(!linkedApplicationCommands.isEmpty()) {
            return linkedApplicationCommands.get(0).getName();
        }
        return null;
    }

    /**
     *
     * @return {@link ApplicationCommandType}
     */
    @ApiStatus.OverrideOnly
    public @Nullable ApplicationCommandType getType() {
        if(getTemplate() == null) {
            return getTypeFromLinkedApplicationCommands();
        }
        ApplicationCommandType type = getTemplate().getType();
        if(type == null) {
            //while creating a command the type is actually optional.
            //The default type is 1 (CHAT_INPUT)
            type = ApplicationCommandType.CHAT_INPUT;
        }
        return type;
    }

    @Nullable ApplicationCommandType getTypeFromLinkedApplicationCommands() {
        if(!linkedApplicationCommands.isEmpty()) {
            return linkedApplicationCommands.get(0).getType();
        }
        return null;
    }

    /**
     * @return {@link CommandScope}
     */
    @ApiStatus.OverrideOnly
    public abstract @NotNull CommandScope getScope();


    @ApiStatus.Internal
    final @Nullable ApplicationCommandTemplate getTemplate() {
        if(template == null) template = create();
        return template;
    }

    /**
     *
     * @param newTemplate template, that should be returned by {@link #getTemplate()}.
     * @throws LApiIllegalStateException if {@link #getTemplate()} is not null.
     */
    @ApiStatus.Internal
    final void setTemplate(@NotNull ApplicationCommandTemplate newTemplate) {
        if(this.template != null) throw new LApiIllegalStateException("Setting template even though it is not null.");
        this.template = newTemplate;
    }

    /**
     * You can easily create a {@link ApplicationCommandTemplate} using a {@link ApplicationCommandBuilder}.
     * @return your {@link ApplicationCommandTemplate}
     */
    @ApiStatus.OverrideOnly
    protected abstract @Nullable ApplicationCommandTemplate create();

    @ApiStatus.OverrideOnly
    public @Nullable Refactor<?> refactor(){
        return null;
    }

    @ApiStatus.OverrideOnly
    public boolean delete() {
        return false;
    }

    /**
     * Respond to users that interacted with your command
     * @param event {@link InteractionCreateEvent}
     * @param response the response builder, which can be queued.
     * @return whether to automatically {@link Queueable#queue() queue} the response after this method. {@code true} will queue it, {@code false} won't.
     */
    @ApiStatus.OverrideOnly
    public abstract boolean onInteract(@NotNull InteractionCreateEvent event, @NotNull SelectedOptions options, @NotNull InteractionResponseBuilder response);

    /**
     * Respond to autocomplete events. Only if {@link ApplicationCommandOption#getAutocomplete()} is {@code true}.
     * @param event {@link InteractionCreateEvent}
     * @param builder the response autocomplete builder, which can be queued.
     * @return whether to automatically {@link Queueable#queue() queue} the response after this method. {@code true} will queue it, {@code false} won't.
     */
    @ApiStatus.OverrideOnly
    public abstract boolean onAutocomplete(@NotNull InteractionCreateEvent event, @NotNull SelectedOptions options, @NotNull AutocompleteBuilder builder);

    /**
     * Handle errors, that happen while working with your command. For example if your command is missing information
     * or does not meet the {@link BaseCommand requirements}.
     * <br><br>
     * Usually {@link Throwable#printStackTrace()}.
     * @param error error relate to your command
     */
    @ApiStatus.OverrideOnly
    public abstract void onError(Throwable error);

    /**
     * Will iterate over all elements until your consumer returns {@code true} or there are no more elements.
     * @param consumer consumer to iterate over the elements
     * @return return value of your consumer for the last element iterated over.
     */
    public final boolean forEachLinkedApplicationCommand(Predicate<ApplicationCommand> consumer) {
        for(ApplicationCommand command : linkedApplicationCommands) {
            if(consumer.test(command)) return true;
        }
        return false;
    }

    @ApiStatus.Internal
    final void linkWith(@NotNull ApplicationCommand command) {
        linkedApplicationCommands.add(command);
    }

    /**
     * checks if given {@link ApplicationCommand} is contained in {@link #linkedApplicationCommands}.
     * @param command {@link ApplicationCommand} to check
     * @return {@code true} if the {@link ApplicationCommand} is already linked to this {@link BaseCommand}
     */
    final boolean isLinkedWith(@NotNull ApplicationCommand command) {
        return forEachLinkedApplicationCommand(c -> c.getId().equals(command.getId()));
    }

    /**
     * called by the {@link CommandManagerImpl}
     * @param lApi {@link LApi}
     */
    @ApiStatus.Internal
    void setlApi(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    /**
     * @see #lApi
     */
    @Override
    public final @NotNull LApi getLApi() {
        return lApi;
    }
}
