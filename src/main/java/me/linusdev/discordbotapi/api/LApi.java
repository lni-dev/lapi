package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;

public class LApi {

    private static final String LAPI_URL = "https://";
    private static final String LAPI_VERSION = "1.0";

    private final String token;

    private final LApiHttpHeader authorizationHeader;
    private final LApiHttpHeader userAgentHeader = new LApiHttpHeader(ATTRIBUTE_USER_AGENT_NAME,
            ATTRIBUTE_USER_AGENT_VALUE.replace(PlaceHolder.LAPI_URL, LAPI_URL).replace(PlaceHolder.LAPI_VERSION, LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();

    public LApi(@NotNull String token){
        this.token = token;
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(PlaceHolder.TOKEN, token));
    }

    public Data sendLApiHttpRequest(LApiHttpRequest request) throws IOException, InterruptedException, ParseException {
        HttpResponse<String> response = client.send(request.getHttpRequest(), HttpResponse.BodyHandlers.ofString());

        StringReader reader = new StringReader(response.body());
        return new JsonParser().readDataFromReader(reader);
    }

    public LApiHttpRequest appendHeader(LApiHttpRequest request){
        request.header(getAuthorizationHeader());
        request.header(getUserAgentHeader());
        return request;
    }

    public LApiHttpHeader getAuthorizationHeader() {
        return authorizationHeader;
    }

    public LApiHttpHeader getUserAgentHeader() {
        return userAgentHeader;
    }

    public HttpClient getClient() {
        return client;
    }
}
