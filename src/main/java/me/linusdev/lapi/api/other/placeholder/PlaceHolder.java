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

package me.linusdev.lapi.api.other.placeholder;

import org.jetbrains.annotations.NotNull;

public class PlaceHolder {

    private final @NotNull Name key;
    private final @NotNull String value;

    /**
     *
     * @param key {@link Name}
     * @param value the value the placeholder should be replaced with
     * @deprecated replaced by {@link Name#withValue(String)}
     */
    @Deprecated
    public PlaceHolder(@NotNull Name key, @NotNull String value){
        this.key = key;
        this.value = value;
    }

    public String place(@NotNull String in){
        return in.replace(key.getPlaceholder(), value);
    }

    public @NotNull String getValue() {
        return value;
    }

    public @NotNull Name getKey() {
        return key;
    }
}
