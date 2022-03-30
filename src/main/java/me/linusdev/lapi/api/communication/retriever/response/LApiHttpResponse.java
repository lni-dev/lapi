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

package me.linusdev.lapi.api.communication.retriever.response;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.file.types.ContentType;
import me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.lapi.api.communication.retriever.response.body.ErrorMessage;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * LApi Wrapper for Http responses
 */
public class LApiHttpResponse {

    private final InputStream inputStream;
    private final @Nullable PushbackReader reader;
    private final @NotNull HttpResponseCode responseCode;

    private @Nullable Boolean isArray = null;
    private @Nullable ErrorMessage error;
    private @Nullable Data data;
    private boolean noContent = false;

    public LApiHttpResponse(HttpResponse<InputStream> source) throws IOException, ParseException {
        this.inputStream = source.body();
        this.responseCode = HttpResponseCode.fromValue(source.statusCode());

        Optional<String> contentTypeHeader = source.headers().firstValue(LApiHttpRequest.CONTENT_TYPE_HEADER);
        if(contentTypeHeader.isPresent()){
            //TODO make a variable to store the content type. Function to check if content type is JSON
            if(contentTypeHeader.get().equalsIgnoreCase(ContentType.APPLICATION_JSON.getContentTypeAsString())){
                this.reader = new PushbackReader(new InputStreamReader(inputStream));
                if (!isArray()) {
                    error = ErrorMessage.fromData(getData());
                }
            } else {
               this.reader = null;
            }
        } else {
            this.reader = null;
        }
    }

    /**
     *
     * @return body-json as {@link Data}. An Empty {@link Data} will be returned,
     * if there is {@link #noContent() no content}. This {@link Data} will be the exact same for subsequent calls.
     * @throws IOException from {@link PushbackReader}
     * @throws ParseException from {@link JsonParser}
     */
    public @NotNull Data getData() throws IOException, ParseException {
        return getData(null);
    }

    /**
     *
     * @param arrayKey to parse pure JSON array
     * @return body-json as {@link Data}. An Empty {@link Data} will be returned,
     * if there is {@link #noContent() no content}. This {@link Data} will be the exact same for subsequent calls.
     * @throws IOException from {@link PushbackReader}
     * @throws ParseException from {@link JsonParser}
     */
    public @NotNull Data getData(@Nullable String arrayKey) throws IOException, ParseException {
        if(data != null) return data;
        if(reader == null) throw new UnsupportedOperationException("No JSON body!");
        int c = reader.read();
        reader.unread(c);
        if (c == -1){
            noContent = true;
            data = new Data(1);
            return data;
        }

        JsonParser jsonParser = new JsonParser();
        data = jsonParser.readDataFromReader(reader, arrayKey != null, arrayKey);
        return data;
    }

    /**
     *
     * @return {@code true} if the response body starts with a '['
     * @throws IOException from {@link PushbackReader}
     */
    public boolean isArray() throws IOException {
        if (isArray != null) return isArray;
        if(reader == null) throw new UnsupportedOperationException("No JSON body!");

        int c;
        while ((c = reader.read()) != -1) {
            if (c != ' ' && c > '\u001F') {
                break;
            }
        }

        if (c == -1) {
            isArray = false;
            noContent = true;
            data = new Data(1);
            return false;
        }

        reader.unread(c);
        isArray = ((char) c) == '[';
        return isArray;
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
     * @return {@link HttpResponseCode}
     */
    public @NotNull HttpResponseCode getResponseCode() {
        return responseCode;
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
}
