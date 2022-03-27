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

package me.linusdev.lapi.api.objects.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object-user-flags" target="_top">User Flags</a>
 */
public enum UserFlag {
    NONE                        (0     , "None", "None"),
    STAFF                       (1 << 0, "STAFF", "Discord Employee"),
    PARTNER                     (1 << 1, "PARTNER", "Partnered Server Owner"),
    HYPESQUAD                   (1 << 2, "HYPESQUAD", "HypeSquad Events Coordinator"),
    BUG_HUNTER_LEVEL_1          (1 << 3, "BUG_HUNTER_LEVEL_1", "Bug Hunter Level 1"),
    HYPESQUAD_ONLINE_HOUSE_1    (1 << 6, "HYPESQUAD_ONLINE_HOUSE_1", "House Bravery Member"),
    HYPESQUAD_ONLINE_HOUSE_2    (1 << 7, "HYPESQUAD_ONLINE_HOUSE_2", "House Brilliance Member"),
    HYPESQUAD_ONLINE_HOUSE_3    (1 << 8, "HYPESQUAD_ONLINE_HOUSE_3", "House Balance Member"),
    PREMIUM_EARLY_SUPPORTER     (1 << 9, "PREMIUM_EARLY_SUPPORTER", "Early Nitro Supporter"),

    /**
     * 	User is a {@link me.linusdev.lapi.api.objects.application.team.Team team}
     */
    TEAM_PSEUDO_USER            (1 << 10, "TEAM_PSEUDO_USER", "User is a team"),
    BUG_HUNTER_LEVEL_2          (1 << 14, "BUG_HUNTER_LEVEL_2", "Bug Hunter Level 2"),
    VERIFIED_BOT                (1 << 16, "VERIFIED_BOT", "Verified Bot"),
    VERIFIED_DEVELOPER          (1 << 17, "VERIFIED_DEVELOPER", "Early Verified Bot Developer"),
    CERTIFIED_MODERATOR         (1 << 18, "CERTIFIED_MODERATOR", "Discord Certified Moderator"),
    BOT_HTTP_INTERACTIONS       (1 << 19, "BOT_HTTP_INTERACTIONS", "Bot uses only HTTP interactions and is shown in the online member list"),
    ;

    private final int value;
    private final @NotNull String name;
    private final @NotNull String description;

    UserFlag(int value, @NotNull String name, @NotNull String description){
        this.value = value;
        this.name = name;
        this.description = description;
    }

    /**
     *
     * @param flags int with set bits
     * @param flag {@link UserFlag} to check if it is set
     * @return whether flag is set in flags
     */
    public static boolean isFlagSet(int flags, @NotNull UserFlag flag){
        return flag.isSet(flags);
    }

    /**
     * @param flags int with set bits
     * @return whether this {@link UserFlag} is set in given flags
     */
    public boolean isSet(int flags){
        if(this == NONE) return flags == 0;
        return (flags & value) == value;
    }

    /**
     *
     * {@link #NONE} flag will only be contained in the returned Array, if given parameter flags == 0
     *
     * @param flags int with set bits
     * @return {@link UserFlag UserFlag[]} corresponding to flags int
     */
    public static @NotNull UserFlag[] getFlagsFromInt(@Nullable Integer flags){
        if(flags == null) return new UserFlag[0];

        //even if no bit is set, we still have the NONE flag!
        int setBits = Integer.bitCount(flags);
        if(setBits == 0) return new UserFlag[]{NONE};

        //this will NOT contain the NONE flag
        UserFlag[] flagsArray = new UserFlag[setBits];
        int i = 0;
        for(UserFlag flag : UserFlag.values()){
            if(flag.isSet(flags)) flagsArray[i++] = flag;
        }

        return flagsArray;
    }

    public int getValue() {
        return value;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getDescription() {
        return description;
    }
}
