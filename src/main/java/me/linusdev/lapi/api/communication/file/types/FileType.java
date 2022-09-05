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

package me.linusdev.lapi.api.communication.file.types;

import org.jetbrains.annotations.NotNull;

/**
 * Enum of common file types
 */
public enum FileType implements AbstractFileType {

    PNG(ContentType.IMAGE_PNG, "png"),
    GIF(ContentType.IMAGE_GIF, "gif"),
    WEBP(ContentType.IMAGE_WEBP, "webp"),
    JPEG(ContentType.IMAGE_JPEG, "jpg", "jpeg"),
    LOTTIE(null, "json"),
    ;

    private final AbstractContentType contentType;
    private final String[] endings;

    FileType(AbstractContentType contentType, String... endings) {
        this.contentType = contentType;
        this.endings = endings;
    }

    /**
     * The http content-type for this file type
     */
    @Override
    public AbstractContentType getContentType() {
        return contentType;
    }

    @Override
    public @NotNull String[] getFileEndings() {
        return endings;
    }
}
