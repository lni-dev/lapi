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

package me.linusdev.lapi.api.communication.http.ratelimit;

import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSocket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Bucket {

    private final static @NotNull LogInstance log = Logger.getLogger("Bucket");

    private final @NotNull LApiImpl lApi;

    private final @NotNull Queue<QueueableFuture<?>> queue;
    private final @NotNull AtomicInteger queueSize = new AtomicInteger(0);

    private final @NotNull Object limitLock = new Object();
    private volatile long limit;
    private volatile long remaining;
    private volatile long resetMillis;
    private volatile String bucket;

    private final @NotNull Object assumedLock = new Object();
    private volatile boolean assumed;

    private final boolean limitless;

    private Bucket(@NotNull LApiImpl lApi, long limit) {
        this.lApi = lApi;
        this.assumed = true;
        this.limit = limit;
        this.resetMillis = -1L;
        this.remaining = limit;
        this.limitless = false;
        queue = new ConcurrentLinkedQueue<>();
    }

    private Bucket(@NotNull LApiImpl lApi, long limit, long remaining, long resetMillis, String bucket, boolean assumed, boolean limitless) {
        this.lApi = lApi;
        this.limit = limit;
        this.remaining = remaining;
        this.resetMillis = resetMillis;
        this.bucket = bucket;
        this.assumed = assumed;
        this.limitless = limitless;
        queue = new ConcurrentLinkedQueue<>();
    }

    public static @NotNull Bucket newAssumedBucket(@NotNull LApiImpl lApi) {
        //TODO: add assumed bucket limit to config.
        return new Bucket(lApi, 3);
    }

    public static @NotNull Bucket newLimitlessBucket(@NotNull LApiImpl lApi, @NotNull String bucket) {
        return new Bucket(lApi, 0L, 1L, -1L, bucket, false, true);
    }

    /**
     *
     * @param future {@link QueueableFuture} to send.
     * @return {@code true} if given future can be sent. {@code false} it will be added to a queue.
     */
    public boolean canSendOrAddToQueue(@NotNull QueueableFuture<?> future) {
        synchronized (limitLock){
            if(remaining >= 1L) {
                if(!limitless) remaining--;
                return true;

            } else if (resetMillis > 0L && resetMillis <= System.currentTimeMillis()) {
                resetMillis = -1L;
                remaining = limitless ? 1L : limit-1L;
                return true;

            }
        }

        queue.add(future);
        return false;
    }

    public void onResponse(@NotNull RateLimitHeaders headers) {
        synchronized (limitLock) {
            if(this.limit != headers.getLimit())
                adjustLimit(headers.getLimit());
            checkRemaining(headers.getRemaining());
            this.resetMillis = headers.getResetMillis();
        }
    }

    public void onRateLimit(@NotNull QueueableFuture<?> future, @NotNull LApiHttpResponse response, @NotNull RateLimitResponse rateLimitResponse) {
        this.remaining = 0L;
        this.resetMillis = rateLimitResponse.getRetryAtMillis();
        add(future);
    }

    public void makeConcrete(@NotNull String bucket, @NotNull RateLimitHeaders headers) {
        synchronized (assumedLock) {
            synchronized (limitLock) {
                this.assumed = false;
                this.remaining = headers.getLimit() - this.limit - this.remaining;
                if(this.remaining > headers.getRemaining()) {
                    log.error("Bucket remaining miscalculation: calculated=" + this.remaining + ", received: " + headers.getRemaining());
                    this.remaining = headers.getRemaining();
                }
                if(!limitless) this.limit = headers.getLimit();
                this.resetMillis = headers.getResetMillis();
                this.bucket = bucket;
            }
        }
    }

    private void add(@NotNull QueueableFuture<?> future) {
        synchronized (queueSize) {
            if(queueSize.get() == 0) {
                //TODO: async reset()
            }

            queueSize.incrementAndGet();
        }
        queue.add(future);
    }

    private void reset() {
        //remaining will be reset in canSendOrAddToQueue

        for(int i = 0; i < limit; i++){
            QueueableFuture<?> future = queue.poll();
            if(future == null) return;
            lApi.queue(future);
        }

        synchronized (queue) {
            if(queue.peek() != null) {
                //TODO: async reset()
            }
        }

    }

    private void adjustLimit(long newLimit) {
        synchronized (limitLock) {
            if (limitless) return;
            this.remaining = newLimit - (this.limit - this.remaining);
            this.limit = newLimit;
        }
    }


    private void checkRemaining(long receivedRemaining) {
        synchronized (limitLock) {
            if (this.remaining > receivedRemaining) {
                log.error("Bucket remaining miscalculation: calculated=" + this.remaining + ", received: " + receivedRemaining);
                this.remaining = receivedRemaining;
            }
        }
    }


}
