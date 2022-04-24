/*
 * Copyright  2022 Linus Andera
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
     * creates a {@link Footer} from {@link SOData}
     * @param data to create {@link Footer}
     * @return {@link Footer} or {@code null} if data is {@code null}
     * @throws InvalidDataException if {@link #TEXT_KEY} is missing in given data
     */
    public static @Nullable Footer fromData(@Nullable SOData data) throws InvalidDataException {
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
     * Creates a {@link SOData} from this {@link Footer}, useful to convert it to JSON
     */
    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(TEXT_KEY, text);
        if(iconUrl != null) data.add(ICON_URL_KEY, iconUrl);
        if(proxyIconUrl != null) data.add(PROXY_ICON_URL, proxyIconUrl);

        return data;
    }
}
