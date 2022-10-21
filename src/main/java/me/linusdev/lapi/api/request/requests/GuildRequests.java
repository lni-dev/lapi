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

package me.linusdev.lapi.api.request.requests;

import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.linusdev.lapi.api.other.placeholder.Name.*;

public interface GuildRequests extends HasLApi {

    /**
     * Returns a list of guild {@link Channel channel} objects. Does not include threads.
     * @param guildId id of the guild
     * @return {@link Queueable} which can retrieve the list of {@link Channel channels}
     */
    default @NotNull Queueable<ArrayList<Channel>> getGuildChannels(@NotNull String guildId) {
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GUILD_CHANNELS,
                GUILD_ID.withValue(guildId));

        return new ArrayRetriever<>(query, Channel::channelFromData);
    }

}
