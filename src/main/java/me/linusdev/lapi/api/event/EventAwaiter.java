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

import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventAwaiter {

    private final AtomicBoolean bool;

    public EventAwaiter(){
        this.bool = new AtomicBoolean(false);
    }

    /**
     * Waits for the first occurrence of this event. If the event has already occurred before the message call,
     * this method will return immediately without blocking the current thread.
     * @throws InterruptedException if interrupted.
     */
    public synchronized void awaitFirst() throws InterruptedException {
        if(bool.get()) return;
        this.wait();
    }

    /**
     * Waits for the first occurrence of this event. If the event has already occurred before the message call,
     * this method will return immediately without blocking the current thread.
     * @param timeout maximal time to wait in milliseconds
     * @throws InterruptedException if interrupted.
     * @throws TimeoutException if the event did not happen within the timout time.
     */
    public synchronized void awaitFirst(long timeout) throws InterruptedException, TimeoutException {
        if(bool.get()) return;
        this.wait(timeout);
        if(!bool.get()) throw new TimeoutException("Waited " + timeout + " ms, but the event did not happen.");
    }

    /**
     * Waits until the next occurrence of this event.
     * @throws InterruptedException if interrupted.
     */
    public synchronized void await() throws InterruptedException {
        this.wait();
    }

    /**
     *
     * @return {@code true} if this event has already triggered before this method call. {@code false} otherwise.
     */
    public synchronized boolean hasTriggered() {
        return bool.get();
    }

    /**
     * After this method call {@link #hasTriggered()} will return {@code true} and all threads waiting on this event
     * will be notified.
     */
    @ApiStatus.Internal
    public synchronized void trigger() {
        bool.set(true);
        this.notifyAll();
    }

}
