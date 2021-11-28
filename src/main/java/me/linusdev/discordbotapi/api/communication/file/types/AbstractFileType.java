package me.linusdev.discordbotapi.api.communication.file.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbstractFileType {

    /**
     * The {@link AbstractContentType} for this file type, if available
     */
    @Nullable AbstractContentType getContentType();

    /**
     *
     * @return array of possible file-endings for this file-type without a dot ("."). Must contain at least one ending!
     */
    @NotNull String[] getFileEndings();
}
