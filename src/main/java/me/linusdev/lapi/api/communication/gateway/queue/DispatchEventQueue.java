/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.queue;

import org.jetbrains.annotations.NotNull;

public class DispatchEventQueue {

    /**
     * Last received sequence
     */
    private long lastSequence;

    private final ReceivedPayload[] array;
    private int pushPosition;
    private int pullPosition;
    private int size;



    public DispatchEventQueue(int capacity) {
        this.lastSequence = 0L;
        this.array = new ReceivedPayload[capacity];
        this.pushPosition = 0;
        this.pullPosition = 0;
        this.size = 0;
    }

    public synchronized void push(@NotNull ReceivedPayload payload) {
        Long sequence = payload.getPayload().getSequence();
        if(sequence == null) {
            //Dispatch events always have a sequence.
            throw new IllegalArgumentException("Sequence may not be null.");

        } else if (sequence <= lastSequence) {
            //we already have that sequence...
            return;

        }

        long difference = sequence - lastSequence;
        set(payload, (int) difference);
    }

    private void set(@NotNull ReceivedPayload payload, int dif) {

        if(dif == 1) {
            array[pushPosition] = payload;
            pushPosition = (pushPosition + 1) % array.length;
            this.size++;

            while (get(pushPosition) != null && this.size < array.length) {
                this.pushPosition++;
                this.size++;
            }

            //noinspection ConstantConditions: This will never be null, because a payload without a sequence cannot be added.
            this.lastSequence = array[pushPosition].getPayload().getSequence();

        } else {
            int index = (pushPosition + (dif - 1)) % array.length;
            array[index] = payload;
            this.size = Math.max(this.size, dif + (pushPosition - pullPosition));

        }

        if(this.size >= array.length) {
            //This is awful.
            throw new IllegalStateException("The queue is completely filled or has already overflown!");
        }
    }

    private ReceivedPayload get(int index) {
        index = index % array.length;
        return array[index];
    }

    public synchronized ReceivedPayload pull() {
        if(array[pullPosition] != null){
            ReceivedPayload payload = array[pullPosition];
            pullPosition = (pullPosition + 1) % array.length;
            return payload;
        }

        return null;
    }

    public synchronized long getLastSequence() {
        return lastSequence;
    }
}
