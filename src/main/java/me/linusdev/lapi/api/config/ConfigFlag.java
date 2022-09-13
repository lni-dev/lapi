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

package me.linusdev.lapi.api.config;

import me.linusdev.data.SimpleDatable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.cache.Cache;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.command.BaseCommand;
import me.linusdev.lapi.api.manager.command.CommandManagerImpl;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMetadata;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import me.linusdev.lapi.api.objects.stage.StageInstance;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("PointlessBitwiseExpression")
public enum ConfigFlag implements SimpleDatable {
    /**
     * This flag has no effects
     */
    NOTHING(0),

    /**
     * <p>
     *     retrieves and caches all voice regions when {@link me.linusdev.lapi.api.lapiandqueue.LApiImpl LApi} starts.
     * </p>
     * @see VoiceRegionManager VoiceRegionManager
     */
    CACHE_VOICE_REGIONS(1 << 0),

    /**
     * <p>
     *     enables the gateway, which connects a websocket to retrieve events from Discord.
     * </p>
     */
    ENABLE_GATEWAY(1 << 1),

    /**
     * <p>
     *     Caches all {@link me.linusdev.lapi.api.objects.role.Role roles} retrieved by the
     *     {@link me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     *
     * @see CachedGuildImpl CachedGuildImpl
     * @see RoleManager RoleManager
     */
    CACHE_ROLES(1 << 2),

    /**
     * <p>
     *     Copies {@link me.linusdev.lapi.api.objects.role.Role role} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     * @see CachedGuildImpl CachedGuildImpl
     * @see RoleManager RoleManager
     */
    COPY_ROLE_ON_UPDATE_EVENT(1 << 3),

    /**
     * <p>
     *     Whether to cache guilds retrieved from Discord.
     * </p>
     */
    CACHE_GUILDS(1 << 4),

    /**
     * <p>
     *     Copies {@link me.linusdev.lapi.api.objects.guild.CachedGuild guild} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     */
    COPY_GUILD_ON_UPDATE_EVENT(1 << 5),

    /**
     * <p>
     *     Caches all {@link me.linusdev.lapi.api.objects.emoji.EmojiObject emojis} retrieved by the
     *     {@link me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     *
     * @see CachedGuildImpl CachedGuildImpl
     */
    CACHE_EMOJIS(1 << 6),

    /**
     * <p>
     *     Copies {@link me.linusdev.lapi.api.objects.emoji.EmojiObject emoji} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     */
    COPY_EMOJI_ON_UPDATE_EVENT(1 << 7),

    /**
     * <p>
     *     Caches all {@link me.linusdev.lapi.api.objects.sticker.Sticker stickers} retrieved by the
     *     {@link me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_STICKERS(1 << 8),

    /**
     * <p>
     *     Copies {@link me.linusdev.lapi.api.objects.sticker.Sticker sticker} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     */
    COPY_STICKER_ON_UPDATE_EVENT(1 << 9),

    /**
     * <p>
     *     Caches all {@link me.linusdev.lapi.api.objects.guild.voice.VoiceState voice states} retrieved by the
     *     {@link me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE VOICE_STATE_UPDATE} events.
     * </p>
     */
    CACHE_VOICE_STATES(1 << 10),

    /**
     * <p>
     *     Copies {@link me.linusdev.lapi.api.objects.guild.voice.VoiceState voice state} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     */
    COPY_VOICE_STATE_ON_UPDATE_EVENT(1 << 11),

    /**
     * <p>
     *     Caches all {@link me.linusdev.lapi.api.objects.guild.member.Member members} retrieved by the
     *     {@link me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_MEMBERS(1 << 12),

    /**
     * <p>
     *     Copies {@link me.linusdev.lapi.api.objects.guild.member.Member member} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     *
     */
    COPY_MEMBER_ON_UPDATE_EVENT(1 << 13),


    /**
     * <p>
     *     Caches all {@link Channel channels} retrieved by the
     *     {@link GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_CHANNELS(1 << 14),

    /**
     * <p>
     *     Copies {@link Channel} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     */
    COPY_CHANNEL_ON_UPDATE_EVENT(1 << 15),

