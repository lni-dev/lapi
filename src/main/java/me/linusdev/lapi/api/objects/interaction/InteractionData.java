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

package me.linusdev.lapi.api.objects.interaction;

import me.linusdev.data.Datable;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommandInteractionDataOption;
import me.linusdev.lapi.api.objects.command.ApplicationCommandType;
import me.linusdev.lapi.api.objects.component.ComponentType;
import me.linusdev.lapi.api.objects.component.selectmenu.SelectOption;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure" target="_top">Interaction Data Structure</a>
 */
public class InteractionData implements Datable, HasLApi {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String RESOLVED_KEY = "resolved";
    public static final String OPTIONS_KEY = "options";
    public static final String CUSTOM_ID_KEY = "custom_id";
    public static final String COMPONENT_TYPE_KEY = "component_type";
    public static final String VALUES_KEY = "values";
    public static final String TARGET_ID_KEY = "target_id";

    private final @NotNull LApi lApi;

    private final @Nullable String id;
    private final @Nullable String name;
    private final @Nullable ApplicationCommandType type;
    private final @Nullable ResolvedData resolved;
    private final @Nullable ArrayList<ApplicationCommandInteractionDataOption> options;
    private final @Nullable String customId;
    private final @Nullable ComponentType componentType;
    private final @Nullable ArrayList<SelectOption> values;
    private final @Nullable Snowflake targetId;

    /**
     *
     * @param lApi {@link LApi}
     * @param id the ID of the invoked command
     * @param name the name of the invoked command
     * @param type the type of the invoked command
     * @param resolved converted users + roles + channels
     * @param options the params + values from the user
     * @param customId the custom_id of the component
     * @param componentType the type of the component
     * @param values the values the user selected
     * @param targetId id the of user or message targeted by a user or message command
     */
    public InteractionData(@NotNull LApi lApi, @Nullable String id, @Nullable String name, @Nullable ApplicationCommandType type, @Nullable ResolvedData resolved, @Nullable ArrayList<ApplicationCommandInteractionDataOption> options, @Nullable String customId, @Nullable ComponentType componentType, @Nullable ArrayList<SelectOption> values, @Nullable Snowflake targetId) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.type = type;
        this.resolved = resolved;
        this.options = options;
        this.customId = customId;
        this.componentType = componentType;
        this.values = values;
        this.targetId = targetId;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link InteractionData}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY} or {@link #TYPE_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable InteractionData fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        ResolvedData resolved = data.getAndConvertWithException(RESOLVED_KEY, (ExceptionConverter<SOData, ResolvedData, InvalidDataException>) convertible -> ResolvedData.fromData(lApi, convertible), null);
        ArrayList<ApplicationCommandInteractionDataOption> options = data.getListAndConvertWithException(OPTIONS_KEY, (ExceptionConverter<SOData, ApplicationCommandInteractionDataOption, InvalidDataException>)
                convertible -> ApplicationCommandInteractionDataOption.fromData(lApi, convertible));
        String customId = (String) data.get(CUSTOM_ID_KEY);
        Number componentType = (Number) data.get(COMPONENT_TYPE_KEY);
        ArrayList<SelectOption> values = data.getListAndConvertWithException(VALUES_KEY, (ExceptionConverter<SOData, SelectOption, InvalidDataException>) convertible -> SelectOption.fromData(lApi, convertible));
        String targetId = (String) data.get(TARGET_ID_KEY);



        return new InteractionData(lApi, id, name,
                type == null ? null : ApplicationCommandType.fromValue(type.intValue())
                , resolved, options,
                customId, componentType == null ? null : ComponentType.fromValue(componentType.intValue()),
                values, Snowflake.fromString(targetId));
    }

    /**
     * the ID of the invoked command.<br>
     * Application Command only
     */
    public @Nullable String getId() {
        return id;
    }

    /**
     * the name of the invoked command<br>
     * Application Command only
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * the type of the invoked command<br>
     * Application Command only
     */
    public @Nullable ApplicationCommandType getType() {
        return type;
    }

    /**
     * converted users + roles + channels<br>
     * Application Command only
     */
    public @Nullable ResolvedData getResolved() {
        return resolved;
    }

    /**
     * the params + values from the user<br>
     * Application Command only
     */
    public @Nullable ArrayList<ApplicationCommandInteractionDataOption> getOptions() {
        return options;
    }

    /**
     * the custom_id of the component<br>
     * 	Component and Modal Submit only
     */
    public @Nullable String getCustomId() {
        return customId;
    }

    /**
     * the type of the component<br>
     * Component only
     */
    public @Nullable ComponentType getComponentType() {
        return componentType;
    }

    /**
     * the values the user selected<br>
     * 	Component (Select) only
     */
    public @Nullable ArrayList<SelectOption> getValues() {
        return values;
    }

    /**
     * id as {@link Snowflake} the of user or message targeted by a user or message command<br>
     * User Command and Message Command only
     */
    public @Nullable Snowflake getTargetIdAsSnowflake() {
        return targetId;
    }

    /**
     * id as {@link String} the of user or message targeted by a user or message command<br>
     * User Command and Message Command only
     */
    public @Nullable String getTargetId() {
        if(targetId == null) return null;
        return targetId.asString();
    }



    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(9);

        data.addIfNotNull(ID_KEY, id);
        data.addIfNotNull(NAME_KEY, name);
        data.addIfNotNull(TYPE_KEY, type);
        data.addIfNotNull(RESOLVED_KEY, resolved);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.addIfNotNull(CUSTOM_ID_KEY, customId);
        data.addIfNotNull(COMPONENT_TYPE_KEY, componentType);
        data.addIfNotNull(VALUES_KEY, values);
        data.addIfNotNull(TARGET_ID_KEY, targetId);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
