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

package me.linusdev.lapi.api.communication.gateway.events.guild.member;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildMemberUpdateEvent extends Event implements GuildEvent {

    private final @Nullable Update<Member, Member> update;
    private final @Nullable SOData data;

    public GuildMemberUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                  @Nullable Snowflake guildId, @NotNull Update<Member, Member> update) {
        super(lApi, payload, guildId);

        this.update = update;
        this.data = null;
    }

    public GuildMemberUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                  @Nullable Snowflake guildId, @NotNull SOData data) {
        super(lApi, payload, guildId);

        this.data = data;
        this.update = null;
    }

    /**
     * Only available if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_MEMBERS CACHE_MEMBERS} is disabled.
     */
    public @Nullable SOData getUpdateData() {
        return data;
    }

    /**
     * Only available if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_MEMBERS CACHE_MEMBERS} is enabled.
     */
    public @Nullable Update<Member, Member> getUpdate() {
        return update;
    }

    //TODO: add functions
}
