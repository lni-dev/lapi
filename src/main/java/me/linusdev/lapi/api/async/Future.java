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

import me.linusdev.lapi.api.async.exception.CancellationException;
import me.linusdev.lapi.api.async.exception.ErrorException;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


/**
 * A {@link Future} represents a {@link Task} that will be executed in the future and whose result or error will
 * be retrieved.
 *
 * @param <R> the result type
 * @param <S> the secondary result type. usually contains information about the task's execution process.
 */
public interface Future<R, S> extends HasLApi {

    /**
     * Cancels this future, if it is not already executing.
     * @return the canceled {@link Future}
     */
    @NotNull Future<R, S> cancel();

    /**
     *
     * @return {@code true} if this {@link Future} is canceled.
     */
    boolean isCanceled();

    /**
     *
     * @return {@code true} if this {@link Future} has started its execution.
     */
    boolean hasStarted();

    /**
     *
     * @return {@code true} if this {@link Future} is done (execution finished and result or error is ready).
     */
    boolean isDone();

    /**
     * Given consumer will be called before the {@link Future} will be executed. The {@link Future} can still
     * be {@link #cancel() canceled}.
     * @param consumer {@link Consumer} that will be called before execution
     * @return the {@link Future} itself.
     */
    @NotNull Future<R, S> beforeExecution(@NotNull Consumer<Future<R, S>> consumer);

    /**
     * Given consumer will be called after {@link Future}'s execution has finished and result or error is ready.
     * @param consumer {@link ResultConsumer}
     * @return the {@link Future} itself.
     */
    @NotNull Future<R, S> then(@NotNull ResultConsumer<R, S> consumer);

    /**
     * Given consumer will be called after {@link Future}'s execution has finished and result or error is ready.
     * @param consumer {@link SingleResultConsumer}
     * @return the {@link Future} itself.
     */
    default @NotNull Future<R, S> then(@NotNull SingleResultConsumer<R, S> consumer) {
        then(new ResultConsumer<>() {
            @Override
            public void consume(@NotNull R result, @NotNull S secondary) {
                consumer.consume(result);
            }

            @Override
            public void onError(@NotNull Error error, @NotNull Task<R, S> task, @NotNull S secondary) {
                consumer.onError(error, task, secondary);
            }
        });

        return this;
    }

    /**
     * Waits the current Thread until this {@link Future} has been executed. If execution has already finished,
     * this method will return immediately.
     * @return {@link ComputationResult} containing the result, secondary result and a potential error.
     * @throws InterruptedException if interrupted while waiting
     * @throws CancellationException if the {@link Future} has been canceled.
     */
    @Blocking
    @NotNull ComputationResult<R, S> get() throws InterruptedException, CancellationException;

    /**
     * Waits the current Thread until this {@link Future} has been executed. If execution has already finished,
     * this method will return immediately.
     * @return {@link R} result
     * @throws InterruptedException if interrupted while waiting
     * @throws ErrorException if the {@link Future} returned with an error.
     */
    @Blocking
    @SuppressWarnings("ConstantConditions")
    default @NotNull R getResult() throws InterruptedException, ErrorException {
        ComputationResult<R, S> result = get();

        if(result.hasError()) throw new ErrorException(result.getError());
        return result.getResult();
    }

}
