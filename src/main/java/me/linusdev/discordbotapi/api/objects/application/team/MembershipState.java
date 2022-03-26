package me.linusdev.discordbotapi.api.objects.application.team;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/teams#data-models-membership-state-enum" target="_top">Membership State Enum</a>
 */
public enum MembershipState implements SimpleDatable {
    INVITED(1),
    ACCEPTED(2),
    ;

    private final int value;

    MembershipState(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link #ACCEPTED} if value equals 1, {@link #INVITED} otherwise
     */
    public static @NotNull MembershipState fromValue(int value){
        if(value == ACCEPTED.value) return ACCEPTED;
        return INVITED;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
