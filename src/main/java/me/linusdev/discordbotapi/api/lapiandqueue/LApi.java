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
import me.linusdev.discordbotapi.api.communication.retriever.MessageRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.converter.Converter;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.discordbotapi.api.objects.invite.Invite;
import me.linusdev.discordbotapi.api.objects.message.Message;
import me.linusdev.discordbotapi.api.objects.user.User;
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
    private volatile Thread queueThread;

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
        this.queueWorker = Executors.newSingleThreadExecutor();
        this.queueWorker.submit(new Runnable() {
            @Override
            @SuppressWarnings({"InfiniteLoopStatement"})
            public void run() {
                queueThread = Thread.currentThread();
                log.log("Started queue thread: " + queueThread.getName());
                try {
                    while (true) {
                        synchronized (queue) {
                            if (queue.peek() == null) {
                                try {
                                    queue.wait(10000L);
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
        new Thread(() -> {
            //TODO catch exception
            synchronized (queue){
                queue.notifyAll();
            }
        }).start();
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
        if(Thread.currentThread().equals(queueThread)){
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
    public @NotNull Queueable<ArrayList<Invite>> getChannelInvites(@NotNull String channelId){
        GetLinkQuery query = new GetLinkQuery(this, GetLinkQuery.Links.GET_CHANNEL_INVITES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<Data, Invite>(this, query, Invite::fromData);
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
