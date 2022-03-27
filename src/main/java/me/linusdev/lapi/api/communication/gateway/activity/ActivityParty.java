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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.Converter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-party" target="_top">Activity Party</a>
 */
public class ActivityParty implements Datable {

    public static final String ID_KEY = "id";
    public static final String SIZE_KEY = "size";

    private final @Nullable String id;
    private final @Nullable Integer currentSize;
    private final @Nullable Integer maxSize;

    /**
     *
     * @param id the id of the party
     * @param currentSize used to show the party's current and maximum size
     * @param maxSize used to show the party's current and maximum size
     */
    public ActivityParty(@Nullable String id, @Nullable Integer currentSize, @Nullable Integer maxSize) {
        this.id = id;
        this.currentSize = currentSize;
        this.maxSize = maxSize;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ActivityParty}
     */
    public static @Nullable ActivityParty fromData(@Nullable Data data){
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        ArrayList<Integer> size = data.getAndConvertArrayList(SIZE_KEY, (Converter<Object, Integer>) convertible -> (Integer) convertible);

        return new ActivityParty(id, size == null ? null : size.get(0), size == null ? null : size.get(1));
    }

    /**
     * the id of the party
     */
    public @Nullable String getId() {
        return id;
    }

    /**
     * current party size
     */
    public @Nullable Integer getCurrentSize() {
        return currentSize;
    }

    /**
     * max party size
     */
    public @Nullable Integer getMaxSize() {
        return maxSize;
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.addIfNotNull(ID_KEY, id);
        if(currentSize != null && maxSize != null) data.add(SIZE_KEY, new int[]{currentSize, maxSize});

        return data;
    }
}
