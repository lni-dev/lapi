package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-author-structure" target="_top">Embed Author Structure</a>
 */
public class Author implements Datable {

    public static final String NAME_KEY = "name";
    public static final String URL_KEY = "url";
    public static final String ICON_URL_KEY = "icon_url";
    public static final String PROXY_ICON_URL_KEY = "proxy_icon_url";

    /**
     * @see #getName()
     */
    private final @NotNull String name;

    /**
     * @see #getUrl()
     */
    private final @Nullable String url;

    /**
     * @see #getIconUrl()
     */
    private final @Nullable String iconUrl;

    /**
     * @see #getProxyIconUrl()
     */
    private final @Nullable String proxyIconUrl;

    /**
     *
     * @param name name of author
     * @param url url of author
     * @param iconUrl url of author icon (only supports http(s) and attachments)
     * @param proxyIconUrl a proxied url of author icon
     */
    public Author(@NotNull String name, @Nullable String url, @Nullable String iconUrl, @Nullable String proxyIconUrl){
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;
        this.proxyIconUrl = proxyIconUrl;
    }

    /**
     *
     * @param name name of author
     * @param url url of author
     * @param iconUrl url of author icon (only supports http(s) and attachments)
     */
    public Author(@NotNull String name, @Nullable String url, @Nullable String iconUrl){
        this(name, url, iconUrl, null);
    }

    /**
     *
     * @param name name of author
     * @param url url of author
     */
    public Author(@NotNull String name, @Nullable String url){
        this(name, url, null, null);
    }

    /**
     *
     * @param name name of author
     */
    public Author(@NotNull String name){
        this(name, null, null, null);
    }

    /**
     * creates a {@link Author} from {@link Data}
     * @param data to create {@link Author}
     * @return {@link Author} or {@code null} if data is {@code null}
     * @throws InvalidDataException if {@link #NAME_KEY} is missing in given data
     */
    public static @Nullable Author fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        final String name = (String) data.get(NAME_KEY);
        final String url = (String) data.get(URL_KEY);
        final String iconUrl = (String) data.get(ICON_URL_KEY);
        final String proxyIconUrl = (String) data.get(PROXY_ICON_URL_KEY);

        if(name == null) throw new InvalidDataException(data, "name may not be null in " + Author.class.getSimpleName()).addMissingFields(NAME_KEY);

        return new Author(name, url, iconUrl, proxyIconUrl);
    }

    /**
     * name of author
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * url of author
     */
    public @Nullable String getUrl() {
        return url;
    }

    /**
     * url of author icon (only supports http(s) and attachments)
     */
    public @Nullable String getIconUrl() {
        return iconUrl;
    }

    /**
     * a proxied url of author icon
     */
    public @Nullable String getProxyIconUrl() {
        return proxyIconUrl;
    }

    /**
     * Creates a {@link Data} from this {@link Author}, useful to convert it to JSON
     */
    @Override
    public Data getData() {
        Data data = new Data(1);

        data.add(NAME_KEY, name);
        if(url != null) data.add(URL_KEY, url);
        if(iconUrl != null) data.add(ICON_URL_KEY, iconUrl);
        if(proxyIconUrl != null) data.add(PROXY_ICON_URL_KEY, proxyIconUrl);

        return data;
    }
}
