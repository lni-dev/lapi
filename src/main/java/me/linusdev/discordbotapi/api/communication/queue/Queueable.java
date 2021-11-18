package me.linusdev.discordbotapi.api.communication.queue;

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
public interface Queueable<T> extends HasLApi {

    /**
     * queues this {@link Queueable} and returns a {@link Future}<br>
     * see {@link Future} and {@link me.linusdev.discordbotapi.api.LApi#queue(Queueable) LApi.queue(Queueable)} for more information!
     * @return {@link Future}
     * @see Future
     * @see me.linusdev.discordbotapi.api.LApi#queue(Queueable) LApi.queue(Queueable)
     */
    default @NotNull Future<T> queue(){
        return getLApi().queue(this);
    }

    /**
     * Queues this {@link Queueable} after given delay and returns a {@link Future}.<br>
     * See {@link Future} and {@link me.linusdev.discordbotapi.api.LApi#queueAfter(Queueable, long, TimeUnit)}  LApi.queueAfter(Queueable, long, TimeUnit)} for more information!
     * @param delay the delay
     * @param timeUnit {@link TimeUnit} to use for the delay
     * @return {@link Future}
     * @see Future
     * @see me.linusdev.discordbotapi.api.LApi#queueAfter(Queueable, long, TimeUnit) LApi.queueAfter(Queueable, long, TimeUnit)
     */
    default @NotNull Future<T> queueAfter(long delay, TimeUnit timeUnit){
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
    default @NotNull Future<T> queue(BiConsumer<T, Error> then){
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
    default @NotNull Future<T> queue(Consumer<T> then){
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
    default @NotNull Future<T> queueAndSetBeforeComplete(Consumer<Future<T>> beforeComplete){
        return queue().beforeComplete(beforeComplete);
    }

    /**
     * Queues this {@link Queueable} and return its result.<br>
     * This will call {@link Future#get(long, TimeUnit)} and wait this Thread!<br>
     * @return the result, should not be {@code null}
     * @throws ExecutionException if an {@link Error} occurred. If you use this method, you won't have access to this Error.
     * @throws InterruptedException if the Thread was interrupted
     */
    default @NotNull T queueAndWait() throws ExecutionException, InterruptedException {
        return queue().get();
    }

    /**
     * Complete this {@link Queueable<T>} in this Thread.
     */
    @NotNull Container<T> completeHere();

}
