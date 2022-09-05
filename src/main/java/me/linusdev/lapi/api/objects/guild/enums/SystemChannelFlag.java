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
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags" target="_top">System Channel Flags</a>
 */
public enum SystemChannelFlag implements SimpleDatable {

    /**
     * Suppress member join notifications
     */
    SUPPRESS_JOIN_NOTIFICATIONS(1 << 0),

    /**
     * Suppress server boost notifications
     */
    SUPPRESS_PREMIUM_SUBSCRIPTIONS(1 << 1),

    /**
     * Suppress server setup tips
     */
    SUPPRESS_GUILD_REMINDER_NOTIFICATIONS(1 << 2),

    /**
     * Hide member join sticker reply buttons
     */
    SUPPRESS_JOIN_NOTIFICATION_REPLIES(1 << 3),
    ;

    private final int value;

    SystemChannelFlag(int value) {
        this.value = value;
    }

    /**
     *
     * @param flags int with set bits
     * @return {@link SystemChannelFlag} array containing all flags set in given int
     */
    public static @NotNull SystemChannelFlag[] fromValue(int flags){
        SystemChannelFlag[] flagArray = new SystemChannelFlag[Integer.bitCount(flags)];

        int i = 0;
        for(SystemChannelFlag flag : SystemChannelFlag.values()){
            if(flag.isSet(flags)) {
                flagArray[i++] = flag;
            }
        }

        return flagArray;
    }

    /**
     *
     * @param flags int with set btis
     * @return {@code true} if this flag is set in flags
     */
    public boolean isSet(int flags){
        return (flags & value) == value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
