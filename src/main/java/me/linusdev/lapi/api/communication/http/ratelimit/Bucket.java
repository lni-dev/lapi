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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class Bucket {

    private final static @NotNull LogInstance log = Logger.getLogger("Bucket");

    private final @NotNull LApiImpl lApi;

    private final @NotNull Queue<QueueableFuture<?>> queue;
    private final @NotNull AtomicInteger queueSize = new AtomicInteger(0);
    private final @NotNull AtomicBoolean resetScheduled = new AtomicBoolean(false);

    private final @NotNull Object limitLock = new Object();
    private volatile long limit;
    private volatile long remaining;
    private volatile long resetMillis;
    private volatile long resetAfterMillis;
    private volatile @Nullable String bucket;

    private final @NotNull Object assumedLock = new Object();
    /**
     * Over the lifetime of a {@link Bucket} this variable may only change from {@code true} to {@code false}, but
     * never wise-versa!
     */
    private volatile boolean assumed;

    private final boolean limitless;

    private volatile boolean deleted = false;

    private Bucket(@NotNull LApiImpl lApi, long limit, long remaining, long resetMillis, long resetAfterMillis, @Nullable String bucket, boolean assumed, boolean limitless) {
        this.lApi = lApi;
        this.limit = limit;
        this.remaining = remaining;
        this.resetMillis = resetMillis;
        this.resetAfterMillis = resetAfterMillis;
        this.bucket = bucket;
        this.assumed = assumed;
        this.limitless = limitless;
        queue = new ConcurrentLinkedQueue<>();
    }

    public static @NotNull Bucket newAssumedBucket(@NotNull LApiImpl lApi) {
        long limit = lApi.getConfig().getHttpRateLimitAssumedBucketLimit();
        return new Bucket(lApi, limit, limit, -1L, -1L, null,true, false);
    }

    /**
     *
     * @param lApi {@link LApiImpl}
     * @param limit how many to queue at once after a rate limit. The queue itself is limitless
     * @param bucket {@link String} name
     * @return new limitless {@link Bucket}
     */
    public static @NotNull Bucket newLimitlessBucket(@NotNull LApiImpl lApi, long limit, @NotNull String bucket) {
        return new Bucket(lApi, limit, 1L, -1L, -1L, bucket, false, true);
    }

    public static @NotNull Bucket newGlobalBucket(@NotNull LApiImpl lApi) {
        long globalBucketRetryLimit = lApi.getConfig().getGlobalHttpRateLimitRetryLimit();
        return newLimitlessBucket(lApi, globalBucketRetryLimit, "global");
    }

    public static @NotNull Bucket newSharedResourceBucket(@NotNull LApiImpl lApi, @NotNull QueueableFuture<?> future) {
        return newLimitlessBucket(lApi, -1L, "sharedResourceBucket_" + future.getTask().getName());
    }

    /**
     *
     * @param future {@link QueueableFuture} to send.
     * @return {@code true} if given future can be sent. {@code false} it will be added to a queue.
     */
    public boolean canSendOrAddToQueue(@NotNull QueueableFuture<?> future) {
        synchronized (limitLock){
            if (resetMillis >= 0L && resetMillis <= System.currentTimeMillis()) {
                reset();
                synchronized (queueSize) {
                    if(queueSize.get() == 0) {
                        return canSendOrAddToQueue(future);
                    }
                }
            }

            if(remaining >= 1L) {
                if(!limitless) remaining--;
                return true;

            }
        }

        add(future);
        return false;
    }

    /**
     * This method should never be called from a synchronized context (of this Bucket).
     * @param headers {@link RateLimitHeaders}
     */
    public void onResponse(@NotNull RateLimitHeaders headers) {
        synchronized (limitLock) {
            if(this.limit != headers.getLimit())
                adjustLimit(headers.getLimit());
            checkRemaining(headers.getRemaining());
            this.resetMillis = headers.getResetMillis();
            this.resetAfterMillis = this.resetMillis - System.currentTimeMillis();
        }
        checkReset();
    }

    public void onRateLimit(@NotNull QueueableFuture<?> future, @NotNull RateLimitResponse rateLimitResponse) {
        synchronized (limitLock) {
            this.remaining = 0L;
            this.resetMillis = rateLimitResponse.getRetryAtMillis();
            this.resetAfterMillis = rateLimitResponse.getRetryAfterMillis();
        }
        add(future);
    }

    public void onRateLimitAndMakeConcrete(@NotNull QueueableFuture<?> future, @NotNull RateLimitResponse rateLimitResponse,
                                           @NotNull String bucket, @NotNull RateLimitHeaders headers) {
        //This is not synchronized, to avoid unnecessary synchronization:
        //assumed is volatile and will only change from true to false, but never from false to true!
        //This means, if the below if-statement evaluates to true, assumed will remain definitely constant
        if(!assumed) onRateLimit(future, rateLimitResponse);

        //now synchronize on assumedLock too.
        //assumed is checked again in makeConcrete, because it may have changed from true to false!
        synchronized (assumedLock) {
            synchronized (limitLock) {
                makeConcrete(bucket, headers);
                onRateLimit(future, rateLimitResponse);
            }
        }
        checkReset();
    }

    /**
     *
     * @param bucket {@link String} bucket name
     * @param headers {@link RateLimitHeaders} of response
     * @return {@code false} if the bucket was not an assumed bucket. {@code true} if this bucket was assumed before this call.
     */
    public boolean makeConcrete(@NotNull String bucket, @NotNull RateLimitHeaders headers) {
        synchronized (assumedLock) {
            if(!assumed) return false;
            synchronized (limitLock) {
                this.assumed = false;
                adjustLimit(headers.getLimit());
                checkRemaining(headers.getRemaining());
                this.resetMillis = headers.getResetMillis();
                this.resetAfterMillis = this.resetMillis - System.currentTimeMillis();
                this.bucket = bucket;
            }
        }

        return true;
    }

    public void delete() {
        synchronized (queueSize) {
            for(QueueableFuture<?> future : queue) {
                lApi.queue(future);
            }
        }
        this.deleted = true;
    }

    /**
     * Not probably synchronized and should not be used for program logic without extra synchronization!
     */
    public boolean isDeleted() {
        return deleted;
    }


    /**
     * Not probably synchronized and should not be used for program logic without extra synchronization!
     */
    public boolean isAssumed() {
        return assumed;
    }


    /**
     * Not probably synchronized and should not be used for program logic without extra synchronization!
     */
    public int getQueueSize() {
        return queueSize.get();
    }

    private void add(@NotNull QueueableFuture<?> future) {
        synchronized (queueSize) {
            queue.add(future);
            queueSize.incrementAndGet();
        }
        checkReset();
    }

    private void reset() {
        synchronized (limitLock){
            resetMillis = -1L;
            remaining = limitless ? 1L : limit;
        }
    }

    public void incrementRemaining() {
        synchronized (limitLock) {
            remaining++;
        }
        synchronized (queueSize) {
            if(queueSize.get() > 0) {
                assert queue.peek() != null;
                lApi.queue(queue.poll());
                queueSize.decrementAndGet();
            }
        }
    }

    private void emptyQueue() {
        log.debug("Emptying the queue...");
        synchronized (queueSize) {
            for (int i = 0; limit < 0 || i < limit; i++) {
                QueueableFuture<?> future = queue.poll();
                if (future == null){
                    synchronized (resetScheduled) {
                        resetScheduled.set(false);
                    }
                    return;
                }
                queueSize.decrementAndGet();
                lApi.queue(future);
            }

            if (queue.peek() != null && !resetScheduled.get()) {
                log.debug("Queue is still not completely empty. Emptying again in " + resetAfterMillis + " ms.");
                synchronized (resetScheduled) {
                    asyncReset(resetAfterMillis);
                }
            } else {
                synchronized (resetScheduled) {
                    resetScheduled.set(false);
                }
            }


        }
    }

    private void checkReset() {
        synchronized (queueSize) {
            synchronized (resetScheduled) {
                final long resetMillisCopy = resetMillis; //So we do not have to synchronize on limitLock
                if (resetMillisCopy > -1 && !resetScheduled.get() && queueSize.get() > 0) {
                    asyncReset(resetMillisCopy - System.currentTimeMillis());
                }
            }
        }
    }

    /**
     * must always be called synchronized on {@link #resetScheduled}!
     * @param delay in how many milliseconds to schedule the reset.
     */
    private void asyncReset(long delay) {
        //This will actually not reset, but only empty the queue.
        //The actual reset will happen in canSendOrAddToQueue.
        log.debug("async reset in " + Math.max(0, delay));
        resetScheduled.set(true);
        lApi.runSupervised(this::emptyQueue, Math.max(0, delay));
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

    public @Nullable String getBucket() {
        return bucket;
    }

    @Override
    public String toString() {
        return String.format(
                "%sBucket '%s'%s:\n" +
                "   limit=%d" +
                "   remaining=%d",
                (assumed ? "Assumed " : "") + (limitless ? "Limitless " : ""),
                bucket,
                limitless || resetMillis < 0 ? "" : String.format(" resets in %.2fs", ((Long) (resetMillis - System.currentTimeMillis())).doubleValue() / 1000d),
                limit,
                remaining) +
                ("    queueSize=" + queueSize.get() + " | " + queue.size());
    }
}
