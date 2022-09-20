/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.communication.cdn.image;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.ResultAndErrorConsumer;
import me.linusdev.lapi.api.async.error.MessageError;
import me.linusdev.lapi.api.async.error.StandardErrorTypes;
import me.linusdev.lapi.api.async.error.ThrowableError;
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.Retriever;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CDNImageRetriever extends Retriever<InputStream>  {

    /**
     * @param image the {@link CDNImage image}
     * @param desiredSize a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param check whether to check if the size is a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     */
    public CDNImageRetriever(@NotNull CDNImage image, int desiredSize, boolean check) {
        super(image.getLApi(), image.getQuery(desiredSize));
        if(check) checkDesiredSize(desiredSize);
    }

    /**
     *
     * No desired size. Discord will decide.
     *
     * @param image the {@link CDNImage image}
     */
    public CDNImageRetriever(@NotNull CDNImage image) {
        this(image, ImageQuery.NO_DESIRED_SIZE , false);
    }

    @Override
    protected @Nullable InputStream process(@NotNull LApiHttpResponse response) throws LApiException, IOException, ParseException, InterruptedException {
        return response.getInputStream();
    }

    /**
     * checks if given size, is a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     */
    public static void checkDesiredSize(int desiredSize){
        if(!(desiredSize >= ImageQuery.SIZE_QUERY_PARAM_MIN && desiredSize <= ImageQuery.SIZE_QUERY_PARAM_MAX))
            throw new IllegalArgumentException("desiredSize must be a power of 2 between "
                    + ImageQuery.SIZE_QUERY_PARAM_MIN + " and " + ImageQuery.SIZE_QUERY_PARAM_MAX);

        int i = Math.max(1, ImageQuery.SIZE_QUERY_PARAM_MIN);

        while(i <= ImageQuery.SIZE_QUERY_PARAM_MAX){
            if(i == desiredSize) return;
            i *=2 ;
        }

        throw new IllegalArgumentException("desiredSize must be a power of 2 between "
                + ImageQuery.SIZE_QUERY_PARAM_MIN + " and " + ImageQuery.SIZE_QUERY_PARAM_MAX);
    }

    /**
     *
     * This will write the http-response-body to the file.
     *
     * @param file Path to the file to save to
     * @param overwriteIfExists whether to overwrite the file if it already exists.
     * @param after {@link ResultAndErrorConsumer}, what to do after the file as been written or an error has occurred. the InputStream will already be closed.
     * @return {@link Future}
     */
    @Override
    public @NotNull Future<InputStream, QResponse> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists, @Nullable ResultAndErrorConsumer<InputStream, QResponse> after) {
        return queue((result, s, error) -> {
            if(error != null){
                if(after != null) after.onError(error, this, s);
                return;
            }

            if(Files.exists(file)){
                if(!overwriteIfExists){
                    if(after != null) after.onError(new MessageError("File " + file + " already exists.", StandardErrorTypes.FILE_ALREADY_EXISTS), this, s);
                    return;
                }
            }

            OutputStream out = null;
            try {
                Files.deleteIfExists(file);
                out = Files.newOutputStream(file);
                result.transferTo(out);
                if(after != null) after.consume(result, s, null);

            } catch (IOException e) {
                Logger.getLogger(this.getClass()).error(e);
                if(after != null) after.onError(new ThrowableError(e), this, s);
            }finally {
                if(out != null && result != null) {
                    try {
                        out.close();
                        result.close();
                    } catch (IOException ignored) {  }
                }
            }
        });
    }
}