    /**
     * <p>
     *     Caches all {@link me.linusdev.lapi.api.objects.channel.abstracts.Thread threads} retrieved by the
     *     {@link GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_THREADS(1 << 16),

    /**
     * <p>
     *     Does not remove {@link me.linusdev.lapi.api.objects.channel.abstracts.Thread threads} where
     *     {@link ThreadMetadata#isArchived()} is {@code true} from the {@link CachedGuildImpl}.
     * </p>
     */
    DO_NOT_REMOVE_ARCHIVED_THREADS(1 << 17),

    /**
     * <p>
     *     Copies {@link Thread} objects, when they receive an update, so
     *     you can check the difference between the old object and the updated one.
     * </p>
     */
    COPY_THREAD_ON_UPDATE_EVENT(1 << 18),

    /**
     * <p>
     *     Caches all {@link PresenceUpdate presences} retrieved by the
     *     {@link GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_PRESENCES(1 << 19),

    /**
     * <p>
     *    Copies {@link PresenceUpdate} objects, when they receive an update, so
     *    you can check the difference between the old object and the updated one.
     * </p>
     */
    COPY_PRESENCE_ON_UPDATE_EVENT(1 << 20),

    /**
     * <p>
     *     Caches all {@link StageInstance stage instances} retrieved by the
     *     {@link GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_STAGE_INSTANCES(1 << 21),

    /**
     * <p>
     *    Copies {@link StageInstance} objects, when they receive an update, so
     *    you can check the difference between the old object and the updated one.
     * </p>
     */
    COPY_STAGE_INSTANCE_ON_UPDATE_EVENT(1 << 22),

    /**
     * <p>
     *     Caches all {@link GuildScheduledEvent scheduled events} retrieved by the
     *     {@link GatewayEvent#GUILD_CREATE GUILD_CREATE} events.
     * </p>
     */
    CACHE_GUILD_SCHEDULED_EVENTS(1 << 23),

    /**
     * <p>
     *    Copies {@link GuildScheduledEvent} objects, when they receive an update, so
     *    you can check the difference between the old object and the updated one.
     * </p>
     */
    COPY_GUILD_SCHEDULED_EVENTS_ON_UPDATE(1 << 24),

    /**
     * <p>
     *     Caches some basic information, like the {@link Cache#getCurrentUserId() current user id} and
     *     the {@link Cache#getCurrentApplicationId() current application id}. You should always enable this.
     * </p>
     * @see LApi#getCache()
     */
    BASIC_CACHE(1 << 25),

    /**
     * <p>
     *     Enables the {@link CommandManagerImpl}, so you can create your own {@link BaseCommand}s.
     * </p>
     * @see LApi#getCommandManager()
     * @see BaseCommand
     */
    COMMAND_MANAGER(1 << 26),
    ;

    private final long value;

    ConfigFlag(long value) {
        this.value = value;
    }

    public static long fromData(@NotNull SOData data){
        long flags = 0;

        for(ConfigFlag flag : ConfigFlag.values()){
            Boolean set = (Boolean) data.get(flag.name());

            if(set == null || !set) continue;
            flags = flags | flag.value;
        }

        return flags;
    }

    /**
     *
     * @param flags long with set bits
     * @return {@link SOData} representing given flags
     */
    public static @NotNull SOData toData(long flags){
        ConfigFlag[] values = ConfigFlag.values();
        SOData data = SOData.newOrderedDataWithKnownSize(values.length);

        for(ConfigFlag flag : values) {
            if(flag == NOTHING) continue;
            data.add(flag.toString(), flag.isSet(flags));
        }

        return data;
    }

    /**
     *
     * @param flags long with set bits
     * @return {@code true} if this flag is set in given long, {@code false} otherwise
     */
    public boolean isSet(long flags){
        return (flags & value) != 0;
    }

    /**
     * Sets the bit for this flag. If it is already set, this will do nothing
     * @param flags long
     * @return new flags long
     */
    public long set(long flags){
        return flags | value;
    }

    /**
     * Unsets the bit for this flag. If it is not set, this will do nothing
     * @param flags long
     * @return new flags long
     */
    public long  remove(long flags){
        return flags & (~value);
    }

    @Override
    public Object simplify() {
        return toString();
    }

    @Override
    public String toString() {
        return name();
    }
}
