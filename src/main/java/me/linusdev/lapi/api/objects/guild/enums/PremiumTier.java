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

package me.linusdev.lapi.api.objects.guild.enums;

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
