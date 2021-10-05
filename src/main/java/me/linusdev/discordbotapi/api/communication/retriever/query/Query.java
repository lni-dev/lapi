package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;

public interface Query {
    Method getMethod();
    LApiHttpRequest getLApiRequest() throws LApiException;
}
