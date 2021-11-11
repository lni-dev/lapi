package me.linusdev.discordbotapi.api.objects.message.embed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmbedBuilder {

    public static final int TITLE_CHAR_LIMIT = 256;
    public static final int DESCRIPTION_CHAR_LIMIT = 4096;
    public static final int FIELDS_AMOUNT_LIMIT = 25;
    public static final int FIELD_NAME_CHAR_LIMIT = 256;
    public static final int FIELD_VALUE_CHAR_LIMIT = 1024;
    public static final int FOOTER_TEXT_CHAR_LIMIT = 2048;
    public static final int AUTHOR_NAME_CHAR_LIMIT = 256;

    private @Nullable String title;
    private @Nullable EmbedType type = EmbedType.RICH;
    private @Nullable String description;
    private @Nullable String url;
    private @Nullable String timestamp;
    private @Nullable Integer color;
    private @Nullable Footer footer;
    private @Nullable Image image;
    private @Nullable Thumbnail thumbnail;
    private @Nullable Video video;
    private @Nullable Provider provider;
    private @Nullable Author author;
    private @Nullable ArrayList<Field> fields;


    public EmbedBuilder(){

    }

    public EmbedBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Default is {@link EmbedType#RICH}
     *
     * @param type {@link EmbedType} of the embed you want to build
     */
    public EmbedBuilder setType(EmbedType type) {
        this.type = type;
        return this;
    }

    public EmbedBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EmbedBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public EmbedBuilder setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public EmbedBuilder setColor(Integer color) {
        this.color = color;
        return this;
    }

    public EmbedBuilder setFooter(Footer footer) {
        this.footer = footer;
        return this;
    }

    public EmbedBuilder setFooter(@NotNull String text, @Nullable String iconUrl, @Nullable String proxyIconUrl){
        this.footer = new Footer(text, iconUrl, proxyIconUrl);
        return this;
    }

    public EmbedBuilder setImage(Image image) {
        this.image = image;
        return this;
    }

    public EmbedBuilder setImage(@NotNull String url, @Nullable String proxyUrl, int height, int width){
        this.image = new Image(url, proxyUrl, height, width);
        return this;
    }

    public EmbedBuilder setImage(@NotNull String url, int height, int width){
        this.image = new Image(url, height, width);
        return this;
    }

    public EmbedBuilder setImage(@NotNull String url){
        this.image = new Image(url);
        return this;
    }

    public EmbedBuilder setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public EmbedBuilder setThumbnail(@NotNull String url, @Nullable String proxyUrl, int height, int width){
        this.thumbnail = new Thumbnail(url, proxyUrl, height, width);
        return this;
    }

    public EmbedBuilder setThumbnail(@NotNull String url, int height, int width){
        this.thumbnail = new Thumbnail(url, height, width);
        return this;
    }

    public EmbedBuilder setThumbnail(@NotNull String url){
        this.thumbnail = new Thumbnail(url);
        return this;
    }

    public EmbedBuilder setVideo(Video video) {
        this.video = video;
        return this;
    }

    public EmbedBuilder setVideo(@NotNull String url, @Nullable String proxyUrl, int height, int width){
        this.video = new Video(url, proxyUrl, height, width);
        return this;
    }

    public EmbedBuilder setVideo(@NotNull String url, int height, int width){
        this.video = new Video(url, height, width);
        return this;
    }

    public EmbedBuilder setVideo(@NotNull String url){
        this.video = new Video(url);
        return this;
    }

    public EmbedBuilder setProvider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public EmbedBuilder setProvider(@Nullable String name, @Nullable String url){
        this.provider = new Provider(name, url);
        return this;
    }

    public EmbedBuilder setAuthor(Author author) {
        this.author = author;
        return this;
    }

    public EmbedBuilder setAuthor(@NotNull String name, @Nullable String url, @Nullable String iconUrl, @Nullable String proxyIconUrl){
        this.author = new Author(name, url, iconUrl, proxyIconUrl);
        return this;
    }

    public EmbedBuilder setAuthor(@NotNull String name, @Nullable String url, @Nullable String iconUrl){
        this.author = new Author(name, url, iconUrl);
        return this;
    }

    /**
     * you are probably looking for {@link #addField(Field)}
     * @param fields
     * @return this
     */
    public EmbedBuilder setFields(ArrayList<Field> fields) {
        this.fields = fields;
        return this;
    }

    public EmbedBuilder addField(Field field){
        if(this.fields == null) this.fields = new ArrayList<>(1);
        this.fields.add(field);
        return this;
    }

    /**
     * Creates a builder-copy of given embed
     * @param embed to copy
     * @return this
     */
    public EmbedBuilder copyOf(@Nullable Embed embed){
        if(embed == null) return this;

        this.title = embed.getTitle();
        this.type = embed.getType();
        this.description = embed.getDescription();
        this.url = embed.getUrl();
        this.timestamp = embed.getTimestamp();
        this.color = embed.getColor();
        this.footer = embed.getFooter();
        this.image = embed.getImage();
        this.thumbnail = embed.getThumbnail();
        this.video = embed.getVideo();
        this.provider = embed.getProvider();
        this.author = embed.getAuthor();

        if(embed.getFields() != null){
            this.fields = new ArrayList<>(embed.getFields().length);
            this.fields.addAll(List.of(embed.getFields()));
        }

        return this;
    }

    /**
     * To facilitate showing rich content, rich embeds do not follow the traditional limits of message content. However, some limits are still in place to prevent excessively large embeds. The following table describes the limits
     * <br><br>
     * All of the following limits are measured inclusively. Leading and trailing whitespace characters are not included (they are trimmed automatically).
     * <br>
     *
     * {@link #title} limit:         256 characters {@link #TITLE_CHAR_LIMIT} <br>
     * {@link #description} limit:   4096 characters {@link #DESCRIPTION_CHAR_LIMIT} <br>
     * {@link #fields} limit:        Up to 25 {@link Field} objects  {@link #FIELDS_AMOUNT_LIMIT}<br>
     * {@link Field#name} limit:     256 characters {@link #FIELD_NAME_CHAR_LIMIT}<br>
     * {@link Field#value} limit:    1024 characters {@link #FIELD_VALUE_CHAR_LIMIT}<br>
     * {@link Footer#text} limit:    2048 characters {@link #FOOTER_TEXT_CHAR_LIMIT}<br>
     * {@link Author#name} limit:    256 characters {@link #AUTHOR_NAME_CHAR_LIMIT}<br>
     *
     * @see <a href="https://discord.com/developers/docs/resources/channel#embed-limits" target="_top">Embed Limits</a>
     */
    public EmbedBuilder checkEmbed() throws InvalidEmbedException {

        if(title != null && title.length() > TITLE_CHAR_LIMIT)
            throw new InvalidEmbedException("title of Embed cannot be larger than" + TITLE_CHAR_LIMIT + " characters");

        if(description != null && description.length() > DESCRIPTION_CHAR_LIMIT)
            throw new InvalidEmbedException("description of Embed cannot be larger than " +  DESCRIPTION_CHAR_LIMIT + " characters");

        if(fields != null){
            if(fields.size() > FIELDS_AMOUNT_LIMIT)
                throw new InvalidEmbedException("Embed may not have more than " + FIELDS_AMOUNT_LIMIT + " fields");

            for (Field field : fields) {
                if (field.getName().length() > FIELD_NAME_CHAR_LIMIT)
                    throw new InvalidEmbedException("Embed Field name may not be larger than " + FIELD_NAME_CHAR_LIMIT + " characters");

                if(field.getValue().length() > FIELD_VALUE_CHAR_LIMIT)
                    throw new InvalidEmbedException("Embed Field value may not be larger than " + FIELD_VALUE_CHAR_LIMIT + " characters");
            }
        }

        if(footer != null && footer.getText().length() > FOOTER_TEXT_CHAR_LIMIT)
            throw new InvalidEmbedException("Footer text may not be larger than " + FOOTER_TEXT_CHAR_LIMIT + " characters");

        if(author != null && author.getName().length() > AUTHOR_NAME_CHAR_LIMIT)
            throw new InvalidEmbedException("Author name may not be larger than " + AUTHOR_NAME_CHAR_LIMIT + " characters");

        return this;
    }

    public @NotNull Embed build(){
        //todo check
        return new Embed(title, type, description, url, timestamp, color, footer, image, thumbnail, video, provider, author, fields == null ? null : fields.toArray(new Field[0]));
    }
}
