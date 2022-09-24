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

package me.linusdev.lapi.api.other.placeholder;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * String that should be concat with other Strings
 */
public interface Concatable {

    @ApiStatus.Internal
    static int construct(@NotNull StringBuilder sb, @NotNull Concatable[] concatables, @NotNull PlaceHolder[] placeHolders) {
        int i = 0;
        for(Concatable concatable : concatables) {
            concatable.connect(sb);
            if(concatable.isKey()) {
                concatable.concat(sb, placeHolders[i].getValue());
                //assert that the value was used for the correct placeHolder
                assert concatable == placeHolders[i].getKey();
                i++;
            } else {
                concatable.concat(sb);
            }
        }
        return i;

    }


    /**
     * Concat this concatable to given {@link StringBuilder}
     * @param sb {@link StringBuilder} to concat to.
     * @param value optional value (max 1). See concrete implementation for more information.
     */
    void concat(@NotNull StringBuilder sb, @NotNull Object... value);

    /**
     * Adds an optional connection after the last {@link Concatable}. This may only
     * be called if something has already been concat to given {@link StringBuilder} and if
     * {@link #concat(StringBuilder, Object...)} will be called afterwords.
     * @param sb {@link StringBuilder} to concat to.
     */
    void connect(@NotNull StringBuilder sb);

    /**
     * as unique as possible id between {@link Name#amount} and 100.
     * @return int code
     */
    int code();

    /**
     *
     * @return {@link String} represented by this {@link Concatable}
     */
    @NotNull String getString();

    /**
     * If this {@link Concatable} is a key, it must be comparable with "=="
     * @return Whether this {@link Concatable} is a key and must be replaced before concatenating
     */
    default boolean isKey() {
        return false;
    }

}
