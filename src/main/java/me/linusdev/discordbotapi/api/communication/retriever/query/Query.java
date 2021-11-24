package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.objects.HasLApi;

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
