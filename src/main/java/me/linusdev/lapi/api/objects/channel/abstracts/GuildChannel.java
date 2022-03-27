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

package me.linusdev.lapi.api.objects.channel.abstracts;

import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuildChannel {

    /**
     * sorting position of the channel
     * todo give more information
     */
    int getPosition();

    /**
     * explicit permission overwrites for members and roles
     */
    @NotNull
    PermissionOverwrites getPermissionOverwrites();

    /**
     * the name of the channel (1-100 characters)
     */
    @NotNull
    String getName();

    /**
     * whether the channel is nsfw
     */
    boolean getNsfw();

    /**
     * for guild channels: id of the parent category for a channel
     * (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
     */
    @Nullable
    Snowflake getParentIdAsSnowflake();

    /**
     * for guild channels: id of the parent category for a channel
     * (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
     */
    @Nullable default String getParentId(){
        if(getParentIdAsSnowflake() == null) return null;
        return getParentIdAsSnowflake().asString();
    }

    /**
     * the guild id
     */
    @NotNull default String getGuildId(){
        return getGuildIdAsSnowflake().asString();
    }

    /**
     * the guild id snowflake or {@code null} if this is not a guild channel
     */
    @NotNull Snowflake getGuildIdAsSnowflake();
}
