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

package me.linusdev.lapi.api.communication.gateway.queue;

import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayOpcode;
import me.linusdev.lapi.api.communication.gateway.other.GatewayPayload;

public class DispatchEventQueueTest {

    public static void main(String[] args) throws InvalidDataException {

        DispatchEventQueue queue = new DispatchEventQueue(10);

        queue.push(createPayload(1));
        queue.push(createPayload(2));
        queue.push(createPayload(3));

        System.out.println(queue);

        queue.push(createPayload(6));
        System.out.println(queue);

        queue.push(createPayload(4));
        queue.push(createPayload(5));
        System.out.println(queue);

        queue.reset();
        System.out.println(queue);

        queue.push(createPayload(1));
        queue.push(createPayload(2));
        queue.push(createPayload(3));

        System.out.println(queue);

        queue.pull();
        queue.pull();
        queue.pull();
        queue.pull();

        System.out.println(queue);

        queue.push(createPayload(4));
        queue.push(createPayload(5));

        System.out.println(queue);

        queue = DispatchEventQueue.fromData(queue.getData());

        System.out.println(queue);
    }

    public static ReceivedPayload createPayload(long sequence) {
        return new ReceivedPayload(new GatewayPayload(GatewayOpcode.DISPATCH, null, sequence, null, null));
    }
}
