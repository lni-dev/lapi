package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.VoiceRegions;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.discordbotapi.api.communication.exceptions.NoInternetException;
import me.linusdev.discordbotapi.api.communication.gateway.other.GetGatewayResponse;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.IllegalRequestMethodException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.ChannelRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.MessageRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.communication.retriever.query.Link;
import me.linusdev.discordbotapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.communication.retriever.response.body.ListThreadsResponseBody;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.config.ConfigFlag;
import me.linusdev.discordbotapi.api.manager.guild.GuildManager;
import me.linusdev.discordbotapi.api.manager.guild.LApiGuildManager;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.discordbotapi.api.objects.permission.Permission;
import me.linusdev.discordbotapi.api.objects.invite.Invite;
import me.linusdev.discordbotapi.api.objects.message.MessageImplementation;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.api.templates.message.AllowedMentions;
import me.linusdev.discordbotapi.api.templates.message.MessageTemplate;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;
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

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;

/**
 * See {@link LApi}
 */
public class LApiImpl implements LApi {

    /**
     * The Discord id of the creator of this api UwU
     */
    public static final String CREATOR_ID = "247421526532554752";

    public static final String LAPI_URL = "https://twitter.com/Linus_Dev";
    public static final String LAPI_VERSION = "1.0";
    public static final String LAPI_NAME = "LApi";

    public static final ApiVersion NEWEST_API_VERSION = ApiVersion.V9;

    public static final long NOT_CONNECTED_WAIT_MILLIS_STANDARD = 10_000L;
    public static final long NOT_CONNECTED_WAIT_MILLIS_INCREASE = 30_000L;
    public static final long NOT_CONNECTED_WAIT_MILLIS_MAX = 300_000L;

    private @NotNull final String token;
    private @NotNull final Config config;

    //Http
    private final LApiHttpHeader authorizationHeader;
    private final LApiHttpHeader userAgentHeader = new LApiHttpHeader(ATTRIBUTE_USER_AGENT_NAME,
            ATTRIBUTE_USER_AGENT_VALUE.replace(PlaceHolder.LAPI_URL, LAPI_URL).replace(PlaceHolder.LAPI_VERSION, LAPI_VERSION));

    private final HttpClient client = HttpClient.newHttpClient();

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
    final VoiceRegions voiceRegions;

    //guild manager
    final GuildManager guildManager;

    //Logger
    private final LogInstance log = Logger.getLogger(LApi.class.getSimpleName(), Logger.Type.INFO);

    public LApiImpl(@NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        this.token = config.getToken();
        this.config = config;
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

        //Gateway
        if(this.config.isFlagSet(ConfigFlag.ENABLE_GATEWAY)){
            this.eventTransmitter = new EventTransmitter(this);
            this.gateway = new GatewayWebSocket(this, eventTransmitter, config);
            gateway.start();
        }else{
            this.eventTransmitter = null;
            this.gateway = null;
        }

        //VoiceRegions
        this.voiceRegions = new VoiceRegions();
        if(this.config.isFlagSet(ConfigFlag.LOAD_VOICE_REGIONS_ON_STARTUP))
            this.voiceRegions.init(this); //Todo add callback

        //Guild Manager
        this.guildManager = new LApiGuildManager(this);
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


    public Data getResponseAsData(LApiHttpRequest request) throws IOException, InterruptedException, ParseException, IllegalRequestMethodException, NoInternetException {
        return getResponseAsData(request, null);
    }

    public Data getResponseAsData(@NotNull LApiHttpRequest request, @Nullable String arrayKey) throws IOException, InterruptedException, ParseException, IllegalRequestMethodException, NoInternetException {
        Reader reader = new InputStreamReader(getResponseAsInputStream(request));

        if(arrayKey != null)
            return new JsonParser().readDataFromReader(reader, true, arrayKey);
        return new JsonParser().readDataFromReader(reader);
    }

    public InputStream getResponseAsInputStream(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, InterruptedException, NoInternetException {
        Throwable throwThrough = null;

        try {
            HttpRequest builtRequest = request.getHttpRequest();

            try {
                return sendRequest(builtRequest);
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
                            return sendRequest(builtRequest);
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
    private InputStream sendRequest(@NotNull HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        notConnectedWaitMillis = NOT_CONNECTED_WAIT_MILLIS_STANDARD;
        return response.body();
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

    public @NotNull Queueable<Channel> getChannelRetriever(@NotNull String channelId){
        return new ChannelRetriever(this, channelId);
    }

    public @NotNull Queueable<MessageImplementation> getChannelMessageRetriever(@NotNull String channelId, @NotNull String messageId){
        return new MessageRetriever(this, channelId, messageId);
    }

    public @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessagesRetriever(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable Integer limit, @Nullable AnchorType anchorType){

        Data queryStringsData = null;

        if(anchorMessageId != null || limit != null){
            queryStringsData = new Data(2);

            if(anchorMessageId != null) {
                if (anchorType == AnchorType.AROUND) queryStringsData.add(GetLinkQuery.AROUND_KEY, anchorMessageId);
                else if (anchorType == AnchorType.BEFORE)
                    queryStringsData.add(GetLinkQuery.BEFORE_KEY, anchorMessageId);
                else if (anchorType == AnchorType.AFTER) queryStringsData.add(GetLinkQuery.AFTER_KEY, anchorMessageId);
            }

            if(limit != null) queryStringsData.add(GetLinkQuery.LIMIT_KEY, limit);
        }


        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_CHANNEL_MESSAGES, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, MessageImplementation>(this, query, MessageImplementation::new);
    }

    public @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessagesRetriever(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable AnchorType anchorType){
        return getChannelMessagesRetriever(channelId, anchorMessageId, null, anchorType);
    }

    public @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessagesRetriever(@NotNull String channelId) {
        return getChannelMessagesRetriever(channelId, null, null, null);
    }

    public @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable String afterUserId, @Nullable Integer limit){
        Data queryStringsData = null;

        if(afterUserId != null || limit != null) {
            queryStringsData = new Data(2);
            if (afterUserId != null) queryStringsData.add(GetLinkQuery.AFTER_KEY, afterUserId);
            if (limit != null) queryStringsData.add(GetLinkQuery.LIMIT_KEY, limit);
        }

        String emojiString;
        if(emoji.isStandardEmoji()){
            emojiString = emoji.getName();
        }else{
            emojiString = emoji.getName() + ":" + emoji.getId();
        }

        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_REACTIONS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId), new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId), new PlaceHolder(PlaceHolder.EMOJI, emojiString));
        return new ArrayRetriever<>(this, query, User::fromData);
    }

    public @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable Integer limit){
        return getReactionsRetriever(channelId, messageId, emoji, null, limit);
    }

    public @NotNull Queueable<ArrayList<Invite>> getChannelInvitesRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_CHANNEL_INVITES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, Invite>(this, query, Invite::fromData);
    }

