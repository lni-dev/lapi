package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-footer-structure" target="_top">Embed Footer Structure</a>
 */
public class Footer implements Datable {

    public static final String TEXT_KEY = "text";
    public static final String ICON_URL_KEY = "icon_url";
    public static final String PROXY_ICON_URL = "proxy_icon_url";

    /**
     * @see #getText()
     */
    private final @NotNull String text;

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
     * @param text footer text
     * @param iconUrl url of footer icon (only supports http(s) and attachments)
     * @param proxyIconUrl a proxied url of footer icon
     */
    public Footer(@NotNull String text, @Nullable String iconUrl, @Nullable String proxyIconUrl){
        this.text = text;
        this.iconUrl = iconUrl;
        this.proxyIconUrl = proxyIconUrl;
    }

    /**
     * creates a {@link Footer} from {@link Data}
     * @param data to create {@link Footer}
     * @return {@link Footer} or {@code null} if data is {@code null}
     * @throws InvalidDataException if {@link #TEXT_KEY} is missing in given data
     */
    public static @Nullable Footer fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        String text = (String) data.get(TEXT_KEY);

        if(text == null){
            throw new InvalidDataException(data, "text may not be null in " + Footer.class.getSimpleName()).addMissingFields(TEXT_KEY);
        }

        String iconUrl = (String) data.get(ICON_URL_KEY);
        String proxyIconUrl = (String) data.get(PROXY_ICON_URL);

        return new Footer(text, iconUrl, proxyIconUrl);
    }

    /**
     *
     * @param text footer text
     * @param iconUrl url of footer icon (only supports http(s) and attachments)
     */
    public Footer(@NotNull String text, @Nullable String iconUrl){
        this(text, iconUrl, null);
    }

    /**
     *
     * @param text footer text
     */
    public Footer(@NotNull String text){
        this(text, null, null);
    }

    /**
     * footer text
     */
    public @NotNull String getText() {
        return text;
    }

    /**
     * url of footer icon (only supports http(s) and attachments)
     */
    public @Nullable String getIconUrl() {
        return iconUrl;
    }

    /**
     * a proxied url of footer icon
     */
    public @Nullable String getProxyIconUrl() {
        return proxyIconUrl;
    }

    /**
     * Creates a {@link Data} from this {@link Footer}, useful to convert it to JSON
     */
    @Override
    public @NotNull Data getData() {
        Data data = new Data(1);

        data.add(TEXT_KEY, text);
        if(iconUrl != null) data.add(ICON_URL_KEY, iconUrl);
        if(proxyIconUrl != null) data.add(PROXY_ICON_URL, proxyIconUrl);

        return data;
    }
}
