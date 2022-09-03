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

package me.linusdev.lapi.api.communication.retriever;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.other.Error;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public abstract class DataRetriever<T> extends Retriever<T>{

    protected LApiHttpResponse response;
    protected @NotNull SOData data;

    /**
     * @param lApi  {@link LApi}
     * @param query the {@link Query} used to retrieve the {@link SOData}
     */
    public DataRetriever(@NotNull LApi lApi, @NotNull Query query) {
        super(lApi, query);
    }

    @Override
    protected @Nullable T process(@NotNull LApiHttpResponse response) throws LApiException, IOException, ParseException, InterruptedException {
        this.response = response;
        this.data = response.getData(LApi.LAPI_ARRAY_WRAPPER_KEY);

        return processData(data);
    }

    protected abstract @Nullable T processData(@NotNull SOData data) throws InvalidDataException;

    /**
     *
     * This will write the {@link SOData data}, that was retrieved.<br>
     * As for the {@link ArrayRetriever}, the retrieved json-array will be wrapped in a json-object
     *
     * @param file Path to the file to save to
     * @param overwriteIfExists whether to overwrite the file if it already exists.
     * @param after {@link BiConsumer}, what to do after the file as been written or an error has occurred
     * @return {@link Future}
     */
    @Override
    public @NotNull Future<T> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists, @Nullable BiConsumer<T, Error> after) {
        return queue(new BiConsumer<T, Error>() {
            @Override
            public void accept(T t, Error error) {

                if(error != null){
                    if(after != null) after.accept(t, error);
                    return;
                }

                if(Files.exists(file)){
                    if(!overwriteIfExists){
                        if(after != null) after.accept(t, new Error(new FileAlreadyExistsException(file + " already exists.")));
                        return;
                    }
                }

                try {
                    Files.deleteIfExists(file);
                    Files.writeString(file, data.toJsonString());
                    if(after != null) after.accept(t, null);

                } catch (IOException e) {
                    Logger.getLogger(this.getClass()).error(e);
                    if(after != null) after.accept(t, new Error(e));
                }
            }
        });
    }
}
