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

package me.linusdev.lapi.api.thread;

import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class LApiThreadFactory implements ThreadFactory {

    private final @NotNull LApiImpl lApi;
    private final @NotNull LApiThreadGroup group;
    private final boolean allowBlockingOperations;

    public LApiThreadFactory(@NotNull LApiImpl lApi, @NotNull LApiThreadGroup group, boolean allowBlockingOperations) {
        this.lApi = lApi;
        this.group = group;
        this.allowBlockingOperations = allowBlockingOperations;
    }


    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new LApiThread(lApi, group, "factorized-lapi-thread") {
            @Override
            public boolean allowBlockingOperations() {
                return allowBlockingOperations;
            }

            @Override
            public @NotNull LApi getLApi() {
                return lApi;
            }

            @Override
            public void run() {
                r.run();
            }
        };
    }
}
