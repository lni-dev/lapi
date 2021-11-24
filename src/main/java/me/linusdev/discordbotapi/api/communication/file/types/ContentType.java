package me.linusdev.discordbotapi.api.communication.file.types;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Media_type" target="_top">Media type</a>
 */
public enum ContentType implements AbstractContentType {
    APPLICATION_JSON("application/json"),

    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),

    MULTIPART_FORM_DATA("multipart/form-data"),
    ;

    /**
     * This content type as {@link String}. Used in the Content-Type http-header
     */
    private final @NotNull String asString;

    /**
     * This content type as {@link String}. Used in the Content-Type http-header
     */
    ContentType(@NotNull String asString) {
        this.asString = asString;
    }

    @Override
    public @NotNull String getContentTypeAsString() {
        return asString;
    }

    /**
     * This will convert the given content-type string, either into its matching {@link ContentType}
     * or into a {@link AbstractContentType}
     * @param contentType as {@link String}
     * @return {@link AbstractContentType} of given content-type string
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable AbstractContentType of(@Nullable String contentType){
        if(contentType == null) return null;
        for(ContentType type : ContentType.values()){
            if(type.asString.equals(contentType)) return type;
        }

        return () -> contentType;
    }
}
