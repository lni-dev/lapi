package me.linusdev.discordbotapi.api.objects.message.abstracts;

import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.application.Application;
import me.linusdev.discordbotapi.api.objects.attachment.Attachment;
import me.linusdev.discordbotapi.api.objects.channel.ChannelMention;
import me.linusdev.discordbotapi.api.objects.enums.MessageFlag;
import me.linusdev.discordbotapi.api.objects.enums.MessageType;
import me.linusdev.discordbotapi.api.objects.guild.member.Member;
import me.linusdev.discordbotapi.api.objects.message.MessageReference;
import me.linusdev.discordbotapi.api.objects.message.Reaction;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.objects.message.interaction.MessageInteraction;
import me.linusdev.discordbotapi.api.objects.message.messageactivity.MessageActivity;
import me.linusdev.discordbotapi.api.objects.message.nonce.Nonce;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.sticker.StickerItem;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.toodo.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.toodo.Role;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.user.UserMention;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**

 * I was thinking about making several Interfaces for different types of messages, like
 * I did with the channels, but I decided against it, duo to its complexity. Like a Reply and a Thread-Starter-Message
 * having the same field (message_reference) but are different types of messages. It would also be really annoying to cast
 * each message depending on it's type. <br><br>
 *
 *
 * <h1 style="margin-bottom:0;padding-bottom:0"><a href="" target="_top">Limits</a></h1>
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
 * <h1 style="margin-bottom:0;padding-bottom:0"><a href="https://discord.com/developers/docs/resources/channel#message-types" target="_top">Message Types</a> ({@link MessageType})</h1>
 * <p>There are multiple message types that have a message_reference object. Since message references are generic attribution
 * to a previous message, there will be more types of messages which have this information in the future.
 * </p>
 *
 * <ul><h3 style="margin-bottom:0;padding-bottom:0"> <a href="https://discord.com/developers/docs/resources/channel#message-types-crosspost-messages" target="_top">Crosspost messages</a> </h3>
 *     <li>
 *         These are messages that originated from another channel ({@link MessageFlag#IS_CROSSPOST IS_CROSSPOST} flag).
 *          <br> see {@link #getFlagsAsMessageFlags()}
 *     </li>
 *     <li>
 *         These messages have all three fields, with data of the original message that was crossposted.
 *     </li>
 * </ul>
 *
 *
 * <ul><h3 style="margin-bottom:0;padding-bottom:0"> <a href="https://discord.com/developers/docs/resources/channel#message-types-channel-follow-add-messages" target="_top">Channel Follow Add messages</a></h3>
 *      <li>
 *          These are automatic messages sent when a channel is followed into the current channel ({@link MessageType#CHANNEL_FOLLOW_ADD type 12}).
 *      </li>
 *      <li>
 *          These messages have the {@link MessageReference#getChannelId() channel_id} and {@link MessageReference#getGuildId() guild_id} fields, with data of the followed announcement channel.
 *      </li>
 * </ul>
 *
 * <ul><h3 style="margin-bottom:0;padding-bottom:0"> <a href="https://discord.com/developers/docs/resources/channel#message-types-pin-messages" target="_top">Pin messages</a></h3>
 *      <li>
 *          These are automatic messages sent when a message is pinned ({@link MessageType#CHANNEL_PINNED_MESSAGE type 6}).
 *      </li>
 *      <li>
 *          These messages have {@link MessageReference#getMessageId() message_id} and {@link MessageReference#getChannelId() channel_id}, and {@link MessageReference#getGuildId() guild_id} (when testing guild_id was missing) if it is in a guild, with data of the message that was pinned.
 *      </li>
 * </ul>
 *
 * <ul><h3 style="margin-bottom:0;padding-bottom:0"> <a href="https://discord.com/developers/docs/resources/channel#message-types-replies" target="_top">Replies</a></h3>
 *      <li>
 *          These are messages replying to a previous message ({@link MessageType#REPLY type 19}).
 *      </li>
 *      <li>
 *          These messages have {@link MessageReference#getMessageId() message_id} and {@link MessageReference#getChannelId() channel_id}, and {@link MessageReference#getGuildId() guild_id} if it is in a guild, with data of the message that was replied to. The channel_id and guild_id will be the same as the reply.
 *      </li>
 *      <li>
 *          Replies are created by including a {@link #getMessageReference() message_reference} when sending a message. When sending, only {@link #getId() message_id} is required.
 *          <br>
 *      </li>
 * </ul>
 *
 * <ul><h3 style="margin-bottom:0;padding-bottom:0"> <a href="https://discord.com/developers/docs/resources/channel#message-types-thread-starter-messsage" target="_top">Thread starter messsage</a></h3>
 *      <li>
 *          These are the first message in a public thread. They point back to the message in the parent channel from which the thread was started ({@link MessageType#THREAD_STARTER_MESSAGE type 21})
 *      </li>
 *      <li>
 *          These messages have {@link MessageReference#getMessageId() message_id}, {@link MessageReference#getChannelId() channel_id}, and {@link MessageReference#getGuildId() guild_id}.
 *      </li>
 *      <li>
 *          These messages will never have content, embeds, or attachments, mainly just the {@link #getMessageReference() message_reference}  and {@link #getReferencedMessage() referenced_message} fields.
 *      </li>
 * </ul>
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object" target="_top">Message Object</a>
 */
public interface Message extends Datable, SnowflakeAble {

    /**
     * id of the message as {@link Snowflake}
     */
    @NotNull Snowflake getIdAsSnowflake();

    /**
     * id of the message as {@link String}
     */
    default @NotNull String getId(){
        return getIdAsSnowflake().asString();
    }

    /**
     * id as {@link Snowflake} of the guild the message was sent in or {@code null} if this message
     * was not sent in a Guild
     */
    @Nullable Snowflake getGuildIdAsSnowflake();

    /**
     * id as {@link String} of the guild the message was sent in or {@code null} if this message
     * was not sent in a Guild
     */
    default @Nullable String getGuildId(){
        Snowflake snowflake = getGuildIdAsSnowflake();
        if(snowflake == null) return null;
        return snowflake.asString();
    }

    /**
     * id of the channel the message was sent in as {@link Snowflake}
     */
    @NotNull Snowflake getChannelIdAsSnowflake();

    /**
     * id of the channel the message was sent in as {@link String}
     */
    default @NotNull String getChannelId(){
        return getChannelIdAsSnowflake().asString();
    }

    /**
     * the author of this message (not guaranteed to be a valid user, see below)
     * <br><br>
     * The author object follows the structure of the user
     * object, but is only a valid user in the case where
     * the message is generated by a user or bot user.
     * If the message is generated by a webhook,
     * the author object corresponds to the webhook's id,
     * username, and avatar. You can tell if a message
     * is generated by a webhook by checking for the
     * webhook_id on the message object.
     *
     * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-structure" target="_top" >Message Structure</a>
     */
    @NotNull User getAuthor();

    /**
     * member properties for this message's author
     *
     * The member object exists in MESSAGE_CREATE and MESSAGE_UPDATE events from text-based guild channels,
     * provided that the author of the message is not a webhook. This allows bots to obtain real-time member
     * data without requiring bots to store member state in memory.
     *
     * todo add @links
     */
    @Nullable Member getMember();

    /**
     * contents of the message
     */
    @NotNull String getContent();

    /**
     * when this message was sent
     *
     * @return ISO8601 timestamp
     */
    @NotNull ISO8601Timestamp getTimestamp();

    /**
     * when this message was edited (or {@code null} if never)
     * @return ISO8601 timestamp
     */
    @Nullable ISO8601Timestamp getEditedTimestamp();

    /**
     * whether this was a TTS message
     *
     * @return true if this was a TTS message
     */
    boolean isTTS();

    /**
     * whether this message mentions everyone
     *
     * @return {@code true} if this message mentions everyone
     */
    boolean mentionsEveryone();

    /**
     * users specifically mentioned in the message
     *
     * todo check array content in private message and in guild
     *
     * The user objects in the mentions array will
     * only have the partial member field present
     * in MESSAGE_CREATE and MESSAGE_UPDATE events
     * from text-based guild channels.
     * todo add @link
     *
     * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-structure" target="_top" >Message Structure</a>
     */
    @NotNull UserMention[] getMentions();

    /**
     * roles specifically mentioned in this message
     */
    @NotNull Role[] getRoleMentions();

    /**
     * channels specifically mentioned in this message
     */
    @Nullable ChannelMention[] getChannelMentions();

    /**
     * any attached files
     */
    @NotNull Attachment[] getAttachments();

    /**
     * any embedded content
     */
    @NotNull Embed[] getEmbeds();

    /**
     * reactions to the message
     */
    @Nullable Reaction[] getReactions();

    /**
     * used for validating a message was sent
     */
    @Nullable Nonce getNonce();

    /**
     * whether this message is pinned
     *
     * @return {@code true} if this message is pinned
     */
    boolean isPinned();

    /**
     * if the message is generated by a webhook, this is the webhook's id as {@link Snowflake}.
     * {@code null} if this message was not generated by a webhook
     */
    @Nullable Snowflake getWebhookIdAsSnowflake();

    /**
     * if the message is generated by a webhook, this is the webhook's id as {@link String}
     * {@code null} if this message was not generated by a webhook
     */
    default @Nullable String getWebhookId(){
        Snowflake snowflake = getWebhookIdAsSnowflake();
        if(snowflake == null) return null;
        return snowflake.asString();
    }

    /**
     * type of message as int
     *
     * @see #getTypeAsMessageType()
     * @see MessageType
     * @see  <a href="https://discord.com/developers/docs/resources/channel#message-object-message-types" target="_top">Message Types</a>
     */
    int getTypeAsValue();

    /**
     * type of message as {@link MessageType}
     *
     * @see MessageType
     * @see  <a href="https://discord.com/developers/docs/resources/channel#message-object-message-types" target="_top">Message Types</a>
     */
    default @Nullable MessageType getTypeAsMessageType(){
        return MessageType.fromValue(getTypeAsValue());
    }

    /**
     * sent with Rich Presence-related chat embeds
     */
    @Nullable MessageActivity getMessageActivity();

    /**
     * sent with Rich Presence-related chat embeds
     * partial application object!
     */
    @Nullable Application getApplication();

    /**
     * if the message is a response to an Interaction, this is the id of the interaction's application as {@link Snowflake},
     * {@code null} otherwise
     */
    @Nullable Snowflake getApplicationIdAsSnowflake();

    /**
     * if the message is a response to an Interaction, this is the id of the interaction's application as {@link String},
     * {@code null} otherwise
     */
    default @Nullable String getApplicationId(){
        Snowflake snowflake = getApplicationIdAsSnowflake();
        if(snowflake == null) return null;
        return getApplicationIdAsSnowflake().asString();
    }

    /**
     * data showing the source of a crosspost, channel follow add, pin, or reply message
     */
    @Nullable MessageReference getMessageReference();

    /**
     * message flags combined as a bitfield
     */
    @Nullable Long getFlagsAsLong();

    /**
     * List of {@link MessageFlag} set for this {@link Message}
     * 
     * Changes to this List do not have any effect on this Message (on Discord, they might affect this Object)!
     * 
     * Every method call will create a new List!
     * @see MessageFlag#getFlagsFromBits(Long)
     */
    default @NotNull List<MessageFlag> getFlagsAsMessageFlags(){
        return MessageFlag.getFlagsFromBits(getFlagsAsLong());
    }

    /**
     * the message associated with the message_reference
     * <br><br>
     * This field is only returned for messages with a type of 19 (REPLY) or 21 (THREAD_STARTER_MESSAGE). If the message is a reply but the referenced_message field is not present, the backend did not attempt to fetch the message that was being replied to, so its state is unknown. If the field exists but is null, the referenced message was deleted.
     * @see  <a href="https://discord.com/developers/docs/resources/channel#message-object-message-types" target="_top">Message Types</a>
     */
    @Nullable Message getReferencedMessage();

    /**
     * sent if the message is a response to an Interaction
     */
    @Nullable MessageInteraction getInteraction();

    /**
     * the thread that was started from this message, includes thread member object
     *
     * todo check if this really gives me a full JSON for a Thread
     */
    @Nullable Thread getThread();

    /**
     * sent if the message contains components like buttons, action rows, or other interactive components
     */
    @Nullable Component[] getComponents();


    /**
     * sent if the message contains stickers
     */
    @Nullable StickerItem[] getStickerItems();

    /**
     * the stickers sent with the message
     */
    @Deprecated @Nullable Sticker[] getStickers();


}
