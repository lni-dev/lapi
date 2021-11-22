package me.linusdev.discordbotapi.api.lapiandqueue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

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

    public void wait(Object lock, long timeoutMillis) throws InterruptedException {
        isWaiting.set(true);
        lock.wait(timeoutMillis);
        isWaiting.set(false);
        lock.wait(2); //to make sure, every notify call gets through while waiting
    }

    public boolean isWaiting(){
        return isWaiting.get();
    }
}
