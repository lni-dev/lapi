/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.lapiandqueue;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link ThreadFactory} that creates {@link QueueThread queue-threads} where {@link Thread#isDaemon()} is {@code false}.
 * This means, that as long as any of these threads are running, the JVM won't exit
 */
public class QueueThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger id;

    public QueueThreadFactory(){
        this.group = new ThreadGroup("queue-thread-group");

        this.id = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        QueueThread queueThread = new QueueThread(group, r, "queue-thread-" + id.getAndIncrement());

        if(!queueThread.isDaemon())
            queueThread.setDaemon(false);
        if(queueThread.getPriority() != Thread.NORM_PRIORITY)
            queueThread.setPriority(Thread.NORM_PRIORITY);

        return queueThread;
    }
}
