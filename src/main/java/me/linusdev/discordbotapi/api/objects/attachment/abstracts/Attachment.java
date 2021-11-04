package me.linusdev.discordbotapi.api.objects.attachment.abstracts;

import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Attachment used in {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message}
 * <br><br>
 * For the attachments array in Message Create/Edit requests, only the id is required. see {@link me.linusdev.discordbotapi.api.objects.attachment.PartialAttachment}
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#attachment-object" target="_top">Attachment Object</a>
 */
public interface Attachment extends Datable {

    /**
     * attachment id as {@link Snowflake}
     */
    @NotNull Snowflake getIdAsSnowflake();

    /**
     * attachment id as {@link String}
     */
    default @NotNull String getId(){
        return getIdAsSnowflake().asString();
    }

    /**
     * name of file attached
     */
    @Nullable String getFileName();

    /**
     * description for the file
     */
    @Nullable String getDescription();

    /**
     * the attachment's media type
     * @see <a href="https://en.wikipedia.org/wiki/Media_type" target="_top">media type</a>
     */
    @Nullable String getContentType();

    /**
     * size of file in bytes
     */
    @Nullable Integer getSize();

    /**
     * source url of file
     */
    @Nullable String getUrl();

    /**
     * a proxied url of file
     */
    @Nullable String getProxyUrl();

    /**
     * height of file (if image)
     */
    @Nullable Integer getHeight();

    /**
     * width of file (if image)
     */
    @Nullable Integer getWidth();

    /**
     * whether this attachment is ephemeral
     */
    @Nullable Boolean isEphemeral();

}
