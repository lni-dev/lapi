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

    private final @NotNull Name name;
    private final @NotNull String value;

    /**
     *
     * @param name {@link Name}
     * @param value the value the placeholder should be replaced with
     */
    public PlaceHolder(@NotNull Name name, @NotNull String value){
        this.name = name;
        this.value = value;
    }

    public String place(String in){
        return in.replace(name.getPlaceholder(), value);
    }

    public @NotNull String getValue() {
        return value;
    }

    public @NotNull Name getName() {
        return name;
    }
}
