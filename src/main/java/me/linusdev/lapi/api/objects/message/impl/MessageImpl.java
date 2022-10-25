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

package me.linusdev.lapi.api.objects.message.impl;

import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.application.PartialApplication;
import me.linusdev.lapi.api.objects.attachment.Attachment;
import me.linusdev.lapi.api.objects.channel.ChannelMention;
import me.linusdev.lapi.api.objects.enums.MessageType;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.message.MessageReference;
import me.linusdev.lapi.api.objects.message.Reaction;
import me.linusdev.lapi.api.objects.message.concrete.ChannelMessage;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.message.interaction.MessageInteraction;
import me.linusdev.lapi.api.objects.message.messageactivity.MessageActivity;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.nonce.Nonce;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.sticker.StickerItem;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MessageImpl implements ChannelMessage {

    protected final @NotNull LApi lApi;

    protected final @NotNull Snowflake id;
    protected final @NotNull Snowflake channelId;
    protected final @NotNull User author;
    protected final @NotNull String content;
    protected final @NotNull ISO8601Timestamp timestamp;
    protected final @Nullable ISO8601Timestamp editedTimestamp;
    protected final @NotNull Boolean tts;
    protected final @NotNull Boolean mentionEveryone;
    protected final @NotNull List<User> mentions;
    protected final @NotNull List<String> mentionRoles;
    protected final @Nullable List<ChannelMention> mentionChannels;
    protected final @NotNull List<Attachment> attachments;
    protected final @NotNull List<Embed> embeds;
    protected final @Nullable List<Reaction> reactions;
    protected final @Nullable Nonce nonce;
    protected final @NotNull Boolean pinned;
    protected final @Nullable Snowflake webhookId;
    protected final @NotNull MessageType type;
    protected final @Nullable MessageActivity activity;
    protected final @Nullable PartialApplication application;
    protected final @Nullable Snowflake applicationId;
    protected final @Nullable MessageReference messageReference;
    protected final @Nullable Long flags;
    protected final @NotNull OptionalValue<ChannelMessage> referencedMessage;
    protected final @Nullable MessageInteraction interaction;
    protected final @Nullable Channel thread;
    protected final @Nullable List<Component> components;
    protected final @Nullable List<StickerItem> stickerItems;
    protected final @Nullable List<Sticker> stickers;
    protected final @Nullable Integer position;


    @SuppressWarnings({"ConstantConditions", "ConditionCoveredByFurtherCondition"})
    public MessageImpl(@NotNull LApi lApi, @NotNull SOData data, boolean doNullChecks) throws InvalidDataException {
        this.lApi = lApi;

        id = data.getAndConvert(ID_KEY, Snowflake::fromString);
        channelId = data.getAndConvert(CHANNEL_ID_KEY, Snowflake::fromString);
        author = data.getAndConvertWithException(AUTHOR_KEY, (SOData c) -> User.fromData(lApi, c));
        content = data.getAs(CONTENT_KEY);
        timestamp = data.getAndConvert(TIMESTAMP_KEY, ISO8601Timestamp::fromString);
        editedTimestamp = data.getAndConvert(EDITED_TIMESTAMP_KEY, ISO8601Timestamp::fromString);
        tts = data.getAs(TTS_KEY);
        mentionEveryone = data.getAs(MENTION_EVERYONE_KEY);
        mentions = data.getListAndConvertWithException(MENTIONS_KEY, (SOData c) -> User.fromData(lApi, c));
        mentionRoles = data.getListAndConvert(MENTION_ROLES_KEY, (String c) -> c);
        mentionChannels = data.getListAndConvertWithException(MENTION_CHANNELS_KEY, ChannelMention::fromData);
        attachments = data.getListAndConvertWithException(ATTACHMENTS_KEY, Attachment::fromData);
        embeds = data.getListAndConvertWithException(EMBEDS_KEY, Embed::fromData);
        reactions = data.getListAndConvertWithException(REACTIONS_KEY, (SOData c) -> Reaction.fromData(lApi, c));
        nonce = data.getAndConvert(NONCE_KEY, Nonce::fromStringOrInteger);
        pinned = data.getAs(PINNED_KEY);
        webhookId = data.getAndConvert(WEBHOOK_ID_KEY, Snowflake::fromString);
        type = data.getAndConvertWithException(TYPE_KEY, (Number v) -> {
            if(v == null) return null;
            return MessageType.fromValue(v.intValue());
        });
        activity = data.getAndConvertWithException(ACTIVITY_KEY, MessageActivity::fromData);
        application = data.getAndConvertWithException(APPLICATION_KEY, (SOData c) -> PartialApplication.fromData(lApi, c));
        applicationId = data.getAndConvert(APPLICATION_ID_KEY, Snowflake::fromString);
        messageReference = data.getAndConvertWithException(MESSAGE_REFERENCE_KEY, MessageReference::fromData);
        flags = data.getAndConvert(FLAGS_KEY, (Number v) -> v == null ? null : v.longValue());
        referencedMessage = data.getOptionalValue(REFERENCED_MESSAGE_KEY);
        interaction = data.getAndConvertWithException(INTERACTION_KEY, (SOData c) -> MessageInteraction.fromData(lApi, c));
        thread = data.getAndConvertWithException(THREAD_KEY, (SOData c) -> Channel.channelFromData(lApi, c));
        components = data.getListAndConvertWithException(COMPONENTS_KEY, (SOData c) -> Component.fromData(lApi, c));
        stickerItems = data.getListAndConvertWithException(STICKER_ITEMS_KEY, StickerItem::fromData);
        stickers = data.getListAndConvertWithException(STICKERS_KEY, (SOData c) -> Sticker.fromData(lApi, c));
        position = data.getAs(POSITION_KEY);

        if(doNullChecks) {
            if(id == null || channelId == null || author == null || content == null || timestamp == null ||
                    tts == null || mentionEveryone == null || mentions == null || mentionRoles == null || attachments == null ||
                    embeds == null || pinned == null || type == null) {
                InvalidDataException.throwException(data, null, this.getClass(),
                        new Object[]{id, channelId, author, content, timestamp, tts, mentionEveryone, mentions, mentionRoles, attachments, embeds, pinned, type},
                        new String[]{ID_KEY, CHANNEL_ID_KEY, AUTHOR_KEY, CONTENT_KEY, TIMESTAMP_KEY, TTS_KEY, MENTION_EVERYONE_KEY, MENTIONS_KEY, MENTION_ROLES_KEY, ATTACHMENTS_KEY, EMBEDS_KEY, PINNED_KEY, TYPE_KEY});
            }
        }
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    @Override
    public @NotNull User getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public @NotNull ISO8601Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public @Nullable ISO8601Timestamp getEditedTimestamp() {
        return editedTimestamp;
    }

    @Override
    public @NotNull Boolean isTTS() {
        return tts;
    }

    @Override
    public @NotNull Boolean isMentionEveryone() {
        return mentionEveryone;
    }

    @Override
    public @NotNull List<User> getMentions() {
        return mentions;
    }

    @Override
    public @NotNull List<String> getMentionRoles() {
        return mentionRoles;
    }

    @Override
    public @Nullable List<ChannelMention> getMentionChannels() {
        return mentionChannels;
    }

    @Override
    public @NotNull List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public @NotNull List<Embed> getEmbeds() {
        return embeds;
    }

    @Override
    public @Nullable List<Reaction> getReactions() {
        return reactions;
    }

    @Override
    public @Nullable Nonce getNonce() {
        return nonce;
    }

    @Override
    public @NotNull Boolean isPinned() {
        return pinned;
    }

    @Override
    public @Nullable Snowflake getWebhookIdAsSnowflake() {
        return webhookId;
    }

    @Override
    public @NotNull MessageType getType() {
        return type;
    }

    @Override
    public @Nullable MessageActivity getActivity() {
        return activity;
    }

    @Override
    public @Nullable PartialApplication getApplication() {
        return application;
    }

    @Override
    public @Nullable Snowflake getApplicationIdAsSnowflake() {
        return applicationId;
    }

    @Override
    public @Nullable MessageReference getMessageReference() {
        return messageReference;
    }

    @Override
    public @Nullable Long getFlags() {
        return flags;
    }

    @Override
    public @NotNull OptionalValue<ChannelMessage> getReferencedMessage() {
        return referencedMessage;
    }

    @Override
    public @Nullable MessageInteraction getInteraction() {
        return interaction;
    }

    @Override
    public @Nullable Channel getThread() {
        return thread;
    }

    @Override
    public @Nullable List<Component> getComponents() {
        return components;
    }

    @Override
    public @Nullable List<StickerItem> getStickerItems() {
        return stickerItems;
    }

    @Override
    public @Nullable List<Sticker> getStickers() {
        return stickers;
    }

    @Override
    public @Nullable Integer getPosition() {
        return position;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(1);

        data.add(ID_KEY, id);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(AUTHOR_KEY, author);
        data.add(CONTENT_KEY, content);
        data.add(TIMESTAMP_KEY, timestamp);
        data.add(EDITED_TIMESTAMP_KEY, editedTimestamp);
        data.add(TTS_KEY, tts);
        data.add(MENTION_EVERYONE_KEY, mentionEveryone);
        data.add(MENTIONS_KEY, mentions);
        data.add(MENTION_ROLES_KEY, mentionRoles);
        data.addIfNotNull(MENTION_CHANNELS_KEY, mentionChannels);
        data.add(ATTACHMENTS_KEY, attachments);
        data.add(EMBEDS_KEY, embeds);
        data.addIfNotNull(REACTIONS_KEY, reactions);
        data.addIfNotNull(NONCE_KEY, nonce);
        data.add(PINNED_KEY, pinned);
        data.addIfNotNull(WEBHOOK_ID_KEY, webhookId);
        data.add(TYPE_KEY, type);
        data.addIfNotNull(ACTIVITY_KEY, activity);
        data.addIfNotNull(APPLICATION_KEY, application);
        data.addIfNotNull(APPLICATION_ID_KEY, applicationId);
        data.addIfNotNull(MESSAGE_REFERENCE_KEY, messageReference);
        data.addIfNotNull(FLAGS_KEY, flags);
        if(referencedMessage.exists()) data.addIfNotNull(REFERENCED_MESSAGE_KEY, referencedMessage.get());
        data.addIfNotNull(INTERACTION_KEY, interaction);
        data.addIfNotNull(THREAD_KEY, thread);
        data.addIfNotNull(COMPONENTS_KEY, components);
        data.addIfNotNull(STICKER_ITEMS_KEY, stickerItems);
        data.addIfNotNull(STICKERS_KEY, stickers);
        data.addIfNotNull(POSITION_KEY, position);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
