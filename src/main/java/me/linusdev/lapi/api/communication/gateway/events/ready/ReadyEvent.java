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

package me.linusdev.lapi.api.communication.gateway.events.ready;

import me.linusdev.data.Datable;
import me.linusdev.data.functions.Converter;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.application.PartialApplication;
import me.linusdev.lapi.api.objects.guild.UnavailableGuild;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#ready" target="_top">Ready Event</a>
 */
public class ReadyEvent extends Event implements Datable, HasLApi {

    public static final String VERSION_KEY = "v";
    public static final String USER_KEY = "user";
    public static final String GUILDS_KEY = "guilds";
    public static final String SESSION_ID_KEY = "session_id";
    public static final String SHARD_KEY = "shard";
    public static final String APPLICATION_KEY = "application";

    private final @NotNull LApi lApi;

    private final @NotNull ApiVersion version;
    private final @NotNull User user;
    private final @NotNull UnavailableGuild[] guilds;
    private final @NotNull String  sessionId;
    private final @Nullable Integer shardId;
    private final @Nullable Integer numShards;
    private final @NotNull PartialApplication application;

    /**
     *
     * @param lApi {@link LApi}
     * @param version gateway version
     * @param user information about the user including email
     * @param guilds the guilds the user is in
     * @param sessionId used for resuming connections
     * @param shardId the shard information associated with this session, if sent when identifying
     * @param numShards the shard information associated with this session, if sent when identifying
     * @param application contains id and flags
     */
    public ReadyEvent(@NotNull LApi lApi, GatewayPayloadAbstract payload, @NotNull ApiVersion version, @NotNull User user, @NotNull UnavailableGuild[] guilds, @NotNull String sessionId, @Nullable Integer shardId, @Nullable Integer numShards, @NotNull PartialApplication application) {
        super(lApi, payload, null);
        this.lApi = lApi;
        this.version = version;
        this.user = user;
        this.guilds = guilds;
        this.sessionId = sessionId;
        this.shardId = shardId;
        this.numShards = numShards;
        this.application = application;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link ReadyEvent}
     * @throws InvalidDataException if {@link #VERSION_KEY}, {@link #USER_KEY}, {@link #GUILDS_KEY}, {@link #SESSION_ID_KEY} or {@link #APPLICATION_KEY} are missing or {@code null}
     */
    @Contract("_, _, null -> null; _, _, !null -> !null")
    public static @Nullable ReadyEvent fromData(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Number version = (Number) data.get(VERSION_KEY);
        User user = data.getAndConvertWithException(USER_KEY,
                (ExceptionConverter<SOData, User, InvalidDataException>) convertible -> User.fromData(lApi, convertible), null);

        ArrayList<UnavailableGuild> guilds = data.getListAndConvertWithException(GUILDS_KEY,
                (ExceptionConverter<SOData, UnavailableGuild, InvalidDataException>) UnavailableGuild::fromData);

        String sessionId = (String) data.get(SESSION_ID_KEY);

        ArrayList<Integer> shard = data.getListAndConvert(SHARD_KEY, (Converter<Number, Integer>) convertible -> {
            if(convertible == null) return null;
            return convertible.intValue();
        });

        PartialApplication application = data.getAndConvertWithException(APPLICATION_KEY,
                (ExceptionConverter<SOData, PartialApplication, InvalidDataException>) convertible -> PartialApplication.fromData(lApi, convertible), null);

        if(version == null || user == null || guilds == null || sessionId == null || application == null){
            InvalidDataException.throwException(data, null, ReadyEvent.class,
                    new Object[]{version, user, guilds, sessionId, application},
                    new String[]{VERSION_KEY, USER_KEY, GUILDS_KEY, SESSION_ID_KEY, APPLICATION_KEY});
        }

        //noinspection ConstantConditions
        return new ReadyEvent(lApi, payload, ApiVersion.fromInt(version.intValue()), user, guilds.toArray(new UnavailableGuild[0]), sessionId,
                shard == null ? null : shard.get(0), shard == null ? null : shard.get(1), application);
    }

    /**
     * gateway version
     */
    public @NotNull ApiVersion getVersion() {
        return version;
    }

    /**
     * 	information about the user including email
     */
    public @NotNull User getUser() {
        return user;
    }

    /**
     * 	the guilds the user is in
     */
    public UnavailableGuild[] getGuilds() {
        return guilds;
    }

    /**
     * used for resuming connections
     */
    public @NotNull String getSessionId() {
        return sessionId;
    }

    /**
     * the shard information associated with this session, if sent when identifying
     */
    public @Nullable Integer getShardId() {
        return shardId;
    }

    /**
     * the shard information associated with this session, if sent when identifying
     */
    public @Nullable Integer getNumShards() {
        return numShards;
    }

    /**
     * contains id and flags
     */
    public @NotNull PartialApplication getApplication() {
        return application;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(VERSION_KEY, version);
        data.add(USER_KEY, user);
        data.add(GUILDS_KEY, guilds);
        data.add(SESSION_ID_KEY, sessionId);
        if(shardId != null && numShards != null) data.add(SHARD_KEY, new int[]{shardId, numShards});
        data.add(APPLICATION_KEY, application);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
