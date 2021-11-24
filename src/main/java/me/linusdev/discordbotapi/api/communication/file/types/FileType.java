package me.linusdev.discordbotapi.api.communication.file.types;

/**
 * Enum of common file types
 */
public enum FileType implements AbstractFileType {

    PNG(ContentType.IMAGE_PNG),
    GIF(ContentType.IMAGE_GIF),
    ;

    private final AbstractContentType contentType;

    FileType(AbstractContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * The http content-type for this file type
     */
    @Override
    public AbstractContentType getContentType() {
        return contentType;
    }
}
