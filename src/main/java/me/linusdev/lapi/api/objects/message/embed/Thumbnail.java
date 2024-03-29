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
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-thumbnail-structure" target="_top">Embed Thumbnail</a>
 */
public class Thumbnail implements Datable {

    public static final String URL_KEY = "url";
    public static final String PROXY_URL_KEY = " proxy_url";
    public static final String HEIGHT_KEY = "height";
    public static final String WIDTH_KEY = "width";

    /**
     * @see #getUrl()
     */
    private final @NotNull String url;

    /**
     * @see #getProxyUrl()
     */
    private final @Nullable String proxyUrl;

    /**
     * @see #getHeight()
     */
    private final @Nullable Integer height;

    /**
     * @see #getWidth()
     */
    private final @Nullable Integer width;

    /**
     *
     * @param url source url of image (only supports http(s) and attachments)
     * @param proxyUrl a proxied url of the image
     * @param height height of image
     * @param width width of image
     */
    public Thumbnail(@NotNull String url, @Nullable String proxyUrl, @Nullable Integer height, @Nullable Integer width){
        this.url = url;
        this.proxyUrl = proxyUrl;
        this.height = height;
        this.width = width;
    }

    /**
     *
     * @param url source url of image (only supports http(s) and attachments)
     * @param height height of image
     * @param width width of image
     */
    public Thumbnail(@NotNull String url, int height, int width){
        this(url, null, height, width);
    }

    /**
     *
     * @param url source url of image (only supports http(s) and attachments)
     */
    public Thumbnail(@NotNull String url){
        this(url, null, null, null);
    }

    /**
     * creates a {@link Thumbnail} from {@link SOData}
     * @param data to create {@link Thumbnail}
     * @return {@link Thumbnail} or {@code null} if data is {@code null}
     * @throws InvalidDataException if {@link #URL_KEY} is missing in given data
     */
    public static @Nullable Thumbnail fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        String url = (String) data.get(URL_KEY);

        if(url == null){
            throw new InvalidDataException(data, "url may not be null in " + Thumbnail.class.getSimpleName()).addMissingFields(URL_KEY);
        }

        String proxyUrl = (String) data.get(PROXY_URL_KEY);
        Number height = (Number) data.get(HEIGHT_KEY);
        Number width = (Number) data.get(WIDTH_KEY);

        return new Thumbnail(url, proxyUrl, height == null ? null : height.intValue(), width == null ? null : width.intValue());
    }

    /**
     * source url of thumbnail (only supports http(s) and attachments)
     */
    public @NotNull String getUrl() {
        return url;
    }

    /**
     * a proxied url of the thumbnail
     */
    public @Nullable String getProxyUrl() {
        return proxyUrl;
    }
    /**
     * height of thumbnail
     */
    public @Nullable Integer getHeight() {
        return height;
    }

    /**
     * width of thumbnail
     */
    public @Nullable Integer getWidth() {
        return width;
    }

    /**
     * Creates a {@link SOData} from this {@link Thumbnail}, useful to convert it to JSON
     */
    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(URL_KEY, url);
        if(proxyUrl != null) data.add(PROXY_URL_KEY, proxyUrl);
        if(height != null) data.add(HEIGHT_KEY, height);
        if(width != null) data.add(WIDTH_KEY, width);

        return data;
    }

}
