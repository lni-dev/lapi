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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-flags" target="_top">Activity Flag</a>
 */
public enum ActivityFlag implements SimpleDatable {
    INSTANCE(1 << 0),
    JOIN(1 << 1),
    SPECTATE(1 << 2),
    JOIN_REQUEST(1 << 3),
    SYNC(1 << 4),
    PLAY(1 << 5),
    PARTY_PRIVACY_FRIENDS(1 << 6),
    PARTY_PRIVACY_VOICE_CHANNEL(1 << 7),
    EMBEDDED(1 << 8),
    ;

    private final int value;

    ActivityFlag(int value) {
        this.value = value;
    }

    /**
     *
     * @param flags int
     * @return {@link ActivityFlag} array containing all flags set in given int.
     */
    public static @NotNull ActivityFlag[] fromInt(int flags){
        ActivityFlag[] array = new ActivityFlag[Integer.bitCount(flags)];

        int i = 0;
        for(ActivityFlag flag : ActivityFlag.values()){
            if(flag.isSet(flags)){
                array[i++] = flag;
                if(i == array.length) break;
            }
        }

        return array;
    }

    /**
     *
     * @param flags int
     * @return {@code true} if this flag is set in given flags.
     */
    public boolean isSet(int flags){
        return (flags & value) != 0;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
