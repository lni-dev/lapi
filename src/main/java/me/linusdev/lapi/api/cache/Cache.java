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

package me.linusdev.lapi.api.cache;

import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;


public class Cache implements EventListener, HasLApi {

    private final @NotNull Object lock = new Object();
    private final @NotNull AtomicBoolean constructorFinished = new AtomicBoolean(false);

    private final @NotNull LApiImpl lApi;

    private @Nullable Snowflake currentUserId = null;
    private @Nullable Snowflake currentApplicationId = null;

    public Cache(@NotNull LApiImpl lApi) {
        this.lApi = lApi;
        this.currentApplicationId = lApi.getConfig().getApplicationId();

        if(lApi.getConfig().isFlagSet(ConfigFlag.ENABLE_GATEWAY)) {
            lApi.getEventTransmitter().addListener(this);

        } else {
            lApi.getRequestFactory().getCurrentUser().queue((user, error) -> {
                if(error != null) {
                    Logger.getLogger(this).error(error.getThrowable());
                    return;
                }

                currentUserId = user.getIdAsSnowflake();
                if(currentApplicationId == null)
                    currentApplicationId = currentUserId;

                synchronized (lock) {
                    if(!constructorFinished.get()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            LogInstance log = Logger.getLogger(this);
                            log.error("Interrupted while waiting for the constructor to finish! (Cache:66)");
                            log.error(e);
                        }
                    }
                }
                lApi.transmitEvent().onCacheReady(lApi, new CacheReadyEvent(lApi, this));
            });

        }

        synchronized (lock) {
            constructorFinished.set(true);
            lock.notifyAll();
        }

    }

    /**
     * If the {@link EventIdentifier#CACHE_READY CACHE_READY} event has been triggered,
     * this will not return {@code null}.
     * @return id as {@link Snowflake} of the current user
     */
    public @Nullable Snowflake getCurrentUserIdAsSnowflake() {
        return currentUserId;
    }

    /**
     * If the {@link EventIdentifier#CACHE_READY CACHE_READY} event has been triggered,
     * this will not return {@code null}.
     * @return id as {@link String} of the current user
     */
    public @Nullable String getCurrentUserId() {
        if(currentUserId == null) return null;
        return currentUserId.asString();
    }

    /**
     * If the {@link EventIdentifier#CACHE_READY CACHE_READY} event has been triggered,
     * this will not return {@code null}.
     * @return id as {@link Snowflake} of the current application
     */
    public @Nullable Snowflake getCurrentApplicationIdAsSnowflake() {
        return currentApplicationId;
    }

    /**
     * If the {@link EventIdentifier#CACHE_READY CACHE_READY} event has been triggered,
     * this will not return {@code null}.
     * @return id as {@link String} of the current application
     */
    public @Nullable String getCurrentApplicationId() {
        if(currentApplicationId == null) return null;
        return currentApplicationId.asString();
    }

    @Override
    public void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        this.currentUserId = event.getUser().getIdAsSnowflake();
        this.currentApplicationId = event.getApplication().getIdAsSnowflake();


        lApi.runSupervised(() -> {
            synchronized (lock) {
                if(!constructorFinished.get()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        LogInstance log = Logger.getLogger(this);
                        log.error("Interrupted while waiting for the constructor to finish! (Cache:134)");
                        log.error(e);
                    }
                }
            }
            this.lApi.transmitEvent().onCacheReady(lApi, new CacheReadyEvent(lApi, this));
            this.lApi.getEventTransmitter().removeListener(this);
        });

    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}