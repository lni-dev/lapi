package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.*;
import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;

public class GetLinkQuery implements Query{

    public static final String AROUND_KEY = "around";
    public static final String BEFORE_KEY = "before";
    public static final String AFTER_KEY = "after";
    public static final String LIMIT_KEY = "limit";
    public static final String _KEY = "";

    public enum Links{
        /**
         * Get a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel} by ID.
         * Returns a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel object}.
         * If the channel is a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread thread},
         * a {@link Thread#getMember() thread member} object is included in the returned result.
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel" target="_top">Get Channel</a>
         */
        GET_CHANNEL(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID),

        /**
         * <p>
         * Returns the messages for a channel. If operating on a guild channel,
         * this endpoint requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#VIEW_CHANNEL VIEW_CHANNEL}
         * permission to be present on the current user. If the current user is missing the
         * '{@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}' permission in the channel
         * then this will return no messages (since they cannot read the message history).
         * Returns an array of {@link me.linusdev.discordbotapi.api.objects.message.Message message objects} on success.
         * </p>
         * <br>
         * <p style="margin-bottom:0;padding-bottom:0;">
         *     This can have <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages-query-string-params" target="_top">Query String parameters</a>:
         * </p>
         * <ul style="margin-bottom:0;padding-bottom:0;margin-top:0;padding-top:0;">
         *     <li>
         *         {@link #AROUND_KEY} get messages around this message ID
         *     </li>
         *     <li>
         *         {@link #BEFORE_KEY} get messages before this message ID
         *     </li>
         *     <li>
         *         {@link #AFTER_KEY} get messages after this message ID
         *     </li>
         *     <li>
         *         {@link #LIMIT_KEY} max number of messages to return (1-100). Default: 50
         *     </li>
         * </ul>
         * <p style="margin-top:0;padding-top:0;">
         *    The before, after, and around keys are mutually exclusive, only one may be passed at a time.
         * </p>
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages" target="_top">Get Channel Messages</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages-query-string-params" target="_top">Query String Params</a>
         */
        GET_CHANNEL_MESSAGES(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/messages", true),

        /**
         * <p>
         *     Returns a specific message in the channel. If operating on a guild channel,
         *     this endpoint requires the '{@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}'
         *     permission to be present on the current user. Returns a {@link me.linusdev.discordbotapi.api.objects.message.Message message object} on success.
         * </p>
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see PlaceHolder#MESSAGE_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-message" target="_top">Get Channel Message</a>
         */
        GET_CHANNEL_MESSAGE(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID),

        /**
         * <p>
         *     Get a list of users that reacted with this emoji. Returns an array of
         *     {@link me.linusdev.discordbotapi.api.objects.user.User user objects} on success.
         *     The emoji must be <a href="https://en.wikipedia.org/wiki/Percent-encoding" target="_top">URL Encoded</a>
         *     or the request will fail with 10014: Unknown Emoji. To use custom emoji,
         *     you must encode it in the format name:id with the emoji name and emoji id.
         * </p>
         * <br>
         *
         * <p>
         *     This can have <a href="https://discord.com/developers/docs/resources/channel#get-reactions-query-string-params" target="_top">Query String parameters</a>
         * </p>
         * @see PlaceHolder#CHANNEL_ID
         * @see PlaceHolder#MESSAGE_ID
         * @see PlaceHolder#EMOJI
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-reactions" target="_top">Get Reactions</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-reactions-query-string-params" target="_top">Query String Params</a>
         */
        GET_REACTIONS(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/reactions/" + EMOJI, true),

        /**
         * <p>
         *     Returns a list of {@link me.linusdev.discordbotapi.api.objects.toodo.Invite invite objects}
         *     (with {@link me.linusdev.discordbotapi.api.objects.toodo.InviteMetadata invite metadata}) for the channel.
         *     Only usable for guild channels. Requires the
         *     {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#MANAGE_CHANNELS MANAGE_CHANNELS}
         *     permission.
         * </p>
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-invites" target="_top">Get Channel Invites</a>
         */
        GET_CHANNEL_INVITES(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/invites"),

        /**
         * <p>
         *     Returns all pinned messages in the channel as an array of {@link me.linusdev.discordbotapi.api.objects.message.Message message} objects.
         * </p>
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-pinned-messages" target="_top">Get Pinned Messages</a>
         */
        GET_PINNED_MESSAGES(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/pins"),

        /**
         * <p>
         *     Returns a {@link me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember thread member} object
         *     for the specified user if they are a member of the thread, returns a 404 response otherwise.
         * </p>
         * @see PlaceHolder#CHANNEL_ID
         * @see PlaceHolder#USER_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#get-thread-member" target="_top">Get Thread Member</a>
         */
        GET_THREAD_MEMBER(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/thread-members/" + USER_ID),

        /**
         * <p>
         *     Returns array of thread members objects that are members of the thread.
         * </p>
         * <p>
         *     This endpoint is restricted according to whether the GUILD_MEMBERS Privileged Intent is enabled for your application.
         * </p>
         * TODO add @links
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-thread-members" target="_top">List Thread Members</a>
         */
        LIST_THREAD_MEMBERS(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/thread-members"),

        /**
         * <p>
         *     Returns all active threads in the channel, including public and private threads. Threads are ordered by their id, in descending order.
         * </p>
         * <p>
         *     This route is deprecated and will be removed in v10. It is replaced by List Active Guild Threads.
         * </p>
         * TODO add LIST_ACTIVE_GUILD_THREADS @link
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-active-threads" target="_top">List Active Threads</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-active-threads-response-body" target="_top">Response Body</a>
         */
        @Deprecated(since = "v10", forRemoval = true)
        LIST_ACTIVE_THREADS(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/threads/active"),

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
         * <p>
         *     This can have <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads-query-string-params" target="_top">Query String parameters</a>
         * </p>
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads" target="_top"> List Public Archived Threads</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads-response-body" target="_top"> Response Body</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads-query-string-params" target="_top"> Query String Params</a>
         */
        LIST_PUBLIC_ARCHIVED_THREADS(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/threads/archived/public", true),

        /**
         * <p>
         *     Returns archived threads in the channel that are of {@link Channel#getType() type}
         *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
         *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
         *     Requires both the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
         *     {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#MANAGE_THREADS MANAGE_THREADS} permissions.
         * </p>
         * <p>
         *     This can have <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads-query-string-params" target="_top">Query String parameters</a>
         * </p>
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads" target="_top"> List Private Archived Threads</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads-response-body" target="_top"> Response Body</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads-query-string-params" target="_top"> Query String Params</a>
         */
        LIST_PRIVATE_ARCHIVED_THREADS(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/threads/archived/private", true),

        /**
         * <p>
         *     Returns archived threads in the channel that are of {@link Channel#getType() type}
         *     {@link me.linusdev.discordbotapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
         *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
         *     Requires the {@link me.linusdev.discordbotapi.api.objects.enums.Permissions#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
         * </p>
         * <p>
         *     This can have <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads-query-string-params" target="_top">Query String parameters</a>
         * </p>
         *
         * @see PlaceHolder#CHANNEL_ID
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads" target="_top"> List Private Archived Threads</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads-response-body" target="_top"> Response Body</a>
         * @see <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads-query-string-params" target="_top"> Query String Params</a>
         */
        LIST_JOINED_PRIVATE_ARCHIVED_THREADS(O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/users/@me/threads/archived/private", true),
        ;

        private final String link;
        private final boolean canHaveQueryData;

        Links(String link, boolean canHaveQueryData){
            this.link = link;
            this.canHaveQueryData = canHaveQueryData;
        }

        Links(String link){
            this(link, false);
        }

        /**
         * whether this link accepts query data for additional params
         */
        public boolean canHaveQueryData() {
            return canHaveQueryData;
        }

        public String getLink() {
            return link;
        }
    }

    private final LApi lApi;
    private final Links link;
    private final Data queryData;
    private final PlaceHolder[] placeHolders;

    public GetLinkQuery(@NotNull LApi lApi, @NotNull Links link, @Nullable Data queryData, PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.queryData = queryData;
        this.placeHolders = placeHolders;
    }

    public GetLinkQuery(@NotNull LApi lApi, @NotNull Links link, PlaceHolder... placeHolders){
        this(lApi, link, null, placeHolders);
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
        String uri = link.getLink();
        for(PlaceHolder p : placeHolders) uri = p.place(uri);

        if(queryData != null){
            return lApi.appendHeader(new LApiHttpRequest(uri, getMethod(), null, queryData));
        }

        return lApi.appendHeader(new LApiHttpRequest(uri, getMethod()));
    }
}
