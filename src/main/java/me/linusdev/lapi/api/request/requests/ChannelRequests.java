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

import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.http.request.body.LApiHttpBody;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.communication.http.response.body.ListThreadsResponseBody;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.NoContentRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.channel.ChannelFlag;
import me.linusdev.lapi.api.objects.channel.ChannelType;
import me.linusdev.lapi.api.objects.channel.thread.AutoArchiveDuration;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.enums.MessageFlag;
import me.linusdev.lapi.api.objects.invite.Invite;
import me.linusdev.lapi.api.objects.message.AnyMessage;
import me.linusdev.lapi.api.objects.message.concrete.ChannelMessage;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.other.ImageData;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import me.linusdev.lapi.api.request.AnchorType;
import me.linusdev.lapi.api.request.BiContainer;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.templates.channel.EditChannelTemplate;
import me.linusdev.lapi.api.templates.message.AllowedMentions;
import me.linusdev.lapi.api.templates.message.EditMessageTemplate;
import me.linusdev.lapi.api.templates.message.ForumThreadMessageTemplate;
import me.linusdev.lapi.api.templates.message.MessageTemplate;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.linusdev.lapi.api.request.RequestFactory.LIMIT_KEY;
import static me.linusdev.lapi.api.request.requests.RequestUtils.*;

