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

package me.linusdev.lapi.api.communication.gateway.queue;

import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadDispatchEventProcessor extends DispatchEventProcessor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SingleThreadDispatchEventProcessor(@NotNull DispatchEventQueue queue, @NotNull GatewayWebSocket gateway) {
        super(queue, gateway);
    }

    @Override
    public synchronized void onNext() {
        ReceivedPayload payload = queue.pull();
        executor.submit(() -> gateway.handleReceivedEvent(payload.getPayload()));
    }
}
