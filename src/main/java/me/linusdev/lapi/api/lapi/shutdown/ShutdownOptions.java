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

package me.linusdev.lapi.api.lapi.shutdown;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.config.Config;

public enum ShutdownOptions implements ShutdownOption {

    /**
     * Stops the queue the next time it is empty.<br>
     * Note: if the queue reaches the {@link Config#getMaxShutdownTime() max shutdown time} it will
     * try to stop immediately instead.
     */
    QUEUE_STOP_IF_EMPTY(1 << 0, 1 << 1),

    /**
     * Stops the queue as soon as possible.
     */
    QUEUE_STOP_IMMEDIATELY(1 << 1, 1 << 0),

    /**
     * If you set this, the queue will accept new futures.<br>
     * Note: if the queue reaches the {@link Config#getMaxShutdownTime() max shutdown time} it will
     * try to stop immediately instead.
     */
    QUEUE_ACCEPT_NEW_FUTURES(1 << 2, 0),

    /**
     * The {@link GatewayWebSocket gateway} will {@link GatewayWebSocket#abort() abort} it's connection instead
     * of {@link GatewayWebSocket#disconnect(String) disconnecting}.
     * This is useful, if you want to try to {@link GatewayWebSocket#resume(SOData) resume} the session after
     * restarting your bot. You should get the {@link GatewayWebSocket#getData() gateway data} directly after
     * calling the shutdown method.
     */
    GATEWAY_ABORT(1 << 3, 0),
    ;

    private final long id;
    private final long mutuallyExclusiveWith;

    ShutdownOptions(long id, long mutuallyExclusiveWith) {
        this.id = id;
        this.mutuallyExclusiveWith = mutuallyExclusiveWith;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public long mutuallyExclusiveWith() {
        return mutuallyExclusiveWith;
    }
}
