/*
 * Copyright  2022 Linus Andera
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

import me.linusdev.lapi.api.communication.exceptions.NoInternetException;
import me.linusdev.lapi.api.other.Container;
import me.linusdev.lapi.api.other.Error;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
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
 * {@link LApi#queue(Queueable, BiConsumer, Consumer, Consumer)}  LApi.queue(Queueable)}.
 * This will create a new {@link Future<Queueable> Future&lt;Queueable&gt;} and add it to the
 * {@link LApi#queue LApi.queue}.
 * The Future will now pass through the queue. If the Future is {@link #cancel(boolean) cancled},
 * it will remain in the queue and pass through it, but it won't be {@link #completeHere() completed}
 * <br>
 * <br>
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">Before the future will supposedly {@link Queueable#completeHereAndIgnoreQueueThread() complete}:</span><br>
 * <p style="margin-top:0;padding-top:0;margin-bottom:0;padding-bottom:0">
 * {@link #beforeComplete} Listener will be called before and can still {@link #cancel(boolean)} this Future
 * </p> <br>
 *
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">Once the Future has finished (if it wasn't canceled):</span><br>
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
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">If the Future was {@link #cancel(boolean) cancled}:</span><br>
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
 * </ul><br>
 *
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">If {@link #then(BiConsumer)} or {@link #then(Consumer)} is set after the {@link #result} has already been retrieved:</span><br>
 * <p style="margin-top:0;padding-top:0;margin-bottom:0;padding-bottom:0">
 *     The Listener will be called immediately in the setting thread
 * </p>
 *
 * @param <T> the result Class
 */
public class Future<T> implements java.util.concurrent.Future<T> {

    private final static LogInstance logger = Logger.getLogger(Future.class.getSimpleName());

    private final LApiImpl lApi;

    private final @NotNull Queueable<T> queueable;
    private volatile boolean cancelled = false;
    private volatile @Nullable Container<T> result = null;
    private volatile boolean executedBeforeComplete = false;

    private volatile @Nullable BiConsumer<T, Error> then;
    private volatile @Nullable Consumer<T> thenSingle;
    private volatile @Nullable Consumer<Future<T>> beforeComplete;


    public Future(LApiImpl lApi, @NotNull Queueable<T> queueable) {
        this.lApi = lApi;
        this.queueable = queueable;
    }

    /**
     * waits this Thread and calls {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * Instead of using this you probably want to use {@link Queueable#completeHereAndIgnoreQueueThread()} directly
     * <p>
     * This method will also {@link #notifyAll() notify} all Threads waiting on {@link #get()} or {@link #get(long, TimeUnit)}
     * <p>
     * This method will also call all Listeners: {@link #beforeComplete}, {@link #then} and {@link #thenSingle}
     *
     * @see Queueable#completeHereAndIgnoreQueueThread()
     */
    public void completeHere() {
        queueable.getLApi().checkQueueThread();
        completeHereAndIgnoreQueueThread();
    }

    /**
     * This is only for {@link LApi}. DO NOT USE!<br><br>
     * <p>
     * This allows {@link #completeHere()} to be executed on the queue thread.
     */
    protected void completeHereAndIgnoreQueueThread() {
        final Consumer<Future<T>> beforeComplete = this.beforeComplete;
        if (!cancelled && beforeComplete != null && !executedBeforeComplete){
            try{
                beforeComplete.accept(this);
                executedBeforeComplete = true;
            }catch (Throwable t){
                logger.warning("Unexpected Exception in a Future beforeComplete listener");
                logger.error(t);
            }
        }

        if (this.cancelled) {
            synchronized (this) {
                this.notifyAll();
            }
            return;
        }

        try {
            this.result = queueable.completeHereAndIgnoreQueueThread();
        }catch (NoInternetException noInternetException){
            lApi.queue(this);
            return;
        }

        synchronized (this) {
            this.notifyAll();
        }
        //Fire the then listeners
        final BiConsumer<T, Error> then = this.then;
        if (then != null){
            try {
                then.accept(result.get(), result.getError());
            } catch (Throwable t){
                logger.warning("Unexpected Exception in a Future then listener");
                logger.error(t);
            }
        }
        final Consumer<T> thenSingle = this.thenSingle;
        if (thenSingle != null){
            try {
                thenSingle.accept(result.get());
            } catch (Throwable t){
                logger.warning("Unexpected Exception in a Future thenSingle listener");
                logger.error(t);
            }
        }

    }


