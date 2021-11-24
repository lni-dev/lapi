package me.linusdev.discordbotapi.api.templates.attachment;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.UnsupportedFileTypeException;
import me.linusdev.discordbotapi.api.communication.file.types.AbstractContentType;
import me.linusdev.discordbotapi.api.communication.file.types.AbstractFileType;
import me.linusdev.discordbotapi.api.communication.file.types.FileType;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.FilePart;
import me.linusdev.discordbotapi.api.objects.attachment.PartialAttachment;
import me.linusdev.discordbotapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * This is a template of an {@link Attachment} to upload a new attachment to discord
 */
public class AttachmentTemplate implements Attachment, FilePart {

    private final @NotNull String filename;
    private final @Nullable String description;
    private final @NotNull Path filePath;
    private final @NotNull AbstractFileType fileType;

    private int attachmentId = -1;

    /**
     *
     * @param filename The filename shown in Discord. Does not have to be the real file name
     * @param description The description of the file
     * @param filePath {@link Path} to the file on your local device
     * @param fileType {@link FileType}
     */
    public AttachmentTemplate(@NotNull String filename, @Nullable String description, @NotNull Path filePath, @NotNull AbstractFileType fileType) {
        this.filename = filename;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;

        if(fileType.getContentType() == null)
            throw new UnsupportedFileTypeException("Cannot use given file type as attachment, because its content-type is null");

    }

    /**
     * @param id the id (unique per message) used for sending this attachment.
     *           will be set by {@link MessageBuilder MessageBuilder}.
     */
    public AttachmentTemplate setAttachmentId(int id){
        this.attachmentId = id;
        return this;
    }

    @Override
    public @NotNull String getFilename() {
        return filename;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @NotNull Path getPath() {
        return filePath;
    }

    @Override
    public @NotNull String getAttachmentId() {
        return String.valueOf(attachmentId);
    }

    @SuppressWarnings("ConstantConditions") // checked in Constructor
    @Override
    public @NotNull AbstractContentType getContentType() {
        return fileType.getContentType();
    }

    /**
     * {@link Data}, that contains information used to upload this attachment to Discord
     */
    @Override
    public Data getData() {
        Data data = new Data(3);

        data.add(PartialAttachment.FILENAME_KEY, filename);
        data.add(PartialAttachment.DESCRIPTION_KEY, description);
        data.add(PartialAttachment.ID_KEY, attachmentId);

        return data;
    }

    @Override
    @Deprecated
    public @Nullable Snowflake getIdAsSnowflake() {
        return null;
    }

    @Deprecated
    @Override
    public @Nullable Integer getSize() {
        return null;
    }

    @Deprecated
    @Override
    public @Nullable String getUrl() {
        return null;
    }

    @Deprecated
    @Override
    public @Nullable String getProxyUrl() {
        return null;
    }

    @Deprecated
    @Override
    public @Nullable Integer getHeight() {
        return null;
    }

    @Deprecated
    @Override
    public @Nullable Integer getWidth() {
        return null;
    }

    @Deprecated
    @Override
    public @Nullable Boolean isEphemeral() {
        return null;
    }
}
