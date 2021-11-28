package me.linusdev.discordbotapi.api.communication.file.types;

import org.jetbrains.annotations.NotNull;

/**
 * Enum of common file types
 */
public enum FileType implements AbstractFileType {

    PNG(ContentType.IMAGE_PNG, "png"),
    GIF(ContentType.IMAGE_GIF, "gif"),
    WEBP(ContentType.IMAGE_WEBP, "webp"),
    JPEG(ContentType.IMAGE_JPEG, "jpg", "jpeg"),
    LOTTIE(null, "json"),
    ;

    private final AbstractContentType contentType;
    private final String[] endings;

    FileType(AbstractContentType contentType, String... endings) {
        this.contentType = contentType;
        this.endings = endings;
    }

    /**
     * The http content-type for this file type
     */
    @Override
    public AbstractContentType getContentType() {
        return contentType;
    }

    @Override
    public @NotNull String[] getFileEndings() {
        return endings;
    }
}
