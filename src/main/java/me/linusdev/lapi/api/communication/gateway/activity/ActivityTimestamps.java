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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-timestamps" target="_top">Activity Timestamps</a>
 */
public class ActivityTimestamps implements Datable {

    public static final String START_KEY = "start";
    public static final String END_KEY = "end";

    private final @Nullable Long start;
    private final @Nullable Long end;

    /**
     *
     * @param start unix time (in milliseconds) of when the activity started
     * @param end unix time (in milliseconds) of when the activity ends
     */
    public ActivityTimestamps(@Nullable Long start, @Nullable Long end) {
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ActivityTimestamps}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ActivityTimestamps fromData(@Nullable Data data){
        if(data == null) return null;

        Number start = (Number) data.get(START_KEY);
        Number end = (Number) data.get(END_KEY);

        return new ActivityTimestamps(start == null ? null : start.longValue(),
                end == null ? null : end.longValue());
    }


    /**
     * unix time (in milliseconds) of when the activity started
     */
    public @Nullable Long getStart() {
        return start;
    }

    /**
     * unix time (in milliseconds) of when the activity ends
     */
    public @Nullable Long getEnd() {
        return end;
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.addIfNotNull(START_KEY, start);
        data.addIfNotNull(END_KEY, end);

        return data;
    }
}
