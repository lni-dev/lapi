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

import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.communication.http.response.RateLimitError;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Response of the http request queue.<br><br>
 * One of {@link #getResponse()} or {@link #getException()} will always be {@code null}, but never both.
 */
public class QResponse {

    private final @NotNull Query query;
    private final @Nullable LApiHttpResponse response;
    private final @Nullable Throwable exception;
    private final @Nullable RateLimitError rateLimitError;

    public QResponse(@NotNull Query query, @NotNull LApiHttpResponse response) {
        this.query = query;
        this.response = response;
        this.rateLimitError = null;
        this.exception = null;
    }

    public QResponse(@NotNull Query query, @NotNull LApiHttpResponse response, @Nullable RateLimitError rateLimitError) {
        this.query = query;
        this.response = response;
        this.rateLimitError = rateLimitError;
        this.exception = null;
    }

    public QResponse(@NotNull Query query, @NotNull Throwable exception) {
        this.query = query;
        this.response = null;
        this.rateLimitError = null;
        this.exception = exception;
    }

    /**
     * The {@link RateLimitError} contains information about the rate limit and if the {@link QueueableFuture} will
     * automatically be sent again (see {@link RateLimitError#doesRetry()}).
     *
     * @return {@link RateLimitError} if this is a rate limit response. {@code null} otherwise
     */
    public @Nullable RateLimitError getRateLimitError() {
        return rateLimitError;
    }

    /**
     * {@link Query} used to send the http request.
     * @return {@link Query}
     */
    public @NotNull Query getQuery() {
        return query;
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
