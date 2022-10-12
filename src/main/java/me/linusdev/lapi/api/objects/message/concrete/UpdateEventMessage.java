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

package me.linusdev.lapi.api.objects.message.concrete;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.objects.message.GatewayMessage;
import me.linusdev.lapi.api.objects.message.ImplementationType;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for messages received in the {@link GatewayEvent#MESSAGE_UPDATE MESSAGE_UPDATE} event.
 */
public interface UpdateEventMessage extends GatewayMessage {

    /**
     * The original {@link SOData} received over the gateway.
     * @return {@link SOData}
     */
    @NotNull SOData getOriginalData();

    @Override
    default @NotNull ImplementationType getImplementationType() {
        return ImplementationType.MESSAGE_UPDATE_EVENT_MESSAGE;
    }
}
