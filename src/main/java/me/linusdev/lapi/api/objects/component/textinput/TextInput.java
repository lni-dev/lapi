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

package me.linusdev.lapi.api.objects.component.textinput;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.component.ComponentType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextInput implements Component, HasLApi, Datable {

    private final @NotNull LApi lApi;

    private final @NotNull ComponentType type;
    private final @NotNull String customId;
    private final @NotNull TextInputStyle style;
    private final @NotNull String label;
    private final @Nullable Integer minLength;
    private final @Nullable Integer maxLength;
    private final @Nullable Boolean required;
    private final @Nullable String value;
    private final @Nullable String placeholder;

    public TextInput(@NotNull LApi lApi, @NotNull ComponentType type, @NotNull String customId, @NotNull TextInputStyle style,
                     @NotNull String label, @Nullable Integer minLength, @Nullable Integer maxLength,
                     @Nullable Boolean required, @Nullable String value, @Nullable String placeholder) {
        this.lApi = lApi;
        this.type = type;
        this.customId = customId;
        this.style = style;
        this.label = label;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.required = required;
        this.value = value;
        this.placeholder = placeholder;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable TextInput fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        ComponentType type = data.getAndConvert(TYPE_KEY, (Number n) -> n == null ? null : ComponentType.fromValue(n.intValue()));
        String customId = data.getAs(CUSTOM_ID_KEY);
        TextInputStyle style = data.getAndConvert(STYLE_KEY, (Number n) -> n == null ? null : TextInputStyle.fromValue(n.intValue()));
        String label = data.getAs(LABEL_KEY);
        Integer minLength = data.getAndConvert(MIN_LENGTH_KEY, (Number n) -> n == null ? null : n.intValue());
        Integer maxLength = data.getAndConvert(MAX_LENGTH_KEY, (Number n) -> n == null ? null : n.intValue());
        Boolean required = data.getAs(REQUIRED_KEY);
        String value = data.getAs(VALUE_KEY);
        String placeholder = data.getAs(PLACEHOLDER_KEY);

        if(type == null || customId == null || style == null || label == null) {
            InvalidDataException.throwException(data, null, TextInput.class,
                    new Object[]{type, customId, style, label},
                    new String[]{TYPE_KEY, CUSTOM_ID_KEY, STYLE_KEY, LABEL_KEY});
            return null; //unreachable statement
        }

        return new TextInput(lApi, type, customId, style, label, minLength, maxLength, required, value, placeholder);
    }

    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(9);

        data.add(TYPE_KEY, type);
        data.add(CUSTOM_ID_KEY, customId);
        data.add(STYLE_KEY, style);
        data.add(LABEL_KEY, label);
        data.addIfNotNull(MIN_LENGTH_KEY, minLength);
        data.addIfNotNull(MAX_LENGTH_KEY, maxLength);
        data.addIfNotNull(REQUIRED_KEY, required);
        data.addIfNotNull(VALUE_KEY, value);
        data.addIfNotNull(PLACEHOLDER_KEY, placeholder);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
