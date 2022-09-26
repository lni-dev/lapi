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

package me.linusdev.lapi.api.communication.http.queue;

import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RateLimitQueue implements HasLApi {

    private final @NotNull Object lock = new Object();

    private final @NotNull LApiImpl lApi;

    private final @NotNull Queue<QueueableFuture<?>> queue;
    private final @NotNull Queue<QueueableFuture<?>> rateLimitedQueue;
    private volatile int retryAmount;
    private volatile long retryAt;
    private volatile long retryIn;
    private volatile boolean rateLimited;

    public RateLimitQueue(@NotNull LApiImpl lApi, @NotNull Queue<QueueableFuture<?>> queue) {
        this.lApi = lApi;
        this.queue = queue;
        this.rateLimitedQueue = new ConcurrentLinkedQueue<>();
        this.rateLimited = false;
    }

    /**
     *
     * @param rateLimitedFuture the rate limited future. Will automatically be {@link #add(QueueableFuture) added}
     * @param retryAmount how many futures to requeue
     * @param retryIn in how many seconds to requeue the futures
     */
    public void onRateLimit(@NotNull QueueableFuture<?> rateLimitedFuture, int retryAmount, double retryIn) {
        synchronized (lock) {
            rateLimited = true;
        }
        this.retryAmount = retryAmount;
        this.retryIn = Double.valueOf(retryIn * 1000d).longValue();
        this.retryAt = System.currentTimeMillis() + this.retryIn;
        add(rateLimitedFuture);

        lApi.runSupervised(this::retry, retryAt - System.currentTimeMillis());
    }

    /**
     *
     * @param rateLimitedFuture the future to add.
     * @return {@code true} if it was added
     */
    public boolean add(@NotNull QueueableFuture<?> rateLimitedFuture) {
        synchronized (lock) {
            if(!rateLimited) return false;
            this.rateLimitedQueue.add(rateLimitedFuture);
        }

        return true;
    }

    private void retry() {
        synchronized (lock) {
            rateLimited = false;
        }
        for(int i = 0; i < retryAmount; i++) {
            QueueableFuture<?> future = rateLimitedQueue.poll();
            if(future == null) return;
            queue.add(future);
        }

        synchronized (lock) {
            if(rateLimitedQueue.peek() != null) {
                this.retryAt = System.currentTimeMillis() + this.retryIn;
                lApi.runSupervised(this::retry, retryAt - System.currentTimeMillis());
            }
        }
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
