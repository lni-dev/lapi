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

package me.linusdev.lapi.api.communication.http.ratelimit;

import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.async.queue.QueueableImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Used to check rate limited queues with a lot of entry, to make sure, that these queues do not build
 * up large amount of unimportant or possibly old {@link QueueableFuture futures} of {@link QueueableImpl tasks}.
 */
public interface RateLimitedQueueChecker {

    enum CheckType {
        /**
         * Removes all entries from the queue
         */
        REMOVE_ALL,

        /**
         * Iterates over all entries of the queue. It will call {@link #check(QueueableFuture)} for every entry.
         * If this method returns {@code true}, the entry will be removed. <br>
         * Then it will call {@link #checkAgain()} if this method returns {@code true}, it will iterate over each of the
         * remaining entries again. It won't call {@link #checkAgain()} after that.
         */
        ITERATE_ALL,
        ;
    }

    /**
     * Called when the {@link Bucket} bound to this checker wants to start a check
     * @param queueSize the current size of the queue
     * @return Whether to {@link CheckType#REMOVE_ALL remove all} entries or call {@link #check(QueueableFuture) check}
     * {@link CheckType#ITERATE_ALL for each entry}
     */
    @NotNull RateLimitedQueueChecker.CheckType startCheck(int queueSize);

    /**
     * This should check the given entry and return {@code true} if it should be removed from the queue.
     * @param toCheck {@link QueueableFuture} to check
     * @return {@code true} to remove the entry. {@code false} if it should not remove the entry.
     */
    boolean check(@NotNull QueueableFuture<?> toCheck);

    /**
     * This will be called after the <b>first and only the first</b> iteration over the queue.
     * @return {@code true} to iterate over all entries again
     */
    boolean checkAgain();

}
