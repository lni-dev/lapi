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

package me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.message.nonce.Nonce;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommandType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#guild-members-chunk-guild-members-chunk-event-fields" target="_top">Guild Members Chunk Event Fields</a>
 */
public class GuildMemberChunkEventData implements Datable, HasLApi {

    public static final String GUILD_ID_KEY = "guild_id";
    public static final String MEMBERS_KEY = "members";
    public static final String CHUNK_INDEX_KEY = "chunk_index";
    public static final String CHUNK_COUNT_KEY = "chunk_count";
    public static final String NOT_FOUND_KEY = "not_found";
    public static final String PRESENCES_KEY = "presences";
    public static final String NONCE_KEY = "nonce";

    private final @NotNull LApi lApi;
    private final @NotNull Snowflake guildId;
    private final @NotNull ArrayList<Member> members;
    private final int chunkIndex;
    private final int chunkCount;
    private final @Nullable List<Object> notFound;
    private final @Nullable ArrayList<PresenceUpdate> presences;
    private final @Nullable Nonce nonce;

    /**
     * @param lApi       {@link LApi}
     * @param guildId    the id of the guild
     * @param members    set of guild members
     * @param chunkIndex the chunk index in the expected chunks for this response (0 &lt;= chunk_index &lt; chunk_count)
     * @param chunkCount the total number of expected chunks for this response
     * @param notFound   if passing an invalid id to {@link GatewayCommandType#REQUEST_GUILD_MEMBERS REQUEST_GUILD_MEMBERS}, it will be returned here
     * @param presences  if passing true to {@link GatewayCommandType#REQUEST_GUILD_MEMBERS REQUEST_GUILD_MEMBERS}, presences of the returned members will be here
     * @param nonce      the nonce used in the {@link GatewayCommandType#REQUEST_GUILD_MEMBERS Guild Members Request}
     */
    public GuildMemberChunkEventData(@NotNull LApi lApi, @NotNull Snowflake guildId, @NotNull ArrayList<Member> members,
                                     int chunkIndex, int chunkCount, @Nullable List<Object> notFound,
                                     @Nullable ArrayList<PresenceUpdate> presences, @Nullable Nonce nonce) {
        this.lApi = lApi;
        this.guildId = guildId;
        this.members = members;
        this.chunkIndex = chunkIndex;
        this.chunkCount = chunkCount;
        this.notFound = notFound;
        this.presences = presences;
        this.nonce = nonce;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable GuildMemberChunkEventData fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if (data == null) return null;

        String guildId = (String) data.get(GUILD_ID_KEY);
        List<Object> membersData = data.getList(MEMBERS_KEY);
        Number chunkIndex = (Number) data.get(CHUNK_INDEX_KEY);
        Number chunkCount = (Number) data.get(CHUNK_COUNT_KEY);
        List<Object> notFound = data.getList(NOT_FOUND_KEY);
        List<Object> presencesData = data.getList(PRESENCES_KEY);
        Nonce nonce = Nonce.fromStringOrInteger(data.get(NONCE_KEY));

        if (guildId == null || membersData == null || chunkIndex == null || chunkCount == null) {
            InvalidDataException.throwException(data, null, GuildMemberChunkEventData.class,
                    new Object[]{guildId, membersData, chunkIndex, chunkCount},
                    new String[]{GUILD_ID_KEY, MEMBERS_KEY, CHUNK_INDEX_KEY, CHUNK_COUNT_KEY});
            return null; //appeasing the IDE null-checks: this line will never be executed.
        }

        ArrayList<Member> members = new ArrayList<>(membersData.size());

        for (int i = 0; i < membersData.size(); i++) {
            members.add(Member.fromData(lApi, (SOData) membersData.get(i)));
            membersData.set(i, null); //release memory
        }

        ArrayList<PresenceUpdate> presences = null;

        if (presencesData != null) {
            presences = new ArrayList<>(presencesData.size());

            for (int i = 0; i < presencesData.size(); i++) {
                SOData psData = (SOData) presencesData.get(i);
                psData.add(PresenceUpdate.GUILD_ID_KEY, guildId); //add guild_id field, because it is missing here
                presences.add(PresenceUpdate.fromData(psData));
                presencesData.set(i, null); //release memory
            }
        }

        return new GuildMemberChunkEventData(
                lApi,
                Snowflake.fromString(guildId),
                members,
                chunkIndex.intValue(),
                chunkCount.intValue(),
                notFound,
                presences,
                nonce
        );
    }

    /**
     * the id of the guild
     */
    public @NotNull Snowflake getGuildId() {
        return guildId;
    }

    /**
     * set of guild members.<br>
     * {@link Member#getUser()} should not return {@code null}
     */
    public @NotNull ArrayList<Member> getMembers() {
        return members;
    }

    /**
     * the chunk index in the expected chunks for this response (0 &lt;= chunk_index &lt; chunk_count)
     */
    public int getChunkIndex() {
        return chunkIndex;
    }

    /**
     * the total number of expected chunks for this response
     */
    public int getChunkCount() {
        return chunkCount;
    }

    /**
     * The type of this array is not specified in Discord's docs. probably a {@link String} though.
     * if passing an invalid id to {@link GatewayCommandType#REQUEST_GUILD_MEMBERS REQUEST_GUILD_MEMBERS}, it will be returned here
     */
    public @Nullable List<Object> getNotFound() {
        return notFound;
    }

    /**
     * if passing true to {@link GatewayCommandType#REQUEST_GUILD_MEMBERS REQUEST_GUILD_MEMBERS}, presences of the returned members will be here
     */
    public @Nullable ArrayList<PresenceUpdate> getPresences() {
        return presences;
    }

    /**
     * the nonce used in the {@link GatewayCommandType#REQUEST_GUILD_MEMBERS Guild Members Request}
     */
    public @Nullable Nonce getNonce() {
        return nonce;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(7);

        data.add(GUILD_ID_KEY, guildId);
        data.add(MEMBERS_KEY, members);
        data.add(CHUNK_INDEX_KEY, chunkIndex);
        data.add(CHUNK_COUNT_KEY, chunkCount);
        data.addIfNotNull(NOT_FOUND_KEY, notFound);
        data.addIfNotNull(PRESENCES_KEY, presences);
        data.addIfNotNull(NONCE_KEY, nonce);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
