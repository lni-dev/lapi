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

import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.communication.gateway.queue.DispatchEventQueue;
import me.linusdev.lapi.api.communication.gateway.queue.ReceivedPayload;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadDispatchEventProcessor extends DispatchEventProcessor implements EventListener {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final LogInstance logger;

    private final ArrayList<ReceivedPayload> postponedEvents;

    public SingleThreadDispatchEventProcessor(@NotNull LApiImpl lApi, @NotNull DispatchEventQueue queue, @NotNull GatewayWebSocket gateway) {
        super(lApi, queue, gateway);
        this.logger = Logger.getLogger(this);
        this.postponedEvents = new ArrayList<>();
    }

    @Override
    public synchronized void onNext() {
        ReceivedPayload payload = queue.peek();
        if(payload == null) return;

        if(payload.getGuildId() != null && payload.getType() != GatewayEvent.GUILD_CREATE && payload.getType() != GatewayEvent.GUILD_DELETE &&
                lApi.getGuildManager() != null && !lApi.getGuildManager().allGuildsReceivedEvent()) {
            //Not all guilds received a ready event yet. we cannot call handle events
            logger.debug("onNext(), but event handle will be postponed because not all guilds are ready yet");
            postponedEvents.add(queue.pull());
            return;

        } else if(postponedEvents.size() > 0 && lApi.getGuildManager() != null && lApi.getGuildManager().allGuildsReceivedEvent()) {
            for(ReceivedPayload p : postponedEvents) {
                executor.submit(() -> gateway.handleReceivedEvent(p.getPayload()));
            }

        }
        //noinspection ConstantConditions
        executor.submit(() -> gateway.handleReceivedEvent(queue.pull().getPayload()));
    }

    @Override
    public synchronized void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {
        while(queue.peek() != null) {
            //noinspection ConstantConditions
            executor.submit(() -> gateway.handleReceivedEvent(queue.pull().getPayload()));
        }
    }
}
