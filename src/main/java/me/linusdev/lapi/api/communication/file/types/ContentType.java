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

package me.linusdev.lapi.api.communication.file.types;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Media_type" target="_top">Media type</a>
 */
public enum ContentType implements AbstractContentType {
    /**
     * Not an actual content type!
     */
    UNKNOWN("-"),

    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),

    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_WEBP("image/webp"),
    IMAGE_SVG("image/svg+xml"),

    MULTIPART_FORM_DATA("multipart/form-data"),

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_CSV("text/csv"),
    TEXT_XML("text/xml"),
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
