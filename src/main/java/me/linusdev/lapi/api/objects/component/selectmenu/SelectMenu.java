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

package me.linusdev.lapi.api.objects.component.selectmenu;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.component.ComponentType;
import me.linusdev.lapi.api.objects.component.button.Button;
import me.linusdev.lapi.api.objects.nchannel.ChannelType;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.component.ComponentLimits;
import me.linusdev.lapi.api.objects.component.actionrow.ActionRow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <h2 style="margin-bottom:0;padding-bottom:0">
 *     <a href="https://discord.com/developers/docs/interactions/message-components#select-menus" target="_top">
 *         Select Menus
 *     </a>
 * </h2>
 * <p>
 *     Select menus are another interactive component that renders on messages. On desktop,
 *     clicking on a select menu opens a dropdown-style UI; on mobile,
 *     tapping a select menu opens up a half-sheet with the options.
 * </p>
 * <img src="https://discord.com/assets/0845178564ed70a6c657d9b40d1de8fc.png" alt="select menu example" height="400">
 * <p>
 *     Select menus support single-select and multi-select behavior, meaning you can prompt a user to choose just one item from a list,
 *     or multiple.
 *     When a user finishes making their choice by clicking out of the dropdown or closing the half-sheet,
 *     your app will receive an {@link Interaction interaction}.
 * </p>
 * <ul>
 *     <li>
 *         Select menus must be sent inside an {@link ActionRow Action Row}
 *     </li>
 *     <li>
 *         An {@link ActionRow Action Row} can contain only one select menu
 *     </li>
 *     <li>
 *         An {@link ActionRow Action Row} containing a {@link SelectMenu select menu} cannot also contain {@link Button buttons}
 *     </li>
 * </ul>
 *
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#select-menu-object" target="_top">Select Menu Object</a>
 */
public class SelectMenu implements Component, HasLApi {

    private final @NotNull ComponentType type;
    private final @NotNull String customId;
    private final @Nullable List<SelectOption> options;
    private final @Nullable List<ChannelType> channelTypes;
    private final @Nullable String placeholder;
    private final @Nullable Integer minValues;
    private final @Nullable Integer maxValues;
    private final @Nullable Boolean disabled;

    private final @NotNull LApi lApi;

    /**
     * @param type         {@link ComponentType#SELECT_MENU 3} for a select menu
     * @param customId     a developer-defined identifier for the button, max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters
     * @param options      the choices in the select, max {@value ComponentLimits#SELECT_OPTIONS_MAX}
     * @param channelTypes List of {@link ChannelType channel types} to include in the channel select component
     * @param placeholder  custom placeholder text if nothing is selected, max {@value ComponentLimits#PLACEHOLDER_MAX_CHARS} characters
     * @param minValues    the minimum number of items that must be chosen; default 1, min {@value ComponentLimits#MIN_VALUE_FIELD_MIN}, max {@value ComponentLimits#MIN_VALUE_FIELD_MAX}
     * @param maxValues    the maximum number of items that can be chosen; default 1, max {@value ComponentLimits#MAX_VALUE_FIELD_MAX}
     * @param disabled     disable the select, default false
     */
    public SelectMenu(@NotNull LApi lApi, @NotNull ComponentType type, @NotNull String customId, @Nullable List<SelectOption> options,
                      @Nullable List<ChannelType> channelTypes, @Nullable String placeholder, @Nullable Integer minValues,
                      @Nullable Integer maxValues, @Nullable Boolean disabled) {
        this.lApi = lApi;
        this.type = type;
        this.customId = customId;
        this.options = options;
        this.channelTypes = channelTypes;
        this.placeholder = placeholder;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.disabled = disabled;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link SelectMenu}
     * @throws InvalidDataException if {@link #TYPE_KEY}, {@link #CUSTOM_ID_KEY} or {@link #OPTION_KEY} are missing or null
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable SelectMenu fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        Number type = (Number) data.get(TYPE_KEY);
        String customId = (String) data.get(CUSTOM_ID_KEY);
        List<SelectOption> options = data.getListAndConvertWithException(OPTION_KEY, (SOData c) -> SelectOption.fromData(lApi, c));
        List<ChannelType> channelTypes = data.getListAndConvert(CHANNEL_TYPES, (Number v) -> ChannelType.fromId(v.intValue()));
        String placeholder = (String) data.get(PLACEHOLDER_KEY);
        Number min_values = (Number) data.get(MIN_VALUES_KEY);
        Number max_values = (Number) data.get(MAX_VALUES_KEY);
        Boolean disabled = (Boolean) data.get(DISABLED_KEY);

        if(type == null || customId == null){
            InvalidDataException.throwException(data, null, SelectMenu.class,
                    new Object[]{type, customId},
                    new String[]{TYPE_KEY, CUSTOM_ID_KEY});
            //unreachable statement
            return null;
        }

        return new SelectMenu(lApi, ComponentType.fromValue(type.intValue()), customId, options, channelTypes, placeholder,
                min_values == null ? null : min_values.intValue(), max_values == null ? null : max_values.intValue(), disabled);
    }

    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * a developer-defined identifier for the button, max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters
     */
    public @NotNull String getCustomId() {
        return customId;
    }

    /**
     * the choices in the select, max {@value ComponentLimits#SELECT_OPTIONS_MAX}
     */
    public @Nullable List<SelectOption> getOptions() {
        return options;
    }

    public @Nullable List<ChannelType> getChannelTypes() {
        return channelTypes;
    }

    /**
     * custom placeholder text if nothing is selected, max {@value ComponentLimits#PLACEHOLDER_MAX_CHARS} characters
     */
    public @Nullable String getPlaceholder() {
        return placeholder;
    }

    /**
     * the minimum number of items that must be chosen; default 1, min {@value ComponentLimits#MIN_VALUE_FIELD_MIN}, max {@value ComponentLimits#MIN_VALUE_FIELD_MAX}
     */
    public @Nullable Integer getMinValues() {
        return minValues;
    }

    /**
     * the maximum number of items that can be chosen; default 1, max {@value ComponentLimits#MAX_VALUE_FIELD_MAX}
     */
    public @Nullable Integer getMaxValues() {
        return maxValues;
    }

    /**
     * disable the select, default false
     */
    public @Nullable Boolean getDisabled() {
        return disabled;
    }

    /**
     * disable the select, default false
     *
     * @return false if {@link #getDisabled()} is {@code null}, {@link #getDisabled()} otherwise
     */
    public boolean isDisabled(){
        return !(disabled == null) && disabled;
    }

    /**
     *
     * @return {@link SOData} for this {@link SelectMenu}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(7);

        data.add(TYPE_KEY, type);
        data.add(CUSTOM_ID_KEY, customId);
        data.addIfNotNull(OPTION_KEY, options);
        data.addIfNotNull(CHANNEL_TYPES, channelTypes);
        data.addIfNotNull(PLACEHOLDER_KEY, placeholder);
        data.addIfNotNull(MIN_VALUES_KEY, minValues);
        data.addIfNotNull(MAX_VALUES_KEY, maxValues);
        data.addIfNotNull(DISABLED_KEY, disabled);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
