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

package me.linusdev.lapi.api.lapi;

import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class LApiReadyListener implements EventListener, HasLApi {
    private final @NotNull LApiImpl lApi;

    private final AtomicBoolean constructorReady = new AtomicBoolean(false);

    public LApiReadyListener(@NotNull LApiImpl lApi) {
        this.lApi = lApi;

        lApi.runSupervised(() -> {

            synchronized (constructorReady) {
                try {
                    if(!constructorReady.get())
                        constructorReady.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            lApi.getReadyEventAwaiter().forEachAwaiter((identifier, eventAwaiter) -> {
                if(identifier.isRequiredForLApiReady()) {
                    try {
                        eventAwaiter.awaitFirst();
                    } catch (InterruptedException e) {throw new RuntimeException(e);}
                }
            });



            lApi.transmitEvent().onLApiReady(lApi, new LApiReadyEvent(lApi));
        });

    }

    public synchronized void lApiConstructorReady() {
        synchronized (constructorReady){
            constructorReady.set(true);
            constructorReady.notifyAll();
        }
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
