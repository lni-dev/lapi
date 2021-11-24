package me.linusdev.discordbotapi.api.communication.file.types;

import org.jetbrains.annotations.Nullable;

public interface AbstractFileType {

    /**
     * The {@link AbstractContentType} for this file type, if available
     */
    @Nullable AbstractContentType getContentType();
}
