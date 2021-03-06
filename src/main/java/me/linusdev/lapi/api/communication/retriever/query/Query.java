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

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.lapihttprequest.Method;
import me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.lapi.api.objects.HasLApi;

/**
 * A {@link Query} is used to build a {@link LApiHttpRequest HttpRequest}.<br>
 * The name query might be a bit misleading. As this is not only a GET request.
 * POST, PUT, DELETE, ... may also be represented by a {@link Query}
 */
public interface Query extends HasLApi {

    /**
     * The method used for this {@link Query}
     */
    Method getMethod();

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
}
