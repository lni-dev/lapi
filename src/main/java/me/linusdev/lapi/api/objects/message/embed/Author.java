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

package me.linusdev.lapi.api.objects.message.embed;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
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
     * creates a {@link Author} from {@link SOData}
     * @param data to create {@link Author}
     * @return {@link Author} or {@code null} if data is {@code null}
     * @throws InvalidDataException if {@link #NAME_KEY} is missing in given data
     */
    public static @Nullable Author fromData(@Nullable SOData data) throws InvalidDataException {
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
     * Creates a {@link SOData} from this {@link Author}, useful to convert it to JSON
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(NAME_KEY, name);
        if(url != null) data.add(URL_KEY, url);
        if(iconUrl != null) data.add(ICON_URL_KEY, iconUrl);
        if(proxyIconUrl != null) data.add(PROXY_ICON_URL_KEY, proxyIconUrl);

        return data;
    }
}
