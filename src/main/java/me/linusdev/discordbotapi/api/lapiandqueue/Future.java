package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.discordbotapi.api.other.Container;
import me.linusdev.discordbotapi.api.other.Error;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * When {@link Queueable#queue()} is called, the Queueable will call
 * {@link LApi#queue(Queueable) LApi.queue(Queueable)}.
 * This will create a new {@link Future<Queueable> Future&lt;Queueable&gt;} and adds it to the
 * {@link LApi#queue LApi.queue}.
 * The Future will now pass through the queue. If the Future is {@link #cancel(boolean) cancled},
 * it will remain in the queue and pass through it, but it won't be {@link #completeHere() completed}
 * <br>
 * <br>
 * <h3 style="margin-bottom:0;padding-bottom:0">Before the future will supposedly {@link Queueable#completeHereAndIgnoreQueueThread() complete}:</h3>
 * <p style="margin-top:0;padding-top:0;margin-bottom:0;padding-bottom:0">
 *     {@link #beforeComplete} Listener will be called before and can still {@link #cancel(boolean)} this Future
 * </p> <br>
 *
 * <h3 style="margin-bottom:0;padding-bottom:0">Once the Future has finished (if it wasn't canceled):</h3>
 * Meaning that {@link Queueable#completeHereAndIgnoreQueueThread()} has been called and finished. The following actions will follow
 * in following order
 * <ul style="margin-top:0;padding-top:0;margin-bottom:0;padding-bottom:0">
 *     <li>
 *         all Threads waiting on {@link #get()} or {@link #get(long, TimeUnit)} will be resumed
 *     </li>
 *     <li>
 *         {@link #then(BiConsumer)} Listener will be called
 *     </li>
 *     <li>
 *         {@link #then(Consumer)} Listener will be called
 *     </li>
 * </ul><br>
 *
 * <h3 style="margin-bottom:0;padding-bottom:0">If the Future was {@link #cancel(boolean) cancled}:</h3>
 * <ul style="margin-top:0;padding-top:0;margin-bottom:0;padding-bottom:0">
 *     <li>
 *         All Threads waiting on {@link #get()} or {@link #get(long, TimeUnit)} will be resumed and {@code null} will
 *         be returned
 *     </li>
 *     <li>
 *         {@link Queueable#completeHereAndIgnoreQueueThread()} will not be called
 *     </li>
 *     <li>
 *         {@link #beforeComplete(Consumer)} Listener will not be called
 *     </li>
 * </ul>
 *
 * @param <T> the result Class
 */
public class Future<T> implements java.util.concurrent.Future<T> {

    private final @NotNull Queueable<T> queueable;
    private boolean cancelled = false;
    private @Nullable Container<T> result = null;

    private @Nullable BiConsumer<T, Error> then;
    private @Nullable Consumer<T> thenSingle;
    private @Nullable Consumer<Future<T>> beforeComplete;


    public Future(@NotNull Queueable<T> queueable){
        this.queueable = queueable;
    }

    /**
     * waits this Thread and calls {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * Instead of using this you probably want to use {@link Queueable#completeHereAndIgnoreQueueThread()} directly
     *
     * This method will also {@link #notifyAll() notify} all Threads waiting on {@link #get()} or {@link #get(long, TimeUnit)}
     *
     * This method will also call all Listeners: {@link #beforeComplete}, {@link #then} and {@link #thenSingle}
     *
     * @see Queueable#completeHereAndIgnoreQueueThread()
     */
    public void completeHere(){
        queueable.getLApi().checkQueueThread();
        completeHereAndIgnoreQueueThread();
    }

    /**
     * This is only for {@link LApi}. DO NOT USE!<br><br>
     *
     * This allows {@link #completeHere()} to be executed on the queue thread.
     */
    protected void completeHereAndIgnoreQueueThread(){
        if(!cancelled && beforeComplete != null) beforeComplete.accept(this);

        if(this.cancelled){
            synchronized (this) {
                this.notifyAll();
            }
            return;
        }

        this.result = queueable.completeHereAndIgnoreQueueThread();

        synchronized (this) {
            this.notifyAll();
        }

        //Fire the then listeners
        if(then != null) then.accept(result.get(), result.getError());
        if(thenSingle != null) thenSingle.accept(result.get());
    }


    //listener

    /**
     * {@link Consumer#accept(Object)} will be called before {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * <br>
     * You can still cancel the {@link Future} in this Listener
     * @param beforeComplete to handle the Future before {@link Queueable#completeHereAndIgnoreQueueThread()} will be called
     * @return this
     */
    public @NotNull Future<T> beforeComplete(Consumer<Future<T>> beforeComplete){
        this.beforeComplete = beforeComplete;
        return this;
    }

    /**
     * {@link BiConsumer#accept(Object, Object)} will be called after {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * {@link #get()} will be notified before too
     * <br>
     * <br> Error will be {@code null} if no Error occurred
     * <br> T will be {@code null} if an {@link Future#hasError() Error} has occurred
     *
     * @param then to consume the result once it has been retrieved
     * @return this
     */
    public @NotNull Future<T> then(@Nullable BiConsumer<T, Error> then){
        this.then = then;
        return this;
    }

    /**
     * {@link Consumer#accept(Object)} will be called after {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * {@link #then(BiConsumer)} will be called before too.<br>
     * {@link #get()} will be notified before too.<br>
     * <br>
     * <br> T will be {@code null} if an {@link Future#hasError() Error} has occurred
     *
     * @param thenSingle to consume the result once it has been retrieved
     * @return this
     */
    public @NotNull Future<T> then(@Nullable Consumer<T> thenSingle){
        this.thenSingle = thenSingle;
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
     *
     * @return {@code true} if an Error has occurred, {@code false} otherwise or if {@link #result} == {@code null}
     */
    public boolean hasError(){
        if(result == null) return false;
        return result.hasError();
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
     * whether {@link #queueable} has {@link Queueable#completeHereAndIgnoreQueueThread() completed}
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
     * It won't call {@link #completeHere()}. {@link LApi LApi} does this.
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
        queueable.getLApi().checkQueueThread();

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
     * It won't call {@link #completeHere()}. {@link LApi LApi} does this.
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
        queueable.getLApi().checkQueueThread();

        synchronized (this) {
            this.wait(unit.toMillis(timeout));
        }

        if(result == null && isCancelled()) throw new CancellationException("Task has been canceled");
        if(result == null) throw new TimeoutException("Timed out");
        if(result.hasError()) throw new ExecutionException("There was an Error while completing the Queueable", result.getError().getThrowable());
        return result.get();
    }
}
