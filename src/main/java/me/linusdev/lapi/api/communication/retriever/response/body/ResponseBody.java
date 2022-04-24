/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.communication.retriever.response.body;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

/**
 * This is returned by some {@link me.linusdev.lapi.api.communication.retriever.Retriever retrievers}, if they retrieve more than one object
 */
public abstract class ResponseBody implements HasLApi {

    protected final @NotNull LApi lApi;
    protected final @NotNull SOData data;

    /**
     *
     * @param lApi {@link LApi}
     * @param data the retrieved {@link SOData}
     */
    public ResponseBody(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        this.data = data;
        this.lApi = lApi;
    }

    /**
     * the retrieved {@link SOData}
     */
    public @NotNull SOData getData() {
        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
