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

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.other.placeholder.Concatable;
import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.http.request.Method;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.invite.Invite;
import me.linusdev.lapi.api.objects.invite.InviteMetadata;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.communication.retriever.query.LinkPart.*;
import static me.linusdev.lapi.api.other.placeholder.Name.*;

/**
 * These are links to communicate with the official discord api.
 */
public enum Link implements AbstractLink{

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                             Application Commands                                          *
     *                                                                                                           *
     *  Done: 02.09.2022                                                                                         *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * <p>
     * Fetch all of the global commands for your application. Returns an array of
     * {@link ApplicationCommand application command} objects.
     * </p>
     * <br>
     * <p style="margin-bottom:0;padding-bottom:0;">
     *     This can have Query String parameters:
     * </p>
     * <ul style="margin-bottom:0;padding-bottom:0;margin-top:0;padding-top:0;">
     *     <li>
     *         {@link RequestFactory#WITH_LOCALIZATIONS_KEY WITH_LOCALIZATIONS_KEY} Whether to include
     *         full localization dictionaries
     *     </li>
     * </ul>
     * <p style="margin-top:0;padding-top:0;">
     *
     * </p>
     *
     * @see Name#APPLICATION_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#get-global-application-commands" target="_top">Discord Documentation</a>
     */
    GET_GLOBAL_APPLICATION_COMMANDS(Method.GET, APPLICATIONS, APPLICATION_ID, COMMANDS),

    /**
     * <p>
     *     Create a new global command. Returns 201 and an {@link ApplicationCommand application command object}.
     * </p>
     * <p>
     *     Creating a command with the same name as an existing command for your application will overwrite the old command.
     * </p>
     * <p>
     *     This can have json params. see {@link ApplicationCommandTemplate}.
     * </p>
     *
     * @see Name#APPLICATION_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#create-global-application-command" target="_top">Discord Documentation</a>
     */
    CREATE_GLOBAL_APPLICATION_COMMAND(Method.POST, APPLICATIONS, APPLICATION_ID, COMMANDS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#get-global-application-command" target="_top">Discord Documentation</a>
     */
    GET_GLOBAL_APPLICATION_COMMAND(Method.GET, APPLICATIONS, APPLICATION_ID, COMMANDS, COMMAND_ID),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#edit-global-application-command" target="_top">Discord Documentation</a>
     */
    EDIT_GLOBAL_APPLICATION_COMMAND(Method.PATCH, APPLICATIONS, APPLICATION_ID, COMMANDS, COMMAND_ID),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#delete-global-application-command" target="_top">Discord Documentation</a>
     */
    DELETE_GLOBAL_APPLICATION_COMMAND(Method.DELETE, APPLICATIONS, APPLICATION_ID, COMMANDS, COMMAND_ID),

    /**
     * @see Name#APPLICATION_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#bulk-overwrite-global-application-commands" target="_top">Discord Documentation</a>
     */
    BULK_OVERWRITE_GLOBAL_APPLICATION_COMMANDS(Method.PUT, APPLICATIONS, APPLICATION_ID, COMMANDS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#get-guild-application-commands" target="_top">Discord Documentation</a>
     */
    GET_GUILD_APPLICATION_COMMANDS(Method.GET, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#create-guild-application-command" target="_top">Discord Documentation</a>
     */
    CREATE_GUILD_APPLICATION_COMMAND(Method.POST, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#get-guild-application-command" target="_top">Discord Documentation</a>
     */
    GET_GUILD_APPLICATION_COMMAND(Method.GET, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, COMMAND_ID),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#edit-guild-application-command" target="_top">Discord Documentation</a>
     */
    EDIT_GUILD_APPLICATION_COMMAND(Method.PATCH, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, COMMAND_ID),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#delete-guild-application-command" target="_top">Discord Documentation</a>
     */
    DELETE_GUILD_APPLICATION_COMMAND(Method.DELETE, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, COMMAND_ID),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#bulk-overwrite-guild-application-commands" target="_top">Discord Documentation</a>
     */
    BULK_OVERWRITE_GUILD_APPLICATION_COMMANDS(Method.PUT, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#get-guild-application-command-permissions" target="_top">Discord Documentation</a>
     */
    GET_GUILD_APPLICATION_COMMAND_PERMISSIONS(Method.GET, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, PERMISSIONS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#get-application-command-permissions" target="_top">Discord Documentation</a>
     */
    GET_APPLICATION_COMMAND_PERMISSIONS(Method.GET, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, COMMAND_ID, PERMISSIONS),

    /**
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see Name#COMMAND_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#edit-application-command-permissions" target="_top">Discord Documentation</a>
     */
    EDIT_APPLICATION_COMMAND_PERMISSIONS(Method.PUT, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, COMMAND_ID, PERMISSIONS),

    /**
     * This endpoint has been disabled with updates to command permissions (Permissions v2).
     * Instead, you can edit each application command permissions (though you should be careful to handle any potential rate limits).
     *
     * @see Name#APPLICATION_ID
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/interactions/application-commands#batch-edit-application-command-permissions" target="_top">Discord Documentation</a>
     */
    @Deprecated
    BATCH_EDIT_APPLICATION_COMMAND_PERMISSIONS(Method.PUT, APPLICATIONS, APPLICATION_ID, GUILDS, GUILD_ID, COMMANDS, PERMISSIONS),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                           Receiving and Responding                                        *
     *                                                                                                           *
     *  The Interaction endpoints below are not bound to the application's Global Rate Limit.                    *
     *  see https://discord.com/developers/docs/interactions/receiving-and-responding#endpoints                  *
     *                                                                                                           *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Create a response to an Interaction from the gateway. Body is an interaction response. Returns 204 No Content.<br>
     *
     * This endpoint also supports file attachments similar to the webhook endpoints. Refer to Uploading Files for details on uploading files and multipart/form-data requests.
     *
     * @see Name#INTERACTION_ID
     * @see Name#INTERACTION_TOKEN
     * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#create-interaction-response" target="_top">Create Interaction Response</a>
     */
    CREATE_INTERACTION_RESPONSE(Method.POST, false, INTERACTIONS, INTERACTION_ID, INTERACTION_TOKEN, CALLBACK),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                   Audit Log                                               *
     *                                                                                                           *
     *  Done:       02.09.2022                                                                                   *
     *  Updated:    06.10.2022                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * <p>
     *     Returns an audit log object for the guild. Requires the {@link Permission#VIEW_AUDIT_LOG VIEW_AUDIT_LOG} permission.
     * </p>
     *
     * @see Name#GUILD_ID
     */
    GET_GUILD_AUDIT_LOG(Method.GET, GUILDS, GUILD_ID, AUDIT_LOGS),


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                Auto Moderation                                            *
     *                                                                                                           *
     *  Done:       06.10.2022                                                                                   *
     *  Updated:    06.10.2022                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/auto-moderation#list-auto-moderation-rules-for-guild">Discord Documentation</a>
     */
    LIST_AUTO_MODERATION_RULES_FOR_GUILD(Method.GET, GUILDS, GUILD_ID, AUTO_MODERATION, RULES),

    /**
     * @see Name#GUILD_ID
     * @see Name#AUTO_MODERATION_RULE_ID
     * @see <a href="https://discord.com/developers/docs/resources/auto-moderation#get-auto-moderation-rule">Discord Documentation</a>
     */
    GET_AUTO_MODERATION_RULE(Method.GET, GUILDS, GUILD_ID, AUTO_MODERATION, RULES, AUTO_MODERATION_RULE_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/auto-moderation#create-auto-moderation-rule">Discord Documentation</a>
     */
    CREATE_AUTO_MODERATION_RULE(Method.POST, GUILDS, GUILD_ID, AUTO_MODERATION, RULES),

    /**
     * @see Name#GUILD_ID
     * @see Name#AUTO_MODERATION_RULE_ID
     * @see <a href="https://discord.com/developers/docs/resources/auto-moderation#modify-auto-moderation-rule">Discord Documentation</a>
     */
    MODIFY_AUTO_MODERATION_RULE(Method.PATCH, GUILDS, GUILD_ID, AUTO_MODERATION, RULES, AUTO_MODERATION_RULE_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#AUTO_MODERATION_RULE_ID
     * @see <a href="https://discord.com/developers/docs/resources/auto-moderation#delete-auto-moderation-rule">Discord Documentation</a>
     */
    DELETE_AUTO_MODERATION_RULE(Method.DELETE, GUILDS, GUILD_ID, AUTO_MODERATION, RULES, AUTO_MODERATION_RULE_ID),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                    Channel                                                *
     *                                                                                                           *
     *  Done:       02.09.2022                                                                                   *
     *  Updated:                                                                                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Get a {@link me.linusdev.lapi.api.objects.channel.abstracts.Channel channel} by ID.
     * Returns a {@link me.linusdev.lapi.api.objects.channel.abstracts.Channel channel object}.
     * If the channel is a {@link me.linusdev.lapi.api.objects.channel.abstracts.Thread thread},
     * a {@link Thread#getMember() thread member} object is included in the returned result.
     *
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel" target="_top">Get Channel</a>
     */
    GET_CHANNEL(Method.GET, CHANNELS, CHANNEL_ID),

    MODIFY_CHANNEL(Method.PATCH, CHANNELS, CHANNEL_ID),
    DELETE_CHANNEL(Method.DELETE, CHANNELS, CHANNEL_ID),

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
     *         {@link RequestFactory#AROUND_KEY AROUND_KEY} get messages around this message ID
     *     </li>
     *     <li>
     *         {@link RequestFactory#BEFORE_KEY BEFORE_KEY} get messages before this message ID
     *     </li>
     *     <li>
     *         {@link RequestFactory#AFTER_KEY AFTER_KEY} get messages after this message ID
     *     </li>
     *     <li>
     *         {@link RequestFactory#LIMIT_KEY LIMIT_KEY} max number of messages to return (1-100). Default: 50
     *     </li>
     * </ul>
     * <p style="margin-top:0;padding-top:0;">
     *    The before, after, and around keys are mutually exclusive, only one may be passed at a time.
     * </p>
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages" target="_top">Get Channel Messages</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-messages-query-string-params" target="_top">Query String Params</a>
     */
    GET_CHANNEL_MESSAGES(Method.GET, CHANNELS, CHANNEL_ID, MESSAGES),

    /**
     * <p>
     *     Returns a specific message in the channel. If operating on a guild channel,
     *     this endpoint requires the '{@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY}'
     *     permission to be present on the current user. Returns a {@link MessageImplementation message object} on success.
     * </p>
     *
     * @see Name#CHANNEL_ID
     * @see Name#MESSAGE_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-message" target="_top">Get Channel Message</a>
     */
    GET_CHANNEL_MESSAGE(Method.GET,  CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID),

    /**
     * Post a message to a guild text or DM channel. Returns a {@link MessageImplementation message} object.
     *
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#create-message" target="_top">Create Message</a>
     */
    CREATE_MESSAGE(Method.POST,  CHANNELS, CHANNEL_ID, MESSAGES),

    CROSSPOST_MESSAGE(Method.POST,  CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, CROSSPOST),

    CREATE_REACTION(Method.PUT,  CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, REACTIONS, EMOJI, ME),

    DELETE_OWN_REACTION(Method.DELETE,  CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, REACTIONS, EMOJI, ME),

    DELETE_USER_REACTION(Method.DELETE, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, REACTIONS, EMOJI, USER_ID),

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
     *          {@link RequestFactory#AFTER_KEY} get users after this user ID. Default: absent
     *      </li>
     *      <li>
     *          {@link RequestFactory#LIMIT_KEY} max number of users to return (1-100). Default: 25
     *      </li>
     * </ul>
     *
     * @see Name#CHANNEL_ID
     * @see Name#MESSAGE_ID
     * @see Name#EMOJI
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-reactions" target="_top">Get Reactions</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-reactions-query-string-params" target="_top">Query String Params</a>
     */
    GET_REACTIONS(Method.GET, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, REACTIONS, EMOJI),

    DELETE_ALL_REACTIONS(Method.DELETE, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, REACTIONS),

    DELETE_ALL_REACTIONS_FOR_EMOJI(Method.DELETE, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, REACTIONS, EMOJI),

    EDIT_MESSAGE(Method.PATCH, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID),

    DELETE_MESSAGE(Method.DELETE, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID),

    BULK_DELETE_MESSAGES(Method.POST, CHANNELS, CHANNEL_ID, MESSAGES, BULK_DELETE),

    EDIT_CHANNEL_PERMISSIONS(Method.PUT, CHANNELS, CHANNEL_ID, PERMISSIONS, OVERWRITE_ID),

    /**
     * <p>
     *     Returns a list of {@link Invite invite objects}
     *     (with {@link InviteMetadata invite metadata}) for the channel.
     *     Only usable for guild channels. Requires the
     *     {@link Permission#MANAGE_CHANNELS MANAGE_CHANNELS}
     *     permission.
     * </p>
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-channel-invites" target="_top">Get Channel Invites</a>
     */
    GET_CHANNEL_INVITES(Method.GET, CHANNELS, CHANNEL_ID, INVITES),

    CREATE_CHANNEL_INVITE(Method.POST, CHANNELS, CHANNEL_ID, INVITES),

    DELETE_CHANNEL_PERMISSION(Method.DELETE, CHANNELS, CHANNEL_ID, PERMISSIONS, OVERWRITE_ID),

    FOLLOW_ANNOUNCEMENT_CHANNEL(Method.POST, CHANNELS, CHANNEL_ID, FOLLOWERS),

    TRIGGER_TYPING_INDICATOR(Method.POST, CHANNELS, CHANNEL_ID, TYPING),

    /**
     * <p>
     *     Returns all pinned messages in the channel as an array of {@link MessageImplementation message} objects.
     * </p>
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-pinned-messages" target="_top">Get Pinned Messages</a>
     */
    GET_PINNED_MESSAGES(Method.GET, CHANNELS, CHANNEL_ID, PINS),

    PIN_MESSAGE(Method.PUT, CHANNELS, CHANNEL_ID, PINS, MESSAGE_ID),

    UNPIN_MESSAGE(Method.DELETE, CHANNELS, CHANNEL_ID, PINS, MESSAGE_ID),

    GROUP_DM_ADD_RECIPIENT(Method.PUT, CHANNELS, CHANNEL_ID, RECIPIENTS, USER_ID),

    GROUP_DM_REMOVE_RECIPIENT(Method.DELETE, CHANNELS, CHANNEL_ID, RECIPIENTS, USER_ID),

    START_THREAD_FROM_MESSAGE(Method.POST, CHANNELS, CHANNEL_ID, MESSAGES, MESSAGE_ID, THREADS),

    START_THREAD_WITHOUT_MESSAGE(Method.POST, CHANNELS, CHANNEL_ID, THREADS),

    START_THREAD_IN_FORUM_CHANNEL(Method.POST, CHANNELS, CHANNEL_ID, THREADS),

    JOIN_THREAD(Method.PUT, CHANNELS, CHANNEL_ID, THREAD_MEMBERS, ME),

    ADD_THREAD_MEMBER(Method.PUT, CHANNELS, CHANNEL_ID, THREAD_MEMBERS, USER_ID),

    LEAVE_THREAD(Method.DELETE, CHANNELS, CHANNEL_ID, THREAD_MEMBERS, ME),

    REMOVE_THREAD_MEMBER(Method.DELETE, CHANNELS, CHANNEL_ID, THREAD_MEMBERS, USER_ID),

    /**
     * <p>
     *     Returns a {@link ThreadMember thread member} object
     *     for the specified user if they are a member of the thread, returns a 404 response otherwise.
     * </p>
     * @see Name#CHANNEL_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#get-thread-member" target="_top">Get Thread Member</a>
     */
    GET_THREAD_MEMBER(Method.GET, CHANNELS, CHANNEL_ID, THREAD_MEMBERS, USER_ID),

    /**
     * <p>
     *     Returns array of thread members objects that are members of the thread.
     * </p>
     * <p>
     *     This endpoint is restricted according to whether the {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS}
     *     Privileged GatewayIntent is enabled for your application.
     * </p>
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-thread-members" target="_top">List Thread Members</a>
     */
    LIST_THREAD_MEMBERS(Method.GET, CHANNELS, CHANNEL_ID, THREAD_MEMBERS),

    /**
     * <p>
     *     Returns all active threads in the channel, including public and private threads. Threads are ordered by their id, in descending order.
     * </p>
     * <p>
     *     This route is deprecated and will be removed in v10. It is replaced by List Active Guild Threads.
     * </p>
     * TODO add LIST_ACTIVE_GUILD_THREADS @link
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-active-threads" target="_top">List Active Threads</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-active-threads-response-body" target="_top">Response Body</a>
     */
    @Deprecated(since = "v10", forRemoval = true)
    LIST_ACTIVE_THREADS(Method.GET, CHANNELS, CHANNEL_ID, THREADS, ACTIVE),

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
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads" target="_top"> List Public Archived Threads</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads-response-body" target="_top"> Response Body</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads-query-string-params" target="_top"> Query String Params</a>
     */
    LIST_PUBLIC_ARCHIVED_THREADS(Method.GET, CHANNELS, CHANNEL_ID, THREADS, ARCHIVED, PUBLIC),

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
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads" target="_top"> List Private Archived Threads</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads-response-body" target="_top"> Response Body</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-private-archived-threads-query-string-params" target="_top"> Query String Params</a>
     */
    LIST_PRIVATE_ARCHIVED_THREADS(Method.GET, CHANNELS, CHANNEL_ID, THREADS, ARCHIVED, PRIVATE),

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
     * @see Name#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads" target="_top"> List Private Archived Threads</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads-response-body" target="_top"> Response Body</a>
     * @see <a href="https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads-query-string-params" target="_top"> Query String Params</a>
     */
    LIST_JOINED_PRIVATE_ARCHIVED_THREADS(Method.GET, CHANNELS,  CHANNEL_ID, USERS, ME, THREADS, ARCHIVED, PRIVATE),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                     Emoji                                                 *
     *                                                                                                           *
     *  Done:       07.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/emoji#list-guild-emojis">Discord Documentation</a>
     */
    LIST_GUILD_EMOJIS(Method.GET, GUILDS, GUILD_ID, EMOJIS),

    /**
     * @see Name#GUILD_ID
     * @see Name#EMOJI_ID
     * @see <a href="https://discord.com/developers/docs/resources/emoji#get-guild-emoji">Discord Documentation</a>
     */
    GET_GUILD_EMOJI(Method.GET, GUILDS, GUILD_ID, EMOJIS, EMOJI_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/emoji#create-guild-emoji">Discord Documentation</a>
     */
    CREATE_GUILD_EMOJI(Method.GET, GUILDS, GUILD_ID, EMOJIS),

    /**
     * @see Name#GUILD_ID
     * @see Name#EMOJI_ID
     * @see <a href="https://discord.com/developers/docs/resources/emoji#modify-guild-emoji">Discord Documentation</a>
     */
    MODIFY_GUILD_EMOJI(Method.PATCH, GUILDS, GUILD_ID, EMOJIS, EMOJI_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#EMOJI_ID
     * @see <a href="https://discord.com/developers/docs/resources/emoji#delete-guild-emoji">Discord Documentation</a>
     */
    DELETE_GUILD_EMOJI(Method.DELETE, GUILDS, GUILD_ID, EMOJIS, EMOJI_ID),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                     Guild                                                 *
     *                                                                                                           *
     *  Done:       07.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * @see <a href="https://discord.com/developers/docs/resources/guild#create-guild">Discord Documentation</a>
     */
    CREATE_GUILD(Method.POST, GUILDS),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild">Discord Documentation</a>
     */
    GET_GUILD(Method.GET, GUILDS, GUILD_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-preview">Discord Documentation</a>
     */
    GET_GUILD_PREVIEW(Method.GET, GUILDS, GUILD_ID, PREVIEW),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild">Discord Documentation</a>
     */
    MODIFY_GUILD(Method.PATCH, true, true, GUILDS, GUILD_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#delete-guild">Discord Documentation</a>
     */
    DELETE_GUILD(Method.DELETE, GUILDS, GUILD_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-channels">Discord Documentation</a>
     */
    GET_GUILD_CHANNELS(Method.GET, GUILDS, GUILD_ID, CHANNELS),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#create-guild-channel">Discord Documentation</a>
     */
    CREATE_GUILD_CHANNEL(Method.POST, true, true, GUILDS, GUILD_ID, CHANNELS),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-channel-positions">Discord Documentation</a>
     */
    MODIFY_GUILD_CHANNEL_POSITIONS(Method.PATCH, GUILDS, GUILD_ID, CHANNELS),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#list-active-guild-threads">Discord Documentation</a>
     */
    LIST_ACTIVE_GUILD_THREADS(Method.GET, GUILDS, GUILD_ID, THREADS, ACTIVE),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-member">Discord Documentation</a>
     */
    GET_GUILD_MEMBER(Method.GET, GUILDS, GUILD_ID, MEMBERS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#list-guild-members">Discord Documentation</a>
     */
    LIST_GUILD_MEMBERS(Method.GET, GUILDS, GUILD_ID, MEMBERS),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#search-guild-members">Discord Documentation</a>
     */
    SEARCH_GUILD_MEMBERS(Method.GET, GUILDS, GUILD_ID, MEMBERS, SEARCH),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#add-guild-member">Discord Documentation</a>
     */
    ADD_GUILD_MEMBER(Method.PUT, GUILDS, GUILD_ID, MEMBERS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-member">Discord Documentation</a>
     */
    MODIFY_GUILD_MEMBER(Method.PATCH, true, true, GUILDS, GUILD_ID, MEMBERS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-current-member">Discord Documentation</a>
     */
    MODIFY_CURRENT_MEMBER(Method.PATCH, true, true, GUILDS, GUILD_ID, MEMBERS, ME),

    /**
     * @deprecated in favor of {@link #MODIFY_CURRENT_MEMBER}
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-current-user-nick">Discord Documentation</a>
     */
    @Deprecated()
    MODIFY_CURRENT_USER_NICK(Method.PATCH, true, true, GUILDS, GUILD_ID, MEMBERS, ME, NICK),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see Name#ROLE_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#add-guild-member-role">Discord Documentation</a>
     */
    ADD_GUILD_MEMBER_ROLE(Method.PUT, true, true, GUILDS, GUILD_ID, MEMBERS, USER_ID, ROLES, ROLE_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see Name#ROLE_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#remove-guild-member-role">Discord Documentation</a>
     */
    REMOVE_GUILD_MEMBER_ROLE(Method.DELETE, true, true, GUILDS, GUILD_ID, MEMBERS, USER_ID, ROLES, ROLE_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#remove-guild-member">Discord Documentation</a>
     */
    REMOVE_GUILD_MEMBER(Method.DELETE, true, true, GUILDS, GUILD_ID, MEMBERS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-bans">Discord Documentation</a>
     */
    GET_GUILD_BANS(Method.GET, GUILDS, GUILD_ID, BANS),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-ban">Discord Documentation</a>
     */
    GET_GUILD_BAN(Method.GET, GUILDS, GUILD_ID, BANS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#create-guild-ban">Discord Documentation</a>
     */
    CREATE_GUILD_BAN(Method.PUT, true, true, GUILDS, GUILD_ID, BANS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#remove-guild-ban">Discord Documentation</a>
     */
    REMOVE_GUILD_BAN(Method.DELETE, true, true, GUILDS, GUILD_ID, BANS, USER_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-roles">Discord Documentation</a>
     */
    GET_GUILD_ROLES(Method.GET, GUILDS, GUILD_ID, ROLES),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#create-guild-role">Discord Documentation</a>
     */
    CREATE_GUILD_ROLES(Method.POST, true, true, GUILDS, GUILD_ID, ROLES),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-role-positions">Discord Documentation</a>
     */
    MODIFY_GUILD_ROLE_POSITIONS(Method.PATCH, true, true, GUILDS, GUILD_ID, ROLES),

    /**
     * @see Name#GUILD_ID
     * @see Name#ROLE_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-role-positions">Discord Documentation</a>
     */
    MODIFY_GUILD_ROLE(Method.PATCH, true, true, GUILDS, GUILD_ID, ROLES, ROLE_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-mfa-level">Discord Documentation</a>
     */
    MODIFY_GUILD_MFA_LEVEL(Method.POST, true, true, GUILDS, GUILD_ID, MFA),

    /**
     * @see Name#GUILD_ID
     * @see Name#ROLE_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#delete-guild-role">Discord Documentation</a>
     */
    DELETE_GUILD_ROLE(Method.DELETE, true, true, GUILDS, GUILD_ID, ROLES, ROLE_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-prune-count">Discord Documentation</a>
     */
    GET_GUILD_PRUNE_COUNT(Method.GET, GUILDS, GUILD_ID, PRUNE),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#begin-guild-prune">Discord Documentation</a>
     */
    BEGIN_GUILD_PRUNE(Method.POST, true, true, GUILDS, GUILD_ID, PRUNE),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-voice-regions">Discord Documentation</a>
     */
    GET_GUILD_VOICE_REGIONS(Method.GET, GUILDS, GUILD_ID, REGIONS),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-invites">Discord Documentation</a>
     */
    GET_GUILD_INVITES(Method.GET, GUILDS, GUILD_ID, INVITES),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-integrations">Discord Documentation</a>
     */
    GET_GUILD_INTEGRATIONS(Method.GET, GUILDS, GUILD_ID, INTEGRATIONS),

    /**
     * @see Name#GUILD_ID
     * @see Name#INTEGRATION_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#delete-guild-integration">Discord Documentation</a>
     */
    DELETE_GUILD_INTEGRATION(Method.DELETE, true, true, GUILDS, GUILD_ID, INTEGRATIONS, INTEGRATION_ID),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-widget-settings">Discord Documentation</a>
     */
    GET_GUILD_WIDGET_SETTINGS(Method.GET, GUILDS, GUILD_ID, WIDGET),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-widget">Discord Documentation</a>
     */
    MODIFY_GUILD_WIDGET(Method.PATCH, true, true, GUILDS, GUILD_ID, WIDGET),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-widget">Discord Documentation</a>
     */
    GET_GUILD_WIDGET(Method.GET, GUILDS, GUILD_ID, WIDGET_JSON),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-vanity-url">Discord Documentation</a>
     */
    GET_GUILD_VANITY_URL(Method.GET, GUILDS, GUILD_ID, VANITY_URL),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-widget-image">Discord Documentation</a>
     */
    GET_GUILD_WIDGET_IMAGE(Method.GET, GUILDS, GUILD_ID, WIDGET_PNG),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-welcome-screen">Discord Documentation</a>
     */
    GET_GUILD_WELCOME_SCREEN(Method.GET, GUILDS, GUILD_ID, WELCOME_SCREEN),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild-welcome-screen">Discord Documentation</a>
     */
    MODIFY_GUILD_WELCOME_SCREEN(Method.PATCH, true, true, GUILDS, GUILD_ID, WELCOME_SCREEN),

    /**
     * @see Name#GUILD_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-current-user-voice-state">Discord Documentation</a>
     */
    MODIFY_CURRENT_USER_VOICE_STATE(Method.PATCH, GUILDS, GUILD_ID, VOICE_STATES, ME),

    /**
     * @see Name#GUILD_ID
     * @see Name#USER_ID
     * @see <a href="https://discord.com/developers/docs/resources/guild#modify-user-voice-state">Discord Documentation</a>
     */
    MODIFY_USER_VOICE_STATE(Method.PATCH, GUILDS, GUILD_ID, VOICE_STATES, USER_ID),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                              Guild Scheduled Event                                        *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                   Guild Template                                          *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                     Invite                                                *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                 Stage Instance                                            *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                     Sticker                                               *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                      User                                                 *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                      Voice                                                *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                    Webhook                                                *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                    Gateway                                                *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                                    Unsorted                                               *
     *                                                                                                           *
     *                                                                                                           *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


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
    GET_CURRENT_USER(Method.GET, USERS, ME),

    /**
     * <p>
     *     Returns a {@link me.linusdev.lapi.api.objects.user.User user object} for a given user ID.
     * </p>
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-user" target="_top">Get User</a>
     * @see Name#USER_ID
     */
    GET_USER(Method.GET, USERS, USER_ID),

    /**
     * Returns a list of partial {@link Guild guild} objects the current user is a member of. Requires the guilds OAuth2 scope.
     * <br><br>
     * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">
     *     This can have Query String parameters:<br>
     * </span>
     * <ul style="margin:0;padding:0">
     *     <li>
     *         {@value RequestFactory#BEFORE_KEY}: get guilds before this guild ID
     *     </li>
     *     <li>
     *         {@value RequestFactory#AFTER_KEY}: get guilds after this guild ID
     *     </li>
     *     <li>
     *         {@value RequestFactory#LIMIT_KEY}: max number of guilds to return (1-200). Default: 200
     *     </li>
     * </ul>
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-current-user-guilds" target="_top">Get Current User Guilds</a>
     * @see <a href="https://discord.com/developers/docs/resources/user#get-current-user-guilds-query-string-params" target="_top">Query String Params</a>
     */
    GET_CURRENT_USER_GUILDS(Method.GET, USERS, ME, GUILDS),

    /**
     * Returns a list of {@link me.linusdev.lapi.api.objects.user.connection.Connection connection}
     * objects. Requires the connections OAuth2 scope.
     *
     * @see <a href="https://discord.com/developers/docs/resources/user#get-user-connections" target="_top">Get User Connections</a>
     */
    GET_CURRENT_USER_CONNECTIONS(Method.GET, USERS, ME, CONNECTIONS),




    /**
     * Returns an object with a single valid WSS URL, which the client can use for Connecting.
     * Clients should cache this value and only call this endpoint to retrieve a new URL if
     * they are unable to properly establish a connection using the cached version of the URL.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway" target="_top">Get Gateway</a>
     */
    GET_GATEWAY(Method.GET, GATEWAY),

    /**
     * Returns an object based on the information in Get Gateway,
     * plus additional metadata that can help during the operation of large or sharded bots.
     * Unlike the Get Gateway, this route should not be cached for extended periods of time
     * as the value is not guaranteed to be the same per-call, and changes as the bot joins/leaves
     * guilds.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway-bot" target="_top">Get Gateway Bot</a>
     */
    GET_GATEWAY_BOT(Method.GET, GATEWAY, BOT),

    /**
     * Returns an array of {@link me.linusdev.lapi.api.objects.voice.region.VoiceRegion voice region objects}
     * that can be used when setting a voice or stage channel's rtc_region.
     *
     * @see <a href="https://discord.com/developers/docs/resources/voice#list-voice-regions" target="_top">List Voice Regions</a>
     */
    GET_VOICE_REGIONS(Method.GET, VOICE, REGIONS),
    ;

    public static final int amount = values().length;

    private final @NotNull Method method;
    private final @NotNull Concatable[] concatables;

    private final boolean supportsAuditLogReasonHeader;

    private final boolean boundToGlobalRateLimits;
    private final boolean containsTopLevelResource;
    private final boolean containsPlaceholders;

    Link(@NotNull Method method, boolean boundToGlobalRateLimits, boolean supportsAuditLogReasonHeader, @NotNull Concatable... parts) {
        this.method = method;
        this.boundToGlobalRateLimits = boundToGlobalRateLimits;
        this.concatables = parts;

        boolean tlr = false;
        boolean placeholders = false;
        for (Concatable c : parts) {
            if (c instanceof Name) {
                placeholders = true;
                if (((Name) c).isTopLevelResource()){
                    tlr = true;
                    break;
                }
            }
        }
        this.containsTopLevelResource = tlr;
        this.containsPlaceholders = placeholders;
        this.supportsAuditLogReasonHeader = supportsAuditLogReasonHeader;
    }

    Link(@NotNull Method method, boolean boundToGlobalRateLimits, @NotNull Concatable... parts) {
        this(method, true, boundToGlobalRateLimits, parts);
    }

    Link(@NotNull Method method, @NotNull Concatable... parts) {
        this(method, true, false, parts);
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @NotNull String construct(@NotNull ApiVersion apiVersion, @NotNull PlaceHolder... placeHolders) {
        StringBuilder sb = new StringBuilder();

        LinkPart.HTTP_PREFIX.concat(sb, apiVersion.getVersionNumber());
        int i = Concatable.construct(sb, concatables, placeHolders);
        assert placeHolders.length == i;

        return sb.toString();
    }

    @NotNull
    public Concatable[] getConcatables() {
        return concatables;
    }

    @Override
    public boolean isBoundToGlobalRateLimit() {
        return boundToGlobalRateLimits;
    }

    @Override
    public boolean containsTopLevelResource() {
        return containsTopLevelResource;
    }

    @Override
    public boolean containsPlaceholders() {
        return containsPlaceholders;
    }

    @Override
    public boolean supportsAuditLogReasonHeader() {
        return supportsAuditLogReasonHeader;
    }

    @Override
    public int uniqueId() {
        return ordinal();
    }
}
