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

package me.linusdev.lapi.api.objects.guild.scheduledevent;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.Nullable;

/**
 * {@link #getLocation() location} is required for events with {@link GuildScheduledEvent#getEntityType() 'entity_type'}: {@link EntityType#EXTERNAL}
 *
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-entity-metadata" target="_top">GuildImpl Scheduled Event Entity Metadata</a>
 */
public class EntityMetadata implements Datable {

    public static final String LOCATION_KEY = "location";

    private final @Nullable String location;

    /**
     *
     * @param location location of the event (1-100 characters)
     */
    public EntityMetadata(@Nullable String location) {
        this.location = location;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link EntityMetadata}
     */
    public static @Nullable EntityMetadata fromData(@Nullable Data data){
        if(data == null) return null;

        return new EntityMetadata((String) data.get(LOCATION_KEY));
    }

    /**
     * location of the event (1-100 characters)
     */
    public @Nullable String getLocation() {
        return location;
    }

    /**
     *
     * @return {@link Data} for this {@link EntityMetadata}
     */
    @Override
    public Data getData() {
        Data data = new Data(1);

        if(location != null) data.add(LOCATION_KEY, location);

        return data;
    }
}
