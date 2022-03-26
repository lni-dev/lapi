package me.linusdev.discordbotapi.api.objects.guild.scheduledevent;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 *
 * <p>
 *     <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-field-requirements-by-entity-type" target="_top">Field Requirements By Entity Type</a><br>
 *      The following table shows field requirements based on current entity type.<br><br>
 *      value : This field is required to be a non-null value<br>
 *      null : This field is required to be null<br>
 *      - : No strict requirements<br>
 * </p><br>
 * <table>
 *     <caption>field requirements based on current entity type</caption>
 *     <thead>
 *         <tr>
 *             <th scope="col">Entity Type&nbsp;&nbsp;&nbsp;</th
 *             ><th scope="col">channel_id&nbsp;&nbsp;&nbsp;</th>
 *             <th scope="col">entity_metadata&nbsp;&nbsp;&nbsp;</th>
 *             <th scope="col">scheduled_end_time</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>{@link #STAGE_INSTANCE}</td><td>value</td><td>null</td><td>-</td>
 *         </tr><tr><td>{@link #VOICE}</td><td>value</td><td>null</td><td>-</td></tr><tr><td>{@link #EXTERNAL}</td><td>null</td><td>value *</td><td>value</td></tr>
 *     </tbody>
 * </table>
 * * entity_metadata with a non-null location must be provided
 *
 *
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-entity-types" target="_top">GuildImpl Scheduled Event Entity Types</a>
 */
public enum EntityType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    STAGE_INSTANCE(1),
    VOICE(2),
    EXTERNAL(3),
    ;

    private final int value;

    EntityType(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link EntityType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull EntityType fromValue(int value){
        for(EntityType type : EntityType.values()){
            if(type.value == value) return type;
        }
        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
