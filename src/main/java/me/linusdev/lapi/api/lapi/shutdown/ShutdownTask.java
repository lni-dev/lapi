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

import me.linusdev.lapi.api.async.*;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.log.LogInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public abstract class ShutdownTask implements ExecutableTask<Nothing, Shutdownable> {

    protected final @NotNull LApi lApi;
    protected final @NotNull Shutdownable parent;
    protected final @NotNull Executor executor;
    protected final @NotNull LogInstance log;

    public ShutdownTask(@NotNull LApi lApi, @NotNull Shutdownable parent, @NotNull Executor executor, @NotNull LogInstance log) {
        this.lApi = lApi;
        this.parent = parent;
        this.executor = executor;
        this.log = log;
    }

    @Override
    public abstract @NotNull ComputationResult<Nothing, Shutdownable> execute() throws InterruptedException;

    @Override
    public @NotNull Future<Nothing, Shutdownable> consumeAndQueue(@Nullable Consumer<Future<Nothing, Shutdownable>> consumer) {
        ShutdownFuture future = new ShutdownFuture(this);
        if(consumer != null) consumer.accept(future);

        executor.execute(() -> {
            try {
                future.executeHere();
            } catch (Exception e) {
                log.error("A shutdown task related to " + parent.getShutdownableName() + " threw an exception.");
                log.error(e);
            }
        });

        return future;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
