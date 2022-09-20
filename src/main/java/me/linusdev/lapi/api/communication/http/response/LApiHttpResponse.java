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

package me.linusdev.lapi.api.communication.http.response;

import me.linusdev.data.so.SOData;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.communication.file.types.ContentType;
import me.linusdev.lapi.api.communication.http.HeaderType;
import me.linusdev.lapi.api.communication.http.HeaderTypes;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitResponse;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitScope;
import me.linusdev.lapi.api.communication.http.request.LApiHttpRequest;
import me.linusdev.lapi.api.communication.http.response.body.HttpErrorMessage;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * LApi Wrapper for Http responses
 */
public class LApiHttpResponse {

    private final @NotNull LogInstance log = Logger.getLogger(LApiHttpResponse.class.getSimpleName());

    private final @NotNull HttpHeaders headers;

    private final InputStream inputStream;
    private final @NotNull HttpResponseCode responseCode;
    private final int responseCodeAsInt;
    private final @NotNull AbstractContentType contentType;

    private @Nullable HttpErrorMessage error;
    private @Nullable RateLimitScope rateLimitScope;
    private @Nullable RateLimitResponse rateLimit;

    private @Nullable SOData data;
    /**
     * The array key used to load above data
     */
    private @Nullable String usedArrayKey;
    /**
     * Only for json-body
     */
    private boolean noContent = false;

    public LApiHttpResponse(HttpResponse<InputStream> source) throws IOException, ParseException {
        this.headers = source.headers();
        this.inputStream = source.body();
        this.responseCodeAsInt = source.statusCode();
        this.responseCode = HttpResponseCode.fromValue(this.responseCodeAsInt);

        Optional<String> contentTypeHeader = source.headers().firstValue(HeaderTypes.CONTENT_TYPE.getName());
        this.contentType = contentTypeHeader.map(ContentType::of).orElse(ContentType.UNKNOWN);


        if(this.responseCode == HttpResponseCode.TOO_MANY_REQUESTS) {
            try {
                this.rateLimit = RateLimitResponse.fromData(getData());
                this.rateLimitScope = RateLimitScope.of(getHeaderFirstValue(HeaderTypes.X_RATE_LIMIT_SCOPE));
                if(rateLimitScope == RateLimitScope.SHARED) {
                    log.warning("LApi hit a shared rate limit: " + rateLimit.getMessage());
                } else {
                    log.error("LApi Hit a " + rateLimitScope + " rate limit: " + rateLimit.getMessage());
                }

            } catch (InvalidDataException e) {
               log.error(e);
            }

        }

        if(contentType == ContentType.APPLICATION_JSON){
            //try to read an error from the data. will return null if it fails
            this.error = HttpErrorMessage.fromData(getData());
        }

    }

    public @Nullable String getHeaderFirstValue(@NotNull HeaderType type) {
        return headers.firstValue(type.getName()).orElse(null);
    }

    /**
     *
     * @return body-json as {@link SOData}. An Empty {@link SOData} will be returned,
     * if there is {@link #noContent() no content}. This {@link SOData} will be the exact same for subsequent calls.
     * @throws IOException from {@link PushbackReader}
     * @throws ParseException from {@link JsonParser}
     */
    public @NotNull SOData getData() throws IOException, ParseException {
        return getData(LApi.LAPI_ARRAY_WRAPPER_KEY);
    }

    /**
     *
     * @param arrayKey to parse pure JSON array
     * @return body-json as {@link SOData}. An Empty {@link SOData} will be returned,
     * if there is {@link #noContent() no content}. This {@link SOData} will be the exact same for subsequent calls.
     * @throws IOException from {@link PushbackReader}
     * @throws ParseException from {@link JsonParser}
     */
    public @NotNull SOData getData(@Nullable String arrayKey) throws IOException, ParseException {
        if(data != null &&
                ((arrayKey == null && usedArrayKey == LApi.LAPI_ARRAY_WRAPPER_KEY) || (arrayKey != null && arrayKey.equals(usedArrayKey)))) return data;
        if(!hasJsonBody()) throw new UnsupportedOperationException("Response has no Json body.");

        JsonParser jsonParser = new JsonParser();
        if(arrayKey == null) arrayKey = LApi.LAPI_ARRAY_WRAPPER_KEY;
        usedArrayKey = arrayKey;
        jsonParser.setArrayWrapperKey(arrayKey);
        data = jsonParser.parseStream(inputStream);
        if(data.size() == 0) noContent = true;
        return data;
    }

    /**
     * {@code false} does not necessarily mean, that there is content!
     * @return {@code true} if the response body did not contain any data
     */
    public boolean noContent(){
        return noContent;
    }

    /**
     *
     * @return {@code true} if the content type is {@link ContentType#APPLICATION_JSON APPLICATION_JSON}.
     */
    public boolean hasJsonBody() {
        return AbstractContentType.equals(contentType, ContentType.APPLICATION_JSON);
    }

    /**
     * @return {@code true} if the response contained an error.
     */
    public boolean isError() {
        return error != null;
    }

    /**
     *
     * @return {@code true} if this response contained a {@link RateLimitResponse}
     */
    public boolean isRateLimitResponse() {
        return rateLimit != null;
    }

    /**
     *
     * @return {@link HttpResponseCode}
     */
    public @NotNull HttpResponseCode getResponseCode() {
        return responseCode;
    }

    public int getResponseCodeAsInt() {
        return responseCodeAsInt;
    }

    /**
     *
     * @return {@link HttpErrorMessage} if the body contains an error message. {@code null} otherwise.
     * @see #isError()
     */
    public @Nullable HttpErrorMessage getErrorMessage() {
        return error;
    }

    /**
     *
     * @return {@link RateLimitResponse} if the body contains a rate limit response. {@code null} otherwise.
     * @see #isRateLimitResponse()
     */
    public @Nullable RateLimitResponse getRateLimitResponse() {
        return rateLimit;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


    @Override
    public String toString() {

        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add("responseCode", responseCode);
        data.add("responseCodeAsInt", responseCodeAsInt);
        data.add("contentType", contentType);
        data.add("error", error);
        data.add("data", this.data);
        data.add("usedArrayKey", usedArrayKey);

        return data.toJsonString().toString();
    }
}
