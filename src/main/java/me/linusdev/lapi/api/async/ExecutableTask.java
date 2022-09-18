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

import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface ExecutableTask<R, S> extends Task<R, S> {

    /**
     * Executes this task in the current thread. Does not call {@link LApi#checkQueueThread()}.
     * @return {@link ComputationResult} result.
     */
    @ApiStatus.Internal
    @NotNull ComputationResult<R, S> execute();

    /**
     * Executes this task in the current thread.
     * @return {@link ComputationResult} result.
     * @throws LApiRuntimeException if the current thread is a thread of {@link LApi} and should not be blocked. See {@link LApi#checkQueueThread()}.
     */
    @NotNull
    @Override
    default ComputationResult<R, S>  executeHere() throws LApiRuntimeException {
        getLApi().checkQueueThread();
        return execute();
    }

}
