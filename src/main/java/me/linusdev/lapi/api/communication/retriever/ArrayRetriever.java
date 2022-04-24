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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.retriever.converter.Converter;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to retrieve any Array and convert it to an {@link ArrayList} of type {@link R}
 * @param <C> the class which will be retrieved from the Discord Api, usually {@link SOData}
 * @param <R> the result class, to which {@link C} should be converted to
 */
public class ArrayRetriever<C, R> extends DataRetriever<ArrayList<R>>{

    private final @NotNull Converter<C, R> converter;

    /**
     *
     * @param lApi {@link LApi}
     * @param query {@link Query} for the HttpRequest
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     */
    public ArrayRetriever(@NotNull LApi lApi, @NotNull Query query, @NotNull Converter<C, R> converter) {
        super(lApi, query);
        this.converter = converter;
    }

    @Override
    @Nullable
    protected ArrayList<R> processData(@NotNull SOData data) throws InvalidDataException {
        List<Object> dataArray = data.getList("array");
        ArrayList<R> resultArray = new ArrayList<>(dataArray.size());

        for(Object o : dataArray)
            resultArray.add(converter.convert(lApi, (C) o));

        return resultArray;
    }
}
