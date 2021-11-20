package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.discordbotapi.api.other.Container;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @param <T> Type of the data that should be retrieved
 */
public abstract class Queueable<T> implements HasLApi {

    /**
     * queues this {@link Queueable} and returns a {@link Future}<br>
     * see {@link Future} and {@link LApi#queue(Queueable) LApi.queue(Queueable)} for more information!
     * @return {@link Future}
     * @see Future
     * @see LApi#queue(Queueable) LApi.queue(Queueable)
     */
    public @NotNull Future<T> queue(){
        return getLApi().queue(this);
    }

    /**
     * Queues this {@link Queueable} after given delay and returns a {@link Future}.<br>
     * See {@link Future} and {@link LApi#queueAfter(Queueable, long, TimeUnit)}  LApi.queueAfter(Queueable, long, TimeUnit)} for more information!
     * @param delay the delay
     * @param timeUnit {@link TimeUnit} to use for the delay
     * @return {@link Future}
     * @see Future
     * @see LApi#queueAfter(Queueable, long, TimeUnit) LApi.queueAfter(Queueable, long, TimeUnit)
     */
    public @NotNull Future<T> queueAfter(long delay, TimeUnit timeUnit){
        return getLApi().queueAfter(this, delay, timeUnit);
    }

    /**
     * Queues this {@link Queueable} and returns a {@link Future}.<br>
     * This also sets the {@link Future#then(BiConsumer)} Listener.<br>
     * See {@link #queue()} for more information.
     * @return {@link Future}
     * @see #queue()
     * @see Future#then(BiConsumer)
     */
    public @NotNull Future<T> queue(BiConsumer<T, Error> then){
        return queue().then(then);
    }

    /**
     * Queues this {@link Queueable} and returns a {@link Future}.<br>
     * This also sets the {@link Future#then(Consumer)} Listener.<br>
     * See {@link #queue()} for more information.
     * @return {@link Future}
     * @see #queue()
     * @see Future#then(Consumer)
     */
    public @NotNull Future<T> queue(Consumer<T> then){
        return queue().then(then);
    }

    /**
     * Queues this {@link Queueable} and returns a {@link Future}.<br>
     * This also sets the {@link Future#beforeComplete(Consumer)} Listener.<br>
     * See {@link #queue()} for more information.
     * @return {@link Future}
     * @see #queue()
     * @see Future#beforeComplete(Consumer)
     */
    public @NotNull Future<T> queueAndSetBeforeComplete(Consumer<Future<T>> beforeComplete){
        return queue().beforeComplete(beforeComplete);
    }

    /**
     * Queues this {@link Queueable} and return its result.<br>
     * This will call {@link Future#get(long, TimeUnit)} and wait this Thread!<br>
     * @return the result, should not be {@code null}
     * @throws ExecutionException if an {@link Error} occurred. If you use this method, you won't have access to this Error.
     * @throws InterruptedException if the Thread was interrupted
     */
    public @NotNull T queueAndWait() throws ExecutionException, InterruptedException {
        return queue().get();
    }

    public @NotNull Container<T> completeHere(){
        getLApi().checkQueueThread();
        return completeHereAndIgnoreQueueThread();
    }

    /**
     * Complete this {@link Queueable<T>} in this Thread.
     */
    protected abstract @NotNull Container<T> completeHereAndIgnoreQueueThread();

}
