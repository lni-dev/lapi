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

import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.thread.LApiThreadFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class ShutdownExecutor implements Executor {

    private final @NotNull ThreadFactory factory;

    public ShutdownExecutor(@NotNull LApiImpl lApi) {
        this.factory = new LApiThreadFactory(lApi, true, "shutdown-thread");
    }

    @Override
    public void execute(@NotNull Runnable command) {
        this.factory.newThread(command).start();
    }
}
