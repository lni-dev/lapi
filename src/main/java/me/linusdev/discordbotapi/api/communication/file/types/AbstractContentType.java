package me.linusdev.discordbotapi.api.communication.file.types;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * represents a http content-type
 */
public interface AbstractContentType extends SimpleDatable {

    /**
     * the content-type as string for the http content-type header
     */
    @NotNull String getContentTypeAsString();

    @Override
    default Object simplify(){
        return getContentTypeAsString();
    }
}
