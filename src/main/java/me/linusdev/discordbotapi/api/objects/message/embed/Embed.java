package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object" target="_top">Embed Object</a>
 */
public class Embed implements Datable {

    public static final String TITLE_KEY = "title";
    public static final String TYPE_KEY = "type";
    public static final String DESCRIPTION_KEY = "description";
    public static final String URL_KEY = "url";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String COLOR_KEY = "color";
    public static final String FOOTER_KEY = "footer";
    public static final String IMAGE_KEY = "image";
    public static final String THUMBNAIL_KEY = "thumbnail";
    public static final String VIDEO_KEY = "video";
    public static final String PROVIDER_KEY = "provider";
    public static final String AUTHOR_KEY = "author";
    public static final String FIELDS_KEY = "fields";


    private final @Nullable String title;
    private final @Nullable EmbedType type;
    private final @Nullable String description;
    private final @Nullable String url;
    private final @Nullable String timestamp;
    private final @Nullable Integer color;
    private final @Nullable Footer footer;
    private final @Nullable Image image;
    private final @Nullable Thumbnail thumbnail;
    private final @Nullable Video video;
    private final @Nullable Provider provider;
    private final @Nullable Author author;
    private final @Nullable Field[] fields;

    public Embed(@Nullable String title, @Nullable EmbedType type, @Nullable String description,
                 @Nullable String url, @Nullable String timestamp, @Nullable Integer color,
                 @Nullable Footer footer, @Nullable Image image, @Nullable Thumbnail thumbnail,
                 @Nullable Video video, @Nullable Provider provider, @Nullable Author author,
                 @Nullable Field[] fields){

        this.title = title;
        this.type = type;
        this.description = description;
        this.url = url;
        this.timestamp = timestamp;
        this.color = color;
        this.footer = footer;
        this.image = image;
        this.thumbnail = thumbnail;
        this.video = video;
        this.provider = provider;
        this.author = author;
        this.fields = fields;

    }

    /**
     * creates a {@link Embed} from {@link Data}
     * @param data to create {@link Embed}
     * @return {@link Embed}
     * @throws InvalidDataException if given data was invalid. for more information see {@link Footer},
     * {@link Image}, {@link Thumbnail}, {@link Video}, {@link Provider}, {@link Author}, {@link Field}
     */
    public static @NotNull Embed fromData(@NotNull Data data) throws InvalidDataException {
        @Nullable final Number color = (Number) data.get(COLOR_KEY);
        @Nullable final Data footerData = (Data) data.get(FOOTER_KEY);
        @Nullable final Data imageData = (Data) data.get(IMAGE_KEY);
        @Nullable final Data thumbnailData = (Data) data.get(THUMBNAIL_KEY);
        @Nullable final Data videoData = (Data) data.get(VIDEO_KEY);
        @Nullable final Data providerData = (Data) data.get(PROVIDER_KEY);
        @Nullable final Data authorData = (Data) data.get(AUTHOR_KEY);
        @Nullable final List<Data> fieldsData = (ArrayList<Data>) data.get(FIELDS_KEY);

        Field[] fields = null;
        if(fieldsData != null){
            fields = new Field[fieldsData.size()];
            int i = 0;
            for(Data field : fieldsData)
                fields[i++] = Field.fromData(data);

        }

        return new Embed((String) data.get(TITLE_KEY), EmbedType.fromTypeString((String) data.get(TYPE_KEY)), (String) data.get(DESCRIPTION_KEY), (String) data.get(URL_KEY),
                (String) data.get(TIMESTAMP_KEY), color == null ? null : color.intValue(), Footer.fromData(footerData), Image.fromData(imageData),
                Thumbnail.fromData(thumbnailData), Video.fromData(videoData), Provider.fromData(providerData), Author.fromData(authorData), fields);
    }

    /**
     * title of embed
     */
    public @Nullable String getTitle() {
        return title;
    }

    /**
     * 	type of embed (always "rich" for webhook embeds)
     * @see EmbedType
     */
    public @Nullable EmbedType getType() {
        return type;
    }

    /**
     * description of embed
     */
    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable String getUrl() {
        return url;
    }

    public @Nullable String getTimestamp() {
        return timestamp;
    }

    public @Nullable Integer getColor() {
        return color;
    }

    public @Nullable Footer getFooter() {
        return footer;
    }

    public @Nullable Image getImage() {
        return image;
    }

    public @Nullable Thumbnail getThumbnail() {
        return thumbnail;
    }

    public @Nullable Video getVideo() {
        return video;
    }

    public @Nullable Provider getProvider() {
        return provider;
    }

    public @Nullable Author getAuthor() {
        return author;
    }

    public @Nullable Field[] getFields() {
        return fields;
    }

    //todo finish getter



    /**
     * Creates a {@link Data} from this {@link Embed}, useful to convert it to JSON
     */
    @Override
    public Data getData() {
        Data data = new Data(0);

        if(title != null) data.add(TITLE_KEY, title);
        if(type != null) data.add(TYPE_KEY, type);
        if(description != null) data.add(DESCRIPTION_KEY, description);
        if(url != null) data.add(URL_KEY, url);
        if(timestamp != null) data.add(TIMESTAMP_KEY, timestamp);
        if(color != null) data.add(COLOR_KEY, color);
        if(footer != null) data.add(FOOTER_KEY, footer);
        if(image != null) data.add(IMAGE_KEY, image);
        if(thumbnail != null) data.add(THUMBNAIL_KEY, thumbnail);
        if(video != null) data.add(VIDEO_KEY, video);
        if(provider != null) data.add(PROVIDER_KEY, provider);
        if(author != null) data.add(AUTHOR_KEY, author);
        if(fields != null) data.add(FIELDS_KEY, fields);

        return data;
    }
}
