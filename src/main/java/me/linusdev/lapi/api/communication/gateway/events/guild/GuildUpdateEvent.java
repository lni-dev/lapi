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

package me.linusdev.lapi.api.communication.gateway.events.guild;

import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.guild.CachedGuild;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildUpdateEvent extends Event implements GuildEvent {

    protected final @Nullable Update<CachedGuildImpl, Guild> update;
    protected final @NotNull Guild guild;

    public GuildUpdateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull Update<CachedGuildImpl, Guild> update) throws InvalidDataException {
        super(lApi, payload, update.getObj().getIdAsSnowflake());
        this.update = update;
        this.guild = update.getObj();
    }

    public GuildUpdateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull Guild guild) throws InvalidDataException {
        super(lApi, payload, guild.getIdAsSnowflake());
        this.update = null;
        this.guild = guild;
    }

    /**
     * Only available if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS} is enabled.
     * @return {@link CachedGuild}
     */
    public @Nullable CachedGuild getCachedGuild() {
        return update == null ? null : update.getObj();
    }

    /**
     * Only available if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_GUILDS CACHE_GUILDS}
     * and {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_GUILD_ON_UPDATE_EVENT COPY_GUILD_ON_UPDATE_EVENT} is enabled.
     * @return {@link Guild copy of a cached guild}
     */
    public @Nullable Guild getOldGuildCopy() {
        return update == null ? null : update.getCopy();
    }

    /**
     * @return {@link #getCachedGuild()} or a {@link Guild temporary guild object} if {@link #getCachedGuild()} is {@code null}.
     */
    public @NotNull Guild getGuild(){
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
