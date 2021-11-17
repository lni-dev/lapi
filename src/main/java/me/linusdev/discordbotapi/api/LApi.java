package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.queue.Future;
import me.linusdev.discordbotapi.api.communication.queue.Queueable;
import me.linusdev.discordbotapi.api.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Queue;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;

public class LApi {

    private static final String LAPI_URL = "https://";
    private static final String LAPI_VERSION = "1.0";

    private @NotNull final String token;
    private @NotNull final Config config;

    private final LApiHttpHeader authorizationHeader;
    private final LApiHttpHeader userAgentHeader = new LApiHttpHeader(ATTRIBUTE_USER_AGENT_NAME,
            ATTRIBUTE_USER_AGENT_VALUE.replace(PlaceHolder.LAPI_URL, LAPI_URL).replace(PlaceHolder.LAPI_VERSION, LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();

    private final Queue<Future> queue;

    //stores and manages the voice regions
    private final VoiceRegions voiceRegions;

    public LApi(@NotNull String token, @NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        this.token = token;
        this.config = config;
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(PlaceHolder.TOKEN, token));


        this.voiceRegions = new VoiceRegions();
        if(config.isFlagSet(Config.LOAD_VOICE_REGIONS_ON_STARTUP))
            this.voiceRegions.init(this); //Todo add callback

        this.queue = config.getQueue();
    }

    /**
     *
     * This queues a {@link Queueable} in {@link #queue}.<br>
     * This is used for sending a lot of {@link LApiHttpRequest} after each is other and not at the same time.
     * This will NOT wait the Thread until the {@link Queueable} has been completed.
     * <br><br>
     * If you want to wait your Thread you could use:
     * <ul>
     *     <li>
     *         {@link Future#get()}, to wait until the {@link Queueable} has been gone through the {@link #queue} and {@link Queueable#completeHere() completed}
     *     </li>
     *     <li>
     *          {@link Queueable#completeHere()}, to wait until the {@link Queueable} has been {@link Queueable#completeHere() completed}.
     *          This wont assure, that several {@link LApiHttpRequest LApiHttpRequests} are sent at the same time
     *     </li>
     * </ul>
     *
     *
     * @param queueable {@link Queueable}
     * @param <T> Return Type of {@link Queueable}
     * @return {@link Future<T>}
     */
    public <T> @NotNull Future<T> queue(@NotNull Queueable<T> queueable){
        Future<T> future = new Future<T>(queueable);
        queue.offer(future);
        return future;
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
