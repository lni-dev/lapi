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

package me.linusdev.lapi.api.communication.gateway.events.message;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.message.abstracts.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-update" target="_TOP">Discord Documentation</a>
 */
public class MessageUpdateEvent extends Event {

    protected final @NotNull MessageImplementation message;

    public MessageUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @NotNull MessageImplementation message) {
        super(lApi, payload, message.getGuildIdAsSnowflake());
        this.message = message;
    }

    /**
     * The message that was created.
     *
     * @return {@link Message}
     */
    public @NotNull Message getMessage() {
        return message;
    }

    /**
     *
     * @return the channel-id of the channel, the message was sent in
     */
    public @NotNull String getChannelId() {
        return message.getChannelId();
    }
}
