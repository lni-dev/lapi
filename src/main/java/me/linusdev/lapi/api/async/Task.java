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

import me.linusdev.lapi.api.async.error.MessageError;
import me.linusdev.lapi.api.async.error.StandardErrorTypes;
import me.linusdev.lapi.api.async.error.ThrowableError;
import me.linusdev.lapi.api.async.exception.ErrorException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Task} can be executed {@link #queue() asynchronous} or {@link #executeHere() in the current thread}.
 *
 * @param <R> the result type
 * @param <S> the secondary result type. usually contains information about the task's execution process.
 * @see Future
 */
public interface Task<R, S> extends HasLApi {

    /**
     * @return {@link String} name of the task.
     */
    default @NotNull String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Executes this task in the current thread.
     * <br><br>
     * For asynchronous execution see {@link #queue()}.
     * @return {@link ComputationResult} result.
     * @throws LApiRuntimeException if the current thread is a thread of {@link LApi} and should not be blocked. See {@link LApi#checkThread()}.
     * @see #queue()
     */
    @NotNull ComputationResult<R, S>  executeHere() throws InterruptedException;

    /**
     *
     * @param consumer {@link Consumer} to set listeners before the Future is queued.
     * @return {@link Future}
     */
    @ApiStatus.Internal
    @NotNull Future<R, S> consumeAndQueue(@Nullable Consumer<Future<R, S>> consumer);

    /**
     * Queues the {@link Task} for future asynchronous execution. That does not mean, that the {@link Task}
     * actually ends up in a queue, but that the {@link Task} will be executed at any point in time in the future.
     * @return {@link Future}
     * @see #queue(SingleResultConsumer)
     * @see #queue(ResultConsumer)
     * @see #queue(ResultAndErrorConsumer)
     * @see #queueAndWait()
     * @see #queueAndWriteToFile(Path, boolean, ResultAndErrorConsumer)
     * @see #queueAndSetBeforeExecutionListener(Consumer)
     */
    default @NotNull Future<R, S> queue() {
        return consumeAndQueue(null);
    }

    /**
     *
     * @param consumer {@link Consumer} listener to be called before execution starts.
     * @return {@link Future}
     * @see #queue()
     */
    default @NotNull Future<R, S> queueAndSetBeforeExecutionListener(@NotNull Consumer<Future<R, S>> consumer) {
        return consumeAndQueue(future -> future.beforeExecution(consumer));
    }

    /**
     *
     * @param consumer {@link ResultConsumer} listener to be called when the result is ready.
     * @return {@link Future}
     * @see #queue()
     */
    default @NotNull Future<R, S> queue(@NotNull ResultConsumer<R, S> consumer) {
        return consumeAndQueue(future -> future.then(consumer));
    }

    /**
     *
     * @param consumer {@link SingleResultConsumer} listener to be called when the result is ready.
     * @return {@link Future}
     * @see #queue()
     */
    default @NotNull Future<R, S> queue(@NotNull SingleResultConsumer<R, S> consumer) {
        return consumeAndQueue(future -> future.then(consumer));
    }

    /**
     *
     * @param consumer {@link ResultAndErrorConsumer} listener to be called when the result is ready.
     * @return {@link Future}
     * @see #queue()
     */
    default @NotNull Future<R, S> queue(@NotNull ResultAndErrorConsumer<R, S> consumer) {
        return consumeAndQueue(future -> future.then(consumer));
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

    /**
     * What exactly will be written to the file, depends on the implementation.
     *
     * @param file {@link Path} of the file to write to.
     * @param overwriteIfExists if the file should be overwritten if it already exists.
     * @param after {@link ResultAndErrorConsumer} to consume the result after it has been written to the file.
     * @return {@link Future}
     */
    default @NotNull Future<R, S> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists,
                                                      @Nullable ResultAndErrorConsumer<R, S> after) {
        return queue((result, secondary, error) -> {

            if(error != null) {
                if(after != null) after.onError(error, this, secondary);
                return;
            }

            if (Files.exists(file)) {
                if (!overwriteIfExists) {
                    if (after != null)
                        after.onError(new MessageError("File " + file + " already exists.", StandardErrorTypes.FILE_ALREADY_EXISTS), this, secondary);
                    return;
                }
            }

            try {
                Files.deleteIfExists(file);
                Files.writeString(file, Objects.toString(result), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                if (after != null) after.consume(result, secondary);

            } catch (IOException e) {
                Logger.getLogger(this.getClass()).error(e);
                if (after != null) after.onError(new ThrowableError(e), this, secondary);
            }

        });
    }

}
