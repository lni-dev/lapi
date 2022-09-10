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

package me.linusdev.lapi.api.communication.retriever.response;

import me.linusdev.data.so.SOData;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.communication.file.types.ContentType;
import me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.lapi.api.communication.retriever.response.body.ErrorMessage;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * LApi Wrapper for Http responses
 */
public class LApiHttpResponse {

    private final InputStream inputStream;
    private final @NotNull HttpResponseCode responseCode;
    private final int responseCodeAsInt;
    private final @NotNull AbstractContentType contentType;

    private @Nullable ErrorMessage error;
    private @Nullable SOData data;
    /**
     * The array key used to load above data
     */
    private @Nullable String usedArrayKey;
    private boolean noContent = false;

    public LApiHttpResponse(HttpResponse<InputStream> source) throws IOException, ParseException {
        this.inputStream = source.body();
        this.responseCodeAsInt = source.statusCode();
        this.responseCode = HttpResponseCode.fromValue(this.responseCodeAsInt);

        Optional<String> contentTypeHeader = source.headers().firstValue(LApiHttpRequest.CONTENT_TYPE_HEADER);
        if(contentTypeHeader.isPresent()){
            this.contentType = ContentType.of(contentTypeHeader.get());
            if(contentType == ContentType.APPLICATION_JSON){
                //try to read an error from the data. will return null if it fails
                this.error = ErrorMessage.fromData(getData());

            }
        } else {
            this.contentType = ContentType.UNKNOWN;
        }
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
     * @return {@link ErrorMessage} if the body contains an error message. {@code null} otherwise.
     */
    public @Nullable ErrorMessage getErrorMessage() {
        return error;
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
