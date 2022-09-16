/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.async;

import me.linusdev.lapi.api.async.exception.CancellationException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractFuture<R, S> implements Future<R, S>{

    public static final @NotNull LogInstance log = Logger.getLogger(AbstractFuture.class.getSimpleName());

    private final @NotNull Task<R, S> task;
    private volatile boolean canceled = false;
    private volatile boolean started = false;
    private volatile boolean done = false;

    private final Object lock = new Object();

    private volatile @Nullable Consumer<Future<R, S>> before;
    private volatile @Nullable ResultConsumer<R, S> then;

    private volatile @Nullable ComputationResult<R, S> result;

    public AbstractFuture(@NotNull Task<R, S> task) {
        this.task = task;
    }

    protected @NotNull Task<R, S> getTask() {
        return task;
    }



    public void completeHere() {

        synchronized (lock) {
            if (isDone() || hasStarted()) return;
        }

        try {
            final Consumer<Future<R, S>> before = this.before;
            if(before != null) before.accept(this);
        } catch (Throwable t) {
            log.error("Unexpected Exception in a Future before listener.");
            log.error(t);
        }

        synchronized (lock) {
            if(isCanceled()) {
                lock.notifyAll();
                return;
            }
            started = true;
        }

        final @NotNull ComputationResult<R, S> result = task.execute();

        synchronized (lock) {
            this.result = result;
            this.done = true;
            lock.notifyAll();
        }

        try {
            final ResultConsumer<R, S> then = this.then;
            if(then != null) then.consume(result.getResult(), result.getSecondary());
        } catch (Throwable t) {
            log.error("Unexpected Exception in a Future then listener.");
            log.error(t);
        }

    }

    @Override
    public @NotNull Future<R, S> cancel() {
        synchronized (lock) {
            canceled = true;
        }
        return this;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public @NotNull Future<R, S> beforeExecution(@NotNull Consumer<Future<R, S>> consumer) {
        synchronized (lock) {
            if(before == null) before = consumer;
            else {
                before = before.andThen(consumer);
            }
        }
        return this;
    }

    @Override
    public @NotNull Future<R, S> then(@NotNull ResultConsumer<R, S> consumer) {
        synchronized (lock) {
            if(then == null) then = consumer;
            else {
                then = then.thenConsume(consumer);
            }
        }
        return this;
    }

    @Override
    public @NotNull ComputationResult<R, S> get() throws InterruptedException {
        synchronized (lock) {
            if(isCanceled()) throw new CancellationException();
            if(!isDone()) lock.wait();
            if(isCanceled()) throw new CancellationException();
            return result;
        }
    }

    @Override
    public @NotNull LApi getLApi() {
        return task.getLApi();
    }
}
