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
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.converter.Converter;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 *
 * This retriever is used to retrieve a JSON as {@link Data} from Discord and convert to it {@link R}
 *
 * @param <R> the result class, the {@link Data} should be converted to
 */
public class ConvertingRetriever<R> extends DataRetriever<R>{

    private final Converter<Data, R> converter;

    /**
     *
     * @param lApi {@link LApi}
     * @param query {@link Query} for the HttpRequest
     * @param converter {@link Converter} to convert from {@link Data} to {@link R}
     */
    public ConvertingRetriever(@NotNull LApi lApi, @NotNull Query query, @NotNull Converter<Data, R> converter) {
        super(lApi, query);
        this.converter = converter;
    }

    @Override
    protected @Nullable R processData(@NotNull Data data) throws InvalidDataException {
        return converter.convert(lApi, data);
    }
}
