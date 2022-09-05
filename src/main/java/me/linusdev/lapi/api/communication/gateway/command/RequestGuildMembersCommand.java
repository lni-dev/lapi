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

package me.linusdev.lapi.api.communication.gateway.command;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#request-guild-members" target="_top"></a>
 */
public class RequestGuildMembersCommand extends GatewayCommand implements HasLApi {

    public static final String GUILD_ID_KEY = "guild_id";
    public static final String QUERY_KEY = "query";
    public static final String LIMIT_KEY = "limit";
    public static final String PRESENCES_KEY = "presences";
    public static final String USER_IDS_KEY = "user_ids";
    public static final String NONCE_KYE = "nonce";

    private final @NotNull LApi lApi;

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data} with required fields
     */
    RequestGuildMembersCommand(@NotNull LApi lApi, @Nullable SOData data) {
        super(GatewayCommandType.REQUEST_GUILD_MEMBERS, data);
        this.lApi = lApi;
    }

    /**
     *
     * - Either query or userIds may be specified. <br>
     * - To request presences, {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is required.<br>
     * - To request all members, {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS} intent is required.<br>
     * - When requesting a query, limit must be 100 or less.<br>
     * - When requesting userIds, limit must be 100 or less.<br>
     *
     * @param lApi {@link LApi}
     * @param guildId id of the guild to get members for
     * @param query string that username starts with, or an empty string to return all members
     * @param limit maximum number of members to send matching the query; a limit of 0 can be used with an empty string query to return all members
     * @param presences used to specify if we want the presences of the matched members
     * @param userIds used to specify which users you wish to fetch
     * @param nonce nonce to identify the
     * {@link me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent Guild Members Chunk response}
     * @throws IllegalArgumentException if query and userIds is both {@code null}
     * @return new {@link RequestGuildMembersCommand}
     */
    public static @NotNull RequestGuildMembersCommand createCommand(@NotNull LApi lApi, @NotNull String guildId,
                                                                    @Nullable String query, int limit,
                                                                    boolean presences,
                                                                    @Nullable Collection<String> userIds,
                                                                    @Nullable String nonce) {

        if(query == null && userIds == null) throw new IllegalArgumentException("one of query or userIds must not be null!");

        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(GUILD_ID_KEY, guildId);
        data.add(QUERY_KEY, query);
        data.add(LIMIT_KEY, limit);
        data.add(PRESENCES_KEY, presences);
        data.add(USER_IDS_KEY, userIds);
        data.add(NONCE_KYE, nonce);

        return new RequestGuildMembersCommand(lApi, data);
    }

    /**
     *
     * - Either query or userIds may be specified. <br>
     * - To request presences, {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is required.<br>
     * - To request all members, {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS} intent is required.<br>
     * - When requesting a query, limit must be 100 or less.<br>
     * - When requesting userIds, limit must be 100 or less.<br>
     *
     * @param lApi {@link LApi}
     * @param guildId id of the guild to get members for
     * @param query string that username starts with, or an empty string to return all members
     * @param limit maximum number of members to send matching the query; a limit of 0 can be used with an empty string query to return all members
     * @param presences used to specify if we want the presences of the matched members
     * @param userIds used to specify which users you wish to fetch
     * @param nonce nonce to identify the
     * {@link me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent Guild Members Chunk response}
     * @throws IllegalArgumentException if query and userIds is both {@code null}
     * @return new {@link RequestGuildMembersCommand}
     */
    public static @NotNull RequestGuildMembersCommand createCommand(@NotNull LApi lApi, @NotNull String guildId,
                                                                    @Nullable String query, int limit,
                                                                    boolean presences,
                                                                    @Nullable String nonce,
                                                                    @Nullable Collection<Snowflake> userIds) {

        if(query == null && userIds == null) throw new IllegalArgumentException("one of query or userIds must not be null!");

        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(GUILD_ID_KEY, guildId);
        data.add(QUERY_KEY, query);
        data.add(LIMIT_KEY, limit);
        data.add(PRESENCES_KEY, presences);
        data.add(USER_IDS_KEY, userIds);
        data.add(NONCE_KYE, nonce);

        return new RequestGuildMembersCommand(lApi, data);
    }

    /**
     * This will retrieve all {@link me.linusdev.lapi.api.objects.guild.member.Member members} of the guild with given
     * guild id.
     *
     * @param lApi {@link LApi}
     * @param guildId id of the guild to get members for
     * @param presences used to specify if we want the presences of the matched members
     * @param nonce nonce to identify the
     * {@link me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent Guild Members Chunk response}
     * @return new {@link RequestGuildMembersCommand}
     * @see #createCommand(LApi, String, String, int, boolean, Collection, String)
     */
    public static @NotNull RequestGuildMembersCommand createRequestAllGuildMembersCommand(@NotNull LApi lApi, @NotNull String guildId,
                                                                    boolean presences, @Nullable String nonce) {
        return createCommand(lApi, guildId, "", 0, presences, null, nonce);
    }

    /**
     * Search for members with given prefix (query). Will retrieve up to 100 members.
     * @param lApi {@link LApi}
     * @param guildId id of the guild to get members for
     * @param query string that username starts with
     * @param presences used to specify if we want the presences of the matched members
     * @param nonce nonce to identify the
     * {@link me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent Guild Members Chunk response}
     * @return new {@link RequestGuildMembersCommand}
     * @see #createCommand(LApi, String, String, int, boolean, Collection, String)
     */
    public static @NotNull RequestGuildMembersCommand createQueryGuildMembersCommand(@NotNull LApi lApi, @NotNull String guildId,
                                                                                     @NotNull String query,
                                                                                     boolean presences, @Nullable String nonce) {
        return createCommand(lApi, guildId, query, 100, presences, null, nonce);
    }

    /**
     * Retrieves members with given user ids.
     * @param lApi {@link LApi}
     * @param guildId id of the guild to get members for
     * @param userIds used to specify which users you wish to fetch
     * @param presences used to specify if we want the presences of the matched members
     * @param nonce nonce to identify the
     * {@link me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent Guild Members Chunk response}
     * @return new {@link RequestGuildMembersCommand}
     * @see #createCommand(LApi, String, String, int, boolean, Collection, String)
     */
    public static @NotNull RequestGuildMembersCommand createRequestUserIdsMembersCommand(@NotNull LApi lApi, @NotNull String guildId,
                                                                                     @NotNull Collection<String> userIds,
                                                                                     boolean presences, @Nullable String nonce) {
        return createCommand(lApi, guildId, null, 100, presences, userIds, nonce);
    }

    /**
     * Retrieves members with given user ids.
     * @param lApi {@link LApi}
     * @param guildId id of the guild to get members for
     * @param userIds used to specify which users you wish to fetch
     * @param presences used to specify if we want the presences of the matched members
     * @param nonce nonce to identify the
     * {@link me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent Guild Members Chunk response}
     * @return new {@link RequestGuildMembersCommand}
     * @see #createCommand(LApi, String, String, int, boolean, Collection, String)
     */
    public static @NotNull RequestGuildMembersCommand createRequestUserIdsMembersCommand(@NotNull LApi lApi, @NotNull String guildId,
                                                                                         boolean presences, @Nullable String nonce,
                                                                                         @NotNull Collection<Snowflake> userIds) {
        return createCommand(lApi, guildId, null, 100, presences, nonce, userIds);
    }

    /**
     * @throws UnsupportedOperationException if {@link me.linusdev.lapi.api.config.ConfigFlag#ENABLE_GATEWAY ENABLE_GATEWAY} is not enabled
     */
    public void send() {
        if(lApi.getGateway() == null) {
            throw new UnsupportedOperationException("Gateway must be enabled to queue a command on it");
        }
        lApi.getGateway().queueCommand(this);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
