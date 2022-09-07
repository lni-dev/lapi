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

package me.linusdev.lapi.api.lapiandqueue;

import me.linusdev.lapi.api.cache.CacheReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManagerReadyEvent;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class LApiReadyListener implements EventListener, HasLApi {
    private final @NotNull LApiImpl lApi;

    //already received events
    private final AtomicBoolean lApiConstructorReady = new AtomicBoolean(false);
    private final AtomicBoolean voiceRegionManagerReady = new AtomicBoolean(false);
    private final AtomicBoolean gatewayReady = new AtomicBoolean(false);
    private final AtomicBoolean guildsReady = new AtomicBoolean(false);
    private final AtomicBoolean cacheReady = new AtomicBoolean(false);

    private final AtomicBoolean firedReadyEvent = new AtomicBoolean(false);

    public LApiReadyListener(@NotNull LApiImpl lApi) {
        this.lApi = lApi;

        lApi.getEventTransmitter().addListener(this);

        //set all events, that we won't receive, because they are not enabled, to true.
        if(!lApi.isCacheVoiceRegionsEnabled())
            voiceRegionManagerReady.set(true);

        if(!lApi.isGatewayEnabled())
            gatewayReady.set(true);

        if(!lApi.isCacheGuildsEnabled() || !lApi.isGatewayEnabled())
            guildsReady.set(true);

        if(!lApi.getConfig().isFlagSet(ConfigFlag.BASIC_CACHE))
            cacheReady.set(true);
    }

    public synchronized void lApiConstructorReady() {
        lApiConstructorReady.set(true);
        checkReadyEvent();
    }

    private synchronized void checkReadyEvent() {
        if(firedReadyEvent.get()) return;

        if(lApiConstructorReady.get()
                && voiceRegionManagerReady.get()
                && gatewayReady.get()
                && guildsReady.get()
                && cacheReady.get()) {
            lApi.transmitEvent().onLApiReady(lApi, new LApiReadyEvent(lApi));
            firedReadyEvent.set(true);
            lApi.getEventTransmitter().removeListener(this);
            this.notifyAll();
        }
    }

    public void waitUntilLApiReadyEvent() throws InterruptedException {
        lApi.checkQueueThread();
        synchronized (this) {
            if(firedReadyEvent.get()) return;
            this.wait();
        }
    }

    @Override
    public synchronized void onVoiceRegionManagerReady(@NotNull LApi lApi, @NotNull VoiceRegionManagerReadyEvent event) {
        voiceRegionManagerReady.set(true);
        checkReadyEvent();
    }

    @Override
    public synchronized void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {
        guildsReady.set(true);
        checkReadyEvent();
    }

    @Override
    public synchronized void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        cacheReady.set(true);
        checkReadyEvent();
    }

    @Override
    public synchronized void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        gatewayReady.set(true);
        checkReadyEvent();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
