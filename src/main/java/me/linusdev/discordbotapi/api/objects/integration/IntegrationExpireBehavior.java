package me.linusdev.discordbotapi.api.objects.integration;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#integration-object-integration-expire-behaviors" target="_top"></a>
 */
public enum IntegrationExpireBehavior implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    REMOVE_ROLE(0),
    KICK(1),
    ;

    private final int value;

    IntegrationExpireBehavior(int value) {
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link IntegrationExpireBehavior} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull IntegrationExpireBehavior fromValue(int value){
        for(IntegrationExpireBehavior behavior : IntegrationExpireBehavior.values()){
            if(behavior.value == value) return behavior;
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
