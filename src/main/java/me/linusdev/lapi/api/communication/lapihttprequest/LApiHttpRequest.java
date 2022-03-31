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

package me.linusdev.lapi.api.communication.lapihttprequest;

import me.linusdev.data.Data;
import me.linusdev.data.Entry;
import me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.lapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.lapi.api.communication.lapihttprequest.body.LApiHttpMultiPartBodyPublisher;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Flow;

public class LApiHttpRequest {

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final String uri;
    private final Method method;
    private final LApiHttpBody body;
    private final Data queryStrings;
    private final ArrayList<LApiHttpHeader> headers;
    private Duration timeout;

    /**
     *
     * @param uri internet location
     * @param method any {@link Method}
     * @param body of the request
     * @throws IllegalRequestMethodException
     */
    public LApiHttpRequest(@NotNull String uri, @NotNull Method method, @Nullable LApiHttpBody body, @Nullable Data queryStrings) throws IllegalRequestMethodException {
        this.uri = uri;
        this.method = method;
        this.body = body;
        this.queryStrings = queryStrings;

        if(!(method == Method.GET || method == Method.DELETE) && body == null)
            throw new IllegalRequestMethodException("LApiHttpRequest's data may not be null with a request method other than GET or DELETE");

        this.headers = new ArrayList<>();
        this.timeout = DiscordApiCommunicationHelper.DEFAULT_TIMEOUT_DURATION;
    }

    /**
     *
     * @param uri internet location
     * @param method {@link Method#GET} or {@link Method#DELETE}
     * @throws IllegalRequestMethodException
     */
    public LApiHttpRequest(@NotNull String uri, @NotNull Method method) throws IllegalRequestMethodException {
        this(uri, method, null, null);
    }


    public LApiHttpRequest header(@NotNull String name, @NotNull String value){
        this.headers.add(new LApiHttpHeader(name, value));
        return this;
    }

    public LApiHttpRequest header(@NotNull LApiHttpHeader header){
        this.headers.add(header);
        return this;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public @NotNull String getFullUri(){
        StringBuilder uri = new StringBuilder(this.uri);
        //Add query string params

        if(queryStrings != null) {
            //TODO some characters might have to be escaped
            boolean first = true;
            for (Entry entry : queryStrings) {
                if(first){
                    uri.append("?");
                    first = false;
                }else{
                    uri.append("&");
                }
                uri.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }
        }

        return uri.toString();
    }

    /**
     * builds a {@link HttpRequest}
     * @return built {@link HttpRequest}
     * @throws IllegalRequestMethodException if {@link #body} is {@code null} and {@link #method} is not {@link Method#GET GET} or {@link Method#DELETE DELETE}
     */
    public HttpRequest getHttpRequest() throws IllegalRequestMethodException, IOException {
        LogInstance log = Logger.getLogger(this.getClass().getSimpleName(), Logger.Type.INFO);
        String uri = getFullUri();

        log.info("Creating HttpRequest: " + method + " " + uri);

        HttpRequest.Builder builder =  HttpRequest.newBuilder(URI.create(uri));

        //GET requests do never have a body!
        if(body != null && method != Method.GET){
            if(body.isMultiPart()) {
                LApiHttpMultiPartBodyPublisher publisher = new LApiHttpMultiPartBodyPublisher(body);
                builder.header(CONTENT_TYPE_HEADER, "multipart/form-data; boundary=" + publisher.getBoundaryString());
                builder.method(method.getMethod(), publisher);
            }else if(body.hasJsonPart()){
                builder.header(CONTENT_TYPE_HEADER, "application/json");
                builder.method(method.getMethod(), HttpRequest.BodyPublishers.ofString(body.getJsonPart().getJsonString().toString()));
            }else{
                //TODO add the support of single file bodies
                throw new UnsupportedOperationException();
            }
        }else{
            if(method == Method.GET)
                builder.GET();
            else if(method == Method.DELETE)
                builder.DELETE();
            else
                throw new IllegalRequestMethodException("body may not be null for request method " + method.toString());
        }

        for(LApiHttpHeader header : headers)
            builder.header(header.getName(), header.getValue());

        builder.timeout(timeout);

        if(Logger.DEBUG_LOG){
            HttpRequest request = builder.build();
            HttpHeaders headers =  request.headers();
            StringBuilder string = new StringBuilder();
            for( Map.Entry<String, List<String>> header :headers.map().entrySet()){
                for(String s : header.getValue()){
                    string.append(header.getKey())
                            .append(": ")
                            .append(s)
                            .append("\n");
                }
            }

            if(string.length() == 0) string.append("None\n"); // if we have no headers, add "None\n"

            //remove last \n
            log.debugData(string.substring(0, string.length()-1), "Headers");
            StringBuilder string2 = new StringBuilder();

            Optional<HttpRequest.BodyPublisher> body = request.bodyPublisher();
            if(body.isEmpty()){
                string2.append("empty body");
            }else{
                body.get().subscribe(new Flow.Subscriber<ByteBuffer>() {
                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(ByteBuffer item) {
                        string2.append(new String(item.array()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        string2.append("Error: ")
                                .append(throwable.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            log.debugData(string2.toString(), "Body");

            return request;
        }

        return builder.build();
    }

    /**
     *
     * @return String in the following format: "GET https://www.example.com?a=b"
     */
    public String toSimpleString() {
        return method.toString() + " " + getFullUri();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            HttpRequest request = getHttpRequest();

            stringBuilder.append(request.method())
                    .append(" ")
                    .append(request.uri())
                    .append("\n");

            HttpHeaders headers =  request.headers();
            for( Map.Entry<String, List<String>> header :headers.map().entrySet()){
                for(String s : header.getValue()){
                    stringBuilder.append(header.getKey())
                            .append(": ")
                            .append(s)
                            .append("\n");
                }
            }

            Optional<HttpRequest.BodyPublisher> body = request.bodyPublisher();
            if(body.isEmpty()){
                stringBuilder.append("NO BODY");
            }else{
                body.get().subscribe(new Flow.Subscriber<ByteBuffer>() {
                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        stringBuilder.append("START OF BODY");
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(ByteBuffer item) {
                        stringBuilder.append(new String(item.array()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        stringBuilder.append("Error: ")
                                .append(throwable.toString());
                    }

                    @Override
                    public void onComplete() {
                        stringBuilder.append("END OF BODY");
                    }
                });
            }

        } catch (Exception e) {
            stringBuilder.append("Exception: ").append(e.getMessage());
        }

        return stringBuilder.toString();
    }
}
