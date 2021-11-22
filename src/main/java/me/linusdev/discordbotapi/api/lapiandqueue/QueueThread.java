package me.linusdev.discordbotapi.api.lapiandqueue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a special thread for the {@link LApi#queue}.
 * This thread has special {@link #wait(Object, long)} and {@link #isWaiting()} methods used by {@link LApi} to avoid some bugs
 * Only one instance of this Thread should be alive at the same time.
 */
public class QueueThread extends Thread{

    private final AtomicBoolean isWaiting;

    public QueueThread(@Nullable ThreadGroup group, @NotNull Runnable target, @NotNull String name) {
        super(group, target, name);

        isWaiting = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        super.run();
    }

    /**
     * This will wait an additional 2 milliseconds after the first wait, to assure every notify call coming through.
     * Because some thread could call {@link #isWaiting()} right before when {@link #isWaiting} is set to {@code false} and then
     * do some stuff. But when this stuff is happening, the thread isn't actually waiting anymore, which could be fatal.
     *
     * @param lock the object to use {@link #wait()} on
     * @param timeoutMillis how long to wait
     */
    public void wait(Object lock, long timeoutMillis) throws InterruptedException {
        try {
            isWaiting.set(true);
            lock.wait(timeoutMillis);
            isWaiting.set(false);
            lock.wait(2); //to make sure, every notify call gets through while waiting
        }finally {
            isWaiting.set(false);
        }
    }

    /**
     *
     * @return true if {@link #wait(Object, long)} has been called and has not yet timed out, notified or interrupted
     */
    public boolean isWaiting(){
        return isWaiting.get();
    }
}
