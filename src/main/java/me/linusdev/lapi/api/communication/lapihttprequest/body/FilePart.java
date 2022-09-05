/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.communication.lapihttprequest.body;

import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link FilePart} is used to upload files with a {@link me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpRequest}.
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
     * An Array of bytes to send in the {@link me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpRequest HttpRequest}
     */
    default byte[] getBytes() throws IOException {
        return Files.readAllBytes(getPath());
    }

    /**
     * The id of this attachment. For you to set, must be unique per message
     */
    @NotNull String getAttachmentId();

    /**
     * The Http content type of this file. For example: image/gif or image/png
     */
    @NotNull AbstractContentType getContentType();
}