    //listener

    /**
     * {@link Consumer#accept(Object)} will be called before {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * <br>
     * You can still cancel the {@link Future} in this Listener
     *
     * <p>
     *     If the {@link #result} has already been retrieved, or is currently being retrieved, at the time, this Listener is set,
     *     the Listener will never be called
     * </p>
     *
     * @param beforeComplete to handle the Future before {@link Queueable#completeHereAndIgnoreQueueThread()} will be called
     * @return this
     */
    public @NotNull Future<T> beforeComplete(Consumer<Future<T>> beforeComplete) {
        this.beforeComplete = beforeComplete;
        return this;
    }

    /**
     * {@link BiConsumer#accept(Object, Object)} will be called after {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * {@link #get()} will be notified before too
     * <br>
     * <br> Error will be {@code null} if no Error occurred
     * <br> T will be {@code null} if an {@link Future#hasError() Error} has occurred
     * <br>
     * <p>
     *     If a {@link #result} has already been retrieved, {@link #then} will be executed immediately in the calling thread
     * </p>
     *
     * @param then to consume the result once it has been retrieved
     * @return this
     */
    public @NotNull Future<T> then(@Nullable BiConsumer<T, Error> then) {
        Logger.log(Logger.Type.DEBUG, "Future", null, "set then listener", false);
        this.then = then;
        if(then != null && result != null) then.accept(result.get(), result.getError());
        return this;
    }

    /**
     * {@link Consumer#accept(Object)} will be called after {@link Queueable#completeHereAndIgnoreQueueThread()}.<br>
     * {@link #then(BiConsumer)} will be called before too.<br>
     * {@link #get()} will be notified before too.<br>
     * <br> T will be {@code null} if an {@link Future#hasError() Error} has occurred
     * <br>
     * <p>
     *     If a {@link #result} has already been retrieved, {@link #thenSingle} will be executed immediately in the calling thread
     * </p>
     *
     * @param thenSingle to consume the result once it has been retrieved
     * @return this
     */
    public @NotNull Future<T> then(@Nullable Consumer<T> thenSingle) {
        Logger.log(Logger.Type.DEBUG, "Future", null, "set thenSingle listener", false);
        this.thenSingle = thenSingle;
        if(thenSingle != null && result != null) thenSingle.accept(result.get());
        return this;
    }

    /**
     * @return {@link Error} if an Error has occurred or {@code null} if no Error has occurred or {@link #isDone()} is {@code false}
     */
    @Nullable
    public Error getError() {
        if (result == null) return null;
        return result.getError();
    }

    /**
     * @return {@code true} if an Error has occurred, {@code false} otherwise or if {@link #result} == {@code null}
     */
    public boolean hasError() {
        if (result == null) return false;
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
        if (isDone()) return false;
        this.cancelled = true;
        return true;
    }

    /**
     * @return true if this Future has been {@link #cancel(boolean) canceled}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * whether {@link #queueable} has {@link Queueable#completeHereAndIgnoreQueueThread() completed}
     *
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
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (result != null) return result.get();
        queueable.getLApi().checkQueueThread();

        synchronized (this) {
            if (result == null)
                this.wait();
        }

        if (result == null && isCancelled()) throw new CancellationException("Task has been canceled");
        if (result != null && result.hasError())
            throw new ExecutionException("There was an Error while completing the Queueable", result.getError().getThrowable());
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
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (result != null) return result.get();
        queueable.getLApi().checkQueueThread();

        synchronized (this) {
            this.wait(unit.toMillis(timeout));
        }

        if (result == null && isCancelled()) throw new CancellationException("Task has been canceled");
        if (result == null) throw new TimeoutException("Timed out");
        if (result.hasError())
            throw new ExecutionException("There was an Error while completing the Queueable", result.getError().getThrowable());
        return result.get();
    }
}
