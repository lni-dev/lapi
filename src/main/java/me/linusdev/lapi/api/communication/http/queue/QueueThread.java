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
import me.linusdev.lapi.api.communication.http.ratelimit.Bucket;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitHeaders;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitId;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitScope;
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

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

/**
 * This is a special thread for the {@link LApi#queue}.
 * Only one instance of this Thread should be alive at the same time.
 */
public class QueueThread extends LApiThread implements HasLApi {

    private final @NotNull LApiImpl lApi;
    private final @NotNull Queue<QueueableFuture<?>> queue;

    private final @NotNull RateLimitQueue globalRateLimitQueue;
    private final @NotNull Map<String, Bucket> buckets;
    private final @NotNull Map<RateLimitId, List<Bucket>> bucketsForId;
    private final @NotNull AtomicInteger sharedResourceRateLimitSize = new AtomicInteger(0);

    private final @NotNull AtomicBoolean stopIfEmpty = new AtomicBoolean(false);
    private final @NotNull AtomicBoolean stopImmediately = new AtomicBoolean(false);

    private final @NotNull AtomicBoolean isWaiting;
    private final @NotNull Object waitingLock = new Object();

    private final @NotNull LogInstance log = Logger.getLogger(this);

    public QueueThread(@NotNull LApiImpl lApi, @NotNull LApiThreadGroup group, @NotNull Queue<QueueableFuture<?>> queue) {
        super(lApi, group, "queue-thread");
        this.lApi = lApi;
        this.queue = queue;

        this.isWaiting = new AtomicBoolean(false);
        this.globalRateLimitQueue = new RateLimitQueue(lApi, queue);

        this.buckets = new ConcurrentHashMap<>();
        this.bucketsForId = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        log.log("Started queue thread.");
        try {
            boolean hasSRRL;
            @Nullable RateLimitId sharedResourceId;
            @NotNull RateLimitId id;

            workLoop: while (!stopImmediately.get()) {
                if(queue.peek() == null && stopIfEmpty.get()) break;
                awaitNotifyIf(10000L, () -> queue.peek() == null);

                QueueableFuture<?> future = queue.poll();
                if (future == null) continue;

                QueueableImpl<?> task = future.getTask();
                Query query = task.getQuery();
                if(query.getLink().isBoundToGlobalRateLimit() && globalRateLimitQueue.add(future)) {
                    //If add returns true, we have been globally rate limited...
                    continue workLoop;
                }

                synchronized (sharedResourceRateLimitSize) {
                    hasSRRL = sharedResourceRateLimitSize.get() > 1;
                }

                if(hasSRRL) {
                    sharedResourceId = RateLimitId.newSharedResourceIdentifier(query);
                    List<Bucket> buckets = bucketsForId.get(sharedResourceId);
                    if(buckets != null){
                        for(int i = 0; i < buckets.size(); i++)
                            if(!buckets.get(i).canSendOrAddToQueue(future)) continue workLoop;
                    }
                }

                id = RateLimitId.newIdentifier(query);
                List<Bucket> buckets = bucketsForId.get(id);

                if(buckets == null) {
                    //TODO: create "Future" Bucket

                } else {
                    for(int i = 0; i < buckets.size(); i++)
                        if(!buckets.get(i).canSendOrAddToQueue(future)) continue workLoop;

                }


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
                    if(response == null) continue;
                    if(response.isRateLimitResponse()) {
                        if(response.getRateLimitResponse().isGlobal()) {
                            //TODO: add retry amount to config
                            globalRateLimitQueue.onRateLimit(future, 25, response.getRateLimitResponse().getRetryAfter());

                        } else if(response.getRateLimitScope() == RateLimitScope.SHARED) {
                            //TODO: get / create bucket
                            //response.getRateLimitHeaders().getBucket()
                            //bucket.onResponse(future, response);

                        } else if(response.getRateLimitScope() == RateLimitScope.USER) {
                            //Bad
                            //TODO: what do we do?#
                            //bucket.onResponse(future, response);
                        }
                    } else {
                        RateLimitHeaders headers = response.getRateLimitHeaders();
                        if(headers == null) {
                            log.warning("Received response without rate limit headers");
                            continue workLoop;
                        }

                        Bucket bucket = this.buckets.get(headers.getBucket());

                        if(bucket == null) {
                            //should never happen
                            //TODO: create new bucket
                            continue workLoop;
                        }

                        bucket.onResponse(future, response);
                    }
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
