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

package me.linusdev.lapi.api.objects.channel.abstracts;

import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.Nullable;

public interface TextChannel {

    /**
     *the id of the last message snowflake id sent in this channel (may not point to an existing or valid message)
     * todo may be null if no msg was sent at all?
     */
    @Nullable
    Snowflake getLastMessageIdAsSnowflake();

    /**
     *the id of the last message id sent in this channel (may not point to an existing or valid message)
     * todo may be null if no msg was sent at all?
     */
    @Nullable
    default String getLastMessageId(){
        Snowflake snowflake = getLastMessageIdAsSnowflake();
        if(snowflake == null) return null;
        return snowflake.asString();
    }

    /**
     * when the last pinned message was pinned. This may be {@code null} in events such as GUILD_CREATE when a message is not pinned.
     * todo link event and check output
     */
    @Nullable
    ISO8601Timestamp getLastPinTimestamp();
}
