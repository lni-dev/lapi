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

import java.util.Queue;

public class ProcessorThread extends Thread{

    private final @NotNull MultiThreadDispatchEventProcessor processor;
    private final @NotNull GatewayWebSocket gateway;

    private boolean running;

    private Queue<ReceivedPayload> queue;
    private boolean changeQueue;
    private boolean waiting;

    public ProcessorThread(@NotNull ThreadGroup group, int id, @NotNull MultiThreadDispatchEventProcessor processor) {
        super(group, null, "dispatch-event-processor-" + id);
        setDaemon(false);
        this.gateway = processor.getGateway();
        this.processor = processor;
    }

    @Override
    public void run() {
        try {
            while(isRunning()) {

                synchronized (this) {
                    if(!isRunning()) break;
                    waiting = true;
                    this.wait();
                    waiting = false;
                }

                if(!isRunning()) break;

                ReceivedPayload payload;

                while ((payload=queue.poll()) != null) {
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

    public synchronized void shutdown() {
        this.running = false;
        this.notify();
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized boolean isWaiting() {
        return waiting;
    }

    public synchronized void notifyChangeQueue() {
        changeQueue = true;
    }
}
