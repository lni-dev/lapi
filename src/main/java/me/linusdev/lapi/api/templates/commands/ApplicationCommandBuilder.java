/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.templates.commands;

import me.linusdev.lapi.api.communication.exceptions.InvalidApplicationCommandException;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.command.ApplicationCommandType;
import me.linusdev.lapi.api.other.localization.Localization;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.local.Locale;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.permission.Permissions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * This helps you create your application commands in code.<br>
 * You should read through the <a href="https://discord.com/developers/docs/interactions/application-commands#application-commands">discord documentation</a>
 * for a general understanding about commands first.
 *
 * @see ApplicationCommand
 */
public class ApplicationCommandBuilder {

    private @Nullable String name = null;
    private @Nullable LocalizationDictionary nameLocalizations = null;

    private @Nullable String description = null;
    private @Nullable LocalizationDictionary descriptionLocalizations = null;

    private @Nullable ArrayList<ApplicationCommandOption> options = null;

    private @Nullable Permissions defaultMemberPermissions = null;
    private @Nullable Boolean dmPermissions = null;
    private @Nullable ApplicationCommandType type = null;

    /**
     *
     * @param name command name
     * @param description command description
     * @see #newSlashCommandBuilder(String, String)
     * @see #newUserCommandBuilder(String)
     * @see #newMessageCommandBuilder(String)
     */
    public ApplicationCommandBuilder(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    /**
     *
     * @param name command name
     * @see #newSlashCommandBuilder(String, String)
     * @see #newUserCommandBuilder(String)
     * @see #newMessageCommandBuilder(String)
     */
    public ApplicationCommandBuilder(@NotNull String name) {
        this.name = name;
    }

    /**
     *
     * @see #newSlashCommandBuilder(String, String)
     * @see #newUserCommandBuilder(String)
     * @see #newMessageCommandBuilder(String)
     */
    public ApplicationCommandBuilder() {

    }

    /**
     * Creates a new builder with the type {@link ApplicationCommandType#CHAT_INPUT}
     * @param name command name
     * @param description command description
     * @return {@link ApplicationCommandBuilder}
     */
    @Contract("_, _ -> new")
    public static @NotNull ApplicationCommandBuilder newSlashCommandBuilder(@NotNull String name, @NotNull String description){
        return new ApplicationCommandBuilder(name, description).setType(ApplicationCommandType.CHAT_INPUT);
    }

    /**
     * Creates a new builder with the type {@link ApplicationCommandType#USER}
     * @param name command name
     * @return {@link ApplicationCommandBuilder}
     */
    @Contract("_ -> new")
    public static @NotNull ApplicationCommandBuilder newUserCommandBuilder(@NotNull String name){
        return new ApplicationCommandBuilder(name, "").setType(ApplicationCommandType.USER);
    }

    /**
     * Creates a new builder with the type {@link ApplicationCommandType#MESSAGE}
     * @param name command name
     * @return {@link ApplicationCommandBuilder}
     */
    @Contract("_ -> new")
    public static @NotNull ApplicationCommandBuilder newMessageCommandBuilder(@NotNull String name){
        return new ApplicationCommandBuilder(name, "").setType(ApplicationCommandType.MESSAGE);
    }

    /**
     *
     * @param name name of the command
     */
    public ApplicationCommandBuilder setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    /**
     * replace the {@link LocalizationDictionary} for the name with given one.
     * <br><br>
     * You are probably looking for {@link #setNameLocalization(Locale, String)}.
     * @param dictionary the new {@link LocalizationDictionary}
     * @see #setNameLocalization(Locale, String)
     */
    public ApplicationCommandBuilder setNameLocalizations(@Nullable LocalizationDictionary dictionary) {
        this.nameLocalizations = dictionary;
        return this;
    }

    /**
     * set name localization for given {@link Locale}. Set to {@code null} to remove the localization for given {@link Locale}
     * @param locale {@link Locale} to set localization for
     * @param localization {@link String} localization or {@code null}
     */
    public ApplicationCommandBuilder setNameLocalization(@NotNull Locale locale, @Nullable String localization) {
        if(this.nameLocalizations == null) this.nameLocalizations = new LocalizationDictionary();

        nameLocalizations.setLocalization(locale, localization);
        return this;
    }

    /**
     *
     * @param description description of the command
     */
    public ApplicationCommandBuilder setDescription(@NotNull String description) {
        this.description = description;
        return this;
    }

    /**
     * replace the {@link LocalizationDictionary} for the description with given one.
     * <br><br>
     * You are probably looking for {@link #setDescriptionLocalization(Locale, String)}.
     * @param dictionary the new {@link LocalizationDictionary}
     * @see #setDescriptionLocalization(Locale, String)
     */
    public ApplicationCommandBuilder setDescriptionLocalizations(@Nullable LocalizationDictionary dictionary) {
        this.descriptionLocalizations = dictionary;
        return this;
    }

    /**
     * set description localization for given {@link Locale}. Set to {@code null} to remove the localization for given {@link Locale}
     * @param locale {@link Locale} to set localization for
     * @param localization {@link String} localization or {@code null}
     */
    public ApplicationCommandBuilder setDescriptionLocalization(@NotNull Locale locale, @Nullable String localization) {
        if(this.descriptionLocalizations == null) this.descriptionLocalizations = new LocalizationDictionary();

        descriptionLocalizations.setLocalization(locale, localization);
        return this;
    }

    /**
     * replaces {@link #defaultMemberPermissions} with given {@link Permissions}.
     * @param permissions {@link Permissions} to replace {@link #defaultMemberPermissions}
     * @return this
     * @see #defaultMemberPermissions
     */
    public ApplicationCommandBuilder setDefaultMemberPermissions(@Nullable Permissions permissions) {
        this.defaultMemberPermissions = permissions;

        return this;
    }

    /**
     * Lets you adjust the {@link #defaultMemberPermissions} with a lambda expression:
     * <pre>
     *     {@code
     *
     *     builder.adjustDefaultMemberPermissions(permissions -> {
     *              //your adjustments:
     *              permissions.addPermission(...);
     *          });
     *
     *     }
     * </pre>
     * @param adjuster to adjust the {@link #defaultMemberPermissions}
     * @return this
     */
    public ApplicationCommandBuilder adjustDefaultMemberPermissions(@NotNull Consumer<Permissions> adjuster) {
        if(this.defaultMemberPermissions == null) this.defaultMemberPermissions = new Permissions();

        adjuster.accept(this.defaultMemberPermissions);
        return this;
    }

    /**
     * Sets the {@link #defaultMemberPermissions}, so that only admins can use the command. Replaces any old {@link #defaultMemberPermissions}.
     * @return this
     * @see #defaultMemberPermissions
     */
    public ApplicationCommandBuilder setDefaultMemberPermissionsToAdminOnly(){
        this.defaultMemberPermissions = Permissions.ofString("0");
        return this;
    }

    /**
     * Sets the {@link #defaultMemberPermissions}, so that everyone can use the command. Replaces any old {@link #defaultMemberPermissions}.
     * @return this
     * @see #defaultMemberPermissions
     */
    public ApplicationCommandBuilder setDefaultMemberPermissionsToEveryone(){
        this.defaultMemberPermissions = null;
        return this;
    }

    /**
     * <b>sets</b> {@link #defaultMemberPermissions} to given permissions. Any old {@link #defaultMemberPermissions}
     * will be replaced.
     * @param permissions {@link Permissions} to set
     * @return this
     * @see #adjustDefaultMemberPermissions(Consumer)
     * @see #defaultMemberPermissions
     */
    public ApplicationCommandBuilder setDefaultMemberPermissions(@NotNull Permission... permissions){
        this.defaultMemberPermissions = new Permissions();
        for(Permission permission : permissions) this.defaultMemberPermissions.addPermission(permission);
        return this;
    }

    /**
     * adds given permissions to the {@link #defaultMemberPermissions default member permissions}.
     * @param permissions {@link Permission}s to add
     * @return this
     * @see #defaultMemberPermissions
     */
    public ApplicationCommandBuilder addDefaultMemberPermissions(@NotNull Permission... permissions){
        if(this.defaultMemberPermissions == null) this.defaultMemberPermissions = new Permissions();
        for(Permission permission : permissions) this.defaultMemberPermissions.addPermission(permission);
        return this;
    }

    /**
     * removes given permissions from the {@link #defaultMemberPermissions default member permissions}
     * if {@link #defaultMemberPermissions} is not {@code null}.
     * @param permissions {@link Permission}s to remove
     * @return this
     * @see #defaultMemberPermissions
     */
    public ApplicationCommandBuilder removeDefaultMemberPermissions(@NotNull Permission... permissions){
        if(this.defaultMemberPermissions == null) return this;
        for(Permission permission : permissions) this.defaultMemberPermissions.removePermission(permission);
        return this;
    }

    /**
     * whether this command can be used in direct messages with your bot (only if the user and the bot share at least one server).
     * @param dmPermissions {@code true} means, the command will be usable in dms. {@code false} means, the command will not be usable in dms.
     * @return this
     */
    public ApplicationCommandBuilder setDmPermissions(@Nullable Boolean dmPermissions) {
        this.dmPermissions = dmPermissions;
        return this;
    }

    /**
     * enables this command in direct messages (default)
     * @return this
     */
    public ApplicationCommandBuilder enableInDms(){
        this.dmPermissions = true;
        return this;
    }

    /**
     * disables this command in direct messages
     * @return this
     */
    public ApplicationCommandBuilder disableInDms(){
        this.dmPermissions = false;
        return this;
    }

    /**
     * Sets the type of the command.
     * <ul>
     *     <li>
     *         {@link ApplicationCommandType#CHAT_INPUT} for slash commands
     *     </li>
     *     <li>
     *         {@link ApplicationCommandType#USER} for user commands<br>
     *         If this type is set description will automatically be set to "".
     *     </li>
     *     <li>
     *         {@link ApplicationCommandType#MESSAGE} for message commands<br>
     *         If this type is set description will automatically be set to "".
     *     </li>
     * </ul>
     *
     * @param type {@link ApplicationCommandType}
     * @return this
     */
    public ApplicationCommandBuilder setType(@NotNull ApplicationCommandType type) {
        this.type = type;

        if(type == ApplicationCommandType.USER || type == ApplicationCommandType.MESSAGE)
            description = "";

        return this;
    }

    /**
     * checks if this would build a valid {@link ApplicationCommandTemplate}.
     * @return this
     * @throws InvalidApplicationCommandException if checks fail
     */
    public ApplicationCommandBuilder check() throws InvalidApplicationCommandException {
        if(type == null) throw new InvalidApplicationCommandException("please specify a command type.");
        if(name == null) throw new InvalidApplicationCommandException("name may not be null.");
        if(description == null) throw new InvalidApplicationCommandException("description may not be null.");

        if(type == ApplicationCommandType.CHAT_INPUT) {
            if(!name.matches(ApplicationCommand.COMMAND_NAME_MATCH_REGEX)) throw new InvalidApplicationCommandException("name does meet requirements (regex check failed).");

            if(nameLocalizations != null) {
                for (Localization localization : nameLocalizations) {
                    if(!localization.getLocalization().matches(ApplicationCommand.COMMAND_NAME_MATCH_REGEX)){
                        throw new InvalidApplicationCommandException("name localization\"" + localization.getLocalization() +
                                "\" for locale \"" + localization.getLocale() + "\" does not meet requirements" +
                                " (regex check failed).");
                    }
                }
            }

        } else if(type == ApplicationCommandType.USER) {
            if(!description.equals("")) throw new InvalidApplicationCommandException("USER or MESSAGE commands must have an empty (\"\") description.");

        } else if(type == ApplicationCommandType.MESSAGE) {
            if(!description.equals("")) throw new InvalidApplicationCommandException("USER or MESSAGE commands must have an empty (\"\") description.");

        }

        return this;
    }

    /**
     *
     * @param check whether to {@link #check()} if this would build a valid {@link ApplicationCommandTemplate}
     * @return built {@link ApplicationCommandTemplate}
     * @throws InvalidApplicationCommandException if {@link #check()} fails.
     * @see #check()
     * @see ApplicationCommandTemplate
     */
    public ApplicationCommandTemplate getTemplate(boolean check) throws InvalidApplicationCommandException {
        if(check) check();

        return new ApplicationCommandTemplate(name, nameLocalizations, description, descriptionLocalizations,
                options == null ? null : options.toArray(new ApplicationCommandOption[0]),
                defaultMemberPermissions, dmPermissions, type);
    }


}
