package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.data.Data;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.VoiceRegions;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.IllegalRequestMethodException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.ChannelRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.MessageRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.communication.retriever.response.body.ListThreadsResponseBody;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.discordbotapi.api.objects.invite.Invite;
import me.linusdev.discordbotapi.api.objects.message.Message;
import me.linusdev.discordbotapi.api.objects.toodo.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;

public class LApi {

    private static final String LAPI_URL = "https://twitter.com/Linus_Dev";
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
    private volatile QueueThread queueThread;

    private final ThreadPoolExecutor executor;

    //stores and manages the voice regions
    private final VoiceRegions voiceRegions;

    //Logger
    private final LogInstance log = Logger.getLogger("LApi", Logger.Type.INFO);

    public LApi(@NotNull String token, @NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        this.token = token;
        this.config = config;
        this.authorizationHeader = new LApiHttpHeader(ATTRIBUTE_AUTHORIZATION_NAME, ATTRIBUTE_AUTHORIZATION_VALUE.replace(PlaceHolder.TOKEN, this.token));
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        //Queue
        this.queue = config.getQueue();
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
                                    ignored.printStackTrace();
                                }
                            }

                            if (queue.peek() == null) continue;

                            if(Logger.DEBUG_LOG){
                                log.debug("queue.poll().completeHereAndIgnoreQueueThread()");
                                long millis = System.currentTimeMillis();
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
     *         {@link Future#get()}, to wait until the {@link Queueable} has been gone through the {@link #queue} and {@link Queueable#completeHereAndIgnoreQueueThread() completed}
     *     </li>
     *     <li>
     *          {@link Queueable#completeHereAndIgnoreQueueThread()}, to wait until the {@link Queueable} has been {@link Queueable#completeHereAndIgnoreQueueThread() completed}.
     *          This wont assure, that several {@link LApiHttpRequest LApiHttpRequests} are sent at the same time
     *     </li>
     * </ul>
     *
     *
     * @param queueable {@link Queueable}
     * @param beforeComplete {@link Future#beforeComplete(Consumer)} will be set with given {@link Consumer}, if beforeComplete is not {@code null}
     * @param then {@link Future#then(BiConsumer)} will be set with given {@link BiConsumer}, if then is not {@code null}
     * @param thenSingle {@link Future#then(Consumer)} will be set with given {@link Consumer}, if thenSingle is not {@code null}
     * @param <T> Return Type of {@link Queueable}
     * @return {@link Future<T>}
     */
    protected <T> @NotNull Future<T> queue(@NotNull Queueable<T> queueable, @Nullable BiConsumer<T, Error> then, @Nullable Consumer<T> thenSingle, @Nullable Consumer<Future<T>> beforeComplete){
        final Future<T> future = new Future<T>(queueable);
        if(beforeComplete != null) future.beforeComplete(beforeComplete);
        if(then != null) future.then(then);
        if(thenSingle != null) future.then(thenSingle);
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

    /**
     * Queues given {@link Future} and notifies the {@link #queueWorker} Thread
     */
    private void queue(@NotNull final Future<?> future){
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
    public Data sendLApiHttpRequest(LApiHttpRequest request) throws IOException, InterruptedException, ParseException, IllegalRequestMethodException {
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
    public Data sendLApiHttpRequest(@NotNull LApiHttpRequest request, @Nullable String arrayKey) throws IOException, InterruptedException, ParseException, IllegalRequestMethodException {
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

    /**
     * This checks whether the currentThread is the queue-thread and throws an {@link LApiRuntimeException} if that is the case
     * @throws LApiRuntimeException if the currentThread is the queue Thread
     */
    public void checkQueueThread() throws LApiRuntimeException {
        if(java.lang.Thread.currentThread().equals(queueThread)){
            throw new LApiRuntimeException("You cannot invoke Future.get() or Queueable.completeHere() on the queue-thread" +
                    ", because this could lead to an infinite wait loop. You should also not wait or sleep the " +
                    "queue-thread, because this will delay all other queued Futures");
        }
    }

    //Retriever

    /**
     *
     * @param channelId the id of the {@link Channel}, which should be retrieved
     * @return {@link Queueable} which can retrieve the {@link Channel}
     * @see Queueable#queue()
     * @see Queueable#completeHereAndIgnoreQueueThread()
     */
    public @NotNull Queueable<Channel> getChannelRetriever(@NotNull String channelId){
        return new ChannelRetriever(this, channelId);
    }

    /**
     *
     * @param channelId the id of the {@link Channel}, in which the message was sent
     * @param messageId the id of the {@link Message}
     * @return {@link Queueable} which can retrieve the {@link Message}
     */
    public @NotNull Queueable<Message> getChannelMessageRetriever(@NotNull String channelId, @NotNull String messageId){
        return new MessageRetriever(this, channelId, messageId);
    }

    public enum AnchorType {
        /**
         * Retrieve objects around the given object
         */
        AROUND,

        /**
         * Retrieve objects before the given object
         */
        BEFORE,

        /**
         * Retrieve objects after the given object
         */
        AFTER,
        ;
    }

    /**
     * <p>
     *     This is used to retrieve a bunch of {@link Message messages} in a {@link Channel channel}.
     * </p>
     * <p>
     *     If anchorType is {@code null}, it should use {@link AnchorType#AROUND}, but it may result in unexpected behavior.
     * </p>
     * <p>
     *     If anchorMessageId and anchorType is {@code null} it should retrieve the latest {@link Message messages} in the {@link Channel channel}.
     * </p>
     * <p>
     *     If limit is {@code null}, the limit will be 50
     * </p>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are<br><br>
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link Message} with this id will most likely be included in the result.
     *                        If this is {@code null}, it will retrieve the latest messages in the channel<br><br>
     * @param limit the limit of how many messages you want to retrieve (between 1-100). Default is 50<br><br>
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link Message Messages}
     * @see me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery.Links#GET_CHANNEL_MESSAGES
     */
    public @NotNull Queueable<ArrayList<Message>> getChannelMessagesRetriever(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable Integer limit, @Nullable AnchorType anchorType){

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
        return new ArrayRetriever<Data, Message>(this, query, Message::new);
    }

    /**
     * This will retrieve 50 or less {@link Message messages}.<br><br> For more information see
     * {@link #getChannelMessagesRetriever(String, String, Integer, AnchorType)}<br><br>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are<br><br>
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link Message} with this id will most likely be included in the result.
     *                       If this is {@code null}, it will retrieve the latest messages in the channel.<br><br>
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link Message Messages}
     * @see #getChannelMessagesRetriever(String, String, Integer, AnchorType)
     */
    public @NotNull Queueable<ArrayList<Message>> getChannelMessagesRetriever(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable AnchorType anchorType){
        return getChannelMessagesRetriever(channelId, anchorMessageId, null, anchorType);
    }

    /**
     * This should retrieve the latest 50 or less {@link Message messages} in given {@link Channel channel}. The newest {@link Message message} will have index 0
     * <br><br>
     * See {@link #getChannelMessagesRetriever(String, String, Integer, AnchorType)} for more information<br><br>
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link Message Messages}
     * @see #getChannelMessagesRetriever(String, String, Integer, AnchorType)
     */
    public @NotNull Queueable<ArrayList<Message>> getChannelMessagesRetriever(@NotNull String channelId) {
        return getChannelMessagesRetriever(channelId, null, null, null);
    }

    /**
     *
     * <p>
     *     This is used to retrieve the {@link User users}, that have reacted with a specific {@link Emoji}.<br>
     *     If you want to retrieve more than 100 {@link User users},
     *     you will have to chain this Queueable with the last {@link User#getId() user's id} as afterUserId.
     * </p>
     *
     * @param channelId the id of the {@link Channel}
     * @param messageId the id of the {@link Message}
     * @param emoji the {@link Emoji}
     * @param afterUserId the {@link User} after which you want to retrieve. This should be {@code null}, if this is your first retrieve
     * @param limit max number of users to return (1-100). Will be 25 if {@code null}
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link User users} that have reacted with given emoji
     */
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

    /**
     *
     * <p>
     *     This is used to retrieve the {@link User users}, that have reacted with a specific {@link Emoji}.<br>
     *     If you want to retrieve more than 100 {@link User users},
     *     you will have to chain {@link #getReactionsRetriever(String, String, Emoji, String, Integer)}.
     * </p>
     *
     * @param channelId the id of the {@link Channel}
     * @param messageId the id of the {@link Message}
     * @param emoji the {@link Emoji}
     * @param limit max number of users to return (1-100). Will be 25 if {@code null}
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link User users} that have reacted with given emoji
     */
    public @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable Integer limit){
        return getReactionsRetriever(channelId, messageId, emoji, null, limit);
    }

    /**
     * <p>
     *     Returns a list of {@link Invite invite} objects (with {@link Invite#getInviteMetadata() invite metadata})
     *     for the {@link Channel channel}. Only usable for guild channels.
     *     Requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#MANAGE_CHANNELS MANAGE_CHANNELS} permission.
     * </p>
     *
     * <p>
     *     In case you didn't know, yes invites to a guild are always bound to a specific channel
     * </p>
     *
     * @param channelId the id of the {@link Channel}, you want a list of invites for
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of {@link Invite invites} for given channel
     * @see GetLinkQuery.Links#GET_CHANNEL_INVITES
     */
    public @NotNull Queueable<ArrayList<Invite>> getChannelInvitesRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_CHANNEL_INVITES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, Invite>(this, query, Invite::fromData);
    }

    /**
     * <p>
     *     Returns all pinned messages in the channel as an array of message objects.
     * </p>
     * <p>
     *     The max amount of pinned messages is 50
     * </p>
     *
     * <p>
     *     In my testing, the the message array was always sorted, so that the last pinned message had index 0.
     *     Independent from when the message was actual sent
     * </p>
     *
     * @param channelId the id of the {@link Channel}, you want the pinned messages from
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of {@link Message pinned messages} for given channel
     * @see GetLinkQuery.Links#GET_PINNED_MESSAGES
     */
    public @NotNull Queueable<ArrayList<Message>> getPinnedMessagesRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_PINNED_MESSAGES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, Message>(this, query, Message::new);
    }

    /**
     * <p>
     *     Returns a {@link me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember thread member} object
     *     for the specified user if they are a member of the thread, returns a 404 response otherwise.
     * </p>
     *
     * @param channelId the id of the {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread thread}, the user is a member of
     * @param userId the {@link User#getId() user id}
     * @return {@link Queueable} to retrieve the {@link ThreadMember} matching given user in given thread
     * @see GetLinkQuery.Links#GET_THREAD_MEMBER
     */
    public @NotNull Queueable<ThreadMember> getThreadMemberRetriever(@NotNull String channelId, @NotNull String userId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_THREAD_MEMBER,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId), new PlaceHolder(PlaceHolder.USER_ID, userId));
        return new ConvertingRetriever<ThreadMember>(this, query, (lApi, data) -> ThreadMember.fromData(data));
    }

    /**
     * <p>
     *     Returns array of thread members objects that are members of the thread.
     * </p>
     * <p>
     *     This endpoint is restricted according to whether the GUILD_MEMBERS Privileged Intent is enabled for your application.
     * </p>
     * TODO add @links
     * @param channelId the channel id of the {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread thread}
     * @return {@link Queueable} to retrieve a {@link ArrayList list} of {@link ThreadMember thread members}
     * @see me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery.Links#LIST_THREAD_MEMBERS
     */
    public @NotNull Queueable<ArrayList<ThreadMember>> getThreadMembersRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_THREAD_MEMBERS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, ThreadMember>(this, query, (lApi, data) -> ThreadMember.fromData(data));
    }

    /**
     * <p>
     *     Returns all active threads in the channel, including public and private threads. Threads are ordered by their id, in descending order.
     * </p>
     * <p>
     *     This route is deprecated and will be removed in v10. It is replaced by List Active Guild Threads.
     * </p>
     * TODO add LIST_ACTIVE_GUILD_THREADS @link
     * @param channelId the id of the {@link Channel channel}, to retrieve all {@link Thread threads} from
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_ACTIVE_THREADS
     * @see ListThreadsResponseBody
     */
    @SuppressWarnings("removal")
    @Deprecated(since = "api v10", forRemoval = true)
    public @NotNull Queueable<ListThreadsResponseBody> getActiveThreadsRetriever(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.LIST_ACTIVE_THREADS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(this, query, ListThreadsResponseBody::new);
    }

    /**
     * <p>
     *     Returns archived threads in the channel that are public.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS GUILD_NEWS} channel returns
     *     threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} by setting
     *     before to the {@link ThreadMetadata#getArchiveTimestamp()} of the last retrieved thread.
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all public archived {@link Thread threads} for
     * @param before optional returns threads before this timestamp. {@code null} to retrieve the latest threads
     * @param limit optional maximum number of threads to return. {@code null} if you want no limit
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_PUBLIC_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
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

    /**
     * <p>
     *     Returns archived threads in the channel that are public.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS GUILD_NEWS} channel returns
     *     threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more with {@link #getPublicArchivedThreadsRetriever(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all public archived {@link Thread threads} for
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_PUBLIC_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
    public @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId) {
        return getPublicArchivedThreadsRetriever(channelId, null, null);
    }

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#MANAGE_THREADS MANAGE_THREADS} permissions.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} by setting
     *     before to the {@link ThreadMetadata#getArchiveTimestamp()} of the last retrieved thread.
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Thread threads} for
     * @param before optional returns threads before this timestamp. {@code null} to retrieve the latest threads
     * @param limit optional maximum number of threads to return. {@code null} if you want no limit
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
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

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#MANAGE_THREADS MANAGE_THREADS} permissions.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} with
     *     {@link #getPrivateArchivedThreadsRetriever(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Thread threads} for
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     * @see #getPrivateArchivedThreadsRetriever(String, ISO8601Timestamp, Integer)
     */
    public @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId){
        return getPrivateArchivedThreadsRetriever(channelId, null, null);
    }

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} by setting
     *     before to the {@link ThreadMetadata#getArchiveTimestamp()} of the last retrieved thread.
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Thread threads} for
     * @param before optional returns threads before this timestamp. {@code null} to retrieve the latest threads
     * @param limit optional maximum number of threads to return. {@code null} if you want no limit
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_JOINED_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
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

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} with
     *     {@link #getJoinedPrivateArchivedThreadsRetriever(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Thread threads} for
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_JOINED_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     * @see #getJoinedPrivateArchivedThreadsRetriever(String, ISO8601Timestamp, Integer)
     */
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

    //Getter

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
