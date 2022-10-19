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

package me.linusdev.lapi.api.objects.nchannel;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.objects.enums.SimpleChannelType;
import me.linusdev.lapi.api.objects.permission.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This contains the different types of channels.
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-types" target="_top">Discord Documentation</a>
 */
public enum ChannelType implements SimpleDatable {

    /**
     * This is LApi specific, in case discord adds another channel type.
     */
    UNKNOWN(-1, "Unknown Channel", SimpleChannelType.UNKNOWN),

    /**
     * A text channel within a server.
     */
    GUILD_TEXT(0, "Guild Text Channel", SimpleChannelType.TEXT),

    /**
     * A direct message between users.
     */
    DM(1, "DM Channel", SimpleChannelType.TEXT),

    /**
     * A voice channel within a server.
     */
    GUILD_VOICE(2, "Guild Voice Channel", SimpleChannelType.VOICE),

    /**
     * A direct message between multiple users.
     */
    GROUP_DM(3, "Group DM Channel", SimpleChannelType.TEXT),

    /**
     * An
     * <a href="https://support.discord.com/hc/en-us/articles/115001580171-Channel-Categories-101" target="_top">
     * organizational category
     * </a>
     * that contains up to 50 channels.
     */
    GUILD_CATEGORY(4, "Category Channel", SimpleChannelType.CATEGORY),

    /**
     * A channel that
     * <a href="https://support.discord.com/hc/en-us/articles/360032008192" target="_top">
     * users can follow and crosspost into their own server
     * </a> (formerly news channels).
     */
    GUILD_ANNOUNCEMENT(5, "Guild Announcement Channel", SimpleChannelType.TEXT),

    /**
     * A temporary sub-channel within a {@link #GUILD_ANNOUNCEMENT} channel
     */
    ANNOUNCEMENT_THREAD(10, "Guild Announcement Thread", SimpleChannelType.THREAD),

    /**
     * A channel in which game developers can
     * <a href="https://discord.com/developers/docs/game-and-server-management/special-channels" target="_top">
     * sell their game on Discord
     * </a>.
     */
    @Deprecated
    GUILD_STORE(6, "Guild Store Channel", SimpleChannelType.TEXT),

    /**
     * A temporary sub-channel within a {@link #GUILD_TEXT} channel.
     */
    PUBLIC_THREAD(11, "Public Thread", SimpleChannelType.THREAD),

    /**
     * A temporary sub-channel within a {@link #GUILD_TEXT} channel
     * that is only viewable by those invited and those with the
     * {@link Permission#MANAGE_THREADS MANAGE_THREADS} permission.
     */
    PRIVATE_THREAD(12, "Private Thread", SimpleChannelType.THREAD),

    /**
     * A voice channel for
     * <a href="https://support.discord.com/hc/en-us/articles/1500005513722" target="_top">
     * hosting events with an audience
     * </a>.
     */
    GUILD_STAGE_VOICE(13, "Guild Stage Voice Channel", SimpleChannelType.STAGE),

    /**
     * The channel in a
     * <a href="https://support.discord.com/hc/en-us/articles/4406046651927-Discord-Student-Hubs-FAQ">hub</a>
     * containing the listed servers.
     */
    GUILD_DIRECTORY(14, "Guild Directory", SimpleChannelType.TEXT),

    /**
     * Channel that can only contain threads.
     */
    GUILD_FORUM(15, "Guild Forum", SimpleChannelType.TEXT),
    ;

    private final int id;
    private final @NotNull String string;
    private final @NotNull SimpleChannelType simpleChannelType;

    ChannelType(int id, @NotNull String string, @NotNull SimpleChannelType simpleChannelType) {
        this.id = id;
        this.string = string;
        this.simpleChannelType = simpleChannelType;
    }

    public int getId() {
        return id;
    }

    /**
     * @see SimpleChannelType
     */
    public @NotNull SimpleChannelType getSimpleChannelType() {
        return simpleChannelType;
    }

    /**
     * Converts from id to {@link ChannelType}
     *
     * @param id of the wanted {@link ChannelType}.
     * @return {@link ChannelType} with given id or {@link #UNKNOWN} if no such {@link ChannelType} exists
     */
    public static @NotNull ChannelType fromId(int id) {
        for (ChannelType type : ChannelType.values())
            if (type.getId() == id) return type;
        return UNKNOWN;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable ChannelType fromNumber(@Nullable Number number) {
        if(number == null) return null;
        return fromId(number.intValue());
    }

    /**
     * @return a readable String for example "Guild Text Channel"
     */
    public @NotNull String asString() {
        return string;
    }

    /**
     * @return the name of this enum constant, as contained in the declaration.
     */
    @Override
    public @NotNull String toString() {
        return super.toString();
    }

    @Override
    public @NotNull Object simplify() {
        return id;
    }
}
