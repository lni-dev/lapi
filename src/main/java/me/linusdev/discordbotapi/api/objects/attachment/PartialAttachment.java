package me.linusdev.discordbotapi.api.objects.attachment;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Use case for this class: For the attachments array in Message Create/Edit requests, only the id is required.
 *
 * @see Attachment
 */
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
    protected final @Nullable String contentType;
    protected final @Nullable Integer size;
    protected final @Nullable String url;
    protected final @Nullable String proxyUrl;
    protected final @Nullable Integer height;
    protected final @Nullable Integer width;
    protected final @Nullable Boolean ephemeral;

    public PartialAttachment(@NotNull Snowflake id, @Nullable String filename, @Nullable String description, @Nullable String contentType,
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

    public PartialAttachment(Data data) throws InvalidDataException {
       String id = (String) data.get(ID_KEY);

       if(id == null) throw new InvalidDataException(data, "Id is missing in PartialAttachment").addMissingFields(ID_KEY);

       this.id = Snowflake.fromString(id);
       this.filename = (String) data.get(FILENAME_KEY);
       this.description = (String) data.get(DESCRIPTION_KEY);
       this.contentType = (String) data.get(CONTENT_TYPE_KEY);
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

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @Nullable String getFileName() {
        return filename;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @Nullable String getContentType() {
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
    public @NotNull Data getData() {
        Data data = new Data(1);

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
