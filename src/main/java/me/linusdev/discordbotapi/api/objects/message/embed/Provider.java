package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-provider-structure" target="_top">Embed Provider Structure</a>
 */
public class Provider implements Datable {

    public static final String NAME_KEY = "name";
    public static final String URL_KEY = "url";

    /**
     * @see #getName()
     */
    private final @Nullable String name;

    /**
     * @see #getUrl()
     */
    private final @Nullable String url;

    /**
     * @param name name of provider
     * @param url url of provider
     */
    public Provider(@Nullable String name, @Nullable String url){
        this.name = name;
        this.url = url;
    }

    /**
     * creates a {@link Provider} from {@link Data}
     * @param data to create {@link Provider}
     * @return {@link Provider} or {@code null} if data is {@code null}
     */
    public static @Nullable Provider fromData(@Nullable Data data){
        if(data == null) return null;
        String name = (String) data.get(NAME_KEY);
        String url = (String) data.get(URL_KEY);

        return new Provider(name, url);
    }

    /**
     * url of provider
     */
    public @Nullable String getUrl() {
        return url;
    }

    /**
     * name of provider
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Creates a {@link Data} from this {@link Provider}, useful to convert it to JSON
     */
    @Override
    public Data getData() {
        Data data = new Data(0);

        if(name != null) data.add(NAME_KEY, name);
        if(url != null) data.add(URL_KEY, url);

        return data;
    }
}
