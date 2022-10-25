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

package me.linusdev.lapi.api.objects.message.embed;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-field-structure" target="_top" >Embed Field Structure</a>
 */
public class Field implements Datable {

    public static final String NAME_KEY = "name";
    public static final String VALUE_KEY = "value";
    public static final String INLINE_KEY = "inline";

    /**
     * @see #getName()
     */
    private final @NotNull String name;

    /**
     * @see #getValue()
     */
    private final @NotNull String value;

    /**
     * @see #isInline()
     */
    private final @Nullable Boolean inline;

    /**
     *
     * @param name name of the field
     * @param value value of the field
     * @param inline whether or not this field should display inline. Inline means, the fields can appear from left to right
     */
    public Field(@NotNull String name, @NotNull String value, @Nullable Boolean inline){
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    /**
     *
     * @param name name of the field
     * @param value value of the field
     */
    public Field(@NotNull String name, @NotNull String value){
        this(name, value, null);
    }

    /**
     * creates a {@link Field} from {@link SOData}
     * @param data to create {@link Field}
     * @return {@link Field}
     * @throws InvalidDataException if {@link #NAME_KEY} or {@link #VALUE_KEY} are missing
     */
    public static @NotNull Field fromData(@NotNull SOData data) throws InvalidDataException {
        final String name = (String) data.get(NAME_KEY);
        final String value = (String) data.get(VALUE_KEY);
        final Boolean inline = (Boolean) data.get(INLINE_KEY);

        if(name == null || value == null){
            InvalidDataException exception = new InvalidDataException(data, "one or more fields are missing. cannot create " + Field.class.getSimpleName());
            if(name == null) exception.addMissingFields(NAME_KEY);
            if(value == null) exception.addMissingFields(VALUE_KEY);
            throw exception;
        }

        return new Field(name, value, inline);
    }

    /**
     * name of the field
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * value of the field
     */
    public @NotNull String getValue() {
        return value;
    }

    /**
     * whether or not this field should display inline
     */
    public @Nullable Boolean isInline() {
        return inline;
    }

    /**
     * Creates a {@link SOData} from this {@link Field}, useful to convert it to JSON
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(NAME_KEY, name);
        data.add(VALUE_KEY, value);
        if(inline != null) data.add(INLINE_KEY, inline);

        return data;
    }
}
