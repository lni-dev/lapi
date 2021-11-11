package me.linusdev.discordbotapi.api.objects.message.embed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can {@link #build()} an {@link Embed}
 *
 * before building, it may be checked ({@link #checkEmbed()}) first
 *
 * @see Embed
 * @see #checkEmbed() Embed rules
 */
public class EmbedBuilder {

    /**
     * all Limits are inclusive
     * @see #checkEmbed()
     */
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

    /**
     * title of embed
     * @param title String max {@value #TITLE_CHAR_LIMIT} chars
     */
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

    /**
     * description of embed
     *
     * @param description String, max {@value #DESCRIPTION_CHAR_LIMIT} chars
     */
    public EmbedBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * url of embed
     * @param url String
     */
    public EmbedBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * timestamp of embed content
     * @param timestamp ISO8601 timestamp
     */
    public EmbedBuilder setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * color code of the embed
     *
     * @param color integer as hex-code color
     */
    public EmbedBuilder setColor(Integer color) {
        this.color = color;
        return this;
    }

    /**
     * You are probably looking for {@link #setFooter(String)} or {@link #setFooter(String, String)}
     *  <br><br>
     * embed footer
     */
    public EmbedBuilder setFooter(Footer footer) {
        this.footer = footer;
        return this;
    }

    /**
     *
     * @param text String, max {@value FOOTER_TEXT_CHAR_LIMIT} chars
     * @param iconUrl url of footer icon (only supports http(s) and attachments)
     * @param proxyIconUrl a proxied url of footer icon
     */
    public EmbedBuilder setFooter(@NotNull String text, @Nullable String iconUrl, @Nullable String proxyIconUrl){
        this.footer = new Footer(text, iconUrl, proxyIconUrl);
        return this;
    }

    /**
     *
     * @param text String, max {@value FOOTER_TEXT_CHAR_LIMIT} chars
     * @param iconUrl url of footer icon (only supports http(s) and attachments)
     */
    public EmbedBuilder setFooter(@NotNull String text, @Nullable String iconUrl){
        this.footer = new Footer(text, iconUrl);
        return this;
    }

    /**
     *
     * @param text String, max {@value FOOTER_TEXT_CHAR_LIMIT} chars
     */
    public EmbedBuilder setFooter(@NotNull String text){
        this.footer = new Footer(text);
        return this;
    }

    /**
     * You are probably looking for {@link #setImage(String)}
     *  <br><br>
     * embed image
     */
    public EmbedBuilder setImage(Image image) {
        this.image = image;
        return this;
    }

    /**
     *
     * @param url source url of image (only supports http(s) and attachments)
     * @param proxyUrl a proxied url of the image
     * @param height height of image
     * @param width width of image
     */
    public EmbedBuilder setImage(@NotNull String url, @Nullable String proxyUrl, int height, int width){
        this.image = new Image(url, proxyUrl, height, width);
        return this;
    }

    /**
     *
     * @param url source url of image (only supports http(s) and attachments)
     * @param height height of image
     * @param width width of image
     */
    public EmbedBuilder setImage(@NotNull String url, int height, int width){
        this.image = new Image(url, height, width);
        return this;
    }

    /**
     * Image of embed
     * <br><br>
     * @param url source url of image (only supports http(s) and attachments)
     */
    public EmbedBuilder setImage(@NotNull String url){
        this.image = new Image(url);
        return this;
    }

    /**
     * You are probably looking for {@link #setThumbnail(String)}
     * <br><br>
     * thumbnail of embed
     */
    public EmbedBuilder setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    /**
     *
     * @param url source url of thumbnail (only supports http(s) and attachments)
     * @param proxyUrl a proxied url of the thumbnail
     * @param height height of thumbnail
     * @param width width of thumbnail
     */
    public EmbedBuilder setThumbnail(@NotNull String url, @Nullable String proxyUrl, int height, int width){
        this.thumbnail = new Thumbnail(url, proxyUrl, height, width);
        return this;
    }

    /**
     *
     * @param url source url of thumbnail (only supports http(s) and attachments)
     * @param height height of thumbnail
     * @param width width of thumbnail
     */
    public EmbedBuilder setThumbnail(@NotNull String url, int height, int width){
        this.thumbnail = new Thumbnail(url, height, width);
        return this;
    }

    /**
     *
     * @param url source url of thumbnail (only supports http(s) and attachments)
     */
    public EmbedBuilder setThumbnail(@NotNull String url){
        this.thumbnail = new Thumbnail(url);
        return this;
    }

    /**
     * You are probably looking for {@link #setVideo(String)}
     * <br><br>
     * Video of Embed
     */
    public EmbedBuilder setVideo(Video video) {
        this.video = video;
        return this;
    }

    /**
     *
     * @param url source url of video
     * @param proxyUrl a proxied url of the video
     * @param height height of video
     * @param width width of video
     */
    public EmbedBuilder setVideo(@NotNull String url, @Nullable String proxyUrl, int height, int width){
        this.video = new Video(url, proxyUrl, height, width);
        return this;
    }

    /**
     *
     * @param url source url of video
     * @param height height of video
     * @param width width of video
     */
    public EmbedBuilder setVideo(@NotNull String url, int height, int width){
        this.video = new Video(url, height, width);
        return this;
    }

    /**
     *
     * @param url source url of video
     */
    public EmbedBuilder setVideo(@NotNull String url){
        this.video = new Video(url);
        return this;
    }

    /**
     * You are probably looking for {@link #setProvider(String, String)}
     * <br><br>
     * provider of embed
     */
    public EmbedBuilder setProvider(Provider provider) {
        this.provider = provider;
        return this;
    }

    /**
     *
     * @param name name of provider
     * @param url url of provider
     */
    public EmbedBuilder setProvider(@Nullable String name, @Nullable String url){
        this.provider = new Provider(name, url);
        return this;
    }

    /**
     * You are probably looking for {@link #setAuthor(String, String, String)} or {@link #setAuthor(String)}
     * <br><br>
     * Author of Embed
     */
    public EmbedBuilder setAuthor(Author author) {
        this.author = author;
        return this;
    }

    /**
     *
     * @param name name of author, max {@value AUTHOR_NAME_CHAR_LIMIT} chars
     * @param url url of author
     * @param iconUrl url of author icon (only supports http(s) and attachments)
     * @param proxyIconUrl a proxied url of author icon
     */
    public EmbedBuilder setAuthor(@NotNull String name, @Nullable String url, @Nullable String iconUrl, @Nullable String proxyIconUrl){
        this.author = new Author(name, url, iconUrl, proxyIconUrl);
        return this;
    }

    /**
     *
     * @param name name of author, max {@value AUTHOR_NAME_CHAR_LIMIT} chars
     * @param url url of author
     * @param iconUrl url of author icon (only supports http(s) and attachments)
     */
    public EmbedBuilder setAuthor(@NotNull String name, @Nullable String url, @Nullable String iconUrl){
        this.author = new Author(name, url, iconUrl);
        return this;
    }

    /**
     *
     * @param name name of author, max {@value AUTHOR_NAME_CHAR_LIMIT} chars
     * @param url url of author
     */
    public EmbedBuilder setAuthor(@NotNull String name, @Nullable String url){
        this.author = new Author(name, url);
        return this;
    }

    /**
     *
     * @param name name of author, max {@value AUTHOR_NAME_CHAR_LIMIT} chars
     */
    public EmbedBuilder setAuthor(@NotNull String name){
        this.author = new Author(name);
        return this;
    }

    /**
     * you are probably looking for {@link #addField(Field)}
     * @param fields ArrayList of {@link Field} for the Embed
     */
    public EmbedBuilder setFields(ArrayList<Field> fields) {
        this.fields = fields;
        return this;
    }

    /**
     * You are probably looking for {@link #addField(String, String)} or {@link #addField(String, String, boolean)}
     * <br><br>
     * An embed can only have {@value FIELDS_AMOUNT_LIMIT} fields
     * @param field {@link Field} to add
     */
    public EmbedBuilder addField(Field field){
        if(this.fields == null) this.fields = new ArrayList<>(1);
        this.fields.add(field);
        return this;
    }

    /**
     * An embed can only have {@value FIELDS_AMOUNT_LIMIT} fields
     *
     * @param name name of the field, max {@value FIELD_NAME_CHAR_LIMIT} chars
     * @param value value of the field, max {@value FIELD_VALUE_CHAR_LIMIT} chars
     * @param inline whether or not this field should display inline
     */
    public EmbedBuilder addField(@NotNull String name, @NotNull String value, boolean inline){
        if(this.fields == null) this.fields = new ArrayList<>(1);
        this.fields.add(new Field(name, value, inline));
        return this;
    }

    /**
     * An embed can only have {@value FIELDS_AMOUNT_LIMIT} fields
     * <br><br>
     * todo whats default for inline?
     *
     * @param name name of the field, max {@value FIELD_NAME_CHAR_LIMIT} chars
     * @param value value of the field, max {@value FIELD_VALUE_CHAR_LIMIT} chars
     */
    public EmbedBuilder addField(@NotNull String name, @NotNull String value){
        if(this.fields == null) this.fields = new ArrayList<>(1);
        this.fields.add(new Field(name, value));
        return this;
    }

    /**
     * Creates a builder-copy of given embed (All values of the embed copied into this EmbedBuilder)
     * @param embed to copy
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

    /**
     *
     * This will check ({@link #checkEmbed()}) and build you an {@link Embed}, which you can attach to a {@link me.linusdev.discordbotapi.api.objects.message.Message}
     *
     * @return a build {@link Embed}
     * @throws InvalidEmbedException see {@link #checkEmbed()}
     * @see #checkEmbed()
     */
    public @NotNull Embed build() throws InvalidEmbedException {
        return build(true);
    }

    /**
     *
     * This will build you an {@link Embed}, which you can attach to a {@link me.linusdev.discordbotapi.api.objects.message.Message}
     *
     * @param check whether to check if this would build a valid {@link Embed}.
     * @return a build {@link Embed}
     * @throws InvalidEmbedException see {@link #checkEmbed()}
     * @see #checkEmbed()
     */
    public @NotNull Embed build(boolean check) throws InvalidEmbedException {

        if(check) checkEmbed();

        return new Embed(title, type, description, url, timestamp, color, footer, image, thumbnail, video, provider, author, fields == null ? null : fields.toArray(new Field[0]));
    }
}
