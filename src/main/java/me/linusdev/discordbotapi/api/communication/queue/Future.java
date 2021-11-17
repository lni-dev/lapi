package me.linusdev.discordbotapi.api.communication.queue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * TODO
 *
 * @param <T> the result Class
 */
public class Future<T> implements java.util.concurrent.Future<T> {

    private final @NotNull Queueable<T> queueable;
    private boolean cancelled = false;
    private @Nullable Container<T> result = null;

    private @Nullable BiConsumer<T, Error> then;


    public Future(@NotNull Queueable<T> queueable){
        this.queueable = queueable;
    }


    /**
     * waits this Thread and calls {@link Queueable#completeHere()}.<br>
     * Instead of using this you probably want to use {@link Queueable#completeHere()} directly
     *
     * This method will also {@link #notify()} all Threads waiting on {@link #get()} or {@link #get(long, TimeUnit)}
     *
     * This method will also call all Listeners: {@link #then}
     *
     * @see Queueable#completeHere()
     */
    public void completeHere(){
        if(this.cancelled){
            synchronized (this) {
                this.notify();
            }
            return;
        }
        this.result = queueable.completeHere();

        synchronized (this) {
            this.notify();
        }

        if(then != null) then.accept(result.get(), result.getError());
    }


    //listener

    /**
     *
     * @param consumer to consume the result once it has been retrieved
     */
    public Future<T> then(BiConsumer<T, Error> consumer){
        this.then = consumer;
        return this;
    }

    /**
     *
     * @return {@link Error} if an Error has occurred or {@code null} if no Error has occurred or {@link #isDone()} is {@code false}
     */
    @Nullable
    public Error getError(){
        if(result == null) return null;
        return result.getError();
    }

    /**
     * This method will cancel this Future, meaning that it won't be completed (if it is not already running!)<br>
     * This method will not remove the Future from the Queue(Doesn't matter tho, since it will just be skipped)
     *
     * @param mayInterruptIfRunning should always be {@code false}. cannot interrupt if {@link Future#completeHere()} is already running!
     * @return {@code false} if {@link #isDone()} is {@code true}, {@code true} otherwise
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if(isDone()) return false;
        this.cancelled = true;
        return true;
    }

    /**
     *
     * @return true if this Future has been {@link #cancel(boolean) canceled}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * whether {@link #queueable} has {@link Queueable#completeHere() completed}
     * @return {@link #result} != {@code null}
     */
    @Override
    public boolean isDone() {
        return result != null;
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     * <br><br>
     * It won't call {@link #completeHere()}. {@link me.linusdev.discordbotapi.api.LApi LApi} does this.
     *
     * @return the computed result, should never be {@code null}
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        if(result != null) return result.get();

        synchronized (this){
            this.wait();
        }

        if(result == null && isCancelled()) throw new CancellationException("Task has been canceled");
        if(result.hasError()) throw new ExecutionException("There was an Error while completing the Queueable", result.getError().getThrowable());
        return result == null ? null : result.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * <br><br>
     * It won't call {@link #completeHere()}. {@link me.linusdev.discordbotapi.api.LApi LApi} does this.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * @throws TimeoutException if the wait timed out
     */
    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(result != null) return result.get();

        synchronized (this) {
            this.wait(unit.toMillis(timeout));
        }

        if(result == null && isCancelled()) throw new CancellationException("Task has been canceled");
        if(result == null) throw new TimeoutException("Timed out");
        if(result.hasError()) throw new ExecutionException("There was an Error while completing the Queueable", result.getError().getThrowable());
        return result.get();
    }
}
