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

package me.linusdev.lapi.api.communication.gateway.events.message;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.message.concrete.CreateEventMessage;
import org.jetbrains.annotations.NotNull;


public class MessageCreateEvent extends Event {

    protected final @NotNull CreateEventMessage message;

    public MessageCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @NotNull CreateEventMessage message) {
        super(lApi, payload, message.getGuildIdAsSnowflake());
        this.message = message;
    }

    /**
     * The message that was created.
     *
     * @return {@link CreateEventMessage}
     */
    public @NotNull CreateEventMessage getMessage() {
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
