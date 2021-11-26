package me.linusdev.discordbotapi.api.templates.message.builder;

import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LimitException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.discordbotapi.api.objects.message.Message;
import me.linusdev.discordbotapi.api.objects.message.MessageReference;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentType;
import me.linusdev.discordbotapi.api.objects.message.component.actionrow.ActionRow;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.objects.toodo.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.toodo.Role;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.templates.attachment.AttachmentTemplate;
import me.linusdev.discordbotapi.api.templates.message.AllowedMentionType;
import me.linusdev.discordbotapi.api.templates.message.AllowedMentions;
import me.linusdev.discordbotapi.api.templates.message.MessageTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This Builder can create you a rather complex {@link MessageTemplate}.<br><br>
 *
 * <h3>Example:</h3>
 * This will send a message with the content:<br>
 * "Hello, @everyone how are you?" <br>
 * in the channel with id = 912377387868639282
 * <pre>
 *     {@code new MessageBuilder(api)
 *                 .appendContent("Hello, ")
 *                 .appendEveryoneMention()
 *                 .appendContent(" how are you?")
 *                 .getQueueable("912377387868639282")
 *                 .queue();
 *     }
 * </pre>
 *
 * <h3>Mention-safe messages</h3>
 * If you create a message containing user input, it is recommended to use {@link #setNoMentionsAllowed()},
 * this will assure, that the message cannot mention @everyone, @here, any role or users. You can still append
 * mentions with {@link #appendUserMention(String)} and the other appendMention() methods. These will then be
 * mentioned again.<br>
 * {@link #appendEveryoneMention()} and {@link #appendHereMention()} must be called after {@link #setNoMentionsAllowed()},
 * if you want your message to mention @everyone or @here.
 */
public class MessageBuilder implements HasLApi {

    /**
     * This class holds a bunch of limits of a {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message Message}
     */
    public static final class Limits{
        public static final int MAX_EMBEDS = 10;
        public static final int MAX_EMBED_CHARACTERS = 6000;

        public static final int MAX_CONTENT_CHARACTERS = 2000;

        public static final int MAX_STICKERS = 3;
    }



    private final @NotNull LApi lApi;

    private final StringBuilder content;

    private ArrayList<Embed> embeds = null;

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

    /**
     *
     * @param lApi {@link LApi}
     */
    public MessageBuilder(@NotNull LApi lApi){
        this(lApi, null);
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param content content for the message
     */
    public MessageBuilder(@NotNull LApi lApi, @Nullable String content){
        this.lApi = lApi;
        this.content = content == null ? new StringBuilder() : new StringBuilder(content);

    }

    /**
     * checks if this would build a valid {@link MessageTemplate}.<br>
     *
     * Does not check if the Embeds have less than {@value Limits#MAX_EMBED_CHARACTERS} or
     * if the message is within the 8 MB (including all attachments and the http request) limit.
     *
     * @return this
     */
    public MessageBuilder check() throws LimitException {
        if(content.length() > Limits.MAX_CONTENT_CHARACTERS)
            throw new LimitException("Message content may not be bigger than " + Limits.MAX_CONTENT_CHARACTERS + " characters");

        if(embeds != null && embeds.size() > Limits.MAX_EMBEDS)
            throw new LimitException("Message may not have more than " + Limits.MAX_EMBEDS + " embeds");

        if(stickerIds != null && stickerIds.size() > Limits.MAX_STICKERS)
            throw new LimitException("Message may not have more than " + Limits.MAX_STICKERS + " stickers");

        if(content.length() == 0 && embeds == null && stickerIds == null && attachments == null){
            throw new LimitException("Cannot create a completely empty message!");
        }
        return this;
    }

    /**
     * Will {@link #check()} and then build a {@link MessageTemplate}
     * @return {@link MessageTemplate} built with this builder
     */
    public MessageTemplate build() throws LimitException {
        return build(true);
    }

    /**
     *
     * @param check whether to {@link #check()} before building
     * @return {@link MessageTemplate} built with this builder
     */
    public MessageTemplate build(boolean check) throws LimitException {

        if(check) check();

        return new MessageTemplate(
                content.length() == 0 ? null : content.toString(),
                tts,
                embeds == null ? null : embeds.toArray(new Embed[0]),
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

    /**
     *
     * @param channelId the id of the {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel channel} the message should be sent in
     * @return {@link Queueable} to create a message
     */
    public Queueable<Message> getQueueable(@NotNull String channelId) throws LimitException {
        return lApi.createMessage(channelId, build());
    }

    /**
     * This will append a {@link MentionType#USER user mention} to the content
     * and add this user's id to the {@link AllowedMentions allowed mentions}
     * @param userId the id of the {@link User user} to mention
     * @return this
     */
    public MessageBuilder appendUserMention(@NotNull String userId){
        return appendMention(MentionType.USER, new PlaceHolder(PlaceHolder.USER_ID, userId));
    }

    /**
     * This will append a {@link MentionType#USER user mention} to the content
     * and add this user's id to the {@link AllowedMentions allowed mentions}
     * @param user the {@link User user} to mention
     * @return this
     */
    public MessageBuilder appendUserMention(@NotNull User user){
        return appendUserMention(user.getId());
    }

    /**
     * This will append a {@link MentionType#USER_NICKNAME user-nickname mention} to the content
     * and add this user's id to the {@link AllowedMentions allowed mentions}
     * @param userId the id of the {@link User user} to mention
     * @return this
     */
    public MessageBuilder appendUserNickNameMention(@NotNull String userId){
        return appendMention(MentionType.USER_NICKNAME, new PlaceHolder(PlaceHolder.USER_ID, userId));
    }

    /**
     * This will append a {@link MentionType#USER_NICKNAME user-nickname mention} to the content
     * and add this user's id to the {@link AllowedMentions allowed mentions}
     * @param user the {@link User user} to mention
     * @return this
     */
    public MessageBuilder appendUserNickNameMention(@NotNull User user){
        return appendUserNickNameMention(user.getId());
    }

    /**
     * This will append a {@link MentionType#ROLE role mention} at the end of the content
     * and add this role's id to the {@link AllowedMentions allowed mentions}
     * @param roleId the id of the {@link Role role} to mention
     * @return this
     */
    public MessageBuilder appendRoleMention(@NotNull String roleId){
        return appendMention(MentionType.ROLE, new PlaceHolder(PlaceHolder.ROLE_ID, roleId));
    }

    /**
     * This will append a {@link MentionType#ROLE role mention} at the end of the content
     * and add this role's id to the {@link AllowedMentions allowed mentions}
     * @param role the {@link Role role} to mention
     * @return this
     */
    public MessageBuilder appendRoleMention(@NotNull Role role){
        return appendRoleMention(role.getId());
    }

    /**
     * This will append a {@link MentionType#CHANNEL channel mention} at the end of the current content.
     * {@link AllowedMentions allowed mentions} is not required for channels and will not be changed
     * @param channelId the id of the {@link Channel channel} you want to mention
     * @return this
     */
    public MessageBuilder appendChannelMention(@NotNull String channelId){
        return appendMention(MentionType.CHANNEL, new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId));
    }

    /**
     * This will append a {@link MentionType#CHANNEL channel mention} at the end of the current content.
     * {@link AllowedMentions allowed mentions} is not required for channels and will not be changed
     * @param channel the {@link Channel channel} you want to mention
     * @return this
     */
    public MessageBuilder appendChannelMention(@NotNull Channel channel){
        return appendChannelMention(channel.getId());
    }

    /**
     * This will append {@link MentionType#EVERYONE mention @everyone} at the end of the current content
     * and add this @everyone to the {@link AllowedMentions allowed mentions}
     * @return this
     */
    public MessageBuilder appendEveryoneMention(){
        return appendMention(MentionType.EVERYONE);
    }

    /**
     * This will append {@link MentionType#HERE mention @here} at the end of the current content
     * and add this @everyone to the {@link AllowedMentions allowed mentions}
     * @return this
     */
    public MessageBuilder appendHereMention(){
        return appendMention(MentionType.HERE);
    }

    /**
     *
     * This will add a mention to the content.<br><br>
     *
     * I recommend, you use:<br>
     * - {@link #appendUserMention(String)}<br>
     * - {@link #appendUserNickNameMention(String)}<br>
     * - {@link #appendRoleMention(String)}<br>
     * - {@link #appendChannelMention(String)}<br>
     * - {@link #appendEveryoneMention()}, {@link #appendHereMention()}<br>
     * - {@link #appendEmoji(String, String, boolean)}<br>
     * - {@link #appendTimestamp(long, TimeUnit, TimestampStyle)}<br>
     *
     * @param type {@link MentionType}
     * @param placeHolders ids and stuff required for the mention
     * @return this
     */
    public MessageBuilder appendMention(MentionType type, PlaceHolder... placeHolders){

        if(type == MentionType.USER || type == MentionType.USER_NICKNAME){
            //only add user to users array, if parse does not contain USER_MENTIONS
            if(parse == null || !parse.contains(AllowedMentionType.USER_MENTIONS)) {
                String id = placeHolders[0].getValue();
                if (users != null) {
                    if (!users.contains(id))
                        users.add(id);
                } else {
                    users = new ArrayList<>();
                    users.add(id);
                }
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
            //only add role to roles array, if parse does not contain ROLE_MENTIONS
            if(parse == null || !parse.contains(AllowedMentionType.ROLE_MENTIONS)) {
                String id = placeHolders[0].getValue();
                if (roles != null) {
                    if (!roles.contains(id))
                        roles.add(id);
                } else {
                    roles = new ArrayList<>();
                    roles.add(id);
                }
            }
        }

        appendContent(type.get(placeHolders));
        return this;
    }

    /**
     * This will append a custom emoji at the end of the current content. If you want
     * to add a {@link me.linusdev.discordbotapi.api.objects.emoji.StandardEmoji standard emoji}, see {@link #appendEmoji(Emoji)}
     * 
     * @param id the id of the {@link Emoji}
     * @param name the name of the {@link Emoji}
     * @param animated whether this is an animated {@link Emoji}
     * @return this
     */
    public MessageBuilder appendEmoji(@NotNull String id, @NotNull String name, boolean animated){
        return appendEmoji(Emoji.of(id, name, animated));
    }

    /**
     * This will append a custom or {@link me.linusdev.discordbotapi.api.objects.emoji.StandardEmoji standard emoji}
     * at the end of the current content.
     * @param emoji the {@link Emoji}
     * @return this
     */
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

    /**
     * This will append a timestamp with the default {@link TimestampStyle style} at the end of the current content.
     * @param time time since 1. January 1970 in given {@link TimeUnit}
     * @param timeUnit {@link TimeUnit}
     * @return this
     */
    public MessageBuilder appendTimestamp(long time, @NotNull TimeUnit timeUnit) {
        return appendTimestamp(time, timeUnit, null);
    }

    /**
     * This will append a timestamp with given {@link TimestampStyle style} at the end of the current content.
     * @param time time since 1. January 1970 in given {@link TimeUnit}
     * @param timeUnit {@link TimeUnit}
     * @param timestampStyle the {@link TimestampStyle style} to use for this timestamp
     * @return this
     */
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

    /**
     * This will append a timestamp with given {@link TimestampStyle style} at the end of the current content.
     * @param timestamp the timestamp to append
     * @param timestampStyle the {@link TimestampStyle style} to use for this timestamp
     * @return this
     */
    public MessageBuilder appendTimestamp(@NotNull ISO8601Timestamp timestamp, @Nullable TimestampStyle timestampStyle){
        return appendTimestamp(timestamp.toEpochSeconds(), TimeUnit.SECONDS, timestampStyle);
    }

    /**
     * This will append a timestamp with the default {@link TimestampStyle style} at the end of the current content.
     * @param timestamp the timestamp to append
     * @return this
     */
    public MessageBuilder appendTimestamp(@NotNull ISO8601Timestamp timestamp){
        return appendTimestamp(timestamp.toEpochSeconds(), TimeUnit.SECONDS);
    }

    /**
     * This will append some content at the end of the currently existing message content
     * @param content the content to add. will call {@link Object#toString()}
     * @return this
     */
    public MessageBuilder appendContent(@Nullable Object content){
        if(content == null) return this;
        this.content.append(content);
        return this;
    }

    /**
     *
     * You can build an {@link Embed} using the {@link me.linusdev.discordbotapi.api.objects.message.embed.EmbedBuilder EmbedBuilder}.
     *
     * @param embed the {@link Embed} to add
     * @return this
     */
    public MessageBuilder addEmbed(@NotNull Embed embed) {
        if(embeds == null) embeds = new ArrayList<>();
        embeds.add(embed);
        return this;
    }

    /**
     *
     * @param tts whether this message should be a text-to-speech message
     */
    public MessageBuilder setTTS(boolean tts) {
        this.tts = tts;
        return this;
    }

    /**
     * @param message the message to reference (reply) to
     * @param fallIfNotExists if {@code true}, you will get an error after sending the message, if the referenced message does not exist
     * @see #setReplyTo(Message, boolean, boolean)
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
     * @see #setReplyTo(Message, boolean, boolean)
     */
    public void setMessageReference(@NotNull Message message) {
        setMessageReference(message, true);
    }
    
    /**
     * If you want your message to be a {@link me.linusdev.discordbotapi.api.objects.enums.MessageType#REPLY reply},
     * you will need to add this.
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
     * If you want your message to be a {@link me.linusdev.discordbotapi.api.objects.enums.MessageType#REPLY reply},
     * you will need to add this.
     * @param message the msg to reply to
     * @param mentionUser whether to user the author of the referenced message
     * @see #setReplyTo(Message, boolean, boolean) 
     */
    public MessageBuilder setReplyTo(@NotNull Message message, boolean mentionUser){
        return setReplyTo(message, true, mentionUser);
    }

    /**
     * If you call this, the message once sent, will not mention anyone, even if a mention is contained in the message content.
     * This is especially useful, if you have user input inside your message content.
     * <br><br>
     * If {@link #appendHereMention()}, {@link #appendMention(MentionType, PlaceHolder...)}, {@link #appendUserMention(String)},
     * {@link #appendRoleMention(String)}, {@link #appendEveryoneMention()} or any other User, Role, Everyone or Here mentioning method is called
     * afterwards (only important for @everyone and @here, others can also be before), the mention will still be present and will ping the mentioned users.
     * @return this
     */
    public MessageBuilder setNoMentionsAllowed(){
        parse = new ArrayList<>(0);
        return this;
    }

    /**
     *
     * Will allow all user mentions inside the message
     *
     * @return this
     */
    public MessageBuilder allowAllUserMentions(){
        if(parse == null){
            parse = new ArrayList<>(1);
            parse.add(AllowedMentionType.USER_MENTIONS);
        }else if(!parse.contains(AllowedMentionType.USER_MENTIONS)){
            parse.add(AllowedMentionType.USER_MENTIONS);
        }

        users = null;
        return this;
    }

    /**
     *
     * Will allow all role mentions inside the message
     *
     * @return this
     */
    public MessageBuilder allowAllRoleMentions(){
        if(parse == null){
            parse = new ArrayList<>(1);
            parse.add(AllowedMentionType.ROLE_MENTIONS);
        }else if(!parse.contains(AllowedMentionType.ROLE_MENTIONS)){
            parse.add(AllowedMentionType.ROLE_MENTIONS);
        }

        roles = null;
        return this;
    }

    /**
     * This will add an attachment to the message. Currently, only {@link AttachmentTemplate} is supported,
     * because Discord does not allow to reuse an already uploaded attachment. If you want to do the latter, your
     * only option is to include the attachment link inside the message content. <br>
     * <br>
     * TODO this may be different when editing messages
     * @param attachment {@link AttachmentTemplate}
     * @return this
     */
    public MessageBuilder addAttachment(@NotNull Attachment attachment) {

        if(attachments == null) attachments = new ArrayList<>();

        attachments.add(attachment);
        if(attachment instanceof AttachmentTemplate){
            ((AttachmentTemplate) attachment).setAttachmentId(nextUploadAttachmentId++);
        }
        return this;
    }

    /**
     * This adds an {@link ActionRow action-row} to the message. Inside the action-row there can be different
     * {@link Component components}. These can be added with the {@link Function}:<br>
     * The function gives you an empty {@link ArrayList} of {@link Component components}. You can add your components to
     * the list and then return the list.<br><br>
     *
     * For more information about components and there limits, see: {@link Component}, {@link ActionRow}
     * @param consumer to add {@link Component components} to the {@link ActionRow action-row}
     * @return this
     */
    public MessageBuilder addComponent(@NotNull Function<ArrayList<Component>, ArrayList<Component>> consumer){
        if(components == null) components = new ArrayList<>();

        ActionRow actionRow = new ActionRow(lApi, ComponentType.ACTION_ROW, consumer.apply(new ArrayList<>()).toArray(new Component[0]));
        components.add(actionRow);

        return this;
    }

    /**
     * Adds a {@link Component} to the message. Should always be an {@link ActionRow}
     * @param actionRow to add
     * @return this
     */
    public MessageBuilder addComponent(@NotNull Component actionRow){
        if(components == null) components = new ArrayList<>(2);
        components.add(actionRow);
        return this;
    }

    /**
     * Adds a {@link me.linusdev.discordbotapi.api.objects.sticker.Sticker sticker} to the message.
     * A message can have up to {@value Limits#MAX_STICKERS} stickers.
     * @param stickerId the id of the sticker to add
     * @return this
     */
    public MessageBuilder addSticker(@NotNull String stickerId){
        if(stickerIds == null) stickerIds = new ArrayList<>(3);

        stickerIds.add(stickerId);

        return this;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
