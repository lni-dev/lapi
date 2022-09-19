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

package me.linusdev.lapi.api.lapiandqueue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * This is a special thread for the {@link LApi#queue}.
 * Only one instance of this Thread should be alive at the same time.
 */
public class QueueThread extends Thread{

    private final @NotNull AtomicBoolean isWaiting;
    private final @NotNull Object waitingLock = new Object();

    public QueueThread(@Nullable ThreadGroup group, @NotNull Runnable target, @NotNull String name) {
        super(group, target, name);

        isWaiting = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        super.run();
    }

    /**
     *
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

    public boolean notifyIfWaiting() {
        synchronized (waitingLock) {
            if(isWaiting.get()) {
                waitingLock.notifyAll();
                return true;
            }
            return false;
        }
    }

    public void notifyAllAwaiting() {
        synchronized (waitingLock) {
            waitingLock.notifyAll();
        }
    }
}
