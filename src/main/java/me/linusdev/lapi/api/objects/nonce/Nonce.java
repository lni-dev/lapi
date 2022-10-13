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

package me.linusdev.lapi.api.objects.nonce;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Can be used for validating that a message was sent.
 * @see MessageBuilder#setNonce(Nonce)
 */
public class Nonce implements SimpleDatable {

    private final @Nullable String string;
    private final @Nullable Integer integer;

    public Nonce(@Nullable String s, @Nullable Integer i){
        this.string = s;
        this.integer = i;
    }

    /**
     *
     * @param stringOrInt {@link String} or {@link Integer} or {@code null}
     * @return {@link Nonce} or {@code null} if stringOrInt was {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable Nonce fromStringOrInteger(@Nullable Object stringOrInt){
        if(stringOrInt == null) return null;
        if(stringOrInt instanceof Number){
            return new Nonce(null, ((Number) stringOrInt).intValue());
        }else if(stringOrInt instanceof String){
            return new Nonce((String) stringOrInt, null);
        }

        return new Nonce(Objects.toString(stringOrInt), null);
    }


    public @Nullable String getString() {
        return string;
    }

    public @Nullable Integer getInteger() {
        return integer;
    }

    public int length() {
        return toString().length();
    }

    /**
     *
     * @return {@link #getString()} if it is not {@code null}, {@link #getInteger()} (as string) otherwise.
     */
    public @NotNull String getAsString() {
        return toString();
    }

    @Override
    public Object simplify() {
        return string == null ? integer : string;
    }

    @Override
    public String toString() {
        return Objects.toString(simplify());
    }
}
