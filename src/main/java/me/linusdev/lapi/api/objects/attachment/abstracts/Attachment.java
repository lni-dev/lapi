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

package me.linusdev.lapi.api.objects.attachment.abstracts;

import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Nullable;

/**
 * Attachment used in {@link me.linusdev.lapi.api.objects.message.abstracts.Message}
 * <br><br>
 * For the attachments array in Message Create/Edit requests, only the id is required. see {@link me.linusdev.lapi.api.objects.attachment.PartialAttachment}
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#attachment-object" target="_top">Attachment Object</a>
 */
public interface Attachment extends Datable {

    /**
     * attachment id as {@link Snowflake}
     *
     * If you upload a new Attachment, this may be {@code null}
     */
    @Nullable Snowflake getIdAsSnowflake();

    /**
     * attachment id as {@link String}
     *
     * If you upload a new Attachment, this may be {@code null}
     */
    default @Nullable String getId(){
        if(getIdAsSnowflake() == null) return null;
        return getIdAsSnowflake().asString();
    }

    /**
     * name of file attached
     */
    @Nullable String getFilename();

    /**
     * description for the file
     */
    @Nullable String getDescription();

    /**
     * the attachment's media type
     * @see <a href="https://en.wikipedia.org/wiki/Media_type" target="_top">media type</a>
     */
    @Nullable AbstractContentType getContentType();

    /**
     * size of file in bytes
     */
    @Nullable Integer getSize();

    /**
     * source url of file
     */
    @Nullable String getUrl();

    /**
     * a proxied url of file
     */
    @Nullable String getProxyUrl();

    /**
     * height of file (if image)
     */
    @Nullable Integer getHeight();

    /**
     * width of file (if image)
     */
    @Nullable Integer getWidth();

    /**
     * whether this attachment is ephemeral
     */
    @Nullable Boolean isEphemeral();

}
