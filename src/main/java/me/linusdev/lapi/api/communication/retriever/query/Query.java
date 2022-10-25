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

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.lapi.api.exceptions.LApiException;
import me.linusdev.lapi.api.communication.http.request.LApiHttpRequest;
import me.linusdev.lapi.api.communication.http.request.Method;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Query} is used to build a {@link LApiHttpRequest HttpRequest}.
 */
public interface Query extends HasLApi {

    /**
     * The method used for this {@link Query}
     */
    @NotNull Method getMethod();

    @NotNull PlaceHolder[] getPlaceHolders();

    /**
     *
     * @return the {@link LApiHttpRequest} built from this {@link Query}
     * @throws LApiException if an Error occurred
     */
    LApiHttpRequest getLApiRequest() throws LApiException;

    /**
     * usually the method and the url of this query. This should NOT be used to build a http-request
     */
    String asString();

    /**
     * link used to create this query.
     * @return {@link AbstractLink}
     */
    @NotNull AbstractLink getLink();
}
