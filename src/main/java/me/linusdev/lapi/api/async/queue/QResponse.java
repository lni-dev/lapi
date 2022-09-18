/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.async.queue;

import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import org.jetbrains.annotations.Nullable;

/**
 * Response of the http request queue.<br><br>
 * One of {@link #getResponse()} or {@link #getException()} will always be {@code null}, but never both.
 */
public class QResponse {

    private final @Nullable LApiHttpResponse response;
    private final @Nullable Throwable exception;

    public QResponse(@Nullable LApiHttpResponse response) {
        this.response = response;
        this.exception = null;
    }

    public QResponse(@Nullable Throwable exception) {
        this.response = null;
        this.exception = exception;
    }

    /**
     * The response of the http request. possibly with {@link LApiHttpResponse#getErrorMessage() error}.
     * @return {@link LApiHttpResponse}
     */
    public @Nullable LApiHttpResponse getResponse() {
        return response;
    }

    /**
     * Not {@code null} if an exception was thrown while building or sending the http request.
     * @return {@link Throwable}
     */
    public @Nullable Throwable getException() {
        return exception;
    }


}
