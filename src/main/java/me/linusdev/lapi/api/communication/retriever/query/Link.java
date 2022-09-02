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

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.lapihttprequest.Method;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.invite.Invite;
import me.linusdev.lapi.api.objects.invite.InviteMetadata;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.communication.PlaceHolder.*;
import static me.linusdev.lapi.api.communication.lapihttprequest.Method.*;

/**
 * These are links to communicate with the official discord api.
 */
public enum Link implements AbstractLink{

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      Application Commands                     *
     *                                                               *
     *  Done: 02.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    GET_GLOBAL_APPLICATION_COMMANDS(GET, "applications/" + APPLICATION_ID + "/commands"),

    CREATE_GLOBAL_APPLICATION_COMMAND(POST, "applications/" + APPLICATION_ID + "/commands"),

    GET_GLOBAL_APPLICATION_COMMAND(GET, "applications/" + APPLICATION_ID + "/commands/" + COMMAND_ID),

    EDIT_GLOBAL_APPLICATION_COMMAND(PATCH, "applications/" + APPLICATION_ID + "/commands/" + COMMAND_ID),

    DELETE_GLOBAL_APPLICATION_COMMAND(DELETE, "applications/" + APPLICATION_ID + "/commands/" + COMMAND_ID),

    BULK_OVERWRITE_GLOBAL_APPLICATION_COMMANDS(PUT, "applications/" + APPLICATION_ID + "/commands"),

    GET_GUILD_APPLICATION_COMMANDS(GET, "applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands"),

    CREATE_GUILD_APPLICATION_COMMAND(POST, "applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands"),

    GET_GUILD_APPLICATION_COMMAND(GET, "applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/" + COMMAND_ID),

    EDIT_GUILD_APPLICATION_COMMAND(PATCH, "applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/" + COMMAND_ID),

    DELETE_GUILD_APPLICATION_COMMAND(DELETE, "applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/" + COMMAND_ID),

    BULK_OVERWRITE_GUILD_APPLICATION_COMMANDS(PUT, "/applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands"),

    GET_GUILD_APPLICATION_COMMAND_PERMISSIONS(GET, "/applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/permissions"),

    GET_APPLICATION_COMMAND_PERMISSIONS(GET, "/applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/" + COMMAND_ID + "/permissions"),

    EDIT_APPLICATION_COMMAND_PERMISSIONS(PUT, "/applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/" + COMMAND_ID + "/permissions"),

