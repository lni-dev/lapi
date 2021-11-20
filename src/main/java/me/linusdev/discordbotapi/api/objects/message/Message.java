package me.linusdev.discordbotapi.api.objects.message;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.objects.application.Application;
import me.linusdev.discordbotapi.api.objects.channel.ChannelMention;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.enums.MessageFlag;
import me.linusdev.discordbotapi.api.objects.guild.member.Member;
import me.linusdev.discordbotapi.api.objects.message.abstracts.AbstractMessage;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.interaction.MessageInteraction;
import me.linusdev.discordbotapi.api.objects.message.messageactivity.MessageActivity;
import me.linusdev.discordbotapi.api.objects.message.nonce.Nonce;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.sticker.StickerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message Message} Implementation
 *
 * @see me.linusdev.discordbotapi.api.objects.message.abstracts.Message Message for more information
 */
public class Message extends AbstractMessage {


    private @Nullable Snowflake guildId;
    private @Nullable Member member;
    private @Nullable ChannelMention[] mentionChannels;
    private @Nullable Reaction[] reactions;
    private @Nullable Nonce nonce;
    private @Nullable Snowflake webhookId;
    private @Nullable MessageActivity activity;
    private @Nullable Application application;
    private @Nullable Snowflake applicationId;
    private @Nullable MessageReference messageReference;
    private @NotNull List<MessageFlag> flags;
    private @Nullable Long flagsAsLong;
    private @Nullable Message referencedMessage;
    private @Nullable MessageInteraction interaction;
    private @Nullable Thread thread;
    private @Nullable Component[] components;
    private @Nullable StickerItem[] stickerItems;
    private @Nullable @Deprecated Sticker[] stickers;


    /**
     * @param data {@link Data} with required fields
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #CHANNEL_ID_KEY}, {@link #CONTENT_KEY}, {@link #TIMESTAMP_KEY}, {@link #TTS_KEY}, {@link #MENTION_EVERYONE_KEY}, {@link #MENTIONS_KEY}, {@link #MENTION_ROLES_KEY}, {@link #ATTACHMENTS_KEY}, {@link #EMBEDS_KEY}, {@link #PINNED_KEY} or {@link #TYPE_KEY} are missing or null
     */
    @SuppressWarnings("unchecked cast")
    public Message(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        super(lApi, data);

        String guildId = (String) data.get(GUILD_ID_KEY);
        Data member = (Data) data.get(MEMBER_KEY);
        ArrayList<Object> mentionChannelsData = (ArrayList<Object>) data.get(MENTION_CHANNELS_KEY);
        ArrayList<Object> reactionsData = (ArrayList<Object>) data.get(REACTIONS_KEY);
        Object nonce = (Object) data.get(NONCE_KEY);
        String webhookId = (String) data.get(WEBHOOK_ID_KEY);
        Data activity = (Data) data.get(ACTIVITY_KEY);
        Data application = (Data) data.get(APPLICATION_KEY);
        String applicationId = (String) data.get(APPLICATION_ID_KEY);
        Data messageReference = (Data) data.get(MESSAGE_REFERENCE_KEY);
        Number flags = (Number) data.get(FLAGS_KEY);
        Data referencedMessage = (Data) data.get(REFERENCED_MESSAGE_KEY);
        Data interaction = (Data) data.get(INTERACTION_KEY);
        Data thread = (Data) data.get(THREAD_KEY);
        ArrayList<Object> components = (ArrayList<Object>) data.get(COMPONENTS_KEY);
        ArrayList<Object> stickerItems = (ArrayList<Object>) data.get(STICKER_ITEMS_KEY);
        ArrayList<Object> stickers = (ArrayList<Object>) data.get(STICKERS_KEY);


        this.guildId = Snowflake.fromString(guildId);
        this.member = Member.fromData(lApi, member);

        if(mentionChannelsData != null){
            this.mentionChannels = new ChannelMention[mentionChannelsData.size()];
            int i = 0;
            for(Object o : mentionChannelsData)
                this.mentionChannels[i++] = new ChannelMention((Data) o);
        }else {
            this.mentionChannels = null;
        }

        if(reactionsData != null){
            this.reactions = new Reaction[reactionsData.size()];
            int i = 0;
            for(Object o : reactionsData)
                this.reactions[i++] = Reaction.fromData(lApi, (Data) o);
        }else {
            this.reactions = null;
        }

        this.nonce = Nonce.fromStringOrInteger(nonce);
        this.webhookId = Snowflake.fromString(webhookId);
        this.activity = activity == null ? null : MessageActivity.fromData(activity);
        this.application = Application.fromData(lApi, application);
        this.applicationId = Snowflake.fromString(applicationId);
        this.messageReference = MessageReference.fromData(messageReference);
        this.flagsAsLong = flags == null ? null : flags.longValue();
        this.flags = MessageFlag.getFlagsFromBits(flags == null ? null : flags.longValue());
        this.referencedMessage = referencedMessage == null ? null : new Message(lApi, referencedMessage);
        this.interaction = MessageInteraction.fromData(lApi, interaction);
        this.thread = thread == null ? null : (Thread) Channel.fromData(lApi, thread);

        if(components != null){
            this.components = new Component[components.size()];
            int i = 0;
            for(Object o : components)
                this.components[i++] = Component.fromData(lApi, (Data) o);
        }else {
            this.components = null;
        }

        if(stickerItems != null){
            this.stickerItems = new StickerItem[stickerItems.size()];
            int i = 0;
            for(Object o : stickerItems)
                this.stickerItems[i++] = StickerItem.fromData((Data) o);
        }else {
            this.stickerItems = null;
        }

        if(stickers != null){
            this.stickers = new Sticker[stickers.size()];
            int i = 0;
            for(Object o : stickers)
                this.stickers[i++] = Sticker.fromData(lApi, (Data) o);
        }else {
            this.stickers = null;
        }

    }

