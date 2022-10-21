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

package me.linusdev.lapi.api.objects.channel.forum;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-sort-order-types">Discord Documentation</a>
 */
public enum SortOrderType implements SimpleDatable {

    UNKNOWN(-1),

    /**
     * Sort forum posts by activity
     */
    LATEST_ACTIVITY(0),

    /**
     * Sort forum posts by creation time (from most recent to oldest)
     */
    CREATION_DATE(1),

    ;

    private final int value;

    SortOrderType(int value) {
        this.value = value;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable SortOrderType fromValue(@Nullable Number number) {
        if(number == null) return null;
        int val = number.intValue();
        if(val == LATEST_ACTIVITY.value) return LATEST_ACTIVITY;
        else if(val == CREATION_DATE.value) return CREATION_DATE;
        else return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
