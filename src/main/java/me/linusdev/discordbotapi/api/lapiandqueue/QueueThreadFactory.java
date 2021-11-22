package me.linusdev.discordbotapi.api.lapiandqueue;

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