    /**
     * This endpoint has been disabled with updates to command permissions (Permissions v2).
     * Instead, you can edit each application command permissions (though you should be careful to handle any potential rate limits).
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#batch-edit-application-command-permissions" target="_top">Discord Documentation</a>
     */
    @Deprecated
    BATCH_EDIT_APPLICATION_COMMAND_PERMISSIONS(PUT, "/applications/" + APPLICATION_ID + "/guilds/" + GUILD_ID + "/commands/permissions"),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Receiving and Responding                    *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Create a response to an Interaction from the gateway. Body is an interaction response. Returns 204 No Content.<br>
     *
     * This endpoint also supports file attachments similar to the webhook endpoints. Refer to Uploading Files for details on uploading files and multipart/form-data requests.
     *
     * @see PlaceHolder#INTERACTION_ID
     * @see PlaceHolder#INTERACTION_TOKEN
     * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#create-interaction-response" target="_top">Create Interaction Response</a>
     */
    CREATE_INTERACTION_RESPONSE(POST, "interactions/" + INTERACTION_ID + "/"  + INTERACTION_TOKEN + "/callback"),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           Audit Log                           *
     *                                                               *
     *  Done: 02.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    GET_GUILD_AUDIT_LOG(GET, "guilds/" + GUILD_ID + "/audit-logs"),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                        Auto Moderation                        *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                            Channel                            *
     *                                                               *
     *  Done: 02.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Get a {@link me.linusdev.lapi.api.objects.channel.abstracts.Channel channel} by ID.
     * Returns a {@link me.linusdev.lapi.api.objects.channel.abstracts.Channel channel object}.
     * If the channel is a {@link me.linusdev.lapi.api.objects.channel.abstracts.Thread thread},
     * a {@link Thread#getMember() thread member} object is included in the returned result.
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel" target="_top">Get Channel</a>
     */
    GET_CHANNEL(GET, "channels/" + CHANNEL_ID),

    MODIFY_CHANNEL(PATCH, "channels/" + CHANNEL_ID),
    DELETE_CHANNEL(DELETE, "channels/" + CHANNEL_ID),

    /**
     * <p>
     * Returns the messages for a channel. If operating on a guild channel,
     * this endpoint requires the {@link Permission#VIEW_CHANNEL VIEW_CHANNEL}
     * permission to be present on the current user. If the current user is missing the
     * '{@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}' permission in the channel
     * then this will return no messages (since they cannot read the message history).
     * Returns an array of {@link MessageImplementation message objects} on success.
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
    GET_CHANNEL_MESSAGES(GET, "channels/" + CHANNEL_ID + "/messages"),

    /**
     * <p>
     *     Returns a specific message in the channel. If operating on a guild channel,
     *     this endpoint requires the '{@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}'
     *     permission to be present on the current user. Returns a {@link MessageImplementation message object} on success.
     * </p>
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see PlaceHolder#MESSAGE_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-message" target="_top">Get Channel Message</a>
     */
    GET_CHANNEL_MESSAGE(GET, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID),

    /**
     * Post a message to a guild text or DM channel. Returns a {@link MessageImplementation message} object.
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#create-message" target="_top">Create Message</a>
     */
    CREATE_MESSAGE(POST, "channels/" + CHANNEL_ID + "/messages"),

    CROSSPOST_MESSAGE(POST, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/crosspost"),

    CREATE_REACTION(PUT, "channels/" +  CHANNEL_ID + "/messages/" +  MESSAGE_ID + "/reactions/" + EMOJI + "/@me"),

    DELETE_OWN_REACTION(DELETE, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/reactions/" + EMOJI + "/@me"),

    DELETE_USER_REACTION(DELETE, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/reactions/" + EMOJI + "/{user.id}"),

    /**
     * <p>
     *     Get a list of users that reacted with this emoji. Returns an array of
     *     {@link me.linusdev.lapi.api.objects.user.User user objects} on success.
     *     The emoji must be <a href="https://en.wikipedia.org/wiki/Percent-encoding" target="_top">URL Encoded</a>
     *     or the request will fail with 10014: Unknown Emoji. To use custom emoji,
     *     you must encode it in the format name:id with the emoji name and emoji id.
     * </p>
     * <br>
     *
     * <p style="margin-bottom:0;padding-bottom:0;">
     *     This can have <a href="https://discord.com/developers/docs/resources/channel#get-reactions-query-string-params" target="_top">Query String parameters</a>
     * </p>
     * <ul style="margin-bottom:0;padding-bottom:0;margin-top:0;padding-top:0;">
     *      <li>
     *          {@link #AFTER_KEY} get users after this user ID. Default: absent
     *      </li>
     *      <li>
     *          {@link #LIMIT_KEY} max number of users to return (1-100). Default: 25
     *      </li>
     * </ul>
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see PlaceHolder#MESSAGE_ID
     * @see PlaceHolder#EMOJI
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-reactions" target="_top">Get Reactions</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-reactions-query-string-params" target="_top">Query String Params</a>
     */
    GET_REACTIONS(GET, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/reactions/" + EMOJI),

    DELETE_ALL_REACTIONS(DELETE, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/reactions"),

    DELETE_ALL_REACTIONS_FOR_EMOJI(DELETE, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/reactions/" + EMOJI),

    EDIT_MESSAGE(PATCH, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID),

    DELETE_MESSAGE(DELETE, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID),

    BULK_DELETE_MESSAGES(POST, "channels/" + CHANNEL_ID + "/messages/bulk-delete"),

    EDIT_CHANNEL_PERMISSIONS(PUT, "channels/" + CHANNEL_ID + "/permissions/" + OVERWRITE_ID),

    /**
     * <p>
     *     Returns a list of {@link Invite invite objects}
     *     (with {@link InviteMetadata invite metadata}) for the channel.
     *     Only usable for guild channels. Requires the
     *     {@link Permission#MANAGE_CHANNELS MANAGE_CHANNELS}
     *     permission.
     * </p>
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-invites" target="_top">Get Channel Invites</a>
     */
    GET_CHANNEL_INVITES(GET, "channels/" + CHANNEL_ID + "/invites"),

    CREATE_CHANNEL_INVITE(POST, "channels/" +  CHANNEL_ID + "/invites"),

    DELETE_CHANNEL_PERMISSION(DELETE, "channels/" + CHANNEL_ID + "/permissions/" + OVERWRITE_ID),

    FOLLOW_ANNOUNCEMENT_CHANNEL(POST, "channels/" + CHANNEL_ID + "/followers"),

    TRIGGER_TYPING_INDICATOR(POST, "channels/" + CHANNEL_ID + "/typing"),

    /**
     * <p>
     *     Returns all pinned messages in the channel as an array of {@link MessageImplementation message} objects.
     * </p>
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-pinned-messages" target="_top">Get Pinned Messages</a>
     */
    GET_PINNED_MESSAGES(GET, "channels/" + CHANNEL_ID + "/pins"),

    PIN_MESSAGE(PUT, "channels/" + CHANNEL_ID + "/pins/" + MESSAGE_ID),

    UNPIN_MESSAGE(DELETE, "channels/" + CHANNEL_ID + "/pins/" + MESSAGE_ID),

    GROUP_DM_ADD_RECIPIENT(PUT, "channels/" + CHANNEL_ID + "/recipients/" + USER_ID),

    GROUP_DM_REMOVE_RECIPIENT(DELETE, "channels/" + CHANNEL_ID + "/recipients/" + USER_ID),

    START_THREAD_FROM_MESSAGE(POST, "channels/" + CHANNEL_ID + "/messages/" + MESSAGE_ID + "/threads"),

    START_THREAD_WITHOUT_MESSAGE(POST, "channels/" + CHANNEL_ID + "/threads"),

    START_THREAD_IN_FORUM_CHANNEL(POST, "channels/" + CHANNEL_ID + "/threads"),

    JOIN_THREAD(PUT, "channels/" + CHANNEL_ID + "/thread-members/@me"),

    ADD_THREAD_MEMBER(PUT, "channels/" + CHANNEL_ID + "/thread-members/" + USER_ID),

    LEAVE_THREAD(DELETE, "channels/" + CHANNEL_ID + "/thread-members/@me"),

    REMOVE_THREAD_MEMBER(DELETE, "channels/" + CHANNEL_ID + "/thread-members/" + USER_ID),

    /**
     * <p>
     *     Returns a {@link ThreadMember thread member} object
     *     for the specified user if they are a member of the thread, returns a 404 response otherwise.
     * </p>
     * @see PlaceHolder#CHANNEL_ID
     * @see PlaceHolder#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-thread-member" target="_top">Get Thread Member</a>
     */
    GET_THREAD_MEMBER(GET, "channels/" + CHANNEL_ID + "/thread-members/" + USER_ID),

    /**
     * <p>
     *     Returns array of thread members objects that are members of the thread.
     * </p>
     * <p>
     *     This endpoint is restricted according to whether the {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS}
     *     Privileged GatewayIntent is enabled for your application.
     * </p>
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-thread-members" target="_top">List Thread Members</a>
     */
    LIST_THREAD_MEMBERS(GET, "channels/" + CHANNEL_ID + "/thread-members"),

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
    LIST_ACTIVE_THREADS(GET, "channels/" + CHANNEL_ID + "/threads/active"),

    /**
     * <p>
     *     Returns archived threads in the channel that are public.
     *     When called on a {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_NEWS GUILD_NEWS} channel returns
     *     threads of {@link Channel#getType() type} {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_NEWS_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
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
    LIST_PUBLIC_ARCHIVED_THREADS(GET, "channels/" + CHANNEL_ID + "/threads/archived/public"),

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link Permission#MANAGE_THREADS MANAGE_THREADS} permissions.
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
    LIST_PRIVATE_ARCHIVED_THREADS(GET, "channels/" + CHANNEL_ID + "/threads/archived/private"),

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
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
    LIST_JOINED_PRIVATE_ARCHIVED_THREADS(GET, "channels/" + CHANNEL_ID + "/users/@me/threads/archived/private"),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             Emoji                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             Other                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

















    /*
     *
     * User https://discord.com/developers/docs/resources/user#users-resource
     *
     */

    /**
     * <p>
     *     Returns the {@link me.linusdev.lapi.api.objects.user.User user object} of the requester's account. For OAuth2, this requires the identify scope,
     *     which will return the object without an email, and optionally the email scope,
     *     which returns the object with an email.
     * </p>
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-current-user" target="_top">Get Current User</a>
     */
    GET_CURRENT_USER(GET, "users/@me"),

    /**
     * <p>
     *     Returns a {@link me.linusdev.lapi.api.objects.user.User user object} for a given user ID.
     * </p>
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-user" target="_top">Get User</a>
     * @see PlaceHolder#USER_ID
     */
    GET_USER(GET, "users/" + USER_ID),

    /**
     * Returns a list of partial {@link Guild guild} objects the current user is a member of. Requires the guilds OAuth2 scope.
     * <br><br>
     * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">
     *     This can have Query String parameters:<br>
     * </span>
     * <ul style="margin:0;padding:0">
     *     <li>
     *         {@value #BEFORE_KEY}: get guilds before this guild ID
     *     </li>
     *     <li>
     *         {@value #AFTER_KEY}: get guilds after this guild ID
     *     </li>
     *     <li>
     *         {@value #LIMIT_KEY}: max number of guilds to return (1-200). Default: 200
     *     </li>
     * </ul>
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-current-user-guilds" target="_top">Get Current User Guilds</a>
     * @see <a href="https://discord.com/developers/docs/resources/user#get-current-user-guilds-query-string-params" target="_top">Query String Params</a>
     */
    GET_CURRENT_USER_GUILDS(GET, "users/@me/guilds"),

    /**
     * Returns a list of {@link me.linusdev.lapi.api.objects.user.connection.Connection connection}
     * objects. Requires the connections OAuth2 scope.
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-user-connections" target="_top">Get User Connections</a>
     */
    GET_CURRENT_USER_CONNECTIONS(GET, "users/@me/connections"),




    /**
     * Returns an object with a single valid WSS URL, which the client can use for Connecting.
     * Clients should cache this value and only call this endpoint to retrieve a new URL if
     * they are unable to properly establish a connection using the cached version of the URL.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway" target="_top">Get Gateway</a>
     */
    GET_GATEWAY(GET, "gateway"),

    /**
     * Returns an object based on the information in Get Gateway,
     * plus additional metadata that can help during the operation of large or sharded bots.
     * Unlike the Get Gateway, this route should not be cached for extended periods of time
     * as the value is not guaranteed to be the same per-call, and changes as the bot joins/leaves
     * guilds.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway-bot" target="_top">Get Gateway Bot</a>
     */
    GET_GATEWAY_BOT(GET, "gateway/bot"),

    /**
     * Returns an array of {@link me.linusdev.lapi.api.objects.voice.region.VoiceRegion voice region objects}
     * that can be used when setting a voice or stage channel's rtc_region.
     *
     * @see <a href="https://discord.com/developers/docs/resources/voice#list-voice-regions" target="_top">List Voice Regions</a>
     */
    GET_VOICE_REGIONS(GET, "voice/regions"),
    ;

    private final @NotNull Method method;
    private final @NotNull String link;

    Link(@NotNull Method method, @NotNull String link){
        this.method = method;
        this.link = link;
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @NotNull String getLink(@NotNull ApiVersion apiVersion) {
        return link.replace(DISCORD_API_VERSION_NUMBER, apiVersion.getVersionNumber());
    }
}
