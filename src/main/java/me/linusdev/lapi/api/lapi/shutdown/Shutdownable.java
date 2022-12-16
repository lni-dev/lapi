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

import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.Nothing;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.log.LogInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;

public interface Shutdownable extends HasLApi {

    /**
     * <p>
     *     This method should not do any blocking operations. It should instead do it's shutdown sequence
     *     inside a {@link ShutdownTask} and return the corresponding {@link Future}.
     * </p>
     *
     * @param lApi {@link LApiImpl} which is shutting down
     * @param shutdownOptions different {@link ShutdownOptions}
     * @param log {@link LogInstance} which should be used to log shutdown related stuff (best effort)
     * @param shutdownExecutor {@link Executor} which should be used to execute the {@link ShutdownTask}
     * @return {@link Future} of your shutdown task or {@code null} if no shutdown sequence is required.
     */
    @ApiStatus.Internal
    @ApiStatus.OverrideOnly
    @NonBlocking
    @Nullable Future<Nothing, Shutdownable> shutdown(@NotNull LApiImpl lApi, long shutdownOptions,
                                                     @NotNull LogInstance log, @NotNull Executor shutdownExecutor);

    default void registerShutdownable() {
        getLApi().registerShutdownable(this);
    }

    default @NotNull String getShutdownableName() {
        return this.getClass().getSimpleName();
    }

}
