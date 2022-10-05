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

package me.linusdev.lapi.api.communication.http.ratelimit;

import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.lapi.LApi;
import org.jetbrains.annotations.NotNull;

public class DefaultRateLimitedQueueChecker implements RateLimitedQueueChecker {

    private final @NotNull LApi lApi;
    private final @NotNull Bucket bucket;

    public DefaultRateLimitedQueueChecker(@NotNull LApi lApi, @NotNull Bucket bucket) {
        this.lApi = lApi;
        this.bucket = bucket;
    }

    @Override
    public @NotNull RateLimitedQueueChecker.CheckType startCheck(int queueSize) {
        return CheckType.REMOVE_ALL;
    }

    @Override
    public boolean check(@NotNull QueueableFuture<?> toCheck) {
        return false;
    }

    @Override
    public boolean checkAgain() {
        return false;
    }
}
