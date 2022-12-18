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
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class LApiThreadGroup extends ThreadGroup implements HasLApi {

    public static final String LAPI_THREAD_GROUP_NAME = "lapi-threads";

    private final @NotNull LApiImpl lApi;

    public LApiThreadGroup(@NotNull LApiImpl lApi) {
        super(LAPI_THREAD_GROUP_NAME);
        this.lApi = lApi;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LogInstance log = Logger.getLogger(LAPI_THREAD_GROUP_NAME + ":" + t.getName());

        log.error("Uncaught exception in a thread of the " + this.getClass().getSimpleName() + ". Thread name: " + t.getName());
        log.error(e);
    }
}
