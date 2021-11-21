package me.linusdev.discordbotapi.api.objects.guild.scheduledevent;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.Nullable;

/**
 * {@link #getLocation() location} is required for events with {@link GuildScheduledEvent#getEntityType() 'entity_type'}: {@link EntityType#EXTERNAL}
 *
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-entity-metadata" target="_top">Guild Scheduled Event Entity Metadata</a>
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
