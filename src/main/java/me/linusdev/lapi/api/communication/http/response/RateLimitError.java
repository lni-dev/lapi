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

package me.linusdev.lapi.api.communication.http.response;

import me.linusdev.lapi.api.async.error.MessageError;
import me.linusdev.lapi.api.async.error.StandardErrorTypes;
import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitResponse;
import org.jetbrains.annotations.NotNull;

public class RateLimitError extends MessageError {

    private final @NotNull RateLimitResponse response;

    public RateLimitError(@NotNull RateLimitResponse response) {
        super(response.getMessage(), StandardErrorTypes.RATE_LIMIT);
        this.response = response;
    }

    /**
     * @return Whether the {@link QueueableFuture} will automatically be sent again
     * after the retry time.
     */
    public boolean doesRetry() {
        return true;
    }

    public @NotNull RateLimitResponse getRateLimitResponse() {
        return response;
    }
}
