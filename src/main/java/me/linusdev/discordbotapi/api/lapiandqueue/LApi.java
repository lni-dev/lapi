package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.VoiceRegionManager;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.discordbotapi.api.communication.exceptions.NoInternetException;
import me.linusdev.discordbotapi.api.communication.gateway.other.GetGatewayResponse;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.IllegalRequestMethodException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.discordbotapi.api.communication.retriever.query.Link;
import me.linusdev.discordbotapi.api.communication.retriever.response.body.ListThreadsResponseBody;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.config.ConfigFlag;
import me.linusdev.discordbotapi.api.objects.HasLApi;
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
import me.linusdev.discordbotapi.api.templates.message.MessageTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <h2 style="margin:0;padding:0;">What is LApi?</h2>
 * <p style="margin:0;padding:0;">
 *     LApi is the facade, that handles all communications with Discord's api
 * </p>
 *
 * <br>
 * <h2 style="margin:0;padding:0;">How to get an Instance?</h2>
 * <p style="margin:0;padding:0;">
 *     The easiest way to get an instance is:<br>
 *     {@code LApi lApi = ConfigBuilder.getDefault(TOKEN).buildLapi()}.<br>
 *     {@code TOKEN} must be replaced with {@link ConfigBuilder#setToken(String) your bot token}
 * </p>
 * <br>
 * <p>
 *     Alternatively you can also make your own Config. For example:<br>
 *     <pre>
 *         {@code
 *  LApi lApi = new ConfigBuilder(TOKEN)
 *          .enable(ConfigFlag.ENABLE_GATEWAY)
 *          .adjustGatewayConfig(gatewayConfigBuilder -> {
 *              gatewayConfigBuilder
 *                      .addIntent(GatewayIntent.GUILD_MESSAGES,
 *                                 GatewayIntent.DIRECT_MESSAGES);
 *                      .adjustStartupPresence(presence -> {
 *                                 presence.setStatus(StatusType.ONLINE);
 *                             });
 *          }).buildLapi();
 *  }
 *     </pre>
 *     {@code TOKEN} must be replaced with {@link ConfigBuilder#setToken(String) your bot token}
 * </p>
 * <br>
 * <br>
 * <h2 style="margin:0;padding:0;">How to listen to events?</h2>
 * <p style="margin:0;padding:0;">
 *     You can listen to events by adding a listener to the {@link AbstractEventTransmitter event transmitter}:
 *     <pre>{@code lApi.getEventTransmitter().addListener(yourListener)}</pre>
 *     for more information see {@link me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener EventListener}
 * </p>
 */
public interface LApi extends HasLApi {

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

    /**
     *
     * This queues a {@link Queueable} in the {@link LApiImpl#queue Queue}.<br>
     * This is used for sending a lot of {@link LApiHttpRequest} after each is other and not at the same time.
     * This will NOT wait the Thread until the {@link Queueable} has been completed.
     * <br><br>
     * If you want to wait your Thread you could use:
     * <ul>
     *     <li>
     *         {@link Future#get()}, to wait until the {@link Queueable} has been gone through the {@link #queue} and {@link Queueable#completeHereAndIgnoreQueueThread() completed}
     *     </li>
     *     <li>
     *          {@link Queueable#completeHere()}, to wait until the {@link Queueable} has been {@link Queueable#completeHereAndIgnoreQueueThread() completed}.
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
    @ApiStatus.Internal
    <T> @NotNull Future<T> queue(@NotNull Queueable<T> queueable, @Nullable BiConsumer<T, Error> then, @Nullable Consumer<T> thenSingle, @Nullable Consumer<Future<T>> beforeComplete);

    /**
     * Queues given {@link Queueable} after a given amount of time. see {@link #queue(Queueable)}
     *
     * @param queueable {@link Queueable}
     * @param delay the delay to wait before queueing
     * @param timeUnit the {@link TimeUnit} for the delay
     * @param <T> Return Type of {@link Queueable}
     * @return {@link Future<T>}
     * @see #queue(Queueable, BiConsumer, Consumer, Consumer)
     * @see Queueable#queueAfter(long, TimeUnit)
     */
    @ApiStatus.Internal
    <T> @NotNull Future<T> queueAfter(@NotNull Queueable<T> queueable, long delay, TimeUnit timeUnit);

    /**
     * @see #getResponseAsData(LApiHttpRequest, String)
     *
     * Cannot parse pure Arrays!
     *
     * @param request
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     */
    public Data getResponseAsData(LApiHttpRequest request) throws IOException, InterruptedException, ParseException, IllegalRequestMethodException, NoInternetException;

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
    Data getResponseAsData(@NotNull LApiHttpRequest request, @Nullable String arrayKey) throws IOException, InterruptedException, ParseException, IllegalRequestMethodException, NoInternetException;

    InputStream getResponseAsInputStream(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, InterruptedException, NoInternetException;

    /**
     * Appends the required headers to the {@link LApiHttpRequest}.<br>
     * These headers are required for Discord to accept the request
     * @param request the {@link LApiHttpRequest} to append the headers to
     * @return the {@link LApiHttpRequest} with the appended headers
     */
    LApiHttpRequest appendHeader(@NotNull LApiHttpRequest request);

    /**
     * This checks whether the currentThread is the queue-thread and throws an {@link LApiRuntimeException} if that is the case
     * @throws LApiRuntimeException if the currentThread is the queue Thread
     */
    void checkQueueThread() throws LApiRuntimeException;

    //Retriever

    /**
     *
     * @param channelId the id of the {@link Channel}, which should be retrieved
     * @return {@link Queueable} which can retrieve the {@link Channel}
     * @see Queueable#queue()
     * @see Queueable#completeHereAndIgnoreQueueThread()
     */
    @NotNull Queueable<Channel> getChannelRetriever(@NotNull String channelId);

    /**
     *
     * @param channelId the id of the {@link Channel}, in which the message was sent
     * @param messageId the id of the {@link MessageImplementation}
     * @return {@link Queueable} which can retrieve the {@link MessageImplementation}
     */
    @NotNull Queueable<MessageImplementation> getChannelMessageRetriever(@NotNull String channelId, @NotNull String messageId);

    enum AnchorType {
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
     *     This is used to retrieve a bunch of {@link MessageImplementation messages} in a {@link Channel channel}.
     * </p>
     * <p>
     *     If anchorType is {@code null}, it should use {@link AnchorType#AROUND}, but it may result in unexpected behavior.
     * </p>
     * <p>
     *     If anchorMessageId and anchorType is {@code null} it should retrieve the latest {@link MessageImplementation messages} in the {@link Channel channel}.
     * </p>
     * <p>
     *     If limit is {@code null}, the limit will be 50
     * </p>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are<br><br>
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link MessageImplementation} with this id will most likely be included in the result.
     *                        If this is {@code null}, it will retrieve the latest messages in the channel<br><br>
     * @param limit the limit of how many messages you want to retrieve (between 1-100). Default is 50<br><br>
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link MessageImplementation Messages}
     * @see me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery.Links#GET_CHANNEL_MESSAGES
     */
    @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessagesRetriever(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable Integer limit, @Nullable AnchorType anchorType);

    /**
     * This will retrieve 50 or less {@link MessageImplementation messages}.<br><br> For more information see
     * {@link #getChannelMessagesRetriever(String, String, Integer, AnchorType)}<br><br>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are<br><br>
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link MessageImplementation} with this id will most likely be included in the result.
     *                       If this is {@code null}, it will retrieve the latest messages in the channel.<br><br>
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link MessageImplementation Messages}
     * @see #getChannelMessagesRetriever(String, String, Integer, AnchorType)
     */
    @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessagesRetriever(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable AnchorType anchorType);

    /**
     * This should retrieve the latest 50 or less {@link MessageImplementation messages} in given {@link Channel channel}. The newest {@link MessageImplementation message} will have index 0
     * <br><br>
     * See {@link #getChannelMessagesRetriever(String, String, Integer, AnchorType)} for more information<br><br>
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link MessageImplementation Messages}
     * @see #getChannelMessagesRetriever(String, String, Integer, AnchorType)
     */
    @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessagesRetriever(@NotNull String channelId);

    /**
     *
     * <p>
     *     This is used to retrieve the {@link User users}, that have reacted with a specific {@link Emoji}.<br>
     *     If you want to retrieve more than 100 {@link User users},
     *     you will have to chain this Queueable with the last {@link User#getId() user's id} as afterUserId.
     * </p>
     *
     * @param channelId the id of the {@link Channel}
     * @param messageId the id of the {@link MessageImplementation}
     * @param emoji the {@link Emoji}
     * @param afterUserId the {@link User} after which you want to retrieve. This should be {@code null}, if this is your first retrieve
     * @param limit max number of users to return (1-100). Will be 25 if {@code null}
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link User users} that have reacted with given emoji
     */
    @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable String afterUserId, @Nullable Integer limit);

    /**
     *
     * <p>
     *     This is used to retrieve the {@link User users}, that have reacted with a specific {@link Emoji}.<br>
     *     If you want to retrieve more than 100 {@link User users},
     *     you will have to chain {@link #getReactionsRetriever(String, String, Emoji, String, Integer)}.
     * </p>
     *
     * @param channelId the id of the {@link Channel}
     * @param messageId the id of the {@link MessageImplementation}
     * @param emoji the {@link Emoji}
     * @param limit max number of users to return (1-100). Will be 25 if {@code null}
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link User users} that have reacted with given emoji
     */
    @NotNull Queueable<ArrayList<User>> getReactionsRetriever(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable Integer limit);

    /**
     * <p>
     *     Returns a list of {@link Invite invite} objects (with {@link Invite#getInviteMetadata() invite metadata})
     *     for the {@link Channel channel}. Only usable for guild channels.
     *     Requires the {@link Permission#MANAGE_CHANNELS MANAGE_CHANNELS} permission.
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
    @NotNull Queueable<ArrayList<Invite>> getChannelInvitesRetriever(@NotNull String channelId);

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
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of {@link MessageImplementation pinned messages} for given channel
     * @see GetLinkQuery.Links#GET_PINNED_MESSAGES
     */
    @NotNull Queueable<ArrayList<MessageImplementation>> getPinnedMessagesRetriever(@NotNull String channelId);

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
    @NotNull Queueable<ThreadMember> getThreadMemberRetriever(@NotNull String channelId, @NotNull String userId);

    /**
     * <p>
     *     Returns array of thread members objects that are members of the thread.
     * </p>
     * <p>
     *     This endpoint is restricted according to whether the {@link me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS} Privileged GatewayIntent is enabled for your application.
     * </p>
     *
     * @param channelId the channel id of the {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread thread}
     * @return {@link Queueable} to retrieve a {@link ArrayList list} of {@link ThreadMember thread members}
     * @see me.linusdev.discordbotapi.api.communication.retriever.query.GetLinkQuery.Links#LIST_THREAD_MEMBERS
     */
    @NotNull Queueable<ArrayList<ThreadMember>> getThreadMembersRetriever(@NotNull String channelId);

    /**
     * <p>
     *     Returns all active threads in the channel, including public and private threads. Threads are ordered by their id, in descending order.
     * </p>
     * <p>
     *     This route is deprecated and will be removed in v10. It is replaced by List Active GuildImpl Threads.
     * </p>
     * TODO add LIST_ACTIVE_GUILD_THREADS @link
     * @param channelId the id of the {@link Channel channel}, to retrieve all {@link Thread threads} from
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see GetLinkQuery.Links#LIST_ACTIVE_THREADS
     * @see ListThreadsResponseBody
     */
    @SuppressWarnings("removal")
    @Deprecated(since = "api v10", forRemoval = true)
    @NotNull Queueable<ListThreadsResponseBody> getActiveThreadsRetriever(@NotNull String channelId);

    /**
     * <p>
     *     Returns archived threads in the channel that are public.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS GUILD_NEWS} channel returns
     *     threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
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
    @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit);

    /**
     * <p>
     *     Returns archived threads in the channel that are public.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS GUILD_NEWS} channel returns
     *     threads of {@link Channel#getType() type} {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
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
    @NotNull Queueable<ListThreadsResponseBody> getPublicArchivedThreadsRetriever(@NotNull String channelId);

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link Permission#MANAGE_THREADS MANAGE_THREADS} permissions.
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
    @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit);

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link Permission#MANAGE_THREADS MANAGE_THREADS} permissions.
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
    @NotNull Queueable<ListThreadsResponseBody> getPrivateArchivedThreadsRetriever(@NotNull String channelId);

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
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
    @NotNull Queueable<ListThreadsResponseBody> getJoinedPrivateArchivedThreadsRetriever(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit);

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
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
    @NotNull Queueable<ListThreadsResponseBody> getJoinedPrivateArchivedThreadsRetriever(@NotNull String channelId);

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
    @NotNull Queueable<User> getCurrentUserRetriever();

    /**
     *
     * <p>
     *     Returns a {@link me.linusdev.discordbotapi.api.objects.user.User user object} for a given user ID.
     * </p>
     *
     * <p>
     *     You can probably retrieve every user, even if your bot does not share guild with them
     * </p>
     *
     * @param userId the id of the {@link User user} you want to retrieve
     * @return {@link Queueable} to retrieve {@link User user} with given id
     * @see GetLinkQuery.Links#GET_USER
     */
    @NotNull Queueable<User> getUserRetriever(@NotNull String userId);

    /**
     * <p>
     *     This will create a new {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id
     * </p>
     *
     * <p>
     *     For a simple {@link MessageTemplate} creation see
     *     {@link me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder MessageBuilder}
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param message the message to create
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull MessageTemplate message);

    /**
     * <p>
     *     This will create a new {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     * <p>
     *     This allows you to control if mentions are allowed. For a better control see
     *     {@link me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder MessageBuilder}
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param content the text content of the message
     * @param allowMentions whether this message is allowed to mention user, roles, everyone, here, etc.
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull String content, boolean allowMentions);

    /**
     * <p>
     *     This will create a new {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     * <p>
     *     All mentions will be allowed. For more control see {@link #createMessage(String, String, boolean)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param content the text content of the message
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull String content);

    /**
     * <p>
     *     This will create a new {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param embeds the embeds for the message
     * @param allowMentions mentions inside embeds will never ping the user. In case that ever changes, you can adjust the behavior here
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, boolean allowMentions, @NotNull Embed... embeds);

    /**
     * <p>
     *     This will create a new {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param embeds the embeds for the message
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull Embed... embeds);

    //Gateway

    /**
     * <p>
     *     Returns an object based on the information in Get Gateway, plus additional metadata that can help during the
     *     operation of large or sharded bots. Unlike the Get Gateway, this route should not be cached for extended
     *     periods of time as the value is not guaranteed to be the same per-call, and changes as the bot joins/leaves
     *     guilds.
     * </p>
     * @return {@link Queueable} to get a {@link GetGatewayResponse}
     * @see Link#GET_GATEWAY_BOT
     */
    @NotNull Queueable<GetGatewayResponse> getGatewayBot();

    //Getter

    /**
     * <p>
     *     The event transmitter is used to listen to events from Discord. For more information on
     *     how to add a listener, see {@link me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener EventListener}
     * </p>
     * @return {@link AbstractEventTransmitter}
     */
    AbstractEventTransmitter getEventTransmitter();

    /**
     * <p>
     *     let's you update the presence of the current self user (your bot).
     * </p>
     * <p>
     *     <b>After you finished adjusting your presence, you will have to call {@link SelfUserPresenceUpdater#updateNow()}!</b>
     * </p>
     * @return {@link SelfUserPresenceUpdater}
     */
    SelfUserPresenceUpdater getSelfPresenceUpdater();

    /**
     * <p>
     *     Manager for the {@link me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion VoiceRegions}
     *     retrieved from Discord if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_VOICE_REGIONS} is enabled
     * </p>
     * @return {@link VoiceRegionManager}
     */
    VoiceRegionManager getVoiceRegionManager();

    /**
     * @see ConfigFlag#CACHE_VOICE_REGIONS
     */
    boolean isCacheVoiceRegionsEnabled();

    /**
     * @see ConfigFlag#CACHE_ROLES
     */
    boolean isCacheRolesEnabled();

    /**
     *
     * @see ConfigFlag#COPY_ROLE_ON_UPDATE_EVENT
     */
    boolean isCopyOldRolesOnUpdateEventEnabled();

    /**
     *
     * @see ConfigFlag#CACHE_GUILDS
     */
    boolean isCacheGuildsEnabled();

    /**
     * @see ConfigFlag#COPY_GUILD_ON_UPDATE_EVENT
     */
    boolean isCopyOldGuildOnUpdateEventEnabled();
}
