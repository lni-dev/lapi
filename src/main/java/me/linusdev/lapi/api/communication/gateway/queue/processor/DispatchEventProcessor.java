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

package me.linusdev.lapi.api.communication.gateway.queue.processor;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayOpcode;
import me.linusdev.lapi.api.communication.gateway.queue.DispatchEventQueue;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

/**
 * This class processes {@link GatewayOpcode#DISPATCH DISPATCH} events in a different {@link Thread}.
 * This means, this class calls {@link GatewayWebSocket#handleReceivedEvent(GatewayPayloadAbstract)  handleReceivedEvent}
 * for every event in a Thread.<br>
 * The first event handled for each guild, must always be {@link GatewayEvent#GUILD_CREATE GUILD_CREATE}, see also
 * {@link GatewayWebSocket#handleReceivedEvent(GatewayPayloadAbstract)  handleReceivedEvent}.
 */
public abstract class DispatchEventProcessor implements HasLApi {

    protected final @NotNull LApiImpl lApi;
    protected final @NotNull DispatchEventQueue queue;
    protected final @NotNull GatewayWebSocket gateway;

    public DispatchEventProcessor(@NotNull LApiImpl lApi, @NotNull DispatchEventQueue queue, @NotNull GatewayWebSocket gateway) {
        this.lApi = lApi;
        this.queue = queue;
        this.gateway = gateway;
    }

    /**
     * Called when a new element is available in the queue - this method should always be thread safe, e.g. synchronized.
     */
    public abstract void onNext();

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    public @NotNull GatewayWebSocket getGateway() {
        return gateway;
    }
}
