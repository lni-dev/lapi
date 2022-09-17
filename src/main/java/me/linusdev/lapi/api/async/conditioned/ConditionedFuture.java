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
import me.linusdev.lapi.api.async.Task;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import org.jetbrains.annotations.NotNull;

public class ConditionedFuture<R, S, T extends ConditionedTask<R, S>> extends AbstractFuture<R, S, T> {
    public ConditionedFuture(@NotNull T task) {
        super(task);
    }

    @Override
    public boolean isExecutable() {
        return getTask().getCondition().check();
    }

    @Override
    public void completeHere() throws InterruptedException {
        if(isExecutable()) {
            super.completeHere();
        } else {
            getTask().getCondition().await();
            super.completeHere();
        }
    }
}
