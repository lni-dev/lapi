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

package me.linusdev.lapi.api.objects.attachment;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.file.types.ContentType;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Use case for this class: For the attachments array in Message Create/Edit requests, only the id is required.
 *
 * @see Attachment
 */
@SuppressWarnings("ConstantConditions")
public class PartialAttachment implements Attachment {

    public static final String ID_KEY = "id";
    public static final String FILENAME_KEY = "filename";
    public static final String DESCRIPTION_KEY = "description";
    public static final String CONTENT_TYPE_KEY = "content_type";
    public static final String SIZE_KEY = "size";
    public static final String URL_KEY = "url";
    public static final String PROXY_URL_KEY = "proxy_url";
    public static final String HEIGHT_KEY = "height";
    public static final String WIDTH_KEY = "width";
    public static final String EPHEMERAL_KEY = "ephemeral";

    protected final @NotNull Snowflake id;
    protected final @Nullable String filename;
    protected final @Nullable String description;
    protected final @Nullable AbstractContentType contentType;
    protected final @Nullable Integer size;
    protected final @Nullable String url;
    protected final @Nullable String proxyUrl;
    protected final @Nullable Integer height;
    protected final @Nullable Integer width;
    protected final @Nullable Boolean ephemeral;

    public PartialAttachment(@NotNull Snowflake id, @Nullable String filename, @Nullable String description, @Nullable AbstractContentType contentType,
                             @Nullable Integer size, @Nullable String url, @Nullable String proxyUrl, @Nullable Integer height,
                             @Nullable Integer width, @Nullable Boolean ephemeral){
        this.id = id;
        this.filename = filename;
        this.description = description;
        this.contentType = contentType;
        this.size = size;
        this.url = url;
        this.proxyUrl = proxyUrl;
        this.height = height;
        this.width = width;
        this.ephemeral = ephemeral;
    }

    public PartialAttachment(SOData data) throws InvalidDataException {
       String id = (String) data.get(ID_KEY);

       if(id == null) throw new InvalidDataException(data, "Id is missing in PartialAttachment").addMissingFields(ID_KEY);

       this.id = Snowflake.fromString(id);
       this.filename = (String) data.get(FILENAME_KEY);
       this.description = (String) data.get(DESCRIPTION_KEY);
       this.contentType = ContentType.of((String) data.get(CONTENT_TYPE_KEY));
       this.url = (String) data.get(URL_KEY);
       this.proxyUrl = (String) data.get(PROXY_URL_KEY);
       this.ephemeral = (Boolean) data.get(EPHEMERAL_KEY);

       Number size = (Number) data.get(SIZE_KEY);
       Number height = (Number) data.get(HEIGHT_KEY);
       Number width = (Number) data.get(WIDTH_KEY);

       this.size     =   size == null     ?  null    :   size.intValue();
       this.height   =   height == null   ?  null    :   height.intValue();
       this.width    =   width == null    ?  null    :   width.intValue();
    }

    /**
     * creates a {@link PartialAttachment} only with an id
     */
    public PartialAttachment(@NotNull Snowflake id){
        this(id, null, null, null, null, null, null, null, null, null);
    }

    /**
     * creates a {@link PartialAttachment} only with an id
     * @param id as {@link String}, will be converted to {@link Snowflake}
     */
    public PartialAttachment(@NotNull String id){
        this(Snowflake.fromString(id));
    }

    /**
     *
     * @param attachment {@link PartialAttachment}
     * @return new {@link PartialAttachment} with the same id.
     */
    public static @NotNull PartialAttachment of(@NotNull PartialAttachment attachment) {
        return new PartialAttachment(attachment.getId());
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @NotNull
    @Override
    public String getId() {
        return Attachment.super.getId();
    }

    @Override
    public @Nullable String getFilename() {
        return filename;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @Nullable AbstractContentType getContentType() {
        return contentType;
    }

    @Override
    public @Nullable Integer getSize() {
        return size;
    }

    @Override
    public @Nullable String getUrl() {
        return url;
    }

    @Override
    public @Nullable String getProxyUrl() {
        return proxyUrl;
    }

    @Override
    public @Nullable Integer getHeight() {
        return height;
    }

    @Override
    public @Nullable Integer getWidth() {
        return width;
    }

    @Override
    public @Nullable Boolean isEphemeral() {
        return ephemeral;
    }

    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(10);

        data.add(ID_KEY, id);
        if(filename != null)     data.add(FILENAME_KEY, filename);
        if(description != null)  data.add(DESCRIPTION_KEY, description);
        if(contentType != null)  data.add(CONTENT_TYPE_KEY, contentType);
        if(size != null)         data.add(SIZE_KEY, size);
        if(url != null)          data.add(URL_KEY, url);
        if(proxyUrl != null)     data.add(PROXY_URL_KEY, proxyUrl);
        if(height != null)       data.add(HEIGHT_KEY, height);
        if(width != null)        data.add(WIDTH_KEY, width);
        if(ephemeral != null)    data.add(EPHEMERAL_KEY, ephemeral);

        return data;
    }
}
