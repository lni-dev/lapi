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

package me.linusdev.lapi.api.objects.voice.region;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/voice#voice-region-object" target="_top">Voice Region Object</a>
 */
public class VoiceRegion implements Datable, Updatable {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String CUSTOM_KEY = "custom";
    public static final String DEPRECATED_KEY = "deprecated";
    public static final String OPTIMAL_KEY = "optimal";

    private @NotNull final String id;
    private String name;
    private boolean custom;
    private boolean deprecated;
    private boolean optimal;

    public VoiceRegion(@NotNull String id, String name, boolean custom, boolean deprecated, boolean optimal){
        this.id = id;
        this.name = name;
        this.custom = custom;
        this.deprecated = deprecated;
        this.optimal = optimal;
    }

    /**
     * Idk why one would use this, but its here anyways
     *
     * @return Data representing this Voice Region
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(5);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(CUSTOM_KEY, custom);
        data.add(DEPRECATED_KEY, deprecated);
        data.add(OPTIMAL_KEY, optimal);

        return data;
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        name = (String) data.get(NAME_KEY, name);
        custom = (boolean) data.getOrDefaultBoth(CUSTOM_KEY, custom);
        deprecated = (boolean) data.getOrDefaultBoth(DEPRECATED_KEY, deprecated);
        optimal = (boolean) data.getOrDefaultBoth(OPTIMAL_KEY, optimal);
    }

    /**
     *
     * @param id
     * @return true if the given id matches {@link VoiceRegion#id}, false otherwise
     */
    public boolean equalsId(String id){
        return this.id.equals(id);
    }

    /**
     * unique ID for the region
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * name of the region
     */
    public String getName() {
        return name;
    }

    /**
     * whether this is a custom voice region (used for events/etc)
     */
    public boolean isCustom() {
        return custom;
    }

    /**
     * whether this is a deprecated voice region (avoid switching to these)
     */
    public boolean isDeprecated() {
        return deprecated;
    }

    /**
     * true for a single server that is closest to the current user's client
     */
    public boolean isOptimal() {
        return optimal;
    }

    /**
     *
     * If this returns {@code true} {@link #isCustom()}, {@link #isDeprecated()} and {@link #isOptimal()} must not be used
     * and {@link #getName()} will always return {@code null}
     *
     * @return {@code true} if this voice region was not retrieved from Discord!
     */
    public boolean isUnknown(){
        return name == null;
    }

    public static VoiceRegion fromData(SOData data){
        return new VoiceRegion((String) data.get(ID_KEY), (String) data.get(NAME_KEY), (boolean) data.getOrDefaultBoth(CUSTOM_KEY, false),
                (boolean) data.getOrDefaultBoth(DEPRECATED_KEY, false), (boolean) data.getOrDefaultBoth(OPTIMAL_KEY, false));
    }
}
