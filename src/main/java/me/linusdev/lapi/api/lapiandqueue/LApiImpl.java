/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.lapiandqueue;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.VoiceRegionManager;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.communication.exceptions.NoInternetException;
import me.linusdev.lapi.api.communication.gateway.other.GetGatewayResponse;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.communication.lapihttprequest.IllegalRequestMethodException;
import me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.lapi.api.communication.retriever.*;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.communication.retriever.response.body.ListThreadsResponseBody;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponse;
import me.linusdev.lapi.api.objects.invite.Invite;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.other.Error;
import me.linusdev.lapi.api.request.RequestFactory;
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
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper.*;

/**
 * See {@link LApi}
 */
public class LApiImpl implements LApi {

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

    //Config
    private @NotNull final String token;
    private @NotNull final Config config;

    private final boolean cacheVoiceRegions;
    private final boolean cacheRoles;
    private final boolean copyOldRolesOnUpdateEvent;
    private final boolean cacheGuilds;
    private final boolean copyOldGuildOnUpdateEvent;
    private final boolean cacheEmojis;
    private final boolean copyOldEmojisOnUpdateEvent;

    //Http
    private final LApiHttpHeader authorizationHeader;
    private final LApiHttpHeader userAgentHeader = new LApiHttpHeader(ATTRIBUTE_USER_AGENT_NAME,
            ATTRIBUTE_USER_AGENT_VALUE.replace(PlaceHolder.LAPI_URL, LAPI_URL).replace(PlaceHolder.LAPI_VERSION, LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();
    private final @NotNull RequestFactory requestFactory;

    //Queue
    private final Queue<Future<?>> queue;
    private final ScheduledExecutorService scheduledExecutor;
    private final ExecutorService queueWorker;
    private volatile QueueThread queueThread;
    private long notConnectedWaitMillis = NOT_CONNECTED_WAIT_MILLIS_STANDARD;

    //Gateway
    final EventTransmitter eventTransmitter;
    final GatewayWebSocket gateway;

    //executor
    private final ThreadPoolExecutor executor;

    //stores and manages the voice regions
    private final @NotNull VoiceRegionManager voiceRegionManager;

    //guild manager
    /**
     * {@link GuildManager} that manages cached guilds. will be {@code null}
     * if {@link ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is not set.
     */
    private final @Nullable GuildManager guildManager;

    //Logger
    private final LogInstance log = Logger.getLogger(LApi.class.getSimpleName(), Logger.Type.INFO);

    public LApiImpl(@NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        //Config
        this.token = config.getToken();
        this.config = config;

        this.cacheVoiceRegions = config.isFlagSet(ConfigFlag.CACHE_VOICE_REGIONS);
        this.cacheRoles = config.isFlagSet(ConfigFlag.CACHE_ROLES);
        this.copyOldRolesOnUpdateEvent = config.isFlagSet(ConfigFlag.COPY_ROLE_ON_UPDATE_EVENT);
        this.cacheGuilds = config.isFlagSet(ConfigFlag.CACHE_GUILDS);
        this.copyOldGuildOnUpdateEvent = config.isFlagSet(ConfigFlag.COPY_GUILD_ON_UPDATE_EVENT);
        this.cacheEmojis = config.isFlagSet(ConfigFlag.CACHE_EMOJIS);
        this.copyOldEmojisOnUpdateEvent = config.isFlagSet(ConfigFlag.COPY_EMOJI_ON_UPDATE_EVENT);

        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(PlaceHolder.TOKEN, this.token));
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        //Queue
        this.queue = config.getNewQueue();
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.queueWorker = Executors.newSingleThreadExecutor(new QueueThreadFactory());
        this.queueWorker.submit(new Runnable() {
            @Override
            @SuppressWarnings({"InfiniteLoopStatement"})
            public void run() {
                queueThread = (QueueThread) java.lang.Thread.currentThread();
                log.log("Started queue thread: " + queueThread.getName());
                try {
                    while (true) {
                        synchronized (queue) {
                            if (queue.peek() == null) {
                                try {
                                    queueThread.wait(queue, 10000L);
                                } catch (InterruptedException ignored) {
                                    log.error("Queue thread interrupted");
                                    log.error(ignored);
                                }
                            }

                            if (queue.peek() == null) continue;

                            if(Logger.DEBUG_LOG){
                                log.debug("queue.poll().completeHereAndIgnoreQueueThread()");
                                long millis = System.currentTimeMillis();
                                //noinspection ConstantConditions
                                queue.poll().completeHereAndIgnoreQueueThread();
                                long finishMillis = System.currentTimeMillis() - millis;
                                log.debug("queue.poll().completeHereAndIgnoreQueueThread() finished in " + finishMillis + " milliseconds");
                            }else{
                                queue.poll().completeHereAndIgnoreQueueThread();
                            }

                        }
                    }
                }catch (Throwable t){
                    //This is so any exceptions in this Thread are caught and printed.
                    //Otherwise, they would just vanish and no one would know what happened
                    log.error(t);
                }
            }
        });

        requestFactory = new RequestFactory(this);

        //Gateway
        if(this.config.isFlagSet(ConfigFlag.ENABLE_GATEWAY)){
            this.eventTransmitter = new EventTransmitter(this);
            this.gateway = new GatewayWebSocket(this, eventTransmitter, config);
            gateway.start();
        }else{
            this.eventTransmitter = null;
            this.gateway = null;
        }

        //TODO: Probably move these before the Gateway! since Gateway uses the GuildManager
        //VoiceRegions
        this.voiceRegionManager = new VoiceRegionManager(this);
        if(this.config.isFlagSet(ConfigFlag.CACHE_VOICE_REGIONS))
            this.voiceRegionManager.init();

        //GuildImpl Manager
        if(isCacheGuildsEnabled()){
            this.guildManager = config.getGuildManagerFactory().newInstance(this);
            this.guildManager.init(16);
        }else{
            this.guildManager = null;
        }
    }

    @Override
    @ApiStatus.Internal
    public  <T> @NotNull Future<T> queue(@NotNull Queueable<T> queueable, @Nullable BiConsumer<T, Error> then, @Nullable Consumer<T> thenSingle, @Nullable Consumer<Future<T>> beforeComplete){
        final Future<T> future = new Future<T>(this, queueable);
        if(beforeComplete != null) future.beforeComplete(beforeComplete);
        if(then != null) future.then(then);
        if(thenSingle != null) future.then(thenSingle);
        this.queue(future);
        return future;
    }

    @Override
    @ApiStatus.Internal
    public <T> @NotNull Future<T> queueAfter(@NotNull Queueable<T> queueable, long delay, TimeUnit timeUnit){
        final Future<T> future = new Future<T>(this, queueable);
        scheduledExecutor.schedule(() -> {
            try {
                this.queue(future);
            }catch (Throwable t){
                //This is so any exceptions in this Thread are caught and printed.
                //Otherwise, they would just vanish and no one would know what happened
                log.error(t);
            }
        }, delay, timeUnit);
        return future;
    }

    @ApiStatus.Internal
    void queue(@NotNull final Future<?> future){
        queue.offer(future);
        if(queueThread.isWaiting()){
            //If the queue is waiting, notify it.
            //If it is not waiting, it is currently working on something and will
            //automatically move to the next entry ones it is free again
            synchronized (queue){
                queue.notifyAll();
            }
        }
    }

    public LApiHttpResponse getResponse(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, NoInternetException, ParseException, InterruptedException {
        LApiHttpResponse response = retrieveResponse(request);

        return response;
    }

    private LApiHttpResponse retrieveResponse(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, InterruptedException, NoInternetException, ParseException {
        Throwable throwThrough = null;

        try {
            HttpRequest builtRequest = request.getHttpRequest();

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
                        throwThrough = new NoInternetException();
                        throw (NoInternetException) throwThrough;
                    }

                } else if (cause instanceof UnresolvedAddressException) {
                    throwThrough = exception;
                    throw exception;
                }


                log.error(exception);
                throwThrough = exception;
                throw exception;
            } catch (Throwable e) {

                log.error(e);
                throwThrough = e;
                throw e;
            }
        }catch (Throwable tt) {
            if(tt == throwThrough) throw tt; //ignore this one
            log.error("Error while building request");
            log.error(tt);
            throw tt;
        }
    }

