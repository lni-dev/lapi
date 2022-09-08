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

package me.linusdev.lapi.api.storage;

import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class Storage implements HasLApi {

    private final @NotNull LApiImpl lApi;

    private final @NotNull Path storageLocation;

    public Storage(@NotNull LApiImpl lApi, @NotNull Path storageLocation) {
        this.lApi = lApi;
        this.storageLocation = storageLocation;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
