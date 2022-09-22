/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.lapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.cache.Cache;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.event.ReadyEventAwaiter;
import me.linusdev.lapi.api.manager.command.CommandManager;
import me.linusdev.lapi.api.manager.command.CommandManagerImpl;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.communication.http.request.IllegalRequestMethodException;
import me.linusdev.lapi.api.communication.http.request.LApiHttpHeader;
import me.linusdev.lapi.api.communication.http.request.LApiHttpRequest;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.thread.LApiThread;
import me.linusdev.lapi.api.thread.LApiThreadFactory;
import me.linusdev.lapi.api.thread.LApiThreadGroup;
import me.linusdev.lapi.api.communication.http.queue.QueueThread;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.UnresolvedAddressException;
import java.util.Queue;
import java.util.concurrent.*;

import static me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper.*;

/**
 * See {@link LApi}
 */
public class LApiImpl implements LApi {


    public static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * The Discord id of the creator of this api UwU
     */
    public static final String CREATOR_ID = "247421526532554752";

    public static final String LAPI_URL = "https://github.com/lni-dev/lapi";
    public static final String LAPI_VERSION = "1.0.3";
    public static final String LAPI_NAME = "LApi";

    public static final ApiVersion DEFAULT_API_VERSION = ApiVersion.V10;

    public static final long NOT_CONNECTED_WAIT_MILLIS_STANDARD = 10_000L;
    public static final long NOT_CONNECTED_WAIT_MILLIS_INCREASE = 30_000L;
    public static final long NOT_CONNECTED_WAIT_MILLIS_MAX = 300_000L;

    //Caller Class
    private final @NotNull Class<?> callerClass;

    //Config
    private @NotNull final String token;
    private @NotNull final Config config;

