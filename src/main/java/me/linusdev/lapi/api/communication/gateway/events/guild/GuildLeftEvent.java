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

package me.linusdev.lapi.api.communication.gateway.events.guild;

import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

public class GuildLeftEvent extends Event implements GuildEvent {

    protected final @NotNull CachedGuildImpl guild;

    public GuildLeftEvent(@NotNull LApi lApi, @NotNull CachedGuildImpl guild) {
        super(lApi, null, guild.getIdAsSnowflake());
        this.guild = guild;
    }

    public @NotNull CachedGuildImpl getGuild() {
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