public interface ChannelRequests extends HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                          Get/Modify/Delete Channel                                        *
     *                                                                                                           *
     *  Done:       13.12.2022                                                                                   *
     *  Updated:    13.12.2022                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * @param channelId the id of the {@link Channel}, which should be retrieved
     * @return {@link Queueable} which can retrieve the {@link Channel}
     * @see Link#GET_CHANNEL
     * @see Queueable#queue()
     */
    default @NotNull Queueable<Channel> getChannel(@NotNull String channelId) {
        return new ConvertingRetriever<>(
                new LinkQuery(getLApi(), Link.GET_CHANNEL,
                        Name.CHANNEL_ID.withValue(channelId)),
                Channel::channelFromData);
    }

    /**
     * <p>
     *     Modify a {@link ChannelType#GROUP_DM group dm} channel.
     * </p>
     * <p>
     *     For more information see {@link Link#MODIFY_CHANNEL}.
     * </p>
     * @param channelId id of the (group dm) channel to modify
     * @param name new channel name or {@code null}
     * @param icon new icon or {@code null}
     * @return {@link Queueable} that can modify and retrieve the {@link Channel}.
     * @see Link#MODIFY_CHANNEL
     * @see Queueable#queue()
     */
    default @NotNull Queueable<Channel> modifyChannel(@NotNull String channelId, @Nullable String name, @Nullable ImageData icon) {
        SOData json = SOData.newOrderedDataWithKnownSize(2);

        json.addIfNotNull(Channel.NAME_KEY, name);
        json.addIfNotNull(Channel.ICON_KEY, icon);

        Query query = new LinkQuery(getLApi(), Link.MODIFY_CHANNEL, new LApiHttpBody(json), Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, Channel::channelFromData);
    }

    /**
     * <p>
     *     Modify a guild channel.
     * </p>
     * <p>
     *     For more information see {@link Link#MODIFY_CHANNEL}.
     * </p>
     * @param channelId id of the (guild) channel to modify
     * @param template {@link EditChannelTemplate}
     * @return {@link Queueable} than can modify and retrieve the {@link Channel}.
     * @see Link#MODIFY_CHANNEL
     * @see Queueable#queue()
     */
    default @NotNull Queueable<Channel> modifyChannel(@NotNull String channelId, @NotNull EditChannelTemplate template) {
        Query query = new LinkQuery(getLApi(), Link.MODIFY_CHANNEL, template.getBody(), Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, Channel::channelFromData);
    }

    /**
     * <p>
     *     Modify a {@link Channel#isThread() thread} channel
     * </p>
     * <p>
     *     For more information see {@link Link#MODIFY_CHANNEL}.
     * </p>
     * @param channelId id of the ({@link Channel#isThread() thread}) channel to modify
     * @param name new name or {@code null}
     * @param archived new value or {@code null}
     * @param autoArchiveDuration new value or {@code null}
     * @param locked new value or {@code null}
     * @param invitable new value or {@code null}
     * @param rateLimitPerUser new value or {@code null}
     * @param flags new value or {@code null}
     * @param appliedTags new value or {@code null}
     * @return {@link Queueable} than can modify and retrieve the {@link Channel}.
     */
    default @NotNull Queueable<Channel> modifyChannel(@NotNull String channelId, @Nullable String name, @Nullable Boolean archived,
                                                      @Nullable AutoArchiveDuration autoArchiveDuration, @Nullable Boolean locked,
                                                      @Nullable Boolean invitable, @Nullable OptionalValue<Integer> rateLimitPerUser,
                                                      @Nullable Collection<ChannelFlag> flags, @Nullable Collection<String> appliedTags) {

        SOData json = SOData.newOrderedDataWithKnownSize(8);

        json.addIfNotNull(Channel.NAME_KEY, name);
        json.addIfNotNull(ThreadMetadata.ARCHIVED_KEY, archived);
        json.addIfNotNull(ThreadMetadata.AUTO_ARCHIVE_DURATION_KEY, autoArchiveDuration);
        json.addIfNotNull(ThreadMetadata.LOCKED_KEY, locked);
        json.addIfNotNull(ThreadMetadata.INVITABLE_KEY, invitable);
        if(rateLimitPerUser != null) json.addIfOptionalExists(Channel.RATE_LIMIT_PER_USER_KEY, rateLimitPerUser);
        json.addIfNotNull(Channel.FLAGS_KEY, flags);
        json.addIfNotNull(Channel.APPLIED_TAGS_KEY, appliedTags);

        Query query = new LinkQuery(getLApi(), Link.MODIFY_CHANNEL, new LApiHttpBody(json), Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, Channel::channelFromData);
    }

    /**
     * <p>
     *     Deletes a {@link Channel}. This cannot be undone. For more information see {@link Link#DELETE_CHANNEL}
     * </p>
     * @param channelId the id of the {@link Channel}, which should be deleted
     * @return {@link Queueable} which can delete the {@link Channel} and retrieve it
     * @see Link#DELETE_CHANNEL
     * @see Queueable#queue()
     */
    default @NotNull Queueable<Channel> deleteChannel(@NotNull String channelId) {
        Query query = new LinkQuery(getLApi(), Link.DELETE_CHANNEL, Name.CHANNEL_ID.withValue(channelId));

        return new ConvertingRetriever<>(query, Channel::channelFromData);
    }

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
     * @param messageId the id of the {@link ChannelMessage message}
     * @return {@link Queueable} which can retrieve the {@link ChannelMessage message}
     */
    default @NotNull Queueable<ChannelMessage> getChannelMessage(@NotNull String channelId, @NotNull String messageId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_CHANNEL_MESSAGE,
                Name.CHANNEL_ID.withValue(channelId),
                Name.MESSAGE_ID.withValue(messageId));
        return new ConvertingRetriever<>(query, AnyMessage::channelMessageFromData);
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
     *     This is used to retrieve a bunch of {@link ChannelMessage messages} in a {@link Channel channel}.
     * </p>
     * <p>
     *     If anchorType is {@code null}, it should use {@link AnchorType#AROUND}, but it may result in unexpected behavior.
     * </p>
     * <p>
     *     If anchorMessageId and anchorType is {@code null} it should retrieve the latest {@link ChannelMessage messages} in the {@link Channel channel}.
     * </p>
     * <p>
     *     If limit is {@code null}, the limit will be 50
     * </p>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link ChannelMessage message} with this id will most likely be included in the result.
     *                        If this is {@code null}, it will retrieve the latest messages in the channel
     * @param limit the limit of how many messages you want to retrieve (between 1-100). Default is 50
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link ChannelMessage messages}
     * @see Link#GET_CHANNEL_MESSAGES
     */
    default @NotNull Queueable<ArrayList<ChannelMessage>> getChannelMessages(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable Integer limit, @Nullable AnchorType anchorType){

        SOData queryStringsData = null;

        if(anchorMessageId != null || limit != null){
            queryStringsData = SOData.newOrderedDataWithKnownSize(2);

            if(anchorMessageId != null && anchorType != null) {
                queryStringsData.add(anchorType.getQueryStringKey(), anchorMessageId);
            }

            if(limit != null) queryStringsData.add(LIMIT_KEY, limit);
        }


        LinkQuery query = new LinkQuery(getLApi(), Link.GET_CHANNEL_MESSAGES, queryStringsData,
                Name.CHANNEL_ID.withValue(channelId));
        return new ArrayRetriever<>(query, AnyMessage::channelMessageFromData);
    }

    /**
     * This will retrieve 50 or less {@link ChannelMessage messages}.<br><br> For more information see
     * {@link #getChannelMessages(String, String, Integer, AnchorType)}<br><br>
     *
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are<br><br>
     * @param anchorMessageId the message around, before or after which you want to retrieve messages.
     *                       The {@link ChannelMessage message} with this id will most likely be included in the result.
     *                       If this is {@code null}, it will retrieve the latest messages in the channel.<br><br>
     * @param anchorType {@link AnchorType#AROUND}, {@link AnchorType#BEFORE} and {@link AnchorType#AFTER}<br><br>
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link ChannelMessage messages}
     * @see #getChannelMessages(String, String, Integer, AnchorType)
     */
    default @NotNull Queueable<ArrayList<ChannelMessage>> getChannelMessages(@NotNull String channelId, @Nullable String anchorMessageId, @Nullable AnchorType anchorType){
        return getChannelMessages(channelId, anchorMessageId, null, anchorType);
    }

    /**
     * This should retrieve the latest 50 or less {@link ChannelMessage messages} in given {@link Channel channel}. The newest {@link ChannelMessage message} will have index 0
     * <br><br>
     * See {@link #getChannelMessages(String, String, Integer, AnchorType)} for more information<br><br>
     * @param channelId the id of the {@link Channel}, in which the messages you want to retrieve are
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link ChannelMessage messages}
     * @see #getChannelMessages(String, String, Integer, AnchorType)
     */
    default @NotNull Queueable<ArrayList<ChannelMessage>> getChannelMessages(@NotNull String channelId) {
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
     *     This will create a new {@link ChannelMessage message}
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
    default @NotNull Queueable<ChannelMessage> createMessage(@NotNull String channelId, @NotNull MessageTemplate message){
        Query query = new LinkQuery(getLApi(), Link.CREATE_MESSAGE, message.getBody(),
                Name.CHANNEL_ID.withValue(channelId));

        return new ConvertingRetriever<>(query, AnyMessage::channelMessageFromData);
    }

    /**
     * <p>
     *     This will create a new {@link ChannelMessage message}
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
    default @NotNull Queueable<ChannelMessage> createMessage(@NotNull String channelId, @NotNull String content, boolean allowMentions){
        return createMessage(channelId, new MessageTemplate(
                content,
                null, false, null,
                allowMentions ? null : AllowedMentions.noneAllowed(),
                null, null, null, null,
                null));
    }

    /**
     * <p>
     *     This will create a new {@link ChannelMessage message}
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
    default @NotNull Queueable<ChannelMessage> createMessage(@NotNull String channelId, @NotNull String content){
        return createMessage(channelId, content,true);
    }

    /**
     * <p>
     *     This will create a new {@link ChannelMessage message}
     *     in the channel with given id.
     * </p>
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param embeds the embeds for the message
     * @param allowMentions mentions inside embeds will never ping the user. In case that ever changes, you can adjust the behavior here
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    default @NotNull Queueable<ChannelMessage> createMessage(@NotNull String channelId, boolean allowMentions, @NotNull Embed... embeds){
        return createMessage(channelId,
                new MessageTemplate(null, null, false, embeds,
                        allowMentions ? null : AllowedMentions.noneAllowed(),
                        null, null, null, null,
                        null));
    }

    /**
     * <p>
     *     This will create a new {@link ChannelMessage message}
     *     in the channel with given id.
     * </p>
     *
     *
     * @param channelId the id of the {@link Channel} the message should be created in
     * @param embeds the embeds for the message
     * @return {@link Queueable} to create the message
     * @see Link#CREATE_MESSAGE
     */
    default @NotNull Queueable<ChannelMessage> createMessage(@NotNull String channelId, @NotNull Embed... embeds){
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
     * @param messageId the id of the {@link ChannelMessage message}
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

        @NotNull String emojiString;
        if(emoji.isStandardEmoji()){
            //noinspection ConstantConditions: checked by above if
            emojiString = emoji.getName();
            assert emojiString != null;
        }else{
            emojiString = emoji.getName() + ":" + emoji.getId();
        }


        LinkQuery query = new LinkQuery(getLApi(), Link.GET_REACTIONS, queryStringsData,
                Name.CHANNEL_ID.withValue(channelId),
                Name.MESSAGE_ID.withValue(messageId),
                Name.EMOJI.withValue(emojiString));
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
     * @param messageId the id of the {@link ChannelMessage message}
     * @param emoji the {@link Emoji}
     * @param limit max number of users to return (1-100). Will be 25 if {@code null}
     * @return {@link Queueable} which can retrieve a {@link ArrayList} of {@link User users} that have reacted with given emoji
     */
    default @NotNull Queueable<ArrayList<User>> getReactions(@NotNull String channelId, @NotNull String messageId, @NotNull Emoji emoji, @Nullable Integer limit){
        return getReactions(channelId, messageId, emoji, null, limit);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                            Edit / Delete Message                                          *
     *                                                                                                           *
     *  Done:       14.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *      Edit a previously sent message. The fields content, embeds, and flags can be edited by the original message author.
     *      Other users can only {@link MessageBuilder#setFlag(MessageFlag) edit flags} and only if they have the
     *      {@link Permission#MANAGE_MESSAGES MANAGE_MESSAGES} permission in the corresponding channel. When specifying
     *      flags, ensure to include all previously set flags in addition to ones that you are modifying.
     *      Only flags documented in the table below may be modified by users (unsupported flag changes are currently ignored without error).
     *      <br><br>
     *      When the {@link MessageBuilder#appendContent(Object) content field} is edited, the mentions array in the message
     *      object will be reconstructed from scratch based on the new content.
     *      The allowed_mentions field of the edit request controls how this happens.
     *      If there is no explicit allowed_mentions in the edit request, the content will be parsed with default allowances,
     *      that is, without regard to whether or not an allowed_mentions was present in the request that originally created the message.
     * </p>
     * <p>
     *     Starting with {@link ApiVersion#V10 API v10}, the attachments array must contain all attachments that should be present after edit,
     *     including retained and new attachments provided in the request body.
     * </p>
     * <p>
     *     If you use {@link MessageBuilder#editMessage(AnyMessage)} the flags, allowed mentions and attachments will be set automatically.
     * </p>
     * @param channelId id of the {@link Channel channel} the message to edit is in.
     * @param messageId id of the {@link AnyMessage message} to edit.
     * @param template {@link EditMessageTemplate}
     * @return {@link Queueable} which can edit the message.
     * @see Link#EDIT_MESSAGE
     * @see MessageBuilder#editMessage(AnyMessage)
     */
    default @NotNull Queueable<ChannelMessage> editMessage(@NotNull String channelId, @NotNull String messageId, @NotNull EditMessageTemplate template) {
        LinkQuery query = new LinkQuery(getLApi(), Link.EDIT_MESSAGE, template.getBody(),
                Name.CHANNEL_ID.withValue(channelId),
                Name.MESSAGE_ID.withValue(messageId));

        return new ConvertingRetriever<>(query, AnyMessage::channelMessageFromData);
    }

    /**
     * <p>
     *     Delete a message. If operating on a guild channel and trying to delete a message that was not sent by the
     *     current user, this endpoint requires the {@link Permission#MANAGE_MESSAGES MANAGE_MESSAGES} permission.
     *     Returns a 204 empty response on success. Fires a {@link GatewayEvent#MESSAGE_DELETE Message Delete Gateway event}.
     * </p>
     *
     * @param channelId id of the {@link Channel channel} the message to delete is in.
     * @param messageId id of the {@link AnyMessage message} to delete.
     * @return {@link Queueable} that can delete the message.
     * @see Link#DELETE_MESSAGE
     */
    default @NotNull Queueable<LApiHttpResponse> deleteMessage(@NotNull String channelId, @NotNull String messageId) {
        LinkQuery query = new LinkQuery(getLApi(), Link.DELETE_MESSAGE,
                Name.CHANNEL_ID.withValue(channelId),
                Name.MESSAGE_ID.withValue(messageId));

        return new NoContentRetriever(query);
    }

    /**
     * <p>
     *     Delete multiple messages in a single request. This endpoint can only be used on guild channels and requires
     *     the {@link Permission#MANAGE_MESSAGES MANAGE_MESSAGES} permission. Returns a 204 empty response on success.
     *     Fires a {@link GatewayEvent#MESSAGE_DELETE_BULK Message Delete Bulk Gateway event}.
     * </p>
     * <p>
     *     Any message IDs given that do not exist or are invalid will count towards the minimum and maximum message count
     *     (currently {@value RequestUtils#BULK_DELETE_MESSAGES_MIN_MESSAGE_COUNT} and {@value RequestUtils#BULK_DELETE_MESSAGES_MAX_MESSAGE_COUNT} respectively)
     * </p>
     * <p>
     *     This endpoint will not delete messages older than {@link RequestUtils#BULK_DELETE_MESSAGES_MAX_OLDNESS_MILLIS 2 weeks}, and will fail with a 400 BAD REQUEST if
     *     any message provided is older than that or if any duplicate message IDs are provided.
     * </p>
     * @param channelId id of the {@link Channel channel} the messages to delete are in.
     * @param messageIds ids of the {@link AnyMessage messages} to delete.
     * @return {@link Queueable} that can delete the messages
     */
    default @NotNull Queueable<LApiHttpResponse> bulkDeleteMessages(@NotNull String channelId, @NotNull List<String> messageIds) {
        SOData data = SOData.newOrderedDataWithKnownSize(1);
        data.add(RequestUtils.MESSAGES_KEY, messageIds);

        LinkQuery query = new LinkQuery(getLApi(), Link.BULK_DELETE_MESSAGES, new LApiHttpBody(data),
                Name.CHANNEL_ID.withValue(channelId));

        return new NoContentRetriever(query);
    }

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
                Name.CHANNEL_ID.withValue(channelId));
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
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of {@link ChannelMessage pinned messages} for given channel
     * @see Link#GET_PINNED_MESSAGES
     */
    default @NotNull Queueable<ArrayList<ChannelMessage>> getPinnedMessages(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_PINNED_MESSAGES,
                Name.CHANNEL_ID.withValue(channelId));
        return new ArrayRetriever<>(query, AnyMessage::channelMessageFromData);
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

    /**
     * <p>
     *     Creates a thread and a message in a {@link ChannelType#GUILD_FORUM forum} channel.
     *     <br><br>
     *     See {@link Link#START_THREAD_IN_FORUM_CHANNEL START_THREAD_IN_FORUM_CHANNEL} for a more detailed description.
     * </p>
     *
     *
     * @param channelId id of the forum channel
     * @param name {@value Channel#CHANNEL_NAME_MIN_CHARS}-{@value Channel#CHANNEL_NAME_MAX_CHARS} character channel name
     * @param autoArchiveDuration duration in minutes to automatically archive the thread after recent activity,
     *                           can be set to: {@link AutoArchiveDuration}.
     * @param rateLimitPerUser amount of seconds a user has to wait before sending another message (0-21600)
     * @param message contents of the first message in the forum thread
     * @param appliedTags the IDs of the set of tags that have been applied to a thread in a GUILD_FORUM channel
     * @return {@link Queueable} that can create a thread in given forum channel and retrieve the created thread and message.
     * @see Link#START_THREAD_IN_FORUM_CHANNEL
     */
    default @NotNull Queueable<BiContainer<Channel, ChannelMessage>> startThreadInForumChannel(@NotNull String channelId,
                                                                      @NotNull String name,
                                                                      @Nullable AutoArchiveDuration autoArchiveDuration,
                                                                      @Nullable OptionalValue<Integer> rateLimitPerUser,
                                                                      @NotNull ForumThreadMessageTemplate message,
                                                                      @NotNull String @Nullable [] appliedTags) {
        SOData jsonParams = SOData.newOrderedDataWithKnownSize(5);

        jsonParams.add(NAME_KEY, name);
        jsonParams.addIfNotNull(AUTO_ARCHIVE_DURATION_KEY, autoArchiveDuration);
        if(rateLimitPerUser != null) jsonParams.addIfOptionalExists(RATE_LIMIT_PER_USER_KEY, rateLimitPerUser);
        jsonParams.add(MESSAGE_KEY, message);
        jsonParams.add(APPLIED_TAGS_KEY, appliedTags);

        LinkQuery query = new LinkQuery(getLApi(), Link.START_THREAD_IN_FORUM_CHANNEL, new LApiHttpBody(jsonParams),
                Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, (lApi, data) -> {

            Channel channel = Channel.channelFromData(lApi, data);
            ChannelMessage msg = data.getContainer(MESSAGE_KEY)
                    .requireNotNull(InvalidDataException.SUPPLIER)
                    .castAndConvertWithException((SOData c) -> AnyMessage.channelMessageFromData(lApi, c))
                    .get();

            return new BiContainer<>(channel, msg);
        });
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *              Join/Leave Thead and Thread Member               *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Returns a {@link ThreadMember thread member} object
     *     for the specified user if they are a member of the thread, returns a 404 response otherwise.
     * </p>
     *
     * @param channelId the id of the {@link Channel#isThread()}  thread}, the user is a member of
     * @param userId the {@link User#getId() user id}
     * @return {@link Queueable} to retrieve the {@link ThreadMember} matching given user in given thread
     * @see Link#GET_THREAD_MEMBER
     */
    default @NotNull Queueable<ThreadMember> getThreadMember(@NotNull String channelId, @NotNull String userId){
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_THREAD_MEMBER,
                Name.CHANNEL_ID.withValue(channelId),
                Name.USER_ID.withValue(userId));
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
     * @param channelId the channel id of the {@link Channel#isThread() thread}
     * @return {@link Queueable} to retrieve a {@link ArrayList list} of {@link ThreadMember thread members}
     * @see Link#LIST_THREAD_MEMBERS
     */
    default @NotNull Queueable<ArrayList<ThreadMember>> getThreadMembers(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_THREAD_MEMBERS,
                Name.CHANNEL_ID.withValue(channelId));
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
     *     When called on a {@link ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link ChannelType#ANNOUNCEMENT_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link ChannelType#GUILD_ANNOUNCEMENT GUILD_ANNOUNCEMENT} channel returns
     *     threads of {@link Channel#getType() type} {@link ChannelType#ANNOUNCEMENT_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Channel#isThread() threads} by setting
     *     before to the {@link ThreadMetadata#getArchiveTimestamp()} of the last retrieved thread.
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all public archived {@link Channel#isThread() threads} for
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
                Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    /**
     * <p>
     *     Returns archived threads in the channel that are public.
     *     When called on a {@link ChannelType#GUILD_TEXT GUILD_TEXT} channel,
     *     returns threads of {@link Channel#getType() type} {@link ChannelType#ANNOUNCEMENT_THREAD GUILD_PUBLIC_THREAD}.
     *     When called on a {@link ChannelType#GUILD_ANNOUNCEMENT GUILD_ANNOUNCEMENT} channel returns
     *     threads of {@link Channel#getType() type} {@link ChannelType#ANNOUNCEMENT_THREAD GUILD_NEWS_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more with {@link #listPublicArchivedThreads(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all public archived {@link Channel#isThread() threads} for
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
     *     {@link ChannelType#PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link Permission#MANAGE_THREADS MANAGE_THREADS} permissions.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Channel#isThread() threads} by setting
     *     before to the {@link ThreadMetadata#getArchiveTimestamp()} of the last retrieved thread.
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Channel#isThread() threads} for
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
                Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    /**
     *
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link ChannelType#PRIVATE_THREAD GUILD_PRIVATE_THREAD}.
     *     Threads are ordered by {@link ThreadMetadata#getArchiveTimestamp() archive_timestamp}, in descending order.
     *     Requires both the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} and
     *     {@link Permission#MANAGE_THREADS MANAGE_THREADS} permissions.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Channel#isThread() threads} with
     *     {@link #listPrivateArchivedThreads(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Channel#isThread() threads} for
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
     *     {@link ChannelType#PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Channel#isThread() threads} by setting
     *     before to the {@link ThreadMetadata#getArchiveTimestamp()} of the last retrieved thread.
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Channel#isThread() threads} for
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
                Name.CHANNEL_ID.withValue(channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

    /**
     * <p>
     *     Returns archived threads in the channel that are of {@link Channel#getType() type}
     *     {@link ChannelType#PRIVATE_THREAD GUILD_PRIVATE_THREAD},
     *     and the user has joined. Threads are ordered by their {@link Channel#getId() id}, in descending order.
     *     Requires the {@link Permission#READ_MESSAGE_HISTORY READ_MESSAGE_HISTORY} permission.
     * </p>
     *
     * <p>
     *     If {@link ListThreadsResponseBody#hasMore()} is {@code true}, you can retrieve more {@link Channel#isThread() threads} with
     *     {@link #listJoinedPrivateArchivedThreads(String, ISO8601Timestamp, Integer)}
     * </p>
     *
     * @param channelId the id of the {@link Channel} you want to get all private archived {@link Channel#isThread() threads} for
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
     * @param channelId the id of the {@link Channel channel}, to retrieve all {@link Channel#isThread() threads} from
     * @return {@link Queueable} to retrieve a {@link ListThreadsResponseBody}
     * @see Link#LIST_ACTIVE_THREADS
     * @see ListThreadsResponseBody
     */
    @SuppressWarnings("removal")
    @Deprecated(since = "api v10", forRemoval = true)
    default @NotNull Queueable<ListThreadsResponseBody> listActiveThreads(@NotNull String channelId){
        LinkQuery query = new LinkQuery(getLApi(), Link.LIST_ACTIVE_THREADS,
                new PlaceHolder(Name.CHANNEL_ID, channelId));
        return new ConvertingRetriever<>(query, ListThreadsResponseBody::new);
    }

}
