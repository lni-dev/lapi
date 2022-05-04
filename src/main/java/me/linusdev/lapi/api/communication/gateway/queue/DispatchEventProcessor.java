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

public abstract class DispatchEventProcessor {

    protected final @NotNull DispatchEventQueue queue;
    protected final @NotNull GatewayWebSocket gateway;

    protected DispatchEventProcessor(@NotNull DispatchEventQueue queue, @NotNull GatewayWebSocket gateway) {
        this.queue = queue;
        this.gateway = gateway;
    }

    /**
     * Called when a new element is available in the queue - this call should always be thread safe, e.g. in a synchronized
     * context.
     */
    public abstract void onNext();
}
