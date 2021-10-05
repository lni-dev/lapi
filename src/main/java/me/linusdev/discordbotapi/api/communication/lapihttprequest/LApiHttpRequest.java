package me.linusdev.discordbotapi.api.communication.lapihttprequest;

import me.linusdev.data.AbstractData;
import me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;

public class LApiHttpRequest {

    private final String uri;
    private final Method method;
    private final AbstractData data;
    private final ArrayList<LApiHttpHeader> headers;
    private Duration timeout;

    /**
     *
     * @param uri internet location
     * @param method any {@link Method}
     * @param data to put if method supports data
     * @throws IllegalRequestMethodException
     */
    public LApiHttpRequest(@NotNull String uri, @NotNull Method method, @Nullable AbstractData data) throws IllegalRequestMethodException {
        this.uri = uri;
        this.method = method;
        this.data = data;

        if(!(method == Method.GET || method == Method.DELETE) && data == null)
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
        this(uri, method, null);
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

    public HttpRequest getHttpRequest() {
        assert data != null;
        HttpRequest.Builder builder =  HttpRequest.newBuilder(URI.create(uri));

        if(method == Method.GET) builder.GET();
        else if(method == Method.DELETE) builder.DELETE();
        else
            builder.method(method.getMethod(), HttpRequest.BodyPublishers.ofString(data.getJsonString().toString()));

        for(LApiHttpHeader header : headers)
            builder.header(header.getName(), header.getValue());

        builder.timeout(timeout);

        return builder.build();
    }
}
