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
import me.linusdev.lapi.api.objects.permission.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-types" target="_top">Message Types</a>
 * @updated 10.10.2022
 */
public enum MessageType implements SimpleDatable {
    DEFAULT                                         (0, true, "default"),
    RECIPIENT_ADD                                   (1, false, "recipient add"),
    RECIPIENT_REMOVE                                (2, false, "recipient remove"),
    CALL                                            (3, false, "call"),
    CHANNEL_NAME_CHANGE                             (4, false, "channel name change"),
    CHANNEL_ICON_CHANGE                             (5, false, "channel icon change"),
    CHANNEL_PINNED_MESSAGE                          (6, true, "channel pinned message"),
    USER_JOIN                                       (7, true, "user join"),
    GUILD_BOOST                                     (8, true, "guild boost"),
    GUILD_BOOST_TIER_1                              (9, true, "guild boost tier 1"),
    GUILD_BOOST_TIER_2                              (10, true, "guild boost tier 2"),
    GUILD_BOOST_TIER_3                              (11, true, "guild boost tier 3"),
    CHANNEL_FOLLOW_ADD                              (12, true, "channel follow add"),
    GUILD_DISCOVERY_DISQUALIFIED                    (14, false, "guild discovery disqualified"),
    GUILD_DISCOVERY_REQUALIFIED                     (15, false, "guild discovery requalified"),
    GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING    (16, false, "guild discovery grace period initial warning"),
    GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING      (17, false, "guild discovery grace period final warning"),
    THREAD_CREATED                                  (18, true, "thread created"),
    REPLY                                           (19, true, "reply"),
    CHAT_INPUT_COMMAND                              (20, true, "chat input command"),
    THREAD_STARTER_MESSAGE                          (21, false, "thread starter message"),
    GUILD_INVITE_REMINDER                           (22, true, "guild invite reminder"),
    CONTEXT_MENU_COMMAND                            (23, true, "context menu command"),

    /**
     *  Can only be deleted by members with {@link Permission#MANAGE_MESSAGES MANAGE_MESSAGES} permission.
     */
    AUTO_MODERATION_ACTION                          (24, true, "auto moderation action"),
    ;

    private final int value;
    private final boolean deletable;
    private final @NotNull String name;

    MessageType(int value, boolean deletable, @NotNull String name){
        this.value = value;
        this.deletable = deletable;
        this.name = name;
    }

    /**
     * the value of this {@link MessageType}
     */
    public int getValue() {
        return value;
    }

    /**
     * yes another useless {@link String}, that takes space on your drive.
     * @return readable name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     *
     * @return {@code true} if messages with this {@link MessageType} are deletable, {@code false} otherwise.
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * @param value {@link #getValue() value} of any {@link MessageType}.
     * @return {@link MessageType} with given value or {@link #DEFAULT} if no such {@link MessageType} exists.
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
