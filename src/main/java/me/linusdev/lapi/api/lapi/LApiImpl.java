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

import de.linusdev.lutils.llist.LLinkedList;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.async.ComputationResult;
import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.Nothing;
import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.cache.Cache;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.communication.http.queue.QueueThread;
import me.linusdev.lapi.api.communication.http.request.IllegalRequestMethodException;
import me.linusdev.lapi.api.communication.http.request.LApiHttpHeader;
import me.linusdev.lapi.api.communication.http.request.LApiHttpRequest;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.event.ReadyEventAwaiter;
import me.linusdev.lapi.api.exceptions.LApiException;
import me.linusdev.lapi.api.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.lapi.shutdown.ShutdownExecutor;
import me.linusdev.lapi.api.lapi.shutdown.ShutdownOption;
import me.linusdev.lapi.api.lapi.shutdown.Shutdownable;
import me.linusdev.lapi.api.manager.command.CommandManager;
import me.linusdev.lapi.api.manager.command.CommandManagerImpl;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.guild.GuildPool;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.thread.LApiThread;
import me.linusdev.lapi.api.thread.LApiThreadFactory;
import me.linusdev.lapi.api.thread.LApiThreadGroup;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper.*;

/**
 * See {@link LApi}
 */
public class LApiImpl implements LApi {


    public static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static final long DEFAULT_GLOBAL_HTTP_RATE_LIMIT_RETRY_LIMIT = 40;
    public static final long DEFAULT_HTTP_RATE_LIMIT_ASSUMED_BUCKET_LIMIT = 3;
    public static final int DEFAULT_BUCKETS_CHECK_AMOUNT = 50;
    public static final long DEFAULT_ASSUMED_BUCKET_MAX_LIFE_TIME = 60L * 1000L; // 1 minute
    public static final long DEFAULT_BUCKET_MAX_LAST_USED_TIME = 12L * 60L * 60L * 1000L; // 12 hours
    public static final long DEFAULT_MIN_TIME_BETWEEN_CHECKS = 60L * 1000L;
    public static final int DEFAULT_BUCKET_QUEUE_CHECK_SIZE = 20;

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
            ATTRIBUTE_USER_AGENT_VALUE.replace(Name.LAPI_URL.toString(), LApi.LAPI_URL).replace(Name.LAPI_VERSION.toString(), LApi.LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();
    private final @NotNull RequestFactory requestFactory;

    //Threads
    private final @NotNull LApiThreadGroup lApiThreadGroup;

    //Queue
    private final @NotNull QueueThread queueThread;
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

    //shutdownables
    private final @NotNull LLinkedList<Shutdownable> shutdownables;
    private final @NotNull AtomicBoolean shutdownSequenceStarted = new AtomicBoolean(false);
    private final @NotNull AtomicBoolean shutdownNowSequenceStarted = new AtomicBoolean(false);
    private final @NotNull AtomicBoolean isShutdown = new AtomicBoolean(false);

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

        //shutdownables
        this.shutdownables = new LLinkedList<>();
        Runtime.getRuntime().addShutdownHook(new LApiThread(this, lApiThreadGroup, "shutdown-hook") {
            @Override
            public boolean allowBlockingOperations() {
                return true;
            }

            @Override
            public void run() {
                shutdown(List.of());
            }
        });

        //http
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(Name.TOKEN.toString(), this.token));

        //Executor
        this.supervisedRunnableExecutor = Executors.newScheduledThreadPool(4, new LApiThreadFactory(this, true, "supervised-runnable-thread"));

        //Queue
        this.queueThread = new QueueThread(this, lApiThreadGroup, config.getNewQueue());
        this.queueThread.start();
        if(config.isDebugRateLimitBucketsEnabled()) this.queueThread.debug();


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
        if(!queueThread.queueFuture(future)) {
            //This only happens, when the queue is currently shutting down and does not accept
            //any futures anymore. So it can safely be ignored
            log.warning("Queue does not accept any futures. A future was ignored. Queue is likely shutting down.");
            return;
        }
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
                        //noinspection BusyWait: Thread can't work, if no internet connection is available
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

    @Override
    public void runSupervised(@NotNull Runnable runnable) {
        //Can not be null because this is not the main method.
        @NotNull StackWalker.StackFrame frame = STACK_WALKER.walk(s -> s.skip(1).findFirst().orElse(null));

        supervisedRunnableExecutor.execute(() -> {
            try {
                runnable.run();
            }catch (Throwable t) {
                LogInstance log = Logger.getLogger("SR (" + frame.getClassName() + ":" + frame.getLineNumber() + ")");
                log.error(t);
            }
        });
    }

