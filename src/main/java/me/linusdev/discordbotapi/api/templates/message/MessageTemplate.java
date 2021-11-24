package me.linusdev.discordbotapi.api.templates.message;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.FilePart;
import me.linusdev.discordbotapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.discordbotapi.api.objects.message.MessageReference;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * <p>
 *     used to {@link me.linusdev.discordbotapi.api.communication.retriever.query.Link#CREATE_MESSAGE create a message}
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/channel#create-message-jsonform-params" target="_top">JSON/Form Params</a>
 */
public class MessageTemplate implements Template {

    public static final String CONTENT_KEY = "content";
    public static final String TTS_KEY = "tts";
    public static final String EMBEDS_KEY = "embeds";
    public static final String ALLOWED_MENTIONS_KEY = "allowed_mentions";
    public static final String MESSAGE_REFERENCE_KEY = "message_reference";
    public static final String COMPONENTS_KEY = "components";
    public static final String STICKER_IDS_KEY = "sticker_ids";
    public static final String ATTACHMENTS_KEY = "attachments";

    private @Nullable final String content;
    private final boolean tts;
    private final @Nullable Embed[] embeds;
    private final @Nullable AllowedMentions allowedMentions;
    private final @Nullable MessageReference messageReference;
    private final @Nullable Component[] components;
    private final @Nullable String[] stickerIds;
    private final @Nullable Attachment[] attachments;

    /**
     *
     * @param content pure text content of the message
     * @param tts whether this message is text to speech
     * @param embeds {@link Embed embeds} for this message
     * @param allowedMentions {@link AllowedMentions}. if {@code null}, Discord will generate the mentions of the content
     * @param messageReference {@link MessageReference}, include to make your message a reply
     * @param components the {@link Component components} to include with the message
     * @param stickerIds IDs of up to 3 stickers in the server to send in the message
     * @param attachments attachment objects with filename and description
     */
    public MessageTemplate(@Nullable String content, boolean tts, @Nullable Embed[] embeds,
                           @Nullable AllowedMentions allowedMentions, @Nullable MessageReference messageReference, @Nullable Component[] components, @Nullable String[] stickerIds, @Nullable Attachment[] attachments){
        this.content = content;
        this.tts = tts;
        this.embeds = embeds;
        this.allowedMentions = allowedMentions;
        this.messageReference = messageReference;
        this.components = components;
        this.stickerIds = stickerIds;
        this.attachments = attachments;
    }

    @Override
    public FilePart[] getFileParts() {
        if(attachments == null) return new FilePart[0];

        ArrayList<FilePart> fileParts = new ArrayList<>();
        for(Attachment attachment : attachments){
            if(attachment instanceof FilePart){
                fileParts.add((FilePart) attachment);
            }
        }
        return fileParts.toArray(new FilePart[0]);
    }

    @Override
    public Data getData() {
        Data data = new Data(1);

        data.addIfNotNull(CONTENT_KEY, content);
        if(tts) data.add(TTS_KEY, true);
        data.addIfNotNull(EMBEDS_KEY, embeds);
        data.addIfNotNull(ALLOWED_MENTIONS_KEY, allowedMentions);
        data.addIfNotNull(MESSAGE_REFERENCE_KEY, messageReference);
        data.addIfNotNull(COMPONENTS_KEY, components);
        data.addIfNotNull(STICKER_IDS_KEY, stickerIds);
        data.addIfNotNull(ATTACHMENTS_KEY, attachments);

        return data;
    }
}
