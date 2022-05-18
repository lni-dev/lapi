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

package me.linusdev.lapi.api.communication.gateway.queue.processor.parallel;

import me.linusdev.lapi.api.communication.gateway.queue.DispatchEventQueue;
import me.linusdev.lapi.api.communication.gateway.queue.ReceivedPayload;
import me.linusdev.lapi.api.communication.gateway.queue.processor.DispatchEventProcessor;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiThreadDispatchEventProcessor extends DispatchEventProcessor {

    public static final String NO_GUILD_KEY = "none";

    private final int threadAmount;
    private final ConcurrentHashMap<String, Queue<ReceivedPayload>> queues;
    private final ConcurrentLinkedQueue<Queue<ReceivedPayload>> nextQueue;
    private int nextThreadToChange = 0;
    private final ProcessorThread[] threads;

    public MultiThreadDispatchEventProcessor(@NotNull LApiImpl lApi, @NotNull DispatchEventQueue queue,
                                                @NotNull GatewayWebSocket gateway, int threadAmount) {
        super(lApi, queue, gateway);
        this.threadAmount = threadAmount;
        queues = new ConcurrentHashMap<>();
        queues.put(NO_GUILD_KEY, new ConcurrentLinkedQueue<>());
        this.nextQueue = new ConcurrentLinkedQueue<>();

        this.threads = new ProcessorThread[threadAmount];
        for(int i = 0; i < threadAmount; i++) {
            threads[i] = new ProcessorThread(Thread.currentThread().getThreadGroup(), i, this);
            threads[i].start();
        }
    }

    @Override
    public synchronized void onNext() {
        ReceivedPayload payload = queue.pull();
        if(payload == null) return;

        Queue<ReceivedPayload> q = queues.computeIfAbsent(payload.isFromGuild() ? payload.getGuildId() : NO_GUILD_KEY,
                s -> new ConcurrentLinkedQueue<>());
        q.offer(payload);

        for(ProcessorThread thread : threads) {
            if(thread.notifyChangeQueue(q)) {
                return;
            }
        }

        nextQueue.add(q);
        threads[nextThreadToChange++].notifyChangeQueue();
        nextThreadToChange = nextThreadToChange % threadAmount;
    }

    public synchronized @Nullable Queue<ReceivedPayload> nextQueue() {
        return nextQueue.poll();
    }



}
