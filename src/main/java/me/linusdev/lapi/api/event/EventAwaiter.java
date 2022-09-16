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

package me.linusdev.lapi.api.event;

import java.util.concurrent.atomic.AtomicBoolean;

public class EventAwaiter {

    private final AtomicBoolean bool;

    EventAwaiter(){
        this.bool = new AtomicBoolean(false);
    }

    public synchronized void awaitFirst() throws InterruptedException {
        if(bool.get()) return;
        this.wait();
    }

    public synchronized void await() throws InterruptedException {
        this.wait();
    }

    public synchronized boolean hasTriggered() {
        return bool.get();
    }

    synchronized void trigger() {
        bool.set(true);
        this.notifyAll();
    }

}
