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

package me.linusdev.lapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-default-message-notification-level" target="_top">
 * Default Message Notification Level
 * </a>
 */
public enum DefaultMessageNotificationLevel implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1),

    /**
     * members will receive notifications for all messages by default
     */
    ALL_MESSAGES(0),

    /**
     * members will receive notifications only for messages that @mention them by default
     */
    ONLY_MENTIONS(1),

    ;

    private final int value;

    DefaultMessageNotificationLevel(int value) {
        this.value = value;
    }

    /**
     * @param value int
     * @return {@link DefaultMessageNotificationLevel} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull DefaultMessageNotificationLevel ofValue(int value) {
        for (DefaultMessageNotificationLevel level : DefaultMessageNotificationLevel.values()) {
            if (level.value == value)
                return level;
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
