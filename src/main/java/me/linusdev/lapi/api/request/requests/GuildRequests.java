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
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.guild.enums.GuildFeature;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.templates.channel.ChannelTemplate;
import me.linusdev.lapi.api.templates.guild.ModifyGuildTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.linusdev.lapi.api.other.placeholder.Name.*;

public interface GuildRequests extends HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                               Create / Get Guild                                          *
     *                                                                                                           *
     *  Done:       00.00.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                               Get Guild Preview                                           *
     *                                                                                                           *
     *  Done:       00.00.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                             Modify / Delete Guild                                         *
     *                                                                                                           *
     *  Done:       26.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Modify a guild's settings. Requires the {@link Permission#MANAGE_GUILD MANAGE_GUILD} permission.
     *     Returns the updated guild object on success. Fires a {@link GatewayEvent#GUILD_UPDATE Guild Update} Gateway event.
     * </p>
     * <p>
     *     Only some {@link GuildFeature guild features} are mutable and these may require special permissions.
     *     See the {@link GuildFeature} you want to add/remove for more information.
     * </p>
     * @param guildId id of the guild to modify
     * @param template {@link ModifyGuildTemplate}
     * @return {@link Queueable} than can modify and retrieve the guild.
     */
    default @NotNull Queueable<Guild> modifyGuild(@NotNull String guildId, @NotNull ModifyGuildTemplate template) {
        LinkQuery query = new LinkQuery(getLApi(), Link.MODIFY_GUILD, template.getBody(),
                GUILD_ID.withValue(guildId));
        return new ConvertingRetriever<>(query, Guild::fromData);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                               Get Guild Channels                                          *
     *                                                                                                           *
     *  Done:       24.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                             Create Guild Channel                                          *
     *                                                                                                           *
     *  Done:       25.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Requires the {@link Permission#MANAGE_CHANNELS MANAGE_CHANNELS} permission.
     * </p>
     * <p>
     *     If setting permission overwrites, only permissions your bot has in the guild can be allowed/denied.
     * </p>
     * <p>
     *     Setting {@link Permission#MANAGE_ROLES MANAGE_ROLES} permission in channels is only possible for guild administrators.
     * </p>
     * <p>
     *     Returns the new {@link Channel channel object} on success. Fires a {@link GatewayEvent#CHANNEL_CREATE Channel Create} Gateway event.
     * </p>
     * @param guildId id of the guild to create the channel in
     * @param template {@link ChannelTemplate} to use for the channel creation
     * @return {@link Queueable} that can create and retrieve the {@link Channel}.
     */
    default @NotNull Queueable<Channel> createGuildChannel(@NotNull String guildId, @NotNull ChannelTemplate template) {
        LinkQuery query = new LinkQuery(getLApi(), Link.CREATE_GUILD_CHANNEL, template.getBody(),
                GUILD_ID.withValue(guildId));
        return new ConvertingRetriever<>(query, Channel::channelFromData);
    }

    //TODO: other requests
}
