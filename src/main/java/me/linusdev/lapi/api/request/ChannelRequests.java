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

package me.linusdev.lapi.api.request;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.message.embed.Embed;
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
     * @see Queueable#completeHereAndIgnoreQueueThread()
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
     *                      --------------------                     *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //TODO: add more requests
}
