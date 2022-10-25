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

package me.linusdev.lapi.api.objects.message;

import me.linusdev.data.Datable;
import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.application.Application;
import me.linusdev.lapi.api.objects.application.PartialApplication;
import me.linusdev.lapi.api.objects.attachment.Attachment;
import me.linusdev.lapi.api.objects.channel.ChannelMention;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.enums.MessageFlag;
import me.linusdev.lapi.api.objects.enums.MessageType;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.message.concrete.ChannelMessage;
import me.linusdev.lapi.api.objects.message.concrete.CreateEventMessage;
import me.linusdev.lapi.api.objects.message.concrete.UpdateEventMessage;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.message.impl.CreateEventMessageImpl;
import me.linusdev.lapi.api.objects.message.impl.MessageImpl;
import me.linusdev.lapi.api.objects.message.impl.UpdateEventMessageImpl;
import me.linusdev.lapi.api.objects.message.interaction.MessageInteraction;
import me.linusdev.lapi.api.objects.message.messageactivity.MessageActivity;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.nonce.Nonce;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.sticker.StickerItem;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * <p>
 *     This message has only {@link #getId() id} and {@link #getChannelId() channel_id} as not null fields.<br>
 *     All other fields may be null in {@link GatewayEvent#MESSAGE_UPDATE MESSAGE_UPDATE} events.
 *     see <a href="https://discord.com/developers/docs/topics/gateway-events#message-update">discord documentation</a>.
 * </p>
 * <p>
 *     A {@code null} field may correspond to a unknown value.
 * </p>
 * <p>
 *     An app will receive empty values in the {@link #getContent() content}, {@link #getEmbeds() embeds},
 *     {@link #getAttachments() attachments}, and {@link #getComponents() components} fields if they have not configured
 *     (or been approved for) the {@link GatewayIntent#MESSAGE_CONTENT MESSAGE_CONTENT} privileged intent.
 * </p>
 * <br>
 * <h2 style="margin-bottom:0;padding-bottom:0">Limits</h2>
 * <ul>
 *     <li>
 *         A Message can have up to {@value MessageBuilder.Limits#MAX_EMBEDS} {@link Embed Embeds}.
 *         And up to {@value MessageBuilder.Limits#MAX_EMBED_CHARACTERS} characters inside these
 *     </li>
 *     <li>
 *         A Message can have up to {@value MessageBuilder.Limits#MAX_STICKERS} {@link Sticker Stickers}
 *     </li>
 *     <li>
 *         Message {@link #getContent() content} can contain uo to {@value MessageBuilder.Limits#MAX_CONTENT_CHARACTERS} characters
 *     </li>
 * </ul>
 *
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object" target="_top">Message Object</a>
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-structure">Message Structure</a>
 */
@SuppressWarnings("UnnecessaryModifier")
public interface AnyMessage extends Datable, SnowflakeAble, HasLApi {

    /*
    All Message Keys
     */

    public static final String ID_KEY = "id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String AUTHOR_KEY = "author";
    public static final String MEMBER_KEY = "member";
    public static final String CONTENT_KEY = "content";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String EDITED_TIMESTAMP_KEY = "edited_timestamp";
    public static final String TTS_KEY = "tts";
    public static final String MENTION_EVERYONE_KEY = "mention_everyone";
    public static final String MENTIONS_KEY = "mentions";
    public static final String MENTION_ROLES_KEY = "mention_roles";
    public static final String MENTION_CHANNELS_KEY = "mention_channels";
    public static final String ATTACHMENTS_KEY = "attachments";
    public static final String EMBEDS_KEY = "embeds";
    public static final String REACTIONS_KEY = "reactions";
    public static final String NONCE_KEY = "nonce";
    public static final String PINNED_KEY = "pinned";
    public static final String WEBHOOK_ID_KEY = "webhook_id";
    public static final String TYPE_KEY = "type";
    public static final String ACTIVITY_KEY = "activity";
    public static final String APPLICATION_KEY = "application";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String MESSAGE_REFERENCE_KEY = "message_reference";
    public static final String FLAGS_KEY = "flags";
    public static final String REFERENCED_MESSAGE_KEY = "referenced_message";
    public static final String INTERACTION_KEY = "interaction";
    public static final String THREAD_KEY = "thread";
    public static final String COMPONENTS_KEY = "components";
    public static final String STICKER_ITEMS_KEY = "sticker_items";
    public static final String STICKERS_KEY = "stickers";
    public static final String POSITION_KEY = "position";

    /**
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link ChannelMessage Message}
     */
    @Contract("_, null -> null; _, !null -> !null")
    static @Nullable ChannelMessage channelMessageFromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        return new MessageImpl(lApi, data, true);
    }

    /**
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link CreateEventMessage Message}
     */
    @Contract("_, null -> null; _, !null -> !null")
    static @Nullable CreateEventMessage createEventMessageFromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        return new CreateEventMessageImpl(lApi, data, true);
    }

    /**
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link UpdateEventMessage Message}
     */
    @Contract("_, null -> null; _, !null -> !null")
    static @Nullable UpdateEventMessage updateEventMessageFromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        return UpdateEventMessageImpl.newInstance(lApi, data);
    }

    /**
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link AnyMessage Message}
     */
    @Contract("_, null -> null; _, !null -> !null")
    static @Nullable AnyMessage fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        try {
            return new MessageImpl(lApi, data, true);
        } catch (InvalidDataException e) {
            return UpdateEventMessageImpl.newInstance(lApi, data);
        }
    }

    /**
     * @return the message-id as {@link Snowflake}.
     */
    @NotNull Snowflake getIdAsSnowflake();

    /**
     *
     * @return the id as {@link Snowflake} of the channel the message was sent in.
     */
    @NotNull Snowflake getChannelIdAsSnowflake();

    /**
     *
     * @return the id as {@link String} of the channel the message was sent in.
     */
    default @NotNull String getChannelId() {
        return getChannelIdAsSnowflake().asString();
    }

    /**
     * <p>
     *   The author object follows the structure of the user object, but is only a valid user in the case where the
     *   message is generated by a user or bot user. If the message is generated by a webhook, the author object
     *   corresponds to the webhook's id, username, and avatar. You can tell if a message is generated by a webhook by
     *   checking for the webhook_id on the message object.
     * </p>
     * @return the {@link User author} of this message (not guaranteed to be a valid user, see above).
     */
    @Nullable User getAuthor();

    /**
     *
     * @return raw contents of the message as {@link String}.
     */
    @Nullable String getContent();

    /**
     *
     * @return when this message was sent.
     */
    @Nullable ISO8601Timestamp getTimestamp();

    /**
     *
     * @return when this message was edited (or {@code null} if never).
     */
    @Nullable ISO8601Timestamp getEditedTimestamp();

    /**
     *
     * @return {@code true} if this was a TTS (text to speech) message, {@code false} otherwise.
     */
    @Nullable Boolean isTTS();

    /**
     *
     * @return {@code true} if this message mentions everyone, {@code false} otherwise.
     */
    @Nullable Boolean isMentionEveryone();

    /**
     *
     * @return Array of {@link User users} specifically mentioned in the message.
     */
    @Nullable List<User> getMentions();

    /**
     *
     * @return Array of ids (as {@link String}) of {@link Role roles} specifically mentioned in this message.
     */
    @Nullable List<String> getMentionRoles();

    /**
     * <p>
     *     Not all channel mentions in a message will appear in mention_channels. Only textual channels that are
     *     visible to everyone in a lurkable guild will ever be included. Only crossposted messages
     *     (via Channel Following) currently include mention_channels at all. If no mentions in the message meet these
     *     requirements, this field will not be sent.
     * </p>
     * @return channels specifically mentioned in this message.
     */
    @Nullable List<ChannelMention> getMentionChannels();

    /**
     *
     * @return any attached files.
     */
    @Nullable List<Attachment> getAttachments();

    /**
     *
     * @return any embedded content.
     */
    @Nullable List<Embed> getEmbeds();

    /**
     * <p>
     *     reactions to the message.
     * </p>
     * @return Array of {@link Reaction reactions} or {@code null}.
     */
    @Nullable List<Reaction> getReactions();

    /**
     * <p>
     *     used for validating a message was sent.
     * </p>
     * @return {@link Nonce} or {@code null}.
     */
    @Nullable Nonce getNonce();

    /**
     *
     * @return {@code true} if this message is pinned, {@code false} otherwise.
     */
    @Nullable Boolean isPinned();

    /**
     *
     * @return if the message is generated by a webhook, this is the webhook's id as {@link Snowflake}. {@code null} otherwise.
     */
    @Nullable Snowflake getWebhookIdAsSnowflake();

    /**
     *
     * @return if the message is generated by a webhook, this is the webhook's id as {@link String}. {@code null} otherwise.
     */
    default @Nullable String getWebhookId() {
        if(getWebhookIdAsSnowflake() == null) return null;
        return getWebhookIdAsSnowflake().asString();
    }

    /**
     *
     * @return {@link MessageType type} of message
     */
    @Nullable MessageType getType();

    /**
     * 	sent with Rich Presence-related chat embeds.
     * @return {@link MessageActivity} or {@code null}.
     */
    @Nullable MessageActivity getActivity();

    /**
     * sent with Rich Presence-related chat embeds.
     * @return {@link PartialApplication partial application object} or {@code null}.
     */
    @Nullable PartialApplication getApplication();

    /**
     * if the message is an {@link Interaction interaction} or application-owned webhook, this is the id of the application.
     * @return id as {@link Snowflake} of the {@link Application} or {@code null}.
     */
    @Nullable Snowflake getApplicationIdAsSnowflake();

    /**
     * if the message is an {@link Interaction interaction} or application-owned webhook, this is the id of the application.
     * @return id as {@link String} of the {@link Application} or {@code null}.
     */
    default @Nullable String getApplicationId() {
        if(getApplicationIdAsSnowflake() == null) return null;
        return getApplicationIdAsSnowflake().asString();
    }

    /**
     * data showing the source of a {@link MessageFlag#IS_CROSSPOST crosspost}, {@link MessageType#CHANNEL_FOLLOW_ADD channel follow add},
     * {@link MessageType#CHANNEL_PINNED_MESSAGE pin}, or {@link MessageType#REPLY reply} message.
     * @return {@link MessageReference}.
     */
    @Nullable MessageReference getMessageReference();

    /**
     * {@link MessageFlag message flags} combined as a bitfield.
     * @return {@link Long} flags or {@code null}.
     * @see MessageFlag#getFlagsFromBits(Long)
     */
    @Nullable Long getFlags();

    /**
     * @return {@link List} of {@link MessageFlag}.
     * @see #getFlags()
     */
    @Contract(" -> new")
    default @NotNull List<MessageFlag> getFlagsAsList() {
        return MessageFlag.getFlagsFromBits(getFlags());
    }

    /**
     * <p>
     *     This field is only returned for messages with a type of {@link MessageType#REPLY} or {@link MessageType#THREAD_STARTER_MESSAGE}.
     *     If the message is a reply but the {@link #getReferencedMessage() referenced_message} field is not present,
     *     the backend did not attempt to fetch the message that was being replied to, so its state is unknown.
     *     If the field exists but is null, the referenced message was deleted.
     * </p>
     * <p>
     *     You can check if the field exists using {@link OptionalValue#exists()}.
     * </p>
     * @return the message associated with the {@link #getMessageReference() message_reference}.
     */
    @SuppressWarnings("JavadocDeclaration")
    @Nullable OptionalValue<ChannelMessage> getReferencedMessage();

    /**
     * sent if the message is a response to an {@link Interaction Interaction}.
     * @return {@link MessageInteraction} or {@code null}.
     */
    @Nullable MessageInteraction getInteraction();

    /**
     * the thread that was started from this message, includes {@link Channel#getMember() thread member object}.
     * @return {@link Channel#isThread() thread} or {@code null}.
     */
    @Nullable Channel getThread();

    /**
     * <p>
     *     sent if the message contains components like buttons, action rows, or other interactive components.
     * </p>
     * @return Array of {@link Component components} or {@code null}.
     */
    @Nullable List<Component> getComponents();

    /**
     * sent if the message contains stickers.
     * @return Array of {@link StickerItem stickers} or {@code null}.
     */
    @Nullable List<StickerItem> getStickerItems();

    /**
     * <p>
     *     the stickers sent with the message
     * </p>
     * @deprecated replaced by {@link #getStickerItems()}.
     * @return Array of {@link Sticker stickers} or {@code null}.
     */
    @Deprecated
    @Nullable List<Sticker> getStickers();

    /**
     * <p>
     *     A generally increasing integer (there may be gaps or duplicates) that represents the approximate position
     *     of the message in a thread, it can be used to estimate the relative position of the message in a thread in
     *     company with total_message_sent on parent thread.
     * </p>
     * TODO: add Thread#getTotalMessageSent @link
     * @return message position in a thread or {@code null}.
     */
    @Nullable Integer getPosition();

    /**
     * The {@link ImplementationType} of this message. Useful to safely {@link ImplementationType#cast(AnyMessage) cast} this message to
     * its actual class.
     * @return {@link ImplementationType}
     */
    @NotNull ImplementationType getImplementationType();

}
