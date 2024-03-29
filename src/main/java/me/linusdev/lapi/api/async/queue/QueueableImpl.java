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

package me.linusdev.lapi.api.async.queue;

import me.linusdev.lapi.api.async.ExecutableTask;
import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class QueueableImpl<T> implements ExecutableTask<T, QResponse>, Queueable<T> {

    @Override
    public @NotNull Future<T, QResponse> consumeAndQueue(@Nullable Consumer<Future<T, QResponse>> consumer) {
        final QueueableFuture<T> future = new QueueableFuture<>(this);
        if(consumer != null) consumer.accept(future);
        getLApi().queue(future);
        return future;
    }

    @ApiStatus.Internal
    public abstract @NotNull Query getQuery();

}
