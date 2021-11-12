package me.linusdev.discordbotapi.api.objects.attachment;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Todo: maybe rename this to RetrievedAttachment and make another class for UploadAttachment
 *
 * Attachment used for example in retrieved {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message}s
 */
public class Attachment extends PartialAttachment {

    public Attachment(@NotNull Snowflake id, @NotNull String filename, @Nullable String description, @Nullable String contentType, @NotNull Integer size, @NotNull String url, @NotNull String proxyUrl, @Nullable Integer height, @Nullable Integer width, @Nullable Boolean ephemeral) {
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
    public @NotNull String getFileName() {
        return super.getFileName();
    }

    @Override
    public @Nullable String getDescription() {
        return super.getDescription();
    }

    @Override
    public @Nullable String getContentType() {
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
