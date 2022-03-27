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
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.NoInternetException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.other.Container;
import me.linusdev.lapi.api.other.Error;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A Retriever is a {@link Queueable} that can be {@link LApi#queue(Queueable, BiConsumer, Consumer, Consumer) queued}.
 * It is used to retrieve an Object from Discords endpoint ({@link MessageRetriever for example a Message}).
 * @param <T> the class of the Object that should be retrieved
 */
public abstract class Retriever<T> extends Queueable<T> implements HasLApi {
    protected final @NotNull LApi lApi;
    protected final @NotNull Query query;

    /**
     *
     * @param lApi {@link LApi}
     * @param query the {@link Query} used to retrieve the {@link Data}
     */
    public Retriever(@NotNull LApi lApi, @NotNull Query query){
        this.lApi = lApi;
        this.query = query;
    }

    /**
     * Retrieves the Object. This will happen on the current Thread! <br>
     * I suggest you use {@link Queueable#queue()} or {@link #completeHereAndIgnoreQueueThread()} instead!
     * @return retrieved Object
     * @see Queueable#queue()
     * @see #completeHereAndIgnoreQueueThread()
     */
    protected @Nullable abstract T retrieve() throws LApiException, IOException, ParseException, InterruptedException;

    @Override
    protected @NotNull Container<T> completeHereAndIgnoreQueueThread() throws NoInternetException {
        Container<T> container;
        try {
            container = new Container<T>(retrieve(), null);

        } catch (NoInternetException noInternetException){
            throw noInternetException; // future will catch this and queue again...

        } catch (InvalidDataException invalidDataException){
            LogInstance log = Logger.getLogger("Retriever", Logger.Type.ERROR);
            log.error("InvalidDataException while trying to retrieve " + query.toString());
            log.error(invalidDataException);
            log.errorAlign(invalidDataException.getData().getJsonString().toString(), "Data: ");
            container = new Container<T>(null, new Error(invalidDataException));

        } catch (Throwable t) {
            LogInstance log = Logger.getLogger("Retriever", Logger.Type.ERROR);
            log.error("Exception while trying to retrieve " + query.toString());
            log.error(t);
            container = new Container<T>(null, new Error(t));

        }

        return container;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
