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

package me.linusdev.lapi.api.objects.nchannel.thread;

import me.linusdev.data.SimpleDatable;

/**
 *
 * Auto archive duration options:
 * <ul>
 *     <li>{@link #HOUR_1 1 hour}</li>
 *     <li>{@link #HOURS_24 24 hours}</li>
 *     <li>{@link #DAYS_3 3 days}</li>
 *     <li>{@link #WEEK_1 1 week}</li>
 * </ul>
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-structure">
 *     Discord Documentation (in description of field default_auto_archive_duration)</a>
 */
public enum AutoArchiveDuration implements SimpleDatable {

    HOUR_1(60),

    HOURS_24(1440),

    DAYS_3(4320),

    WEEK_1(10080),

    ;

    private final int minutes;

    AutoArchiveDuration(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public Object simplify() {
        return minutes;
    }

}
