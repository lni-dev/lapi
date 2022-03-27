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

package me.linusdev.lapi.api.objects.guild.scheduledevent;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-privacy-level" target="_top">GuildImpl Scheduled Event Privacy Level</a>
 */
public enum PrivacyLevel implements SimpleDatable {
    /**
     * LApi specific
     */
    UNKNOWN(0),

    /**
     * the scheduled event is only accessible to guild members
     */
    GUILD_ONLY(2),
    ;

    private final int value;

    PrivacyLevel(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link PrivacyLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static PrivacyLevel fromValue(int value){
        for(PrivacyLevel level : PrivacyLevel.values()){
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
