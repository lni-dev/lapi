/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.request.requests;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.communication.http.response.body.ListThreadsResponseBody;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.invite.Invite;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.request.AnchorType;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.templates.message.AllowedMentions;
import me.linusdev.lapi.api.templates.message.MessageTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.linusdev.lapi.api.request.RequestFactory.*;

public interface ChannelRequests extends HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Get/Modify/Delete Channel                   *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * @param channelId the id of the {@link Channel}, which should be retrieved
     * @return {@link Queueable} which can retrieve the {@link Channel}
     * @see Queueable#queue()
     */
    default @NotNull Queueable<Channel<?>> getChannel(@NotNull String channelId){
        return new ConvertingRetriever<>(
                new LinkQuery(getLApi(), Link.GET_CHANNEL, new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId)),
                Channel::fromData);
    }

    //TODO: modify Channel request

    //TODO: Delete/Close Channel request

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                       Get Channel Message                     *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * @param channelId the id of the {@link Channel}, in which the message was sent
     * @param messageId the id of the {@link MessageImplementation}
     * @return {@link Queueable} which can retrieve the {@link MessageImplementation}
     */
    default @NotNull Queueable<MessageImplementation> getChannelMessage(@NotNull String channelId, @NotNull String messageId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_CHANNEL_MESSAGE,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId), new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId));
        return new ConvertingRetriever<>(query, MessageImplementation::new);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      Get Channel Messages                     *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link MessageImplementation} with this id will most likely be included in the result.
     *                        If this is {@code null}, it will retrieve the latest messages in the channel
     * @param limit the limit of how many messages you want to retrieve (between 1-100). Default is 50
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link MessageImplementation Messages}
     * @see Link#GET_CHANNEL_MESSAGES
     */
    default @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessages(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable Integer limit, @Nullable AnchorType anchorType){

        SOData queryStringsData = null;

        if(anchorMessageId != null || limit != null){
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);

            if(anchorMessageId != null && anchorType != null) {
                queryStringsData.add(anchorType.getQueryStringKey(), anchorMessageId);
            }

            if(limit != null) queryStringsData.add(LIMIT_KEY, limit);
        }


        LinkQuery query = new LinkQuery(getLApi(), Link.GET_CHANNEL_MESSAGES, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<SOData, MessageImplementation>(query, MessageImplementation::new);
    }

    /**
     * This will retrieve 50 or less {@link MessageImplementation messages}.<br><br> For more information see
     * {@link #getChannelMessages(String, String, Integer, AnchorType)}<br><br>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are<br><br>
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link MessageImplementation} with this id will most likely be included in the result.
     *                       If this is {@code null}, it will retrieve the latest messages in the channel.<br><br>
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link MessageImplementation Messages}
     * @see #getChannelMessages(String, String, Integer, AnchorType)
     */
    default @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessages(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable AnchorType anchorType){
        return getChannelMessages(channelId, anchorMessageId, null, anchorType);
    }

    /**
     * This should retrieve the latest 50 or less {@link MessageImplementation messages} in given {@link Channel channel}. The newest {@link MessageImplementation message} will have index 0
     * <br><br>
     * See {@link #getChannelMessages(String, String, Integer, AnchorType)} for more information<br><br>
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link MessageImplementation Messages}
     * @see #getChannelMessages(String, String, Integer, AnchorType)
     */
    default @NotNull Queueable<ArrayList<MessageImplementation>> getChannelMessages(@NotNull String channelId) {
        return getChannelMessages(channelId, null, null, null);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                         Create Message                        *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     This will create a new {@link me.linusdev.lapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id
     * </p>
     *
     * <p>
     *     For a simple {@link MessageTemplate} creation see
     *     {@link me.linusdev.lapi.api.templates.message.builder.MessageBuilder MessageBuilder}
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param message the message to create
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    default @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull MessageTemplate message){
        Query query = new LinkQuery(getLApi(), Link.CREATE_MESSAGE, message.getBody(),
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));

        return new ConvertingRetriever<>(query, MessageImplementation::new);
    }

    /**
     * <p>
     *     This will create a new {@link me.linusdev.lapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     * <p>
     *     This allows you to control if mentions are allowed. For a better control see
     *     {@link me.linusdev.lapi.api.templates.message.builder.MessageBuilder MessageBuilder}
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param content the text content of the message
     * @param allowMentions whether this message is allowed to mention user, roles, everyone, here, etc.
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    default @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull String content, boolean allowMentions){
        return createMessage(channelId, new MessageTemplate(
                content,
                false, null,
                allowMentions ? null : AllowedMentions.noneAllowed(),
                null, null, null, null,
                null));
    }

    /**
     * <p>
     *     This will create a new {@link me.linusdev.lapi.api.objects.message.abstracts.Message message}
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
    default @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull String content){
        return createMessage(channelId, content,true);
    }

    /**
     * <p>
     *     This will create a new {@link me.linusdev.lapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param embeds the embeds for the message
     * @param allowMentions mentions inside embeds will never ping the user. In case that ever changes, you can adjust the behavior here
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    default @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, boolean allowMentions, @NotNull Embed... embeds){
        return createMessage(channelId,
                new MessageTemplate(null, false, embeds,
                        allowMentions ? null : AllowedMentions.noneAllowed(),
                        null, null, null, null,
                        null));
    }

    /**
     * <p>
     *     This will create a new {@link me.linusdev.lapi.api.objects.message.abstracts.Message message}
     *     in the channel with given id.
     * </p>
     *
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param embeds the embeds for the message
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    default @NotNull Queueable<MessageImplementation> createMessage(@NotNull String channelId, @NotNull Embed... embeds){
        return createMessage(channelId, true, embeds);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      Crosspost Message                        *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                            Reactions                          *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
    default @NotNull Queueable<ArrayList<User>> getReactions(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable String afterUserId, @Nullable Integer limit){
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

        LinkQuery query = new LinkQuery(getLApi(), Link.GET_REACTIONS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId),
                new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId),
                new PlaceHolder(PlaceHolder.EMOJI, emojiString));
        return new ArrayRetriever<>(query, User::fromData);
    }

    /**
     *
     * <p>
     *     This is used to retrieve the {@link User users}, that have reacted with a specific {@link Emoji}.<br>
     *     If you want to retrieve more than 100 {@link User users},
     *     you will have to chain {@link #getReactions(String, String, Emoji, String, Integer)}.
     * </p>
     *
     * @param channelId the id of the {@link Channel}
     * @param messageId the id of the {@link MessageImplementation}
     * @param emoji the {@link Emoji}
     * @param limit max number of users to return (1-100). Will be 25 if {@code null}
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link User users} that have reacted with given emoji
     */
    default @NotNull Queueable<ArrayList<User>> getReactions(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable Integer limit){
        return getReactions(channelId, messageId, emoji, null, limit);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      Edit/Delete Message                      *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Edit Channel Permissions                    *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                        Channel Invites                        *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
     * @see Link#GET_CHANNEL_INVITES
     */
    default @NotNull Queueable<ArrayList<Invite>> getChannelInvites(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_CHANNEL_INVITES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<>(query, Invite::fromData);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                  Delete Channel Permissions                   *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                  Follow Announcement Channel                  *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Trigger Typing Indicator                    *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                        Pinned Messages                        *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
     * @see Link#GET_PINNED_MESSAGES
     */
    default @NotNull Queueable<ArrayList<MessageImplementation>> getPinnedMessages(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_PINNED_MESSAGES,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<>(query, MessageImplementation::new);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                           Group DM                            *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                         Start Thread                          *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *              Join/Leave Thead and Thread Member               *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Returns a {@link me.linusdev.lapi.api.objects.channel.thread.ThreadMember thread member} object
     *     for the specified user if they are a member of the thread, returns a 404 response otherwise.
     * </p>
     *
     * @param channelId the id of the {@link me.linusdev.lapi.api.objects.channel.abstracts.Thread thread}, the user is a member of
     * @param userId the {@link User#getId() user id}
     * @return {@link Queueable} to retrieve the {@link ThreadMember} matching given user in given thread
     * @see Link#GET_THREAD_MEMBER
     */
    default @NotNull Queueable<ThreadMember> getThreadMember(@NotNull String channelId, @NotNull String userId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_THREAD_MEMBER,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId), new PlaceHolder(PlaceHolder.USER_ID, userId));
        return new ConvertingRetriever<>(query, (lApi, data) -> ThreadMember.fromData(data));
    }

    /**
     * <p>
     *     Returns array of thread members objects that are members of the thread.
     * </p>
     * <p>
     *     This endpoint is restricted according to whether the {@link me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS} Privileged GatewayIntent is enabled for your application.
     * </p>
     *
     * @param channelId the channel id of the {@link me.linusdev.lapi.api.objects.channel.abstracts.Thread thread}
     * @return {@link Queueable} to retrieve a {@link ArrayList list} of {@link ThreadMember thread members}
     * @see Link#LIST_THREAD_MEMBERS
     */
    default @NotNull Queueable<ArrayList<ThreadMember>> getThreadMembers(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_THREAD_MEMBERS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ArrayRetriever<SOData, ThreadMember>(query, (lApi, data) -> ThreadMember.fromData(data));
    }

    //TODO: list thread members https://discord.com/developers/docs/resources/channel#list-thread-members

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                     List Archived Threads                     *
     *                                                               *
     *  Done: 04.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
     * @see Link#LIST_PUBLIC_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
    default @NotNull Queueable<ListThreadsResponseBody> listPublicArchivedThreads(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){

        SOData queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            queryStringsData.addIfNotNull(RequestFactory.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(RequestFactory.LIMIT_KEY, limit);
        }

        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_PUBLIC_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

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
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more with {@link #listPublicArchivedThreads(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all public archived {@link Thread threads} for
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see Link#LIST_PUBLIC_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
    default @NotNull Queueable<ListThreadsResponseBody> listPublicArchivedThreads(@NotNull String channelId) {
        return listPublicArchivedThreads(channelId, null, null);
    }

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
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
     * @see Link#LIST_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
    default @NotNull Queueable<ListThreadsResponseBody> listPrivateArchivedThreads(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){
        SOData queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            queryStringsData.addIfNotNull(RequestFactory.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(RequestFactory.LIMIT_KEY, limit);
        }

        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_PRIVATE_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link Permission#MANAGE_THREADS MANAGE_THREADS} permissions.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} with
     *     {@link #listPrivateArchivedThreads(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Thread threads} for
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see Link#LIST_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     * @see #listPrivateArchivedThreads(String, ISO8601Timestamp, Integer)
     */
    default @NotNull Queueable<ListThreadsResponseBody> listPrivateArchivedThreads(@NotNull String channelId){
        return listPrivateArchivedThreads(channelId, null, null);
    }

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
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
     * @see Link#LIST_JOINED_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     */
    default @NotNull Queueable<ListThreadsResponseBody> listJoinedPrivateArchivedThreads(@NotNull String channelId, @Nullable ISO8601Timestamp before, @Nullable Integer limit){
        SOData queryStringsData = null;
        if(before != null || limit != null) {
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);
            queryStringsData.addIfNotNull(RequestFactory.BEFORE_KEY, before);
            queryStringsData.addIfNotNull(RequestFactory.LIMIT_KEY, limit);
        }

        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_JOINED_PRIVATE_ARCHIVED_THREADS, queryStringsData,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link me.linusdev.lapi.api.objects.enums.ChannelType#GUILD_PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Thread threads} with
     *     {@link #listJoinedPrivateArchivedThreads(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Thread threads} for
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see Link#LIST_JOINED_PRIVATE_ARCHIVED_THREADS
     * @see ListThreadsResponseBody
     * @see #listJoinedPrivateArchivedThreads(String, ISO8601Timestamp, Integer)
     */
    default @NotNull Queueable<ListThreadsResponseBody> listJoinedPrivateArchivedThreads(@NotNull String channelId) {
        return listJoinedPrivateArchivedThreads(channelId, null, null);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                       Deprecated/Removed                      *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
     * @see Link#LIST_ACTIVE_THREADS
     * @see ListThreadsResponseBody
     */
    @SuppressWarnings("removal")
    @Deprecated(since = "api v10", forRemoval = true)
    default @NotNull Queueable<ListThreadsResponseBody> listActiveThreads(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_ACTIVE_THREADS,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

}
