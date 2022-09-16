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
import java.util.Collections;
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
 * <br><br><h2>Requirements</h2>
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
 *         return your command's id. (Only for {@link CommandScope#GLOBAL global} commands)
 *     </li>
 *     <li>
 *         Your command is already uploaded to discord and you have overwritten the functions
 *         {@link #getType()} and {@link #getName()} to return the values corresponding to your command.
 *     </li>
 *
 * </ul>
 *
 * <h2>Enable Guild Commands</h2>
 * <p>
 *     A {@link CommandScope#GUILD guild} command must be enabled for each guild individually using
 *     {@link CommandManager#enabledCommandForGuilds(Class, String...)}.<br>
 *     For Example:
 *     <pre>{@code lApi.getCommandManager().enabledCommandForGuilds(YourGuildCommand.class, "someGuildId");}</pre>
 *     Note: If you enable a command for a guild, which it has already been enable for, nothing will happen.
 * </p>
 *
 * <br><br><h2>Changing the Command</h2>
 * <p>
 *     How you change your command depends on what you want to change.
 * </p>
 * <br>
 * <h3>Changing Name or Scope</h3>
 * <p>
 * If you want to change the {@link #getScope() scope} or the {@link #getName() name} of the command, you must use the
 * {@link #refactor()} method. The {@link Refactor} must contain the old value and your {@link ApplicationCommandTemplate} must
 * return the new value.
 * </p>
 * <p>
 * For example if you want to change your command's name from 'oldName' to 'newName' your {@link #refactor()}
 * method should return:
 * <pre>{@code new Refactor<>(this, RefactorType.RENAME, "oldName");}</pre>
 * And your {@link #create()} method would return a template with the name set to 'newName':
 * <pre>{@code applicationCommandBuilder.setName("newName")}</pre>
 * </p>
 * <p>
 *     Note: You <b>cannot</b> have another command that matches either the old or the new command!
 *     Matches means {@link #getScope() scope}, {@link #getType() type} and {@link #getName()} are the same.
 * </p>
 * <br>
 * <h3>Changing Type</h3>
 * <p>
 * {@link #getType() Type} can currently not be changed.
 * </p>
 * <br>
 * <h3>Changing Anything else</h3>
 * <p>
 *     If you want to change anything except {@link #getScope() scope}, {@link #getName() name} or {@link #getType() type},
 *     you can just change it in your {@link #create()} template and it will automatically be changed the next time you
 *     run your program.
 * </p>
 *
 * <br><h2>Deleting the Command</h2>
 * The command can be easily deleted by overwriting {@link #delete()} to return {@code true}.
 * After running your program, {@link LApi} will automatically delete the command. If the command is already deleted,
 * nothing will happen.
 *
 */
@Command
public abstract class BaseCommand implements HasLApi {

    /**
     * Will be available, after the {@link CommandManagerImpl} has called {@link #setlApi(LApi)}.<br><br>
     * Will always be {@code null} in the constructor!<br>
     * Will never be after that.
     * (given that this class is only used by the {@link CommandManagerImpl})
     */
    protected LApi lApi;

    private @Nullable ApplicationCommandTemplate template;
    private final @NotNull List<ApplicationCommand> linkedApplicationCommands = new ArrayList<>();

    /**
     * The id of your {@link ApplicationCommand}. Usually not required. see {@link BaseCommand} for more information.
     * @return The id of your {@link ApplicationCommand}.
     */
    @ApiStatus.OverrideOnly
    public @Nullable String getId() {
        return null;
    }

    /**
     * The name of your command. Only required if {@link #create()} is not overwritten.<br>
     * Note: if you have both {@link #getName()} and {@link #create()} overwritten, they must both always have the same name.
     * @return The name of your command.
     */
    @ApiStatus.OverrideOnly
    public @Nullable String getName() {
        return null;
    }

    @Nullable String getNameFromLinkedApplicationCommands() {
        synchronized (linkedApplicationCommands) {
            if (!linkedApplicationCommands.isEmpty()) {
                return linkedApplicationCommands.get(0).getName();
            }
        }
        return null;
    }

    @Nullable String getName0() {
        if(getName() != null) return getName();
        if(getTemplate() == null) {
            return getNameFromLinkedApplicationCommands();
        }

        return getTemplate().getName();
    }

    /**
     * The type of your command. Only required if {@link #create()} is not overwritten.<br>
     * Note: if you have both {@link #getType()} ()} and {@link #create()} overwritten, they must both always have the same type.
     * @return The type of your command.
     */
    @ApiStatus.OverrideOnly
    public @Nullable ApplicationCommandType getType() {
        return null;
    }

    @Nullable ApplicationCommandType getTypeFromLinkedApplicationCommands() {
        synchronized (linkedApplicationCommands) {
            if (!linkedApplicationCommands.isEmpty()) {
                return linkedApplicationCommands.get(0).getType();
            }
        }
        return null;
    }

    @Nullable ApplicationCommandType getType0() {
        if(getType() != null) return getType();

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

    /**
     * The scope of your command ({@link CommandScope#GUILD GUILD} or {@link CommandScope#GLOBAL GLOBAL}.
     * @return  The {@link CommandScope scope} of your command .
     * @see CommandScope
     */
    @ApiStatus.OverrideOnly
    public abstract @NotNull CommandScope getScope();


    @ApiStatus.Internal
    @Nullable ApplicationCommandTemplate getTemplate() {
        if(template == null) template = create();
        return template;
    }

    /**
     *
     * @param newTemplate template, that should be returned by {@link #getTemplate()}.
     * @throws LApiIllegalStateException if {@link #getTemplate()} is not null.
     */
    @ApiStatus.Internal
    void setTemplate(@NotNull ApplicationCommandTemplate newTemplate) {
        if(this.template != null) throw new LApiIllegalStateException("Setting template even though it is not null.");
        this.template = newTemplate;
    }

    /**
     * The valid template of your command.
     * <br><br>
     * You can easily create a {@link ApplicationCommandTemplate} using a {@link ApplicationCommandBuilder}.
     * @return your {@link ApplicationCommandTemplate}
     */
    @ApiStatus.OverrideOnly
    protected abstract @Nullable ApplicationCommandTemplate create();

    /**
     * If you want to change {@link #getScope() scope} or {@link #getName() name} of your command, you
     * must use this method. Read more {@link BaseCommand here}.
     * @return {@link Refactor refactor information}
     */
    @ApiStatus.OverrideOnly
    public @Nullable Refactor<?> refactor(){
        return null;
    }

    /**
     * If you want to delete your command make this method return {@code true}.
     * @return whether this command should be deleted.
     */
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
        synchronized (linkedApplicationCommands) {
            for(ApplicationCommand command : linkedApplicationCommands) {
                if(consumer.test(command)) return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    void linkWith(@NotNull ApplicationCommand command) {
        synchronized (linkedApplicationCommands) {
            linkedApplicationCommands.add(command);
        }
    }

    /**
     * checks if given {@link ApplicationCommand} is contained in {@link #linkedApplicationCommands}.
     * @param command {@link ApplicationCommand} to check
     * @return {@code true} if the {@link ApplicationCommand} is already linked to this {@link BaseCommand}
     */
    public final boolean isLinkedWith(@NotNull ApplicationCommand command) {
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
