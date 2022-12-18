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
import me.linusdev.lapi.api.interfaces.HasLApi;
import org.jetbrains.annotations.NotNull;

public abstract class LApiThread extends Thread implements HasLApi {

    private final @NotNull LApiImpl lApi;

    protected LApiThread(@NotNull LApiImpl lApi, @NotNull LApiThreadGroup group, @NotNull String name) {
        super(group, name);
        this.lApi = lApi;
    }

    @Override
    public final synchronized void start() {
        super.start();
    }


    /**
     * Whether this thread allows blocking operations.
     * @return {@code true} if this thread allows blocking operations, {@code false} otherwise.
     */
    public abstract boolean allowBlockingOperations();

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
