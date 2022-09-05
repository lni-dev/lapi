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

package me.linusdev.lapi.api.communication.gateway.queue;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.queue.processor.DispatchEventProcessor;
import org.jetbrains.annotations.*;

import java.util.Arrays;
import java.util.List;

/**
 * Queue that handles all Dispatch Events.<br>
 * It also manages the last received sequence, since only Dispatch Events have a sequence.
 */
public class DispatchEventQueue implements Datable {

    public static final String LAST_RECEIVED_SEQUENCE_KEY = "last_received_sequence";
    public static final String RECEIVED_FIRST_SEQUENCE_KEY = "received_first_sequence";
    public static final String ARRAY_KEY = "array";
    public static final String PUSH_POSITION_KEY = "push_position";
    public static final String PULL_POSITION_KEY = "pull_position";
    public static final String SIZE_KEY = "size";

    /**
     * Last received sequence
     */
    private long lastSequence;
    private boolean receivedFirstSequence;

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
        this.receivedFirstSequence = false;
        this.array = new ReceivedPayload[capacity];
        this.pushPosition = 0;
        this.pullPosition = 0;
        this.size = 0;
    }

    private DispatchEventQueue(long lastSequence, boolean receivedFirstSequence, ReceivedPayload[] array, int pushPosition, int pullPosition, int size) {
        this.lastSequence = lastSequence;
        this.receivedFirstSequence = receivedFirstSequence;
        this.array = array;
        this.pushPosition = pushPosition;
        this.pullPosition = pullPosition;
        this.size = size;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable DispatchEventQueue fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Number lastSequence = (Number) data.get(LAST_RECEIVED_SEQUENCE_KEY);
        Boolean receivedFirstSeq = (Boolean) data.get(RECEIVED_FIRST_SEQUENCE_KEY);
        List<Object> list = data.getList(ARRAY_KEY);
        Number pushPosition = (Number) data.get(PUSH_POSITION_KEY);
        Number pullPosition = (Number) data.get(PULL_POSITION_KEY);
        Number size = (Number) data.get(SIZE_KEY);

        if(lastSequence == null || receivedFirstSeq == null || list == null || pushPosition == null || pullPosition == null || size == null){
            InvalidDataException.throwException(data, null, DispatchEventQueue.class,
                    new Object[]{lastSequence, receivedFirstSeq, list, pushPosition, pullPosition, size},
                    new String[]{LAST_RECEIVED_SEQUENCE_KEY, RECEIVED_FIRST_SEQUENCE_KEY, ARRAY_KEY, PUSH_POSITION_KEY, PULL_POSITION_KEY, SIZE_KEY});
            return null; //Will never be executed
        }

        ReceivedPayload[] array = new ReceivedPayload[list.size()];

        int i = 0;
        for(Object o : list) {
            if(o instanceof ReceivedPayload) array[i++] = (ReceivedPayload) o;
            else array[i++] = ReceivedPayload.fromData((SOData) o);
        }

        return new DispatchEventQueue(lastSequence.longValue(), receivedFirstSeq, array,
                pushPosition.intValue(), pullPosition.intValue(),
                size.intValue());
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
        }

        if(!receivedFirstSequence) {
            receivedFirstSequence = true;
            lastSequence = sequence-1;

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
    private synchronized void set(@NotNull ReceivedPayload payload, @Range(from = 1, to = Integer.MAX_VALUE) int dif) {

        if(dif == 1) {
            array[pushPosition] = payload;
            //noinspection ConstantConditions: This will never be null, because a payload without a sequence cannot be added.
            this.lastSequence = array[pushPosition].getPayload().getSequence();
            pushPosition = (pushPosition + 1) % array.length;
            calculateSize(0);

            if(processor != null) processor.onNext();

            while (get(pushPosition) != null) {
                //noinspection ConstantConditions: This will never be null, because a payload without a sequence cannot be added.
                this.lastSequence = array[pushPosition].getPayload().getSequence();
                pushPosition = (pushPosition + 1) % array.length;



                if(processor != null) processor.onNext();
            }


        } else {
            int index = (pushPosition + (dif - 1)) % array.length;
            array[index] = payload;
            calculateSize(dif);

        }

        if(this.size >= array.length) {
            //This is awful.
            throw new IllegalStateException("The queue is completely filled or has already overflown!");
        }
    }

    /**
     * Get {@link ReceivedPayload} at given index in {@link #array} (not in the queue!)
     * @param index index. An index higher than array length is valid.
     *              Array-index will be calculated by given index modulo array length
     * @return {@link ReceivedPayload} at given index mod {@link java.lang.reflect.Array#getLength(Object) array length}
     */
    @ApiStatus.Internal
    private synchronized @Nullable ReceivedPayload get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        index = index % array.length;
        return array[index];
    }

    /**
     * Set given {@link ReceivedPayload} at given index in {@link #array} (not in the queue!)
     * @param index index. An index higher than array length is valid.
     *              Array-index will be calculated by given index modulo array length
     * @param payload the {@link ReceivedPayload} or {@code null} to set at given index
     */
    private synchronized void set(@Range(from = 0, to = Integer.MAX_VALUE) int index, @Nullable ReceivedPayload payload) {
        array[index % array.length] = payload;
    }

    /**
     * Pulls {@link ReceivedPayload} at {@link #pullPosition} and increases {@link #pullPosition} by 1 (if returned value is not {@code null}).
     * @return {@link ReceivedPayload} at the first position in the queue or {@code null} if there is no such.
     */
    public synchronized @Nullable ReceivedPayload pull() {
        if(array[pullPosition] != null){
            ReceivedPayload payload = array[pullPosition];
            array[pullPosition] = null;
            pullPosition = (pullPosition + 1) % array.length;
            size--;
            return payload;
        }

        return null;
    }

    /**
     * {@link #pullPosition} is not increased.
     * @return {@link ReceivedPayload} at the first position in the queue or {@code null} if there is no such.
     */
    public synchronized @Nullable ReceivedPayload peek() {
        return array[pullPosition];
    }

    /**
     * resets this queue:
     * <ul>
     *     <li>
     *         {@link #lastSequence} will be 0.
     *     </li>
     *     <li>
     *         The queue's {@link #size} will be minimized. That means all missing sequences will be ignored.
     *     </li>
     *     <li>
     *         {@link #pushPosition} will be updated accordingly.
     *     </li>
     * </ul>
     */
    public synchronized void reset() {
        lastSequence = 0;

        int tempSize = size - (pushPosition - pullPosition);
        for(;tempSize > 0; tempSize--) {
            if(array[pushPosition] != null) continue;

            int i = 1;
            while(get(pushPosition+i) == null && i < tempSize) i++;

            if(get(pushPosition+i) == null) break;

            array[pushPosition] = get(pushPosition+i);
            set(pushPosition+i, null);
            pushPosition = (pushPosition + 1) % array.length;
        }

        calculateSize(0);
        receivedFirstSequence = false;
    }

    /**
     * Calculates the size of the queue after a new element has been added.
     * @param dif the difference of the position of the added element and the current {@link #pushPosition} (always positive or 0)
     * @return {@link #size}
     */
    @SuppressWarnings("UnusedReturnValue")
    private int calculateSize(int dif) {
        int tempPushPosition = pushPosition;

        if(pushPosition < pullPosition)
            tempPushPosition += array.length;

        size = Math.max(size, dif + (tempPushPosition - pullPosition));
        return size;
    }

    public void setProcessor(@Nullable DispatchEventProcessor processor) {
        this.processor = processor;
    }

    /**
     * @return next required sequence - 1
     */
    public synchronized long getLastSequence() {
        return lastSequence;
    }

    /**
     *
     * @return the queues max capacity
     */
    public int getCapacity() {
        return array.length;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(5);

        data.add(LAST_RECEIVED_SEQUENCE_KEY, lastSequence);
        data.add(RECEIVED_FIRST_SEQUENCE_KEY, receivedFirstSequence);
        data.add(ARRAY_KEY, Arrays.asList(array));
        data.add(PUSH_POSITION_KEY, pushPosition);
        data.add(PULL_POSITION_KEY, pullPosition);
        data.add(SIZE_KEY, size);

        return data;
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s
                .append("lastSequence: ").append(lastSequence)
                .append("\npushPosition: ").append(pushPosition)
                .append("\npullPosition: ").append(pullPosition)
                .append("\nsize: ").append(size)
                .append("\npush - pull: ").append(pushPosition - pullPosition);

        int i = 0;
        for(ReceivedPayload payload : array) {
            s.append("\n");
            s.append(String.format("[%3d]: ", i));
            if(payload == null) s.append("null");
            if(payload != null) s.append(payload.getPayload().getSequence());

            if(i == pushPosition) s.append(" <- push, lastSequence: ").append(lastSequence);
            if(i == pullPosition) s.append(" <- pull");


            i++;
        }

        return s + "\n\n";
    }
}
