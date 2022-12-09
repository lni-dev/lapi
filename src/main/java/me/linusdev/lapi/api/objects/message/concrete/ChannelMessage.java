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

package me.linusdev.lapi.api.objects.message.concrete;

import me.linusdev.data.OptionalValue;
import me.linusdev.lapi.api.objects.application.PartialApplication;
import me.linusdev.lapi.api.objects.attachment.Attachment;
import me.linusdev.lapi.api.objects.channel.ChannelMention;
import me.linusdev.lapi.api.objects.enums.MessageType;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.message.*;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.message.interaction.MessageInteraction;
import me.linusdev.lapi.api.objects.message.messageactivity.MessageActivity;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.nonce.Nonce;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.sticker.StickerItem;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface for messages received from discord with all normal fields present.
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-structure">Message Structure</a>
 * @see AnyMessage
 */
public interface ChannelMessage extends AnyMessage {

    @Override
    @NotNull User getAuthor();

    @Override
    @NotNull String getContent();

    @Override
    @NotNull ISO8601Timestamp getTimestamp();

    @Override
    @Nullable ISO8601Timestamp getEditedTimestamp();

    @Override
    @NotNull Boolean isTTS();

    @Override
    @NotNull Boolean isMentionEveryone();

    @Override
    @Nullable List<User> getMentions();

    @Override
    @Nullable List<String> getMentionRoles();

    @Override
    @Nullable List<ChannelMention> getMentionChannels();

    @Override
    @Nullable List<Attachment> getAttachments();

    @Override
    @Nullable List<Embed> getEmbeds();

    @Override
    @Nullable List<Reaction> getReactions();

    @Override
    @Nullable Nonce getNonce();

    @Override
    @NotNull Boolean isPinned();

    @Override
    @Nullable Snowflake getWebhookIdAsSnowflake();

    @Override
    @NotNull MessageType getType();

    @Override
    @Nullable MessageActivity getActivity();

    @Override
    @Nullable PartialApplication getApplication();

    @Override
    @Nullable Snowflake getApplicationIdAsSnowflake();

    @Override
    @Nullable MessageReference getMessageReference();

    @Override
    @Nullable Long getFlags();

    @Override
    @NotNull OptionalValue<ChannelMessage> getReferencedMessage();

    @Override
    @Nullable MessageInteraction getInteraction();

    @Override
    @Nullable Channel getThread();

    @Override
    @Nullable List<Component> getComponents();

    @Override
    @Nullable List<StickerItem> getStickerItems();

    @Override
    @Deprecated
    @Nullable List<Sticker> getStickers();

    @Override
    @Nullable Integer getPosition();

    @Override
    default @NotNull ImplementationType getImplementationType() {
        return ImplementationType.CHANNEL_MESSAGE;
    }
}
