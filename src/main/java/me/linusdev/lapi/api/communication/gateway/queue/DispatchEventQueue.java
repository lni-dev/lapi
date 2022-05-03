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

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class DispatchEventQueue {

    /**
     * Last received sequence
     */
    private long lastSequence;

    private final ReceivedPayload[] array;
    private int pushPosition;
    private int pullPosition;
    private int size;

    private @Nullable DispatchEventProcessor processor;

    /**
     * @param capacity the capacity of the queue
     */
    public DispatchEventQueue(int capacity) {
        this.lastSequence = 0L;
        this.array = new ReceivedPayload[capacity];
        this.pushPosition = 0;
        this.pullPosition = 0;
        this.size = 0;
    }

    /**
     * Will add given payload to the correct position in the queue
     * based on {@link GatewayPayloadAbstract#getSequence() the sequence of given payload}. If this sequence is less than
     * the {@link #getLastSequence() last received sequence} it will be ignored.<br>
     * Only if the given sequence is exactly one more than the last sequence, the last sequence will be changed afterwards.
     * @param payload the payload to push
     * @throws IllegalArgumentException if {@link GatewayPayloadAbstract#getSequence()} is {@code null}
     * @throws IllegalStateException if this queue is full or has overflown
     */
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

    /**
     * Given payload will be placed at the correct position in the queue (depending on given dif).
     * If dif is exactly one, the {@link #lastSequence} will be updated accordingly
     * (if there has not been retrieved any sequence, higher than {@link #lastSequence} + 1, {@link #lastSequence} will
     * be increased by exactly one)
     *
     * @param payload the payload to add to this queue
     * @param dif the offset from the {@link #lastSequence}
     * @throws IllegalStateException If after this operation was done, the queue is full or has overflown
     */
    private void set(@NotNull ReceivedPayload payload, @Range(from = 1, to = Integer.MAX_VALUE) int dif) {

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

    public void setProcessor(@Nullable DispatchEventProcessor processor) {
        this.processor = processor;
    }

    public synchronized long getLastSequence() {
        return lastSequence;
    }
}
