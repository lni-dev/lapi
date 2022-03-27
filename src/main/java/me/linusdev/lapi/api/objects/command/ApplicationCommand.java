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

package me.linusdev.lapi.api.objects.command;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
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
 * {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} command names and command option names must match the following regex "^[\w-]{1,32}$"
 * with the unicode flag set. If there is a lowercase variant of any letters used, you must use those.
 * Characters with no lowercase variants and/or uncased letters are still allowed.
 *  {@link ApplicationCommandType#USER USER} and  {@link ApplicationCommandType#MESSAGE MESSAGE} commands may be mixed case and can include spaces.
 *
 * @see me.linusdev.lapi.api.templates.commands.ApplicationCommandBuilder ApplicationCommandBuilder
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object" target="_top">Application Command Object</a>
 */
public class ApplicationCommand implements Datable, HasLApi, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String TYPE_KEY = "type";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String OPTIONS_KEY = "options";
    public static final String DEFAULT_PERMISSIONS_KEY = "default_permission";
    public static final String VERSION_KEY = "version";

    public static final int NAME_MAX_CHARS = 32;
    public static final int DESCRIPTION_MAX_CHARS = 100;
    public static final int MAX_OPTIONS_AMOUNT = 25;

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @Nullable ApplicationCommandType type;
    private final @NotNull Snowflake applicationId;
    private final @Nullable Snowflake guildId;
    private final @NotNull String name;
    private final @NotNull String description;
    private final @Nullable ApplicationCommandOption[] options;
    private final @Nullable Boolean defaultPermission;
    private final @NotNull Snowflake version;

    /**
     *
     * @param lApi {@link LApi}
     * @param id unique id of the command
     * @param type the type of command, defaults {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} if not set
     * @param applicationId unique id of the parent application
     * @param guildId guild id of the command, if not global
     * @param name 1-{@value #NAME_MAX_CHARS} character name. see {@link ApplicationCommand restrictions}
     * @param description 1-{@value #DESCRIPTION_MAX_CHARS} character description for {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} commands, empty string for {@link ApplicationCommandType#USER USER} and {@link ApplicationCommandType#MESSAGE MESSAGE} commands
     * @param options the parameters for the command, max {@value #MAX_OPTIONS_AMOUNT}
     * @param defaultPermission whether the command is enabled by default when the app is added to a guild
     * @param version autoincrementing version identifier updated during substantial record changes
     */
    public ApplicationCommand(@NotNull LApi lApi, @NotNull Snowflake id, @Nullable ApplicationCommandType type, @NotNull Snowflake applicationId, @Nullable Snowflake guildId, @NotNull String name, @NotNull String description, @Nullable ApplicationCommandOption[] options, @Nullable Boolean defaultPermission, @NotNull Snowflake version) {
        this.lApi = lApi;
        this.id = id;
        this.type = type;
        this.applicationId = applicationId;
        this.guildId = guildId;
        this.name = name;
        this.description = description;
        this.options = options;
        this.defaultPermission = defaultPermission;
        this.version = version;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data}
     * @return {@link ApplicationCommand}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #APPLICATION_ID_KEY}, {@link #NAME_KEY}, {@link #DESCRIPTION_KEY} or {@link #VERSION_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ApplicationCommand fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        String applicationId = (String) data.get(APPLICATION_ID_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        ArrayList<ApplicationCommandOption> options = data.getAndConvertArrayList(OPTIONS_KEY, (ExceptionConverter<Data, ApplicationCommandOption, InvalidDataException>)
                convertible -> ApplicationCommandOption.fromData(lApi, convertible));
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
                Snowflake.fromString(applicationId), Snowflake.fromString(guildId), name, description,
                options == null ? null : options.toArray(new ApplicationCommandOption[0]),
                defaultPermission, Snowflake.fromString(version));
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
     * 1-{@value #DESCRIPTION_MAX_CHARS} character description for {@link ApplicationCommandType#CHAT_INPUT CHAT_INPUT} commands, empty string for {@link ApplicationCommandType#USER USER} and {@link ApplicationCommandType#MESSAGE MESSAGE} commands
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * the parameters for the command, max {@value #MAX_OPTIONS_AMOUNT}
     */
    public @Nullable ApplicationCommandOption[] getOptions() {
        return options;
    }

    /**
     * whether the command is enabled by default when the app is added to a guild
     */
    public @Nullable Boolean getDefaultPermission() {
        return defaultPermission;
    }

    /**
     * whether the command is enabled by default when the app is added to a guild
     * @return {@code false} if and only if {@link #getDefaultPermission()} is {@code false}, {@code true} otherwise
     */
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
    public Data getData() {
        Data data = new Data(9);

        data.add(ID_KEY, id);
        data.addIfNotNull(TYPE_KEY, type);
        data.add(APPLICATION_ID_KEY, applicationId);
        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.add(NAME_KEY, name);
        data.add(DESCRIPTION_KEY, description);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.addIfNotNull(DEFAULT_PERMISSIONS_KEY, defaultPermission);
        data.add(VERSION_KEY, version);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

}
