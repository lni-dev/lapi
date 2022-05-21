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

package me.linusdev.lapi.api.communication.gateway.events.typing;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypingStartEvent extends Event {

    private final @NotNull TypingStartEventFields fields;

    public TypingStartEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                            @NotNull TypingStartEventFields fields) {
        super(lApi, payload, fields.getGuildIdAsSnowflake());
        this.fields = fields;
    }

    /**
     * 	id of the channel as {@link String}
     */
    public @NotNull String getChannelId() {
        return fields.getChannelId();
    }

    /**
     * id of the user as {@link String}
     */
    public @NotNull String getUserId() {
        return fields.getUserId();
    }

    /**
     * 	unix time (in milliseconds) of when the user started typing
     */
    public long getTimestamp() {
        return fields.getTimestamp();
    }

    /**
     * {@link ISO8601Timestamp} of {@link #getTimestamp()}
     */
    public ISO8601Timestamp getTimestampAsISO8601Timestamp() {
        return fields.getTimestampAsISO8601Timestamp();
    }

    /**
     * the member who started typing if this happened in a guild
     */
    public @Nullable Member getMember() {
        return fields.getMember();
    }
}
