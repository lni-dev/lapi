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

package me.linusdev.lapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.permission.overwrite.PermissionOverwrites;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is basically the same as {@link GuildTextChannel}
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-guild-news-channel" target="_top">Exmaple GuildImpl News Channel</a>
 * Bots can post or publish messages in this type of channel if they have the proper permissions. These are called "Announcement Channels" in the client.
 * */
public class GuildNewsChannel extends GuildTextChannel{
    public GuildNewsChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull String name, @Nullable String topic, boolean nsfw, @NotNull Snowflake guildId, int position, @NotNull PermissionOverwrites permissionOverwrites, @Nullable Snowflake parentId, int rateLimitPerUser, @Nullable Snowflake lastMessageId, @Nullable String lastPinTimestamp) {
        super(lApi, id, type, name, topic, nsfw, guildId, position, permissionOverwrites, parentId, rateLimitPerUser, lastMessageId, lastPinTimestamp);
    }

    public GuildNewsChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);
    }
}
