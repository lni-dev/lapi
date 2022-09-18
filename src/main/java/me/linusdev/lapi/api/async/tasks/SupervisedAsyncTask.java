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

package me.linusdev.lapi.api.async.tasks;

import me.linusdev.lapi.api.async.AbstractFuture;
import me.linusdev.lapi.api.async.ExecutableTask;
import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.conditioned.ConditionedFuture;
import me.linusdev.lapi.api.async.conditioned.ConditionedTask;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class SupervisedAsyncTask<R, S> implements ExecutableTask<R, S>, ConditionedTask<R, S> {

    private final @NotNull LApi lApi;

    public SupervisedAsyncTask(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    @Override
    public @NotNull Future<R, S> consumeAndQueue(@Nullable Consumer<Future<R, S>> consumer) {

        final @NotNull AbstractFuture<R, S, SupervisedAsyncTask<R, S>> future = new ConditionedFuture<>(this);

        if(consumer != null) consumer.accept(future);

        lApi.runSupervised(() -> {
            try {
                future.executeHere();
            } catch (InterruptedException e) {
                throw new LApiRuntimeException(e);
            }
        });

        return future;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
