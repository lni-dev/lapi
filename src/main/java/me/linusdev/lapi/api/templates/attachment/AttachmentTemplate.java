/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.templates.attachment;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.UnsupportedFileTypeException;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.communication.file.types.FileType;
import me.linusdev.lapi.api.communication.lapihttprequest.body.FilePart;
import me.linusdev.lapi.api.objects.attachment.PartialAttachment;
import me.linusdev.lapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
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
     * {@link SOData}, that contains information used to upload this attachment to Discord
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

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
