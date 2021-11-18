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
import java.util.concurrent.*;

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

    //Queue
    private final Queue<Future<?>> queue;
    private final ScheduledExecutorService scheduledExecutor;
    private final ExecutorService queueWorker;

    private final ThreadPoolExecutor executor;

    //stores and manages the voice regions
    private final VoiceRegions voiceRegions;

    public LApi(@NotNull String token, @NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        this.token = token;
        this.config = config;
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(PlaceHolder.TOKEN, this.token));

        //Queue
        this.queue = config.getQueue();
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.queueWorker = Executors.newSingleThreadExecutor();
        this.queueWorker.submit(new Runnable() {
            @Override
            @SuppressWarnings({"InfiniteLoopStatement"})
            public void run() {
                while(true){
                    synchronized (queue){
                        if(queue.peek() == null) {
                            try {
                                queue.wait(10000L);
                            } catch (InterruptedException ignored) {
                                ignored.printStackTrace();
                            }
                        }

                        if(queue.peek() == null) continue;

                        queue.poll().completeHere();
                    }
                }
            }
        });

        this.voiceRegions = new VoiceRegions();
        if(this.config.isFlagSet(Config.LOAD_VOICE_REGIONS_ON_STARTUP))
            this.voiceRegions.init(this); //Todo add callback


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
        final Future<T> future = new Future<T>(queueable);
        this.queue(future);
        return future;
    }

    /**
     * Queues given {@link Queueable} after a given amount of time. see {@link #queue(Queueable)}
     *
     * @param queueable {@link Queueable}
     * @param delay the delay to wait before queueing
     * @param timeUnit the {@link TimeUnit} for the delay
     * @param <T> Return Type of {@link Queueable}
     * @return {@link Future<T>}
     * @see #queue(Queueable)
     */
    public <T> @NotNull Future<T> queueAfter(@NotNull Queueable<T> queueable, long delay, TimeUnit timeUnit){
        final Future<T> future = new Future<T>(queueable);
        scheduledExecutor.schedule(() -> this.queue(future), delay, timeUnit);
        return future;
    }

    /**
     * Queues given {@link Future} and notifies the {@link #queueWorker} Thread
     */
    private void queue(@NotNull final Future<?> future){
        queue.offer(future);
        synchronized (queue){
            queue.notifyAll();
        }
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

    /**
     * Appends the required headers to the {@link LApiHttpRequest}.<br>
     * These headers are required for Discord to accept the request
     * @param request the {@link LApiHttpRequest} to append the headers to
     * @return the {@link LApiHttpRequest} with the appended headers
     */
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
