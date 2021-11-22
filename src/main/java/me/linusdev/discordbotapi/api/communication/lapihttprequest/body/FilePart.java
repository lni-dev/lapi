package me.linusdev.discordbotapi.api.communication.lapihttprequest.body;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * A {@link FilePart} is used to upload files with a {@link me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest}.
 * This is very much WIP
 */
public interface FilePart {

    /**
     * the name of the file, when it is uploaded to Discord. Doesn't have to be the actual filename
     */
    @NotNull String getFilename();

    /**
     * the {@link Path} to the file on your local device
     */
    @NotNull Path getPath();

    /**
     * An Array of bytes to send in the {@link me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest HttpRequest}
     */
    byte[] getBytes();

    /**
     * The id of this attachment. For you to set, must be unique per message
     */
    @NotNull String getAttachmentId();

    /**
     * The Http content type of this file. For example: image/gif or image/png
     */
    @NotNull String getContentType();
}
