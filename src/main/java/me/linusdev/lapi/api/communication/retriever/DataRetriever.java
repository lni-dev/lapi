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

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
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

    protected boolean storeData = false;
    protected Data data = null;

    /**
     * @param lApi  {@link LApi}
     * @param query the {@link Query} used to retrieve the {@link Data}
     */
    public DataRetriever(@NotNull LApi lApi, @NotNull Query query) {
        super(lApi, query);
    }


    /**
     * Retrieves the wanted Objects JSON and saves it into a {@link Data}.<br>
     * You are probably looking for {@link Queueable#queue()} or {@link #completeHereAndIgnoreQueueThread()}
     *
     * @return {@link Data} with all JSON fields
     * @see Queueable#queue()
     * @see #completeHereAndIgnoreQueueThread()
     */
    protected @NotNull Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException{
        if(storeData){
            data = lApi.getResponse(query.getLApiRequest()).getData();
            return data;
        }
        return lApi.getResponse(query.getLApiRequest()).getData();
    }

    /**
     *
     * This will write the {@link Data data}, that was retrieved.<br>
     * As for the {@link ArrayRetriever}, the retrieved json-array will be wrapped in a json-object
     *
     * @param file Path to the file to save to
     * @param overwriteIfExists whether to overwrite the file if it already exists.
     * @param after {@link BiConsumer}, what to do after the file as been written or an error has occurred
     * @return {@link Future}
     */
    @Override
    public @NotNull Future<T> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists, @Nullable BiConsumer<T, Error> after) {
        storeData = true;
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
                    Files.writeString(file, data.getJsonString());
                    if(after != null) after.accept(t, null);

                } catch (IOException e) {
                    StringBuilder s = new StringBuilder();
                    s.append(e);
                    for (StackTraceElement traceElement : e.getStackTrace())
                        s.append("\tat ").append(traceElement);

                    Logger.log(Logger.Type.ERROR, this.getClass().getSimpleName(), null, s.toString(), true);
                    if(after != null) after.accept(t, new Error(e));
                }
            }
        });
    }
}
