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

package me.linusdev.lapi.api.request;

import org.jetbrains.annotations.NotNull;

public class BiContainer<V, F> {

    private final @NotNull V value1;
    private final @NotNull F value2;

    public BiContainer(@NotNull V value1, @NotNull F value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public @NotNull V getValue1() {
        return value1;
    }

    public @NotNull F getValue2() {
        return value2;
    }
}
