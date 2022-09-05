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

package me.linusdev.lapi.api.objects.guild.scheduledevent;

import me.linusdev.data.SimpleDatable;

/**
 * <p>
 *     Once status is set to {@link #COMPLETED} or {@link #CANCELED}, the status can no longer be updated.
 * </p>
 * <p>
 * <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-valid-guild-scheduled-event-status-transitions" target="_top">Valid GuildImpl Scheduled Event Status Transitions</a>:<br>
 *
 * SCHEDULED --> ACTIVE<br>
 *
 * ACTIVE --------> COMPLETED<br>
 *
 * SCHEDULED --> CANCELED<br>
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-status" target="_top">GuildImpl Scheduled Event Status</a>
 */
public enum Status implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    SCHEDULED(1),
    ACTIVE(2),
    COMPLETED(3),
    CANCELED(4),
    ;

    private final int value;

    Status(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link Status} matching given value or {@link #UNKNOWN} if none matches
     */
    public static Status fromValue(int value){
        for(Status status : Status.values()){
            if(status.value == value) return status;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
