package me.linusdev.discordbotapi.api.config;

import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Config {

    /**
     * retrieves and saves the voice regions on startup
     */
    public final static long LOAD_VOICE_REGIONS_ON_STARTUP = 0x1L;

    private final long flags;
    private final Queue<Future<?>> queue;

    public Config(long flags, Queue<Future<?>> queue){
        this.flags = flags;

        if(queue == null)
            this.queue = new ConcurrentLinkedQueue<Future<?>>();
        else this.queue = queue;
    }

    /**
     *
     * @param flag to check, can also be more than one flag
     * @return true if all bits in flag are also set int {@link #flags}
     */
    public boolean isFlagSet(long flag){
        return (flags & flag) == flag;
    }

    /**
     * The Queue used by {@link LApi} to queue any {@link Queueable}
     */
    public Queue<Future<?>> getQueue() {
        return queue;
    }
}
