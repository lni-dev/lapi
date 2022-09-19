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

package me.linusdev.lapi.api.communication.retriever;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.async.ComputationResult;
import me.linusdev.lapi.api.async.error.Error;
import me.linusdev.lapi.api.async.error.ThrowableError;
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.async.queue.QueueableImpl;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * A Retriever is a {@link Queueable} that can be {@link #queue() queued}.
 * It is used to retrieve an Object from Discords endpoints.
 * @param <T> the class of the Object that should be retrieved
 */
public abstract class Retriever<T> extends QueueableImpl<T> implements HasLApi {
    protected final @NotNull LApi lApi;
    protected final @NotNull Query query;

    /**
     *
     * @param lApi {@link LApi}
     * @param query the {@link Query} used to retrieve the {@link SOData}
     */
    public Retriever(@NotNull LApi lApi, @NotNull Query query){
        this.lApi = lApi;
        this.query = query;
    }

    /**
     * Retrieves the Object. This will happen on the current Thread! <br>
     * @return retrieved Object
     */
    protected @NotNull LApiHttpResponse retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return lApi.getResponse(query.getLApiRequest());
    }

    /**
     * Processes the retrieved response
     * @param response the retrieved response
     * @return {@link T} converted
     */
    protected @Nullable abstract T process(@NotNull LApiHttpResponse response) throws LApiException, IOException, ParseException, InterruptedException;

    @Override
    public @NotNull ComputationResult<T, QResponse> execute() {
        ComputationResult<T, QResponse> result;

        LApiHttpResponse response;
        try {
            response = retrieve();
        }  catch (Throwable t) {
            LogInstance log = Logger.getLogger("Retriever", Logger.Type.ERROR);
            log.error("Exception while trying to retrieve " + query.toString());
            log.error(t);
            result = new ComputationResult<>(null, new QResponse(t), new ThrowableError(t));
            return result;
        }

        try {
            if(response.isError()){
                result = new ComputationResult<>(null, new QResponse(response), Error.of(response.getErrorMessage()));
            } else {
                result = new ComputationResult<>(process(response), new QResponse(response), null);
            }

        } catch (InvalidDataException invalidDataException){
            LogInstance log = Logger.getLogger("Retriever", Logger.Type.ERROR);
            log.error("InvalidDataException while trying to process result of " + query.toString());
            log.error(invalidDataException);
            log.errorAlign(invalidDataException.getData() == null ? null : invalidDataException.getData().toJsonString().toString(), "Data: ");
            return new ComputationResult<>(null, new QResponse(response), new ThrowableError(invalidDataException));

        }  catch (Throwable t) {
            LogInstance log = Logger.getLogger("Retriever", Logger.Type.ERROR);
            log.error("Exception while trying to process result of " + query.toString());
            log.error(t);
            return new ComputationResult<>(null, new QResponse(response), new ThrowableError(t));
        }

        return result;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
