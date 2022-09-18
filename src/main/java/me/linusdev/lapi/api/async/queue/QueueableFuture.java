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

import me.linusdev.lapi.api.async.AbstractFuture;
import me.linusdev.lapi.api.async.ExecutableTask;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import org.jetbrains.annotations.NotNull;

public class QueueableFuture<R, T extends ExecutableTask<R, QResponse>> extends AbstractFuture<R, QResponse, T> {

    public QueueableFuture(@NotNull T task) {
        super(task);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
