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

package me.linusdev.lapi.api.objects.component;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This does not actually exist in Discord! But will be created if Discord adds a new type, not yet supported by this api
 */
public class UnknownComponent implements Component{

    private final @NotNull ComponentType type;
    private final int typeInt;
    private final @NotNull SOData data;

    /**
     *
     * @param data data of this Component
     * @param type {@link ComponentType#UNKNOWN}
     * @param typeInt type as int
     */
    public UnknownComponent(@NotNull SOData data, @NotNull ComponentType type, int typeInt){
        this.data = data;
        this.type = type;
        this.typeInt = typeInt;
    }

    /**
     *
     * @param data {@link SOData} for this Component
     * @return {@link UnknownComponent}
     * @throws InvalidDataException if {@link #TYPE_KEY} is null or missing
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable UnknownComponent fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        Number type = (Number) data.get(TYPE_KEY);

        if(type == null){
            InvalidDataException.throwException(data, null, UnknownComponent.class, new Object[]{type}, new String[]{TYPE_KEY});
            return null; // this will never happen, because above method will throw an exception
        }

        return new UnknownComponent(data, ComponentType.fromValue(type.intValue()), type.intValue());
    }

    /**
     * will always be {@link ComponentType#UNKNOWN}
     */
    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * int value of this Components type
     */
    public int getTypeInt() {
        return typeInt;
    }

    /**
     * Data of this component, to read all fields
     */
    @Override
    public @NotNull SOData getData() {
        return data;
    }
}
