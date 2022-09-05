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

package me.linusdev.lapi.api.objects.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-types" target="_top">Message Types</a>
 */
public enum MessageType implements SimpleDatable {
    DEFAULT                                         (0, "default"),
    RECIPIENT_ADD                                   (1, "recipient add"),
    RECIPIENT_REMOVE                                (2, "recipient remove"),
    CALL                                            (3, "call"),
    CHANNEL_NAME_CHANGE                             (4, "channel name change"),
    CHANNEL_ICON_CHANGE                             (5, "channel icon change"),
    CHANNEL_PINNED_MESSAGE                          (6, "channel pinned message"),
    GUILD_MEMBER_JOIN                               (7, "guild member join"),
    USER_PREMIUM_GUILD_SUBSCRIPTION                 (8, "user premium guild subscription"),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_1          (9, "user premium guild subscription tier 1"),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_2          (10, "user premium guild subscription tier 2"),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_3          (11, "user premium guild subscription tier 3"),
    CHANNEL_FOLLOW_ADD                              (12, "channel follow add"),
    GUILD_DISCOVERY_DISQUALIFIED                    (14, "guild discovery disqualified"),
    GUILD_DISCOVERY_REQUALIFIED                     (15, "guild discovery requalified"),
    GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING    (16, "guild discovery grace period initial warning"),
    GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING      (17, "guild discovery grace period final warning"),
    THREAD_CREATED                                  (18, "thread created"),
    REPLY                                           (19, "reply"),
    CHAT_INPUT_COMMAND                              (20, "chat input command"),
    THREAD_STARTER_MESSAGE                          (21, "thread starter message"),
    GUILD_INVITE_REMINDER                           (22, "guild invite reminder"),
    CONTEXT_MENU_COMMAND                            (23, "context menu command"),
    ;

    private final int value;
    private final @NotNull String name;

    MessageType(int value, String name){
        this.value = value;
        this.name = name;
    }

    /**
     * the value of this {@link MessageType}
     */
    public int getValue() {
        return value;
    }

    /**
     * yes another useless {@link String}, that takes space on your drive
     * @return
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @param value
     * @return {@link MessageType} with given value or {@link #DEFAULT} if no such {@link MessageType} exists
     */
    public static @NotNull MessageType fromValue(int value){
        for(MessageType type : MessageType.values()){
            if(type.getValue() == value)
                return type;
        }
        return DEFAULT;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
