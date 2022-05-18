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

import me.linusdev.lapi.api.communication.gateway.queue.ReceivedPayload;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class ProcessorThread extends Thread{

    private final @NotNull MultiThreadDispatchEventProcessor processor;
    private final @NotNull GatewayWebSocket gateway;

    private volatile boolean running;

    private volatile @Nullable Queue<ReceivedPayload> queue;
    private boolean changeQueue;
    private boolean waiting;

    public ProcessorThread(@NotNull ThreadGroup group, int id, @NotNull MultiThreadDispatchEventProcessor processor) {
        super(group, null, "dispatch-event-processor-" + id);
        setDaemon(false);
        this.gateway = processor.getGateway();
        this.processor = processor;

        this.running = true;
        this.changeQueue = false;
        this.waiting = false;
    }

    @Override
    public void run() {
        try {
            while(isRunning()) {

                synchronized (this) {
                    queue = processor.nextQueue();
                    if(queue != null || changeQueue) {
                        changeQueue = false;
                    } else {
                        waiting = true;
                        this.wait();
                        waiting = false;

                    }
                }

                if(!isRunning()) break;

                ReceivedPayload payload;

                while (queue != null && (payload=queue.poll()) != null) {
                    gateway.handleReceivedEvent(payload.getPayload());

                    synchronized (this) {
                        if(changeQueue) {
                            queue = processor.nextQueue();
                            changeQueue = false;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(this).error(t);
        }
    }

    public void shutdown() {
        this.running = false;
        synchronized (this){
            this.notify();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized boolean isWaiting() {
        return waiting;
    }

    public synchronized void notifyChangeQueue() {
        changeQueue = true;
        this.notify();
    }

    public synchronized boolean notifyChangeQueue(@NotNull Queue<ReceivedPayload> queue) {
        if(isWaiting()) {
            this.queue = queue;
            this.notify();
            return true;
        }

        return false;
    }
}
