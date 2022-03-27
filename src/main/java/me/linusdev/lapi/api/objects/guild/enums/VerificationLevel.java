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

package me.linusdev.lapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-verification-level" target="_top">Verification Level</a>
 */
public enum VerificationLevel implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * unrestricted
     */
    NONE(0),

    /**
     * must have verified email on account
     */
    LOW(1),

    /**
     * must be registered on Discord for longer than 5 minutes
     */
    MEDIUM(2),

    /**
     * must be a member of the server for longer than 10 minutes
     */
    HIGH(3),

    /**
     * must have a verified phone number
     */
    VERY_HIGH(4),

    ;

    private final int integer;

    VerificationLevel(int integer) {
        this.integer = integer;
    }

    /**
     *
     * @param value int
     * @return {@link VerificationLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull VerificationLevel ofValue(int value){
        for(VerificationLevel level : VerificationLevel.values()){
            if(level.integer == value)
                return level;
        }

        return UNKNOWN;
    }

    public int getvalue() {
        return integer;
    }

    @Override
    public Object simplify() {
        return integer;
    }
}