    //Http
    private final LApiHttpHeader authorizationHeader;
    private final LApiHttpHeader userAgentHeader = new LApiHttpHeader(ATTRIBUTE_USER_AGENT_NAME,
            ATTRIBUTE_USER_AGENT_VALUE.replace(Name.LAPI_URL.toString(), LAPI_URL).replace(Name.LAPI_VERSION.toString(), LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();
    private final @NotNull RequestFactory requestFactory;

    //Threads
    private final @NotNull LApiThreadGroup lApiThreadGroup;

    //Queue
    private final Queue<QueueableFuture<?>> queue;
    private final QueueThread queueThread;
    private long notConnectedWaitMillis = NOT_CONNECTED_WAIT_MILLIS_STANDARD;

    //LApiReadyEventListener
    @NotNull final LApiReadyListener lApiReadyListener;
    @NotNull final ReadyEventAwaiter readyEventAwaiter;

    //Gateway
    @NotNull final EventTransmitter eventTransmitter;
    @Nullable final GatewayWebSocket gateway;

    //Executor
    private final ScheduledExecutorService supervisedRunnableExecutor;

    //Cache
    private final @Nullable Cache cache;

    //Command Manager
    private final @Nullable CommandManagerImpl commandManager;

    //stores and manages the voice regions
    private final @Nullable VoiceRegionManager voiceRegionManager;

    //guild manager
    /**
     * {@link GuildManager} that manages cached guilds. will be {@code null}
     * if {@link ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is not set.
     */
    private final @Nullable GuildManager guildManager;

    //Logger
    private final LogInstance log = Logger.getLogger(LApi.class.getSimpleName(), Logger.Type.INFO);

    public LApiImpl(@NotNull Config config, @NotNull Class<?> callerClass) throws LApiException, IOException, ParseException, InterruptedException {
        //Caller Class
        this.callerClass = callerClass;

        //Config
        this.token = config.getToken();
        this.config = config;

        //Thread
        this.lApiThreadGroup = new LApiThreadGroup(this);

        //http
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(Name.TOKEN.toString(), this.token));

        //Executor
        this.supervisedRunnableExecutor = Executors.newScheduledThreadPool(4, new LApiThreadFactory(this, true, "supervised-runnable-thread"));

        //Queue
        this.queue = config.getNewQueue();
        this.queueThread = new QueueThread(this, lApiThreadGroup, queue);
        this.queueThread.start();

        requestFactory = new RequestFactory(this);
        eventTransmitter = new EventTransmitter(this);
        readyEventAwaiter = new ReadyEventAwaiter(this);
        lApiReadyListener = new LApiReadyListener(this);

        //Basic Cache
        if(config.isFlagSet(ConfigFlag.BASIC_CACHE)){
            cache = new Cache(this);
        } else {
            cache = null;
        }

        //Command Manager
        if(config.isFlagSet(ConfigFlag.COMMAND_MANAGER)) {
            commandManager = new CommandManagerImpl(this, config.getCommandProvider());
        } else  {
            commandManager = null;
        }

        //VoiceRegions

        if(this.config.isFlagSet(ConfigFlag.CACHE_VOICE_REGIONS)){
            this.voiceRegionManager = new VoiceRegionManager(this);
            this.voiceRegionManager.init(0);
        } else {
            this.voiceRegionManager = null;
        }


        //GuildImpl Manager
        if(getConfig().isFlagSet(ConfigFlag.CACHE_GUILDS)){
            this.guildManager = config.getGuildManagerFactory().newInstance(this);
            this.guildManager.init(16);
        }else{
            this.guildManager = null;
        }

        //Gateway
        if(config.isFlagSet(ConfigFlag.ENABLE_GATEWAY)){
            this.gateway = new GatewayWebSocket(this, eventTransmitter, config);
            gateway.start();
        }else{
            this.gateway = null;
        }

        lApiReadyListener.lApiConstructorReady();
    }

    @Override
    @ApiStatus.Internal
    public <T> void queue(@NotNull QueueableFuture<T> future) {
        queue.offer(future);
        //notify the queue in case it is waiting.
        queueThread.notifyAllAwaiting();
    }

    public LApiHttpResponse getResponse(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, ParseException, InterruptedException {
        return retrieveResponse(request);
    }

    private LApiHttpResponse retrieveResponse(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, InterruptedException, ParseException {

        HttpRequest builtRequest;
        try {
            builtRequest = request.getHttpRequest();
        }catch (Throwable tt) {
            log.error("Error while building request");
            log.error(tt);
            throw tt;
        }

        while(true) {
            try {
                return sendRequest(request, builtRequest);
            } catch (ConnectException | HttpTimeoutException exception) {
                Throwable cause = null;
                if (exception instanceof ConnectException) {
                    log.debug("ConnectException while sending request...");
                    cause = exception.getCause();
                    while (cause instanceof ConnectException) cause = cause.getCause();
                }

                if (cause instanceof ClosedChannelException || exception instanceof HttpTimeoutException) {
                    // probably no internet connection
                    try {
                        URL url = new URL(DISCORD_COM);
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        //if we reach this, internet is connected
                        //let's try again
                        try {
                            return sendRequest(request, builtRequest);
                        } catch (Throwable t) {
                            log.error("Cannot send Request even though internet is connected. Uri:" + request.getHttpRequest().uri());
                            throw exception;
                        }
                    } catch (Throwable e) {
                        log.debug("Internet is most likely not connected");
                        java.lang.Thread.sleep(notConnectedWaitMillis);
                        notConnectedWaitMillis = Math.min(NOT_CONNECTED_WAIT_MILLIS_MAX, notConnectedWaitMillis + NOT_CONNECTED_WAIT_MILLIS_INCREASE);
                        //while loop will retry...
                        continue;
                    }

                } else if (cause instanceof UnresolvedAddressException) {
                    throw exception;
                }

                log.error("Unexpected ConnectException or HttpTimeoutException while trying to send a http request.");
                log.error(exception);
                throw exception;
            } catch (Throwable e) {
                log.error("Unexpected exception while trying to send a http request.");
                log.error(e);
                throw e;
            }
        }

    }

    @ApiStatus.Internal
    private LApiHttpResponse sendRequest(@NotNull LApiHttpRequest request, @NotNull HttpRequest built) throws IOException, InterruptedException, ParseException {
        LApiHttpResponse response = new LApiHttpResponse(client.send(built, HttpResponse.BodyHandlers.ofInputStream()));
        notConnectedWaitMillis = NOT_CONNECTED_WAIT_MILLIS_STANDARD;
        if(Logger.DEBUG_LOG) log.debug("Request: " + request.toSimpleString()
                + " returned with code " + response.getResponseCode() + " (" + response.getResponseCodeAsInt() + ")");
        if(Logger.DEBUG_DATA_LOG) log.debugData("Data: " + response.getData());
        return response;
    }

    public LApiHttpRequest appendHeader(@NotNull LApiHttpRequest request){
        request.header(getAuthorizationHeader());
        request.header(getUserAgentHeader());
        return request;
    }


    public void checkThread() throws LApiRuntimeException {
        if(Thread.currentThread() instanceof LApiThread) {
            LApiThread thread = (LApiThread) Thread.currentThread();
            if(!thread.allowBlockingOperations()) {
                throw new LApiRuntimeException("This thread does not allow blocking operations.");
            }
        }
    }

    @Override
    public void waitUntilLApiReadyEvent() throws InterruptedException {
        readyEventAwaiter.getAwaiter(EventIdentifier.LAPI_READY).awaitFirst();
    }

    @Override
    public void runSupervised(@NotNull Runnable runnable, long delay) {

        //Can not be null because this is not the main method.
        @NotNull StackWalker.StackFrame frame = STACK_WALKER.walk(s -> s.skip(1).findFirst().orElse(null));

        supervisedRunnableExecutor.schedule(() -> {
            try {
                runnable.run();
            }catch (Throwable t) {
                LogInstance log = Logger.getLogger("SR (" + frame.getClassName() + ":" + frame.getLineNumber() + ")");
                log.error(t);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    //Getter

    @Override
    public @NotNull ApiVersion getHttpRequestApiVersion() {
        return config.getApiVersion();
    }

    @Override
    public @NotNull RequestFactory getRequestFactory() {
        return requestFactory;
    }

    @Override
    public @NotNull ReadyEventAwaiter getReadyEventAwaiter() {
        return readyEventAwaiter;
    }

    @Override
    public AbstractEventTransmitter getEventTransmitter() {
        return eventTransmitter;
    }

    /**
     * <p>
     *     let's you update the presence of the current self user (your bot).
     * </p>
     * <p>
     *     <b>After you finished adjusting your presence, you will have to call {@link SelfUserPresenceUpdater#updateNow()}!</b>
     * </p>
     * @return {@link SelfUserPresenceUpdater} or {@code null} if {@link ConfigFlag#ENABLE_GATEWAY} is not set.
     */
    @Override
    public SelfUserPresenceUpdater getSelfPresenceUpdater(){
        if(gateway == null) return null;
        return gateway.getSelfUserPresenceUpdater();
    }

    @Override
    public VoiceRegionManager getVoiceRegionManager() {
        return voiceRegionManager;
    }

    @Override
    public GatewayWebSocket getGateway() {
        return gateway;
    }

    @Override
    public Cache getCache() {
        return cache;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public @NotNull LApi getLApi() {
        return this;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      LApi Internal Getter                     *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * @return {@link EventTransmitter} used by this {@link LApiImpl}
     */
    @ApiStatus.Internal
    public @NotNull EventTransmitter transmitEvent() {
        return eventTransmitter;
    }

    /**
     *
     * @return {@link LApiThreadGroup} for threads of lapi.
     */
    @ApiStatus.Internal
    public LApiThreadGroup getLApiThreadGroup() {
        return lApiThreadGroup;
    }

    /**
     * @see #guildManager
     */
    @ApiStatus.Internal
    public @Nullable GuildManager getGuildManager() {
        return guildManager;
    }

    public @NotNull Class<?> getCallerClass() {
        return callerClass;
    }

    @ApiStatus.Internal
    public @NotNull Config getConfig() {
        return config;
    }

    @ApiStatus.Internal
    public HttpClient getClient() {
        return client;
    }

    @ApiStatus.Internal
    public LApiHttpHeader getAuthorizationHeader() {
        return authorizationHeader;
    }

    @ApiStatus.Internal
    public LApiHttpHeader getUserAgentHeader() {
        return userAgentHeader;
    }
}

