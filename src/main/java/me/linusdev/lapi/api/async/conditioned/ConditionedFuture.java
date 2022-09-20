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

package me.linusdev.lapi.api.async.conditioned;

import me.linusdev.lapi.api.async.AbstractFuture;
import me.linusdev.lapi.api.async.ComputationResult;
import me.linusdev.lapi.api.async.ExecutableTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConditionedFuture<R, S, T extends ConditionedTask<R, S> & ExecutableTask<R, S>> extends AbstractFuture<R, S, T> {
    public ConditionedFuture(@NotNull T task) {
        super(task);
    }

    @Override
    public boolean isExecutable() {
        return getTask().getCondition().check();
    }

    @Override
    @ApiStatus.Internal
    public @Nullable ComputationResult<R, S> executeHere() throws InterruptedException {
        if(isExecutable()) {
            return super.executeHere();
        } else {
            //waiting may take a while, this should not be done on blocking threads.
            getLApi().checkThread();
            getTask().getCondition().await();
            return super.executeHere();
        }
    }
}
