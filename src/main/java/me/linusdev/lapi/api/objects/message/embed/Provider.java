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
    public static @Nullable Provider fromData(@Nullable SOData data){
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
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        if(name != null) data.add(NAME_KEY, name);
        if(url != null) data.add(URL_KEY, url);

        return data;
    }
}
