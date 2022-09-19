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

package me.linusdev.lapi.api.communication.gateway.events.invite;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InviteDeleteEvent extends Event {

    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String CODE_KEY = "code";

    private final @NotNull Snowflake channelId;
    private final @NotNull String code;

    public InviteDeleteEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, @NotNull Snowflake channelId, @NotNull String code) {
        super(lApi, payload, guildId);
        this.channelId = channelId;
        this.code = code;
    }

    public static @NotNull InviteDeleteEvent fromData(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                                      @Nullable Snowflake guildId, @NotNull SOData data) throws InvalidDataException {
        Snowflake channelId = data.getAndConvert(CHANNEL_ID_KEY, Snowflake::fromString);
        String code = (String) data.get(CODE_KEY);

        if(channelId == null || code == null) {
            InvalidDataException.throwException(data, null, InviteDeleteEvent.class,
                    new Object[]{channelId, code}, new String[]{CHANNEL_ID_KEY, CODE_KEY});
            return null; //unreachable Statement
        }

        return new InviteDeleteEvent(lApi, payload, guildId, channelId, code);
    }

    /**
     * the id as {@link Snowflake} of the channel the invite is for
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the id as {@link Snowflake} of the channel the invite is for
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }

    /**
     * 	the unique invite code
     */
    public @NotNull String getCode() {
        return code;
    }
}
