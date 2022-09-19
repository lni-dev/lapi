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
    private final @NotNull String name;

    public LApiThreadFactory(@NotNull LApiImpl lApi, boolean allowBlockingOperations, @NotNull String name) {
        this.lApi = lApi;
        this.group = lApi.getLApiThreadGroup();
        this.allowBlockingOperations = allowBlockingOperations;
        this.name = name;
    }


    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new LApiThread(lApi, group, name) {
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
