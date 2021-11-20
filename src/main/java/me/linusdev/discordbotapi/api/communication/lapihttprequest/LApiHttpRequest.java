package me.linusdev.discordbotapi.api.communication.lapihttprequest;

import me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpMultiPartBodyPublisher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

public class LApiHttpRequest {

    private final String uri;
    private final Method method;
    private final LApiHttpBody body;
    private final ArrayList<LApiHttpHeader> headers;
    private Duration timeout;

    /**
     *
     * @param uri internet location
     * @param method any {@link Method}
     * @param body of the request
     * @throws IllegalRequestMethodException
     */
    public LApiHttpRequest(@NotNull String uri, @NotNull Method method, @Nullable LApiHttpBody body) throws IllegalRequestMethodException {
        this.uri = uri;
        this.method = method;
        this.body = body;

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

    /**
     * builds a {@link HttpRequest}
     * @return built {@link HttpRequest}
     * @throws IllegalRequestMethodException if {@link #body} is {@code null} and {@link #method} is not {@link Method#GET GET} or {@link Method#DELETE DELETE}
     */
    public HttpRequest getHttpRequest() throws IllegalRequestMethodException {
        HttpRequest.Builder builder =  HttpRequest.newBuilder(URI.create(uri));

        if(body != null){
            if(body.isMultiPart()) {
                LApiHttpMultiPartBodyPublisher publisher = new LApiHttpMultiPartBodyPublisher(body);
                builder.header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundaryString());
                builder.method(method.getMethod(), publisher);
            }else if(body.hasJsonPart()){
                builder.header("Content-Type", "application/json");
                builder.method(method.getMethod(), HttpRequest.BodyPublishers.ofString(Objects.requireNonNull(body.getJsonPart()).getJsonString().toString()));
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

        return builder.build();
    }
}
