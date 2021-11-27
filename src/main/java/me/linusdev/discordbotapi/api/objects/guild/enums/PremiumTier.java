package me.linusdev.discordbotapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-premium-tier" target="_top">Premium Tier</a>
 */
public enum PremiumTier implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * guild has not unlocked any Server Boost perks
     */
    NONE(0),

    /**
     * guild has unlocked Server Boost level 1 perks
     */
    TIER_1(1),

    /**
     * guild has unlocked Server Boost level 2 perks
     */
    TIER_2(2),

    /**
     * guild has unlocked Server Boost level 3 perks
     */
    TIER_3(3),
    ;

    private final int integer;

    PremiumTier(int integer) {
        this.integer = integer;
    }

    /**
     *
     * @param value int
     * @return {@link PremiumTier} matching given value or {@link #UNKNOWN}
     */
    public static @NotNull PremiumTier fromValue(int value){
        for(PremiumTier tier : PremiumTier.values()){
            if(tier.integer == value) return tier;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return integer;
    }

    @Override
    public Object simplify() {
        return integer;
    }
}
