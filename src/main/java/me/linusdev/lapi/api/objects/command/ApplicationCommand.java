/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.objects.command;

import me.linusdev.data.Datable;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 *
 * This class only holds information about an already built ApplicationCommand.
 * If you are looking to create your own Command,
 * see {@link me.linusdev.lapi.api.templates.commands.ApplicationCommandBuilder ApplicationCommandBuilder}.<br><br>
 *
 * <a style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';" href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-naming" target="_top">Application Command Naming:</a><br>
 * {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} command names and command option names must match the following regex {@value #COMMAND_NAME_MATCH_REGEX}
 * with the unicode flag set. If there is a lowercase variant of any letters used, you must use those.
 * Characters with no lowercase variants and/or uncased letters are still allowed.
 *  {@link ApplicationCommandType#USER USER} and  {@link ApplicationCommandType#MESSAGE MESSAGE} commands may be mixed case and can include spaces.
 *
 * @see me.linusdev.lapi.api.templates.commands.ApplicationCommandBuilder ApplicationCommandBuilder
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object" target="_top">Application Command Object</a>
 */
public class ApplicationCommand implements Datable, HasLApi, SnowflakeAble {

    public static final String COMMAND_NAME_MATCH_REGEX = "^[-_\\p{L}\\p{N}\\p{sc=Deva}\\p{sc=Thai}]{1,32}$";

    public static final String ID_KEY = "id";
    public static final String TYPE_KEY = "type";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String NAME_KEY = "name";
    public static final String NAME_LOCALIZED_KEY = "name_localized";
    public static final String NAME_LOCALIZATIONS_KEY = "name_localizations";
    public static final String DESCRIPTION_KEY = "description";
    public static final String DESCRIPTION_LOCALIZED_KEY = "description_localized";
    public static final String DESCRIPTION_LOCALIZATIONS_KEY = "description_localizations";
    public static final String OPTIONS_KEY = "options";
    public static final String DEFAULT_MEMBER_PERMISSIONS_KEY = "default_member_permissions";
    public static final String DM_PERMISSIONS_KEY = "dm_permission";
    public static final String DEFAULT_PERMISSIONS_KEY = "default_permission";
    public static final String VERSION_KEY = "version";

    public static final int NAME_MIN_CHARS = 1;
    public static final int NAME_MAX_CHARS = 32;

    /**
     * Description is only for {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} commands.
     * For {@link ApplicationCommandType#USER USER} and {@link ApplicationCommandType#MESSAGE MESSAGE} commands,
     * description must be an empty string: "".
     */
    public static final int DESCRIPTION_MIN_CHARS = 1;
    public static final int DESCRIPTION_MAX_CHARS = 100;
    public static final int MAX_OPTIONS_AMOUNT = 25;

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @Nullable ApplicationCommandType type;
    private final @NotNull Snowflake applicationId;
    private final @Nullable Snowflake guildId;
    private final @NotNull String name;
    private final @Nullable String nameLocalized;
    private final @Nullable LocalizationDictionary nameLocalizations;
    private final @NotNull String description;
    private final @Nullable String descriptionLocalized;
    private final @Nullable LocalizationDictionary descriptionLocalizations;
    private final @Nullable ApplicationCommandOption[] options;
    private final @Nullable Permissions defaultMemberPermissions;
    private final @Nullable Boolean dmPermission;
    private final @Deprecated @Nullable Boolean defaultPermission;
    private final @NotNull Snowflake version;

    /**
     * @param lApi                     {@link LApi}
     * @param id                       unique id of the command
     * @param type                     the type of command, defaults {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} if not set
     * @param applicationId            unique id of the parent application
     * @param guildId                  guild id of the command, if not global
     * @param name                     1-{@value #NAME_MAX_CHARS} character name. see {@link ApplicationCommand restrictions}
     * @param nameLocalized
     * @param nameLocalizations        Localization dictionary for name field. Values follow the same restrictions as name
     * @param description              1-{@value #DESCRIPTION_MAX_CHARS} character description for {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} commands, empty string for {@link ApplicationCommandType#USER USER} and {@link ApplicationCommandType#MESSAGE MESSAGE} commands
     * @param descriptionLocalized
     * @param descriptionLocalizations Localization dictionary for description field. Values follow the same restrictions as description
     * @param options                  the parameters for the command, max {@value #MAX_OPTIONS_AMOUNT}
     * @param defaultMemberPermissions Set of permissions represented as a bit set
     * @param dmPermission             Indicates whether the command is available in DMs with the app, only for globally-scoped commands. By default, commands are visible.
     * @param defaultPermission        whether the command is enabled by default when the app is added to a guild
     * @param version                  autoincrementing version identifier updated during substantial record changes
     */
    public ApplicationCommand(@NotNull LApi lApi, @NotNull Snowflake id, @Nullable ApplicationCommandType type, @NotNull Snowflake applicationId, @Nullable Snowflake guildId, @NotNull String name, @Nullable String nameLocalized, @Nullable LocalizationDictionary nameLocalizations, @NotNull String description, @Nullable String descriptionLocalized, @Nullable LocalizationDictionary descriptionLocalizations, @Nullable ApplicationCommandOption[] options, @Nullable Permissions defaultMemberPermissions, @Nullable Boolean dmPermission, @Nullable Boolean defaultPermission, @NotNull Snowflake version) {
        this.lApi = lApi;
        this.id = id;
        this.type = type;
        this.applicationId = applicationId;
        this.guildId = guildId;
        this.name = name;
        this.nameLocalized = nameLocalized;
        this.nameLocalizations = nameLocalizations;
        this.description = description;
        this.descriptionLocalized = descriptionLocalized;
        this.descriptionLocalizations = descriptionLocalizations;
        this.options = options;
        this.defaultMemberPermissions = defaultMemberPermissions;
        this.dmPermission = dmPermission;
        this.defaultPermission = defaultPermission;
        this.version = version;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link ApplicationCommand}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #APPLICATION_ID_KEY}, {@link #NAME_KEY}, {@link #DESCRIPTION_KEY} or {@link #VERSION_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ApplicationCommand fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        String applicationId = (String) data.get(APPLICATION_ID_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String nameLocalized = (String) data.get(NAME_LOCALIZED_KEY);
        LocalizationDictionary nameLocalizations = data.getAndConvert(NAME_LOCALIZATIONS_KEY, LocalizationDictionary::fromData);
        String description = (String) data.get(DESCRIPTION_KEY);
        String descriptionLocalized = (String) data.get(DESCRIPTION_LOCALIZED_KEY);
        LocalizationDictionary descriptionLocalizations = data.getAndConvert(DESCRIPTION_LOCALIZATIONS_KEY, LocalizationDictionary::fromData);
        ArrayList<ApplicationCommandOption> options = data.getListAndConvertWithException(OPTIONS_KEY, (ExceptionConverter<SOData, ApplicationCommandOption, InvalidDataException>)
                convertible -> ApplicationCommandOption.fromData(lApi, convertible));
        Permissions defaultMemberPermissions = data.getAndConvert(DEFAULT_MEMBER_PERMISSIONS_KEY, Permissions::ofString, null);
        Boolean dmPermission = (Boolean) data.get(DM_PERMISSIONS_KEY);
        Boolean defaultPermission = (Boolean) data.get(DEFAULT_PERMISSIONS_KEY);
        String version = (String) data.get(VERSION_KEY);

        if(id == null || applicationId == null || name == null || description == null || version == null){
            InvalidDataException.throwException(data, null, ApplicationCommand.class,
                    new Object[]{id, applicationId, name, description, version},
                    new String[]{ID_KEY, APPLICATION_ID_KEY, NAME_KEY, DESCRIPTION_KEY, VERSION_KEY});
        }

        //noinspection ConstantConditions
        return new ApplicationCommand(lApi, Snowflake.fromString(id),
                type == null ? null : ApplicationCommandType.fromValue(type.intValue()),
                Snowflake.fromString(applicationId), Snowflake.fromString(guildId), name, nameLocalized, nameLocalizations, description,
                descriptionLocalized, descriptionLocalizations, options == null ? null : options.toArray(new ApplicationCommandOption[0]),
                defaultMemberPermissions, dmPermission, defaultPermission, Snowflake.fromString(version));
    }

    /**
     * unique id of the command
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the type of command, defaults {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} if not set
     */
    public @NotNull ApplicationCommandType getType() {
        if(type == null) return ApplicationCommandType.CHAT_INPUT;
        return type;
    }

    /**
     * 	unique id as {@link Snowflake} of the parent application
     */
    public @NotNull Snowflake getApplicationIdAsSnowflake() {
        return applicationId;
    }

    /**
     * 	unique id as {@link String} of the parent application
     */
    public @NotNull String getApplicationId() {
        return applicationId.asString();
    }

    /**
     * guild id as {@link Snowflake} of the command, if not global
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * guild id as {@link Snowflake} of the command, if not global
     */
    public @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
    }

    /**
     * 1-{@value #NAME_MAX_CHARS} character name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Only returned by some endpoints. Returns name localization relevant to the requester's locale
     */
    public @Nullable String getNameLocalized() {
        return nameLocalized;
    }

    /**
     * Name localization dictionary
     */
    public @Nullable LocalizationDictionary getNameLocalizations() {
        return nameLocalizations;
    }

    /**
     * 1-{@value #DESCRIPTION_MAX_CHARS} character description for {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} commands, empty string for {@link ApplicationCommandType#USER USER} and {@link ApplicationCommandType#MESSAGE MESSAGE} commands
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Only returned by some endpoints. Returns description localization relevant to the requester's locale
     */
    public @Nullable String getDescriptionLocalized() {
        return descriptionLocalized;
    }

    /**
     * Description localization dictionary
     */
    public @Nullable LocalizationDictionary getDescriptionLocalizations() {
        return descriptionLocalizations;
    }

    /**
     * the parameters for the command, max {@value #MAX_OPTIONS_AMOUNT}
     */
    public @Nullable ApplicationCommandOption[] getOptions() {
        return options;
    }

    /**
     * Set of permissions represented as a bit set
     */
    public @Nullable Permissions getDefaultMemberPermissions() {
        return defaultMemberPermissions;
    }

    /**
     * Indicates whether the command is available in DMs with the app, only for globally-scoped commands. By default, commands are visible.
     */
    public @Nullable Boolean getDmPermission() {
        return dmPermission;
    }

    /**
     * whether the command is enabled by default when the app is added to a guild
     */
    @Deprecated
    public @Nullable Boolean getDefaultPermission() {
        return defaultPermission;
    }

    /**
     * whether the command is enabled by default when the app is added to a guild
     * @return {@code false} if and only if {@link #getDefaultPermission()} is {@code false}, {@code true} otherwise
     */
    @Deprecated
    public boolean isDefaultPermission() {
        return defaultPermission == null || defaultPermission;
    }

    /**
     * autoincrementing version identifier updated during substantial record changes
     */
    public @NotNull Snowflake getVersion() {
        return version;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(9);

        data.add(ID_KEY, id);
        data.addIfNotNull(TYPE_KEY, type);
        data.add(APPLICATION_ID_KEY, applicationId);
        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.add(NAME_KEY, name);
        data.addIfNotNull(NAME_LOCALIZED_KEY, nameLocalized);
        data.addIfNotNull(NAME_LOCALIZATIONS_KEY, nameLocalizations);
        data.add(DESCRIPTION_KEY, description);
        data.addIfNotNull(DESCRIPTION_LOCALIZED_KEY, descriptionLocalized);
        data.addIfNotNull(DESCRIPTION_LOCALIZATIONS_KEY, descriptionLocalizations);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.add(DEFAULT_MEMBER_PERMISSIONS_KEY, defaultMemberPermissions);
        data.addIfNotNull(DM_PERMISSIONS_KEY, dmPermission);
        data.addIfNotNull(DEFAULT_PERMISSIONS_KEY, defaultPermission);
        data.add(VERSION_KEY, version);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

}