    public @NotNull Queueable<ArrayList<MessageImplementation>> getPinnedMessagesRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_PINNED_MESSAGES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, MessageImplementation>(this, query, MessageImplementation::new);
    }

    public @NotNull Queueable<ThreadMember> getThreadMemberRetriever(@NotNull String channelId, @NotNull String userId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_THREAD_MEMBER,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId), new PlaceHolder(PlaceHolder.USER_ID, userId));
        return new ConvertingRetriever<ThreadMember>(this, query, (lApi, data) -> ThreadMember.fromData(data));
    }

    public @NotNull Queueable<ArrayList<ThreadMember>> getThreadMembersRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_THREAD_MEMBERS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, ThreadMember>(this, query, (lApi, data) -> ThreadMember.fromData(data));
    }

    @SuppressWarnings("removal")
    @Deprecated(since = "api v10", forRemoval = true)
    public @NotNull Queueable<ListThreadsResponseBody> getActiveThreadsRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_ACTIVE_THREADS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(this, query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){

        Data queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = new Data(2);
            queryStringsData.addIfNotNull(GetLinkQuery.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(GetLinkQuery.LIMIT_KEY, limit);
        }

        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_PUBLIC_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(this, query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId) {
        return getPublicArchivedThreadsRetriever(channelId, null, null);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){
        Data queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = new Data(2);
            queryStringsData.addIfNotNull(GetLinkQuery.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(GetLinkQuery.LIMIT_KEY, limit);
        }

        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_PRIVATE_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(this, query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId){
        return getPrivateArchivedThreadsRetriever(channelId, null, null);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getJoinedPrivateArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){
        Data queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = new Data(2);
            queryStringsData.addIfNotNull(GetLinkQuery.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(GetLinkQuery.LIMIT_KEY, limit);
        }

        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_JOINED_PRIVATE_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(this, query, ListThreadsResponseBody::new);
    }

    public @NotNull Queueable<ListThreadsResponseBody> getJoinedPrivateArchivedThreadsRetriever(@NotNull String channelId) {
        return getJoinedPrivateArchivedThreadsRetriever(channelId, null, null);
    }

    /**
     * <p>
     *     Returns the {@link me.linusdev.discordbotapi.api.objects.user.User user object} of the requester's account. For OAuth2, this requires the identify scope,
     *     which will return the object without an email, and optionally the email scope,
     *     which returns the object with an email.
     * </p>
     *
     * @return {@link Queueable} to retrieve the current {@link User user} (your bot)
     * @see GetLinkQuery.Links#GET_CURRENT_USER
     */
    public @NotNull Queueable<User> getCurrentUserRetriever(){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_CURRENT_USER);
        return new ConvertingRetriever<>(this, query, User::fromData);
    }

    public @NotNull Queueable<User> getUserRetriever(@NotNull String userId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_USER,
                new PlaceHolder(PlaceHolder.USER_ID, userId));
        return new ConvertingRetriever<>(this, query, User::fromData);
    }

    public @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull MessageTemplate message){
        Query query = new LinkQuery(this, Link.CREATE_MESSAGE, message.getBody(), null,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));

        return new ConvertingRetriever<>(this, query, MessageImplementation::new);
    }

    public @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull String content, boolean allowMentions){
        return createMessage(channelId, new MessageTemplate(
                content,
                false, null,
                allowMentions ? null : AllowedMentions.noneAllowed(),
                null, null, null, null
        ));
    }

    public @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull String content){
        return createMessage(channelId, content,true);
    }

    public @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, boolean allowMentions, @NotNull Embed... embeds){
        return createMessage(channelId,
                new MessageTemplate(null, false, embeds,
                        allowMentions ? null : AllowedMentions.noneAllowed(),
                        null, null, null, null
                ));
    }

    public @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull Embed... embeds){
        return createMessage(channelId, true, embeds);
    }

    //Gateway

    public @NotNull Queueable<GetGatewayResponse> getGatewayBot(){
        Query query = new LinkQuery(this, Link.GET_GATEWAY_BOT, null, null);
        return new ConvertingRetriever<>(this, query, (lApi, data) -> GetGatewayResponse.fromData(data));
    }

    //Getter

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
    public @NotNull LApi getLApi() {
        return this;
    }


    //api-internal getter

    @ApiStatus.Internal
    public GuildManager getGuildManager() {
        return guildManager;
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
