package me.linusdev.discordbotapi.api.templates.message;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.Nullable;

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
    public static final String COMPONENTS_KEY = "components";
    public static final String STICKER_IDS_KEY = "sticker_ids";
    public static final String ATTACHMENTS_KEY = "attachments";

    //TODO add all fields, etc

    private @Nullable final String content;
    private final boolean tts;
    private final Embed[] embeds;
    //allowed mentions
    private final Component[] components;
    private final String[] stickerIds;
    //Attachments

    public MessageTemplate(@Nullable String content, boolean tts, @Nullable Embed[] embeds,
                           @Nullable Component[] components, @Nullable String[] stickerIds){
        this.content = content;
        this.tts = tts;
        this.embeds = embeds;
        this.components = components;
        this.stickerIds = stickerIds;
    }

    @Override
    public Data getData() {
        Data data = new Data(1);

        data.addIfNotNull(CONTENT_KEY, content);
        if(tts) data.add(TTS_KEY, true);
        data.addIfNotNull(EMBEDS_KEY, embeds);
        data.addIfNotNull(COMPONENTS_KEY, components);
        data.addIfNotNull(STICKER_IDS_KEY, stickerIds);

        return data;
    }
}
