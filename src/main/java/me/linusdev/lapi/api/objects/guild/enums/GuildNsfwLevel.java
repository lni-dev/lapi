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
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level" target="_top">GuildImpl NSFW Level</a>
 */
public enum GuildNsfwLevel implements SimpleDatable {

    /**
     * LApi Specific
     */
    UNKNOWN(-1),

    DEFAULT(0),
    EXPLICIT(1),
    SAFE(2),
    AGE_RESTRICTED(3),
    ;

    private final int value;

    GuildNsfwLevel(int value) {
        this.value = value;
    }

    /**
     *
     * @param value int value
     * @return {@link GuildNsfwLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull GuildNsfwLevel fromValue(int value){
        for(GuildNsfwLevel level : GuildNsfwLevel.values()){
            if(level.value == value) return level;
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
