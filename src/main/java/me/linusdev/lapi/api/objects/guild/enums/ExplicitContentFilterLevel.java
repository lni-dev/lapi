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

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-explicit-content-filter-level" target="_top">Explicit Content Filter Level</a>
 */
public enum ExplicitContentFilterLevel implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * media content will not be scanned
     */
    DISABLED(0),

    /**
     * media content sent by members without roles will be scanned
     */
    MEMBERS_WITHOUT_ROLES(1),

    /**
     * media content sent by all members will be scanned
     */
    ALL_MEMBERS(2),
    ;

    private final int integer;

    ExplicitContentFilterLevel(int integer) {
        this.integer = integer;
    }

    /**
     *
     * @param value int
     * @return {@link ExplicitContentFilterLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static ExplicitContentFilterLevel fromValue(int value){
        for(ExplicitContentFilterLevel level : ExplicitContentFilterLevel.values()){
            if(level.integer == value) return level;
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
