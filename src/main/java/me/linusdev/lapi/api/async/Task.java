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

package me.linusdev.lapi.api.async;

import me.linusdev.lapi.api.async.exception.ErrorException;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @param <R> the result type
 * @param <S> the secondary result type. usually contains information about the task's execution process.
 */
public interface Task<R, S> extends HasLApi {

    default @NotNull String getName() {
        return this.getClass().getSimpleName();
    }

    @NotNull ComputationResult<R, S> execute();

    /**
     * Queues the {@link Task} for future execution. That does not mean, that the {@link Task}
     * actually ends up in a queue, but that the {@link Task} will be executed at any point in time in the future.
     * @return {@link Future}
     */
    @NotNull Future<R, S> queue();

    default @NotNull Future<R, S> queue(ResultConsumer<R, S> consumer) {
        return queue().then(consumer);
    }

    default @NotNull Future<R, S> queue(SingleResultConsumer<R, S> consumer) {
        return queue().then(consumer);
    }

    /**
     * Waits the current Thread until the {@link Future} of this queued {@link Task} has been executed.
     * @return {@link R} result
     * @throws InterruptedException if interrupted while waiting
     * @throws ErrorException if the {@link Future} returned with an error.
     */
    default @NotNull R queueAndWait() throws InterruptedException, ErrorException {
        return queue().getResult();
    }

}
