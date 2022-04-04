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

package me.linusdev.lapi.api.communication.gateway.events.guild.member;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildMemberAddEvent extends Event implements GuildEvent {

    private final @NotNull Member member;

    /**
     *
     * @param lApi {@link LApi}
     * @param payload {@link GatewayPayloadAbstract}
     * @param guildId id of the guild, the member was added to
     * @param member the added member, must contain a user field.
     */
    public GuildMemberAddEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull Snowflake guildId, @NotNull Member member) {
        super(lApi, payload, guildId);

        this.member = member;
    }

    /**
     *
     * @return the {@link Member} that joined this guild.
     */
    public @NotNull Member getMember() {
        return member;
    }

    /**
     *
     * @return user-id of the added member.
     */
    public @NotNull String getUserId() {
        //noinspection ConstantConditions: will never be null, checked in Gatewaywebsocket
        return member.getUser().getId();
    }
}