    @Override
    @Blocking
    public void shutdown(@NotNull List<@NotNull ShutdownOption> options) {

        synchronized (shutdownSequenceStarted) {
            if(shutdownSequenceStarted.get()) return;
            shutdownSequenceStarted.set(true);
        }

        final long shutdownStart = System.currentTimeMillis();
        final long shutdownBy = shutdownStart + config.getMaxShutdownTime();
        final LogInstance log = Logger.getLogger("Shutdown Sequence");
        final Executor executor = new ShutdownExecutor(this);

        log.log("Starting shutdown sequence. Max shutdown time: " + config.getMaxShutdownTime() + " milliseconds.");

        long optionBitfield = 0L;

        for(ShutdownOption option : options) {
            if(!option.canBeSet(optionBitfield)) {
                List<ShutdownOption> mutuallyExclusive = new ArrayList<>();
                for(ShutdownOption o : options) {
                    if((o.id() & option.mutuallyExclusiveWith()) > 0L) {
                        mutuallyExclusive.add(o);
                    }
                }

                StringBuilder meo = new StringBuilder();
                boolean first = true;
                for(ShutdownOption o : mutuallyExclusive) {
                    if(first) first = false;
                    else meo.append(", ");
                    meo.append(o);
                }

                throw new IllegalArgumentException(option.name() + " cannot be applied with " + meo + ".");
            }
        }

        List<Future<Nothing, Shutdownable>> futures = new ArrayList<>(options.size());

        for(Shutdownable shutdownable : shutdownables) {
            futures.add(shutdownable.shutdown(this, optionBitfield, log, executor, shutdownBy));
        }

        try {
            int i = 0;
            for(Future<Nothing, Shutdownable> future : futures) {
                i++;

                if(future == null) {
                    log.info(shutdownables.get(i-1).getShutdownableName() + " did not return a " +
                            "shutdown future.");
                    continue;
                }

                ComputationResult<Nothing, Shutdownable> result;
                try {
                    result = future.get();
                } catch (CancellationException e) {
                    log.error("Future of the shutdownable "
                            + shutdownables.get(i-1).getShutdownableName() +
                            " was canceled.");
                    continue;
                }


                String name = result.getSecondary().getShutdownableName();

                if(result.getError() != null) {
                    log.error("Error while shutting down " + name + ".");
                    result.getError().log(log);

                } else {
                    log.info(name + " shut down successfully.");

                }

            }
        } catch (InterruptedException e) {
            log.error("Shutdown sequence was interrupted. Calling shutdownNow()...");
            shutdownNow();
        }

        supervisedRunnableExecutor.shutdownNow();
        try {
            long remaining = Shutdownable.calcRemainingShutdownTime(shutdownBy, 50);
            if(remaining > 0) {
                //noinspection ResultOfMethodCallIgnored: checked below
                supervisedRunnableExecutor.awaitTermination(remaining, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException ignored) {}

        if(!supervisedRunnableExecutor.isTerminated())
            log.warning("Executor for supervised runnables could not shutdown successfully.");

        synchronized (isShutdown) {
            isShutdown.set(true);
        }

        log.log("Shutdown finished. Took " + (System.currentTimeMillis() - shutdownStart) + " milliseconds.");

        //finally close the Logger stream
        try {
            Logger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonBlocking
    @Override
    public void shutdownNow() {

        synchronized (shutdownNowSequenceStarted) {
            if(shutdownNowSequenceStarted.get()) return;
            shutdownNowSequenceStarted.set(true);
        }

        final long shutdownStart = System.currentTimeMillis();
        final LogInstance log = Logger.getLogger("Shutdown Sequence");
        final Executor executor = new ShutdownExecutor(this);


        for(Shutdownable shutdownable : shutdownables) {
           shutdownable.shutdownNow(this, log, executor);
        }

        synchronized (isShutdown) {
            isShutdown.set(true);
        }

        log.log("ShutdownNow finished. Took " + (System.currentTimeMillis() - shutdownStart) + " milliseconds.");

        //finally close the Logger stream
        try {
            Logger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerShutdownable(@NotNull Shutdownable shutdownable) {
        this.shutdownables.add(shutdownable);
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
    public @Nullable VoiceRegionManager getVoiceRegionManager() {
        return voiceRegionManager;
    }

    @Override
    public @Nullable GatewayWebSocket getGateway() {
        return gateway;
    }

    @Override
    public @Nullable Cache getCache() {
        return cache;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public GuildPool getGuildPool() {
        return guildManager;
    }

    @Override
    public @NotNull LApi getLApi() {
        return this;
    }

    public @NotNull QueueThread getQueueThread() {
        return queueThread;
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

