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

package me.linusdev.lapi.api.communication.gateway.events.guild;

import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.CachedGuild;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildCreateEvent extends Event implements GuildEvent {

    protected final @Nullable CachedGuild cachedGuild;
    protected final @NotNull Guild guild;

    public GuildCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull Guild guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.cachedGuild = null;
        this.guild = guild;
    }

    public GuildCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull CachedGuild guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.cachedGuild = guild;
        this.guild = guild;

    }

    /**
     * The {@link CachedGuild} associated with this event. <br>
     * Will be {@code null} if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is disabled
     */
    public @Nullable CachedGuild getCachedGuild() {
        return cachedGuild;
    }

    /**
     * This will be a {@link Guild temporary guild object} if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is disabled
     * or a {@link CachedGuild cached guild object} if it is enabled.
     */
    public @NotNull Guild getGuild() {
        return guild;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return super.getGuildIdAsSnowflake();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull String getGuildId() {
        return super.getGuildId();
    }
}
