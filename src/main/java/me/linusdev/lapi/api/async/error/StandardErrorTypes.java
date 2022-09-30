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

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.communication.http.response.RateLimitError;
import org.jetbrains.annotations.NotNull;

public enum StandardErrorTypes implements ErrorType, SimpleDatable {

    THROWABLE,

    /**
     * {@link QueueableFuture} was rate limited. The error can be cast to {@link RateLimitError}.
     * If {@link RateLimitError#doesRetry()} is {@code true}, the future will be automatically executed later.
     * @see QResponse#getRateLimitError()
     */
    RATE_LIMIT,

    COMMAND_NOT_FOUND,
    COMMAND_ALREADY_ENABLED,

    UNKNOWN_GUILD,

    FILE_ALREADY_EXISTS,

    HTTP_ERROR_MESSAGE,

    ;

    @Override
    public String simplify() {
        return this.toString();
    }

    @Override
    public @NotNull String getName() {
        return this.toString();
    }
}
