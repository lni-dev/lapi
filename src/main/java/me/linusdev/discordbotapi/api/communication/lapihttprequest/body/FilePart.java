package me.linusdev.discordbotapi.api.communication.lapihttprequest.body;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface FilePart {

    @NotNull String getFilename();

    @NotNull Path getPath();

    byte[] getBytes();

    @NotNull String getAttachmentId();

    @NotNull String getContentType();
}