    @Override
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    @Override
    public @Nullable Member getMember() {
        return member;
    }

    @Override
    public @Nullable ChannelMention[] getChannelMentions() {
        return mentionChannels;
    }

    @Override
    public @Nullable Reaction[] getReactions() {
        return reactions;
    }

    @Override
    public @Nullable Nonce getNonce() {
        return nonce;
    }

    @Override
    public @Nullable Snowflake getWebhookIdAsSnowflake() {
        return webhookId;
    }

    @Override
    public @Nullable MessageActivity getMessageActivity() {
        return activity;
    }

    @Override
    public @Nullable Application getApplication() {
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
    public @Nullable Long getFlagsAsLong() {
        return flagsAsLong;
    }

    @Override
    public @NotNull List<MessageFlag> getFlagsAsMessageFlags() {
        return flags;
    }

    @Override
    public @Nullable Message getReferencedMessage() {
        return referencedMessage;
    }

    @Override
    public @Nullable MessageInteraction getInteraction() {
        return interaction;
    }

    @Override
    public @Nullable Thread getThread() {
        return thread;
    }

    @Override
    public @Nullable Component[] getComponents() {
        return components;
    }

    @Override
    public @Nullable StickerItem[] getStickerItems() {
        return stickerItems;
    }

    @Override
    @Deprecated
    public @Nullable Sticker[] getStickers() {
        return stickers;
    }

    /**
     *
     * @return {@link Data} for this {@link Message}
     */
    @Override
    public @NotNull Data getData() {
        Data data = super.getData();

        if(guildId != null) data.add(GUILD_ID_KEY, guildId);
        if(member != null) data.add(MEMBER_KEY, member);
        if(mentionChannels != null) data.add(MENTION_CHANNELS_KEY, mentionChannels);
        if(reactions != null) data.add(REACTIONS_KEY, reactions);
        if(nonce != null) data.add(NONCE_KEY, nonce);
        if(webhookId != null) data.add(WEBHOOK_ID_KEY, webhookId);
        if(activity != null) data.add(ACTIVITY_KEY, activity);
        if(application != null) data.add(APPLICATION_KEY, application);
        if(applicationId != null) data.add(APPLICATION_ID_KEY, applicationId);
        if(messageReference != null) data.add(MESSAGE_REFERENCE_KEY, messageReference);
        if(flagsAsLong != null) data.add(FLAGS_KEY, flagsAsLong);
        if(referencedMessage != null) data.add(REFERENCED_MESSAGE_KEY, referencedMessage);
        if(interaction != null) data.add(INTERACTION_KEY, interaction);
        if(thread != null) data.add(THREAD_KEY, thread);
        if(components != null) data.add(COMPONENTS_KEY, components);
        if(stickerItems != null) data.add(STICKER_ITEMS_KEY, stickerItems);
        if(stickers != null) data.add(STICKERS_KEY, stickers);

        return data;
    }


}
