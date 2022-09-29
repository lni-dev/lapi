/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.lapi.api.async.ComputationResult;
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.async.queue.QueueableImpl;
import me.linusdev.lapi.api.communication.http.ratelimit.*;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.thread.LApiThread;
import me.linusdev.lapi.api.thread.LApiThreadGroup;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * This is a special thread for the {@link LApi#queue}.
 * Only one instance of this Thread should be alive at the same time.
 */
public class QueueThread extends LApiThread implements HasLApi {

    private final @NotNull LApiImpl lApi;
    private final @NotNull Queue<QueueableFuture<?>> queue;

    private final @NotNull Bucket globalBucket;
    private final @NotNull Map<String, Bucket> buckets;
    private final @NotNull Map<RateLimitId, Bucket> bucketsForId;
    private final @NotNull Object bucketsWriteLock = new Object();
    private final @NotNull AtomicInteger sharedResourceRateLimitSize = new AtomicInteger(0);

    private final @NotNull AtomicBoolean stopIfEmpty = new AtomicBoolean(false);
    private final @NotNull AtomicBoolean stopImmediately = new AtomicBoolean(false);

    private final @NotNull AtomicBoolean isWaiting;
    private final @NotNull Object waitingLock = new Object();

    private final @NotNull LogInstance log = Logger.getLogger(this);

    private final BucketDebugger d;

    public QueueThread(@NotNull LApiImpl lApi, @NotNull LApiThreadGroup group, @NotNull Queue<QueueableFuture<?>> queue) {
        super(lApi, group, "queue-thread");
        this.lApi = lApi;
        this.queue = queue;

        this.isWaiting = new AtomicBoolean(false);

        this.globalBucket = Bucket.newGlobalBucket(lApi);
        this.buckets = new ConcurrentHashMap<>();
        this.bucketsForId = new ConcurrentHashMap<>();
        d = new BucketDebugger(bucketsForId, globalBucket);
    }

    @Override
    public void run() {
        log.log("Started queue thread.");
        try {
            boolean hasSRRL;
            @NotNull RateLimitId id;

            workLoop: while (!stopImmediately.get()) {
                if(queue.peek() == null && stopIfEmpty.get()) break;
                awaitNotifyIf(10000L, () -> queue.peek() == null);

                //noinspection ConstantConditions: checked by below if
                final @NotNull QueueableFuture<?> future = queue.poll();
                if (future == null) continue;

                final @NotNull QueueableImpl<?> task = future.getTask();
                final @NotNull Query query = task.getQuery();
                if(query.getLink().isBoundToGlobalRateLimit() && !globalBucket.canSendOrAddToQueue(future)) {
                    continue workLoop;
                }

                synchronized (sharedResourceRateLimitSize) {
                    hasSRRL = sharedResourceRateLimitSize.get() > 1;
                }

                @Nullable RateLimitId sharedResourceId = null;
                @Nullable Bucket sharedResourceBucket = null;
                if(hasSRRL) {
                    sharedResourceId = RateLimitId.newSharedResourceIdentifier(query);
                    sharedResourceBucket = bucketsForId.get(sharedResourceId);
                    if(sharedResourceBucket != null){
                        if(!sharedResourceBucket.canSendOrAddToQueue(future)) continue workLoop;
                    }
                }

                id = RateLimitId.newIdentifier(query);
                Bucket bucket = getOrPutBucket(id, () -> Bucket.newAssumedBucket(lApi));
                if (!bucket.canSendOrAddToQueue(future)) continue workLoop;


                ComputationResult<?, QResponse> result;
                if(Logger.DEBUG_LOG){
                    log.debug("queue.poll().executeHere()");
                    long millis = System.currentTimeMillis();
                    result = future.executeHere();
                    long finishMillis = System.currentTimeMillis() - millis;
                    log.debug("queue.poll().executeHere() finished in " + finishMillis + " milliseconds");

                }else{
                    result = future.executeHere();

                }

                if(result != null) {
                    LApiHttpResponse response = result.getSecondary().getResponse();
                    if(response == null) {
                        //It could not send the request for some reason. It's important that we increment the remaining
                        //amount for the bucket of this id. Otherwise, the bucket might never reset, and request could get stuck
                        bucket.incrementRemaining();
                        if(sharedResourceBucket != null) sharedResourceBucket.incrementRemaining();
                        continue;
                    }
                    if(response.isRateLimitResponse()) {
                        //noinspection ConstantConditions: checked by above if
                        final @NotNull RateLimitResponse rateLimitResponse = response.getRateLimitResponse();
                        assert rateLimitResponse != null;

                        if(rateLimitResponse.isGlobal()) {
                            globalBucket.onRateLimit(future, rateLimitResponse);

                        } else if(response.getRateLimitScope() == RateLimitScope.SHARED) {
                            if(sharedResourceId == null) sharedResourceId = RateLimitId.newSharedResourceIdentifier(query);
                            final @NotNull Bucket sRBucket = getOrPutBucket(sharedResourceId, () -> Bucket.newSharedResourceBucket(lApi, future));
                            sRBucket.onRateLimit(future, rateLimitResponse);

                        } else if(response.getRateLimitScope() == RateLimitScope.USER) {
                            RateLimitHeaders headers = response.getRateLimitHeaders();
                            if(headers == null) {
                                log.warning("Received response without rate limit headers");
                                bucket.incrementRemaining();
                                continue workLoop;
                            }
                            final @NotNull String bucketName = headers.getBucket();
                            final @NotNull Bucket gotBucket = getOrPutBucket(bucketName, id, bucket);
                            gotBucket.onRateLimitAndMakeConcrete(future, rateLimitResponse, bucketName, headers);
                            log.warning("We got user rate limited!");
                        }

                    } else {
                        //Not a RateLimitResponse
                        RateLimitHeaders headers = response.getRateLimitHeaders();
                        if(headers == null) {
                            log.warning("Received response without rate limit headers");
                            bucket.incrementRemaining();
                            continue workLoop;
                        }
                        final @NotNull String bucketName = headers.getBucket();
                        final @NotNull Bucket gotBucket = getOrPutBucket(bucketName, id, bucket);

                        if(!gotBucket.makeConcrete(bucketName, headers))
                            gotBucket.onResponse(headers);
                    }
                } else {
                    //result is null
                    bucket.incrementRemaining();
                    if(sharedResourceBucket != null) sharedResourceBucket.incrementRemaining();
                }
            }
        } catch (InterruptedException e) {
            if(stopImmediately.get()) {
                log.debug("Queue thread interrupted to stop immediately.");
                return;
            }
            log.error("Queue thread interrupted for no reason");
            log.error(e);

        } catch (Throwable t) {
            //This is so any exceptions in this Thread are caught and printed.
            //Otherwise, they would just vanish and no one would know what happened
            log.error(t);

        }
    }

    private @NotNull Bucket getOrPutBucket(@NotNull RateLimitId id, @NotNull Supplier<Bucket> supplier) {
        synchronized (bucketsWriteLock) {
            return bucketsForId.computeIfAbsent(id, rateLimitId -> {
                Bucket b = supplier.get();
                if(b.getBucket() != null) buckets.put(b.getBucket(), b);
                return b;
            });
        }
    }

    private @NotNull Bucket getOrPutBucket(@NotNull String bucket, @NotNull RateLimitId id, @NotNull Bucket other) {
        synchronized (bucketsWriteLock) {
            Bucket got = buckets.get(bucket);
            if(got == null) {
                buckets.put(bucket, other);
                return other;
            } else {
                bucketsForId.put(id, got);
                other.delete();
                return got;
            }
        }
    }

    public void stopIfEmpty() {
        this.stopIfEmpty.set(true);
    }

    public void stopImmediately() {
        this.stopImmediately.set(true);
        this.interrupt();
    }

    /**
     * Synchronizes on {@link #waitingLock} and waits only if given condition is met. This assures, that the queue does
     * always work on any new incoming {@link QueueableFuture futures} immediately.
     * @param timeoutMillis how long to wait
     * @param check {@link BooleanSupplier}. Only if {@code true} is returned the queue will wait.
     */
    public <T> void awaitNotifyIf(long timeoutMillis, @NotNull BooleanSupplier check) throws InterruptedException {
        synchronized (waitingLock) {
            if(!check.getAsBoolean()) return;
            try {
                isWaiting.set(true);
                waitingLock.wait(timeoutMillis);
                isWaiting.set(false);
            }finally {
                isWaiting.set(false);
            }
        }

    }

    public void notifyAllAwaiting() {
        synchronized (waitingLock) {
            waitingLock.notifyAll();
        }
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public boolean allowBlockingOperations() {
        return false;
    }
}
