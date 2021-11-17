package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;

public class LApi {

    private static final String LAPI_URL = "https://";
    private static final String LAPI_VERSION = "1.0";

    private final String token;
    private final Config config;

    private final LApiHttpHeader authorizationHeader;
    private final LApiHttpHeader userAgentHeader = new LApiHttpHeader(ATTRIBUTE_USER_AGENT_NAME,
            ATTRIBUTE_USER_AGENT_VALUE.replace(PlaceHolder.LAPI_URL, LAPI_URL).replace(PlaceHolder.LAPI_VERSION, LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();

    //stores and manages the voice regions
    private final VoiceRegions voiceRegions;

    public LApi(@NotNull String token, @NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        this.token = token;
        this.config = config;
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(PlaceHolder.TOKEN, token));


        this.voiceRegions = new VoiceRegions();
        if(config.isFlagSet(Config.LOAD_VOICE_REGIONS_ON_STARTUP))
            this.voiceRegions.init(this); //Todo add callback
    }

    /**
     * @see #sendLApiHttpRequest(LApiHttpRequest, String)
     *
     * Cannot parse pure Arrays!
     *
     * @param request
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     */
    public Data sendLApiHttpRequest(LApiHttpRequest request) throws IOException, InterruptedException, ParseException {
        return sendLApiHttpRequest(request, null);
    }

    /**
     *
     * Sends a Http-request and returns a Data representing the response body`s json
     *
     * @param request to send
     * @param arrayKey if request json is an array, it will be available in the returned Data by this key
     *                 if this is {@code null}, the json may not be an Array (start with '[')
     * @return the generated Data
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     */
    public Data sendLApiHttpRequest(@NotNull LApiHttpRequest request, @Nullable String arrayKey) throws IOException, InterruptedException, ParseException {
        HttpResponse<String> response = client.send(request.getHttpRequest(), HttpResponse.BodyHandlers.ofString());

        StringReader reader = new StringReader(response.body());
        if(arrayKey != null)
            return new JsonParser().readDataFromReader(reader, true, arrayKey);
        return new JsonParser().readDataFromReader(reader);
    }

    public LApiHttpRequest appendHeader(@NotNull LApiHttpRequest request){
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
