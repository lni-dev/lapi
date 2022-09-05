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

package me.linusdev.lapi.api.communication.gateway.other;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#session-start-limit-object" target="_top">Session Start Limit Object</a>
 */
public class SessionStartLimit implements Datable {

    public static final String TOTAL_KEY = "total";
    public static final String REMAINING_KEY = "remaining";
    public static final String RESET_AFTER_KEY = "reset_after";
    public static final String MAX_CONCURRENCY_KEY = "max_concurrency";

    private final int total;
    private final int remaining;
    private final int resetAfter;
    private final int maxConcurrency;

    /**
     *
     * @param total The total number of session starts the current user is allowed
     * @param remaining The remaining number of session starts the current user is allowed
     * @param resetAfter The number of milliseconds after which the limit resets
     * @param maxConcurrency The number of identify requests allowed per 5 seconds
     */
    public SessionStartLimit(int total, int remaining, int resetAfter, int maxConcurrency) {
        this.total = total;
        this.remaining = remaining;
        this.resetAfter = resetAfter;
        this.maxConcurrency = maxConcurrency;
    }

    /**
     *
     * @param data {@link SOData}
     * @return {@link SessionStartLimit}
     * @throws InvalidDataException if {@link #TOTAL_KEY}, {@link #REMAINING_KEY}, {@link #RESET_AFTER_KEY} or {@link #MAX_CONCURRENCY_KEY} are missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable SessionStartLimit fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Number total = (Number) data.get(TOTAL_KEY);
        Number remaining = (Number) data.get(REMAINING_KEY);
        Number resetAfter = (Number) data.get(RESET_AFTER_KEY);
        Number maxConcurrency = (Number) data.get(MAX_CONCURRENCY_KEY);

        if(total == null || remaining == null || resetAfter == null || maxConcurrency == null){
            InvalidDataException.throwException(data, null, SessionStartLimit.class,
                    new Object[]{total, remaining, resetAfter, maxConcurrency},
                    new String[]{TOTAL_KEY, REMAINING_KEY, RESET_AFTER_KEY, MAX_CONCURRENCY_KEY});
        }

        //noinspection ConstantConditions
        return new SessionStartLimit(total.intValue(), remaining.intValue(), resetAfter.intValue(), maxConcurrency.intValue());
    }

    /**
     * The total number of session starts the current user is allowed
     */
    public int getTotal() {
        return total;
    }

    /**
     * The remaining number of session starts the current user is allowed
     */
    public int getRemaining() {
        return remaining;
    }

    /**
     * The number of milliseconds after which the limit resets
     */
    public int getResetAfter() {
        return resetAfter;
    }

    /**
     * The number of identify requests allowed per 5 seconds
     */
    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(TOTAL_KEY, total);
        data.add(REMAINING_KEY, remaining);
        data.add(RESET_AFTER_KEY, resetAfter);
        data.add(MAX_CONCURRENCY_KEY, maxConcurrency);

        return data;
    }
}
