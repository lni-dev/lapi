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

package me.linusdev.lapi.api.objects.attachment;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Attachment used for example in retrieved {@link me.linusdev.lapi.api.objects.message.abstracts.Message messages}
 */
public class Attachment extends PartialAttachment {

    public Attachment(@NotNull Snowflake id, @NotNull String filename, @Nullable String description, @Nullable AbstractContentType contentType, @NotNull Integer size, @NotNull String url, @NotNull String proxyUrl, @Nullable Integer height, @Nullable Integer width, @Nullable Boolean ephemeral) {
        super(id, filename, description, contentType, size, url, proxyUrl, height, width, ephemeral);
    }

    /**
     *
     * Creates a {@link Attachment} from an {@link Data}
     *
     * Required fields: {@link #ID_KEY}, {@link #FILENAME_KEY}, {@link #URL_KEY}, {@link #PROXY_URL_KEY}, {@link #SIZE_KEY}
     *
     * @throws InvalidDataException if a required field is missing
     */
    public Attachment(Data data) throws InvalidDataException {
        super(data);

        //make sure @NotNull stuff is not null!
        if(filename == null || url == null || proxyUrl == null || size == null){
            InvalidDataException exception = new InvalidDataException(data, "Cannot create Attachment from Data, cause one or more required fields are null!");

            if(filename == null) exception.addMissingFields(FILENAME_KEY);
            if(url == null) exception.addMissingFields(URL_KEY);
            if(proxyUrl == null) exception.addMissingFields(PROXY_URL_KEY);
            if(size == null) exception.addMissingFields(SIZE_KEY);

            throw exception;
        }

    }


    @Override
    public @NotNull String getFilename() {
        return super.getFilename();
    }

    @Override
    public @Nullable String getDescription() {
        return super.getDescription();
    }

    @Override
    public @Nullable AbstractContentType getContentType() {
        return super.getContentType();
    }

    @Override
    public @NotNull Integer getSize() {
        return super.getSize();
    }

    @Override
    public @NotNull String getUrl() {
        return super.getUrl();
    }

    @Override
    public @NotNull String getProxyUrl() {
        return super.getProxyUrl();
    }

    @Override
    public @Nullable Integer getHeight() {
        return super.getHeight();
    }

    @Override
    public @Nullable Integer getWidth() {
        return super.getWidth();
    }

    @Override
    public @Nullable Boolean isEphemeral() {
        return super.isEphemeral();
    }

    @Override
    public @NotNull Data getData() {
        return super.getData();
    }


}
