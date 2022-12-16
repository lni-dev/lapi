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

package me.linusdev.lapi.api.lapi.shutdown;

import org.jetbrains.annotations.NotNull;

public interface ShutdownOption {

    long id();

    long mutuallyExclusiveWith();

    default boolean canBeSet(long bitfield) {
        return (bitfield & mutuallyExclusiveWith()) == 0L;
    }

    @NotNull String name();

    default boolean isSet(long bitfield) {
        return (bitfield & id()) > 0L;
    }

}