    @ApiStatus.Internal
    private LApiHttpResponse sendRequest(@NotNull LApiHttpRequest request, @NotNull HttpRequest built) throws IOException, InterruptedException, ParseException {
        LApiHttpResponse response = new LApiHttpResponse(client.send(built, HttpResponse.BodyHandlers.ofInputStream()));
        notConnectedWaitMillis = NOT_CONNECTED_WAIT_MILLIS_STANDARD;
        if(Logger.DEBUG_LOG) log.debug("Request: " + request.toSimpleString()
                + " returned with code " + response.getResponseCode() + " (" + response.getResponseCodeAsInt() + ")");
        //TODO maybe log response body too.
        return response;
    }

    public LApiHttpRequest appendHeader(@NotNull LApiHttpRequest request){
        request.header(getAuthorizationHeader());
        request.header(getUserAgentHeader());
        return request;
    }


    public void checkQueueThread() throws LApiRuntimeException {
        if(java.lang.Thread.currentThread().equals(queueThread)){
            throw new LApiRuntimeException("You cannot invoke Future.get() or Queueable.completeHere() on the queue-thread" +
                    ", because this could lead to an infinite wait loop. You should also not wait or sleep the " +
                    "queue-thread, because this will delay all other queued Futures");
        }
    }

    public @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable String afterUserId, @Nullable Integer limit){
        SOData queryStringsData = null;

        if(afterUserId != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            if (afterUserId != null) queryStringsData.add(RequestFactory.AFTER_KEY, afterUserId);
            if (limit != null) queryStringsData.add(RequestFactory.LIMIT_KEY, limit);
        }

        String emojiString;
        if(emoji.isStandardEmoji()){
            emojiString = emoji.getName();
        }else{
            emojiString = emoji.getName() + ":" + emoji.getId();
        }

        LinkQuery query = new LinkQuery(this, Link.GET_REACTIONS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId),
                new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId),
                new PlaceHolder(PlaceHolder.EMOJI, emojiString));
        return new ArrayRetriever<>(this, query, User::fromData);
    }

    public @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable Integer limit){
        return getReactionsRetriever(channelId, messageId, emoji, null, limit);
    }

    public @NotNull Queueable<ArrayList<Invite>> getChannelInvitesRetriever(@NotNull String channelId){
        LinkQuery query = new LinkQuery(this, Link.GET_CHANNEL_INVITES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<>(this, query, Invite::fromData);
    }

    public @NotNull Queueable<ArrayList<MessageImplementation>> getPinnedMessagesRetriever(@NotNull String channelId){
        LinkQuery query = new LinkQuery(this, Link.GET_PINNED_MESSAGES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<>(this, query, MessageImplementation::new);
    }

    public @NotNull Queueable<ThreadMember> getThreadMemberRetriever(@NotNull String channelId, @NotNull String userId){
        LinkQuery query = new LinkQuery(this, Link.GET_THREAD_MEMBER,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId), new PlaceHolder(PlaceHolder.USER_ID, userId));
        return new ConvertingRetriever<>(query, (lApi, data) -> ThreadMember.fromData(data));
    }

    public @NotNull Queueable<ArrayList<ThreadMember>> getThreadMembersRetriever(@NotNull String channelId){
        LinkQuery query = new LinkQuery(this, Link.LIST_THREAD_MEMBERS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<SOData, ThreadMember>(this, query, (lApi, data) -> ThreadMember.fromData(data));
    }

    @SuppressWarnings("removal")
    @Deprecated(since = "api v10", forRemoval = true)
    public @NotNull Queueable<ListThreadsResponseBody> getActiveThreadsRetriever(@NotNull String channelId){
        LinkQuery query = new LinkQuery(this, Link.LIST_ACTIVE_THREADS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){

        SOData queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            queryStringsData.addIfNotNull(RequestFactory.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(RequestFactory.LIMIT_KEY, limit);
        }

        LinkQuery query = new LinkQuery(this, Link.LIST_PUBLIC_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId) {
        return getPublicArchivedThreadsRetriever(channelId, null, null);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){
        SOData queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            queryStringsData.addIfNotNull(RequestFactory.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(RequestFactory.LIMIT_KEY, limit);
        }

        LinkQuery query = new LinkQuery(this, Link.LIST_PRIVATE_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId){
        return getPrivateArchivedThreadsRetriever(channelId, null, null);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getJoinedPrivateArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){
        SOData queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            queryStringsData.addIfNotNull(RequestFactory.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(RequestFactory.LIMIT_KEY, limit);
        }

        LinkQuery query = new LinkQuery(this, Link.LIST_JOINED_PRIVATE_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getJoinedPrivateArchivedThreadsRetriever(@NotNull String channelId) {
        return getJoinedPrivateArchivedThreadsRetriever(channelId, null, null);
    }

    /**
     * <p>
     *     Returns the {@link me.linusdev.lapi.api.objects.user.User user object} of the requester's account. For OAuth2, this requires the identify scope,
     *     which will return the object without an email, and optionally the email scope,
     *     which returns the object with an email.
     * </p>
     *
     * @return {@link Queueable} to retrieve the current {@link User user} (your bot)
     * @see Link#GET_CURRENT_USER
     */
    public @NotNull Queueable<User> getCurrentUserRetriever(){
        LinkQuery query = new LinkQuery(this, Link.GET_CURRENT_USER);
        return new ConvertingRetriever<>(query, User::fromData);
    }

    public @NotNull Queueable<User> getUserRetriever(@NotNull String userId){
        LinkQuery query = new LinkQuery(this, Link.GET_USER,
                new PlaceHolder(PlaceHolder.USER_ID, userId));
        return new ConvertingRetriever<>(query, User::fromData);
    }



    @Override
    public @NotNull Queueable<LApiHttpResponse> createInteractionResponse(@NotNull String interactionId,
                                                                   @NotNull String interactionToken,
                                                                   @NotNull InteractionResponse response) {
        Query query = new LinkQuery(this, Link.CREATE_INTERACTION_RESPONSE, response.getBody(),
                new PlaceHolder(PlaceHolder.INTERACTION_ID, interactionId),
                new PlaceHolder(PlaceHolder.INTERACTION_TOKEN, interactionToken));

        return new NoContentRetriever(this, query);
    }

    //Gateway

    public @NotNull Queueable<GetGatewayResponse> getGatewayBot(){
        Query query = new LinkQuery(this, Link.GET_GATEWAY_BOT);
        return new ConvertingRetriever<>(query, (lApi, data) -> GetGatewayResponse.fromData(data));
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
     * @return {@link SelfUserPresenceUpdater}
     */
    @Override
    public SelfUserPresenceUpdater getSelfPresenceUpdater(){
        return gateway.getSelfUserPresenceUpdater();
    }

    @Override
    public @NotNull VoiceRegionManager getVoiceRegionManager() {
        return voiceRegionManager;
    }

    @Override
    public @Nullable GatewayWebSocket getGateway() {
        return gateway;
    }

    @Override
    public @NotNull LApi getLApi() {
        return this;
    }

    @Override
    public boolean isCacheVoiceRegionsEnabled() {
        return cacheVoiceRegions;
    }

    @Override
    public boolean isCacheRolesEnabled() {
        return cacheRoles;
    }

    @Override
    public boolean isCopyOldRolesOnUpdateEventEnabled() {
        return copyOldRolesOnUpdateEvent;
    }

    @Override
    public boolean isCacheGuildsEnabled(){
        return cacheGuilds;
    }

    @Override
    public boolean isCopyOldGuildOnUpdateEventEnabled(){
        return copyOldGuildOnUpdateEvent;
    }

    @Override
    public boolean isCacheEmojisEnabled() {
        return cacheEmojis;
    }

    @Override
    public boolean isCopyOldEmojiOnUpdateEventEnabled() {
        return copyOldEmojisOnUpdateEvent;
    }

    @Override
    public boolean isCacheStickersEnabled(){
        return config.isFlagSet(ConfigFlag.CACHE_STICKERS);
    }

    @Override
    public boolean isCopyOldStickerOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_STICKER_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCacheVoiceStatesEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_VOICE_STATES);
    }

    @Override
    public boolean isCopyOldVoiceStateOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_VOICE_STATE_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCacheMembersEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_MEMBERS);
    }

    @Override
    public boolean isCopyOldMemberOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_MEMBER_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCacheChannelsEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_CHANNELS);
    }

    @Override
    public boolean isCopyOldChannelOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_CHANNEL_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCacheThreadsEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_THREADS);
    }

    @Override
    public boolean isDoNotRemoveArchivedThreadsEnabled() {
        return config.isFlagSet(ConfigFlag.DO_NOT_REMOVE_ARCHIVED_THREADS);
    }

    @Override
    public boolean isCopyOldThreadOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_THREAD_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCachePresencesEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_PRESENCES);
    }

    @Override
    public boolean isCopyOldPresenceOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_PRESENCE_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCacheStageInstancesEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_STAGE_INSTANCES);
    }

    @Override
    public boolean isCopyOldStageInstanceOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_STAGE_INSTANCE_ON_UPDATE_EVENT);
    }

    @Override
    public boolean isCacheGuildScheduledEventsEnabled() {
        return config.isFlagSet(ConfigFlag.CACHE_GUILD_SCHEDULED_EVENTS);
    }

    @Override
    public boolean isCopyOldGuildScheduledEventOnUpdateEventEnabled() {
        return config.isFlagSet(ConfigFlag.COPY_GUILD_SCHEDULED_EVENTS_ON_UPDATE);
    }
//api-internal getter

    /**
     * @see #guildManager
     */
    @ApiStatus.Internal
    public @Nullable GuildManager getGuildManager() {
        return guildManager;
    }

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

