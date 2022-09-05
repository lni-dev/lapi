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

package me.linusdev.lapi.api.communication.gateway.events.error;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This Event occurs, when an Error in {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket GatewayWebSocket's}
 * event handling appears. Errors that cause this event, are by no means {@link Exception Exceptions}, but logical errors instead.
 */
public class LApiErrorEvent extends Event {

     private final @Nullable GatewayEvent inEvent;
     private final @Nullable LApiError error;

    public LApiErrorEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable GatewayEvent inEvent, @Nullable LApiError error) {
        super(lApi, payload, null);
        this.inEvent = inEvent;
        this.error = error;
    }

    /**
     * The event, the error was caused by.
     */
    public @Nullable GatewayEvent getInEvent() {
        return inEvent;
    }

    public @Nullable LApiError getError() {
        return error;
    }
}
