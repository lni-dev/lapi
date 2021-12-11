package me.linusdev.discordbotapi.api.config;

import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class Config {

    /**
     * retrieves and saves the voice regions on startup
     */
    public final static long LOAD_VOICE_REGIONS_ON_STARTUP = 0x1L;

    private final long flags;
    private final @NotNull Supplier<Queue<Future<?>>> queueSupplier;
    private final @NotNull String token;

    public Config(long flags, @NotNull Supplier<Queue<Future<?>>> queueSupplier, @NotNull String token){
        this.flags = flags;
        this.token = token;

        this.queueSupplier = queueSupplier;
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
    public Queue<Future<?>> getNewQueue() {
        return queueSupplier.get();
    }

    public @NotNull String getToken() {
        return token;
    }
}
