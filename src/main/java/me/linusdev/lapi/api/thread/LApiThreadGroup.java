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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class LApiThreadGroup extends ThreadGroup implements HasLApi {

    private final @NotNull LApiImpl lApi;

    public LApiThreadGroup(@NotNull LApiImpl lApi) {
        super("lapi-threads");
        this.lApi = lApi;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
