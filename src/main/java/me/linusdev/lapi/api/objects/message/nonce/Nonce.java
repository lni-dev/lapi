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

package me.linusdev.lapi.api.objects.message.nonce;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * used in {@link me.linusdev.lapi.api.objects.message.abstracts.Message}
 * "used for validating a message was sent"
 * TODO what is this?
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

    @Override
    public Object simplify() {
        return string == null ? integer : string;
    }

    @Override
    public String toString() {
        return Objects.toString(simplify());
    }
}
