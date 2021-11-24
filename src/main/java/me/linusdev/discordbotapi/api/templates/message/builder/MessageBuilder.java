package me.linusdev.discordbotapi.api.templates.message.builder;

import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LimitException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.discordbotapi.api.objects.message.Message;
import me.linusdev.discordbotapi.api.objects.message.MessageReference;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentType;
import me.linusdev.discordbotapi.api.objects.message.component.actionrow.ActionRow;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.templates.attachment.AttachmentTemplate;
import me.linusdev.discordbotapi.api.templates.message.AllowedMentionType;
import me.linusdev.discordbotapi.api.templates.message.AllowedMentions;
import me.linusdev.discordbotapi.api.templates.message.MessageTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MessageBuilder implements HasLApi {

    private final @NotNull LApi lApi;

    private final StringBuilder content;

    private final Embed[] embeds = new Embed[10];
    private int embedIndex = 0;

    private boolean tts;

    //Allowed mentions
    private @Nullable ArrayList<AllowedMentionType> parse;
    private @Nullable ArrayList<String> roles;
    private @Nullable ArrayList<String> users;
    private boolean repliedUser;

    private @Nullable MessageReference messageReference;

    private @Nullable ArrayList<Component> components;

    private @Nullable ArrayList<String> stickerIds;

    private @Nullable ArrayList<Attachment> attachments;
    private int nextUploadAttachmentId = 0;



    public MessageBuilder(@NotNull LApi lApi, @Nullable String content){
        this.lApi = lApi;
        this.content = new StringBuilder(content == null ? "" : content);

    }

    public MessageTemplate build(){
        return new MessageTemplate(
                content.toString(),
                tts,
                embedIndex == 0 ? null : embeds,
                new AllowedMentions(
                        parse == null ? null : parse.toArray(new AllowedMentionType[0]),
                        roles == null ? null : roles.toArray(new String[0]),
                        users == null ? null : users.toArray(new String[0]),
                        repliedUser),
                messageReference,
                components == null ? null : components.toArray(new Component[0]),
                stickerIds == null ? null : stickerIds.toArray(new String[0]),
                attachments == null ? null : attachments.toArray(new Attachment[0])
        );
    }

    public Queueable<Message> getQueueable(String channelId){
        return lApi.createMessage(channelId, build());
    }

    public MessageBuilder appendUserMention(@NotNull String userId){
        return appendMention(MentionType.USER, new PlaceHolder(PlaceHolder.USER_ID, userId));
    }

    public MessageBuilder appendUserNickNameMention(@NotNull String userId){
        return appendMention(MentionType.USER_NICKNAME, new PlaceHolder(PlaceHolder.USER_ID, userId));
    }

    public MessageBuilder appendRoleMention(@NotNull String roleId){
        return appendMention(MentionType.ROLE, new PlaceHolder(PlaceHolder.ROLE_ID, roleId));
    }

    public MessageBuilder appendChannelMention(@NotNull String channelId){
        return appendMention(MentionType.CHANNEL, new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
    }

    public MessageBuilder appendEveryoneMention(){
        return appendMention(MentionType.EVERYONE);
    }

    public MessageBuilder appendHereMention(){
        return appendMention(MentionType.HERE);
    }

    public MessageBuilder appendMention(MentionType type, PlaceHolder... placeHolders){

        if(type == MentionType.USER || type == MentionType.USER_NICKNAME){
            String id = placeHolders[0].getValue();
            if(users != null) {
                if(!users.contains(id))
                    users.add(id);
            }else{
                users = new ArrayList<>();
                users.add(id);
            }

        }else if(type == MentionType.EVERYONE || type == MentionType.HERE){
            if(parse != null) {
                if (!parse.contains(AllowedMentionType.EVERYONE_MENTIONS))
                    parse.add(AllowedMentionType.EVERYONE_MENTIONS);
            }else{
                parse = new ArrayList<>(1); //This builder will always only add this
                parse.add(AllowedMentionType.EVERYONE_MENTIONS);
            }

        }else if(type == MentionType.ROLE){
            String id = placeHolders[0].getValue();
            if(roles != null) {
                if(!roles.contains(id))
                    roles.add(id);
            }else{
                roles = new ArrayList<>();
                roles.add(id);
            }

        }

        appendContent(type.get(placeHolders));
        return this;
    }

    public MessageBuilder appendEmoji(@NotNull String id, @NotNull String name, boolean animated){
        return appendEmoji(Emoji.of(id, name, animated));
    }

    public MessageBuilder appendEmoji(@NotNull Emoji emoji){

        if(emoji.isStandardEmoji()){
            content.append(emoji.getName());
        }else{
            if(emoji.isAnimated()){
                content.append(MentionType.CUSTOM_EMOJI_ANIMATED.get(
                        new PlaceHolder(PlaceHolder.EMOJI_NAME, emoji.getName()),
                        new PlaceHolder(PlaceHolder.EMOJI_ID, emoji.getId()))
                );
            }else{
                content.append(MentionType.CUSTOM_EMOJI.get(
                        new PlaceHolder(PlaceHolder.EMOJI_NAME, emoji.getName()),
                        new PlaceHolder(PlaceHolder.EMOJI_ID, emoji.getId()))
                );
            }
        }

        return this;
    }

    public MessageBuilder appendTimestamp(long time, @NotNull TimeUnit timeUnit) {
        return appendTimestamp(time, timeUnit, null);
    }

    public MessageBuilder appendTimestamp(long time, @NotNull TimeUnit timeUnit, @Nullable TimestampStyle timestampStyle){
        if(timestampStyle == null){
            content.append(MentionType.TIMESTAMP.get(
                    new PlaceHolder(PlaceHolder.TIMESTAMP, String.valueOf(timeUnit.toSeconds(time)))
            ));

        }else {
            content.append(MentionType.TIMESTAMP_STYLED.get(
                    new PlaceHolder(PlaceHolder.TIMESTAMP, String.valueOf(timeUnit.toSeconds(time))),
                    new PlaceHolder(PlaceHolder.TIMESTAMP_STYLE, timestampStyle.getValue())
            ));

        }

        return this;
    }

    public MessageBuilder appendContent(@Nullable Object content){
        if(content == null) return this;
        this.content.append(content);
        return this;
    }

    public MessageBuilder addEmbed(Embed embed) throws LimitException {
        if(embedIndex == embeds.length) throw new LimitException("A message cannot have more than 10 embeds");
        embeds[embedIndex++] = embed;
        return this;
    }

    /**
     * whether this message should be a text-to-speech message
     * @param tts
     */
    public MessageBuilder setTTS(boolean tts) {
        this.tts = tts;
        return this;
    }

    /**
     *
     * @param message the message to reference (reply) to
     * @param fallIfNotExists if {@code true}, you will get an error after sending the message, if the referenced message does not exist
     */
    public MessageBuilder setMessageReference(@NotNull Message message, boolean fallIfNotExists) {
        this.messageReference = new MessageReference(
                message.getIdAsSnowflake(),
                message.getChannelIdAsSnowflake(),
                message.getGuildIdAsSnowflake(),
                fallIfNotExists);
        return this;
    }

    /**
     * @param message the message to reference (reply) to
     * @see #setMessageReference(Message, boolean)
     */
    public void setMessageReference(@NotNull Message message) {
        setMessageReference(message, true);
    }
    
    /**
     *
     * @param message the msg to reply to
     * @param fallIfNotExists if {@code true}, you will get an error after sending the message, if the referenced message does not exist
     * @param mentionUser whether to user the author of the referenced message
     */
    public MessageBuilder setReplyTo(@NotNull Message message, boolean fallIfNotExists, boolean mentionUser){
        setMessageReference(message, fallIfNotExists);
        repliedUser = mentionUser;
        return this;
    }

    /**
     *
     * @param message the msg to reply to
     * @param mentionUser whether to user the author of the referenced message
     * @see #setReplyTo(Message, boolean, boolean) 
     */
    public MessageBuilder setReplyTo(@NotNull Message message, boolean mentionUser){
        return setReplyTo(message, true, mentionUser);
    }

    
    public MessageBuilder addAttachment(Attachment attachment) throws LimitException {

        if(attachments == null) attachments = new ArrayList<>();

        attachments.add(attachment);
        if(attachment instanceof AttachmentTemplate){
            ((AttachmentTemplate) attachment).setAttachmentId(nextUploadAttachmentId++);
        }
        return this;
    }

    public MessageBuilder addComponent(Function<ArrayList<Component>, ArrayList<Component>> consumer){
        if(components == null) components = new ArrayList<>();

        ActionRow actionRow = new ActionRow(lApi, ComponentType.ACTION_ROW, consumer.apply(new ArrayList<>()).toArray(new Component[0]));
        components.add(actionRow);

        return this;
    }

    public MessageBuilder addComponent(ActionRow actionRow){
        if(components == null) components = new ArrayList<>(2);
        components.add(actionRow);
        return this;
    }

    public MessageBuilder addSticker(String stickerId){
        if(stickerIds == null) stickerIds = new ArrayList<>(3);

        stickerIds.add(stickerId);

        return this;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
