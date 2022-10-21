/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.objects.channel;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-flags">Discord Documentation</a>
 * @see Channel#getFlagsAsList()
 */
public enum ChannelFlag implements SimpleDatable {

    /**
     * this thread is pinned to the top of its parent {@link ChannelType#GUILD_FORUM GUILD_FORUM} channel.
     */
    PINNED(1 << 1),

    /**
     * whether a tag is required to be specified when creating a thread in a {@link ChannelType#GUILD_FORUM GUILD_FORUM} channel.
     * Tags are specified in the {@link Channel#getAvailableTags applied_tags} field.
     */
    REQUIRE_TAG(1 << 4),

    ;

    private final int value;

    ChannelFlag(int value) {
        this.value = value;
    }

    /**
     *
     * @param bitfield int with set bits
     * @return {@link List} of {@link ChannelFlag flags} contained in given bitfield.
     */
    public static @NotNull List<ChannelFlag> fromBits(@Nullable Integer bitfield) {
        if(bitfield == null) return List.of();

        List<ChannelFlag> flags = new ArrayList<>(Integer.bitCount(bitfield));

        for(ChannelFlag flag : ChannelFlag.values()) {
            if(flag.isSet(bitfield)) flags.add(flag);
        }

        return flags;
    }

    public int getValue() {
        return value;
    }

    public boolean isSet(int flags) {
        return (value & flags) != 0;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
