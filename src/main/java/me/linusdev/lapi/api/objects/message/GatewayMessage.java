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

package me.linusdev.lapi.api.objects.message;

import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.UserMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway-events#messages">Discord Documentation</a>
 */
public interface GatewayMessage extends AnyMessage {

    /**
     * ID of the guild the message was sent in - unless it is an ephemeral message.
     * @return guild id as {@link Snowflake} or {@code null}.
     */
    @Nullable Snowflake getGuildIdAsSnowflake();

    /**
     * ID of the guild the message was sent in - unless it is an ephemeral message.
     * @return guild id as {@link String} or {@code null}.
     */
    default @Nullable String getGuildId() {
        if(getGuildIdAsSnowflake() == null) return null;
        return getGuildIdAsSnowflake().asString();
    }

    /**
     * Member properties for this message's author. Missing for ephemeral messages and messages from webhooks.
     * @return {@link Member} or {@code null}.
     */
    @Nullable Member getMember();

    /**
     * Users specifically mentioned in the message.
     * @return {@link UserMember} array: array of user objects, with an additional partial member field.
     */
    @NotNull List<UserMember> getMentionsWithMember();

}
