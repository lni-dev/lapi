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

package me.linusdev.lapi.api.async.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageError implements Error{

    private final @NotNull String message;
    private final @NotNull StandardErrorTypes type;

    public MessageError(@NotNull String message, @NotNull StandardErrorTypes type) {
        this.message = message;
        this.type = type;
    }

    @Override
    public @Nullable Throwable getThrowable() {
        return null;
    }

    @Override
    public @NotNull ErrorType getType() {
        return type;
    }

    @Override
    public @NotNull String getMessage() {
        return message;
    }
}
