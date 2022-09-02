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

package me.linusdev.lapi.api.communication.gateway.events.webhooks;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#webhooks-update" target="_top">Discord Documentation</a>
 */
public class WebhooksUpdateEvent extends Event {

    private final @NotNull Snowflake channelId;

    public WebhooksUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId, @NotNull Snowflake channelId) {
        super(lApi, payload, guildId);
        this.channelId = channelId;
    }

    /**
     * id as {@link Snowflake} of the channel
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * id as {@link String} of the channel
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }
}
