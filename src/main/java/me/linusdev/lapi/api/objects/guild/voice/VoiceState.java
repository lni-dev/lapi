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

package me.linusdev.lapi.api.objects.guild.voice;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.updatable.IsUpdatable;
import me.linusdev.lapi.api.interfaces.updatable.NotUpdatable;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @see <a href="https://discord.com/developers/docs/resources/voice#voice-state-object" target="_top">Voice State Object</a>
 */
public class VoiceState implements CopyAndUpdatable<VoiceState>, Datable, HasLApi {

    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String USER_ID_KEY = "user_id";
    public static final String MEMBER_KEY = "member";
    public static final String SESSION_ID_KEY = "session_id";
    public static final String DEAF_KEY = "deaf";
    public static final String MUTE_KEY = "mute";
    public static final String SELF_DEAF_KEY = "self_deaf";
    public static final String SELF_MUTE_KEY = "self_mute";
    public static final String SELF_STREAM_KEY = "self_stream";
    public static final String SELF_VIDEO_KEY = "self_video";
    public static final String SUPPRESS_KEY = "suppress";
    public static final String REQUEST_TO_SPEAK_TIMESTAMP_KEY = "request_to_speak_timestamp";

    private final @NotNull LApi lApi;

    private @NotUpdatable @Nullable Snowflake guildId;
    private @IsUpdatable @Nullable Snowflake channelId;
    private @NotUpdatable @NotNull final Snowflake userId;
    private @NotUpdatable @Nullable Member member = null;
    private @IsUpdatable @NotNull String sessionId;
    private @IsUpdatable boolean deaf;
    private @IsUpdatable boolean mute;
    private @IsUpdatable boolean selfDeaf;
    private @IsUpdatable boolean selfMute;
    private @IsUpdatable @Nullable Boolean selfStream;
    private @IsUpdatable boolean selfVideo;
    private @IsUpdatable boolean suppress;
    private @IsUpdatable @Nullable ISO8601Timestamp requestToSpeakTimestamp;

    /**
     *
     * @param lApi {@link LApi}
     * @param guildId the guild id this voice state is for
     * @param channelId the channel id this user is connected to
     * @param userId the user id this voice state is for
     * @param member the guild member this voice state is for
     * @param sessionId the session id for this voice state
     * @param deaf whether this user is deafened by the server
     * @param mute whether this user is muted by the server
     * @param selfDeaf whether this user is locally deafened
     * @param selfMute whether this user is locally muted
     * @param selfStream whether this user is streaming using "Go Live"
     * @param selfVideo whether this user's camera is enabled
     * @param suppress whether this user is muted by the current user
     * @param requestToSpeakTimestamp the time at which the user requested to speak
     */
    public VoiceState(@NotNull LApi lApi, @Nullable Snowflake guildId, @Nullable Snowflake channelId, @NotNull Snowflake userId,
                      @Nullable Member member, @NotNull String sessionId, boolean deaf, boolean mute, boolean selfDeaf,
                      boolean selfMute, @Nullable Boolean selfStream, boolean selfVideo, boolean suppress,
                      @Nullable ISO8601Timestamp requestToSpeakTimestamp) {
        this.lApi = lApi;
        this.guildId = guildId;
        this.channelId = channelId;
        this.userId = userId;
        this.member = member;
        this.sessionId = sessionId;
        this.deaf = deaf;
        this.mute = mute;
        this.selfDeaf = selfDeaf;
        this.selfMute = selfMute;
        this.selfStream = selfStream;
        this.selfVideo = selfVideo;
        this.suppress = suppress;
        this.requestToSpeakTimestamp = requestToSpeakTimestamp;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData} with required fields
     * @return {@link VoiceState}
     * @throws InvalidDataException if data is invalid
     */
    @SuppressWarnings("ConstantConditions")
    public static @NotNull VoiceState fromData(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        String guildId = (String) data.get(GUILD_ID_KEY);
        String channelId = (String) data.get(CHANNEL_ID_KEY);
        String userId = (String) data.get(USER_ID_KEY);
        SOData member = (SOData) data.get(MEMBER_KEY);
        String sessionId = (String) data.get(SESSION_ID_KEY);
        Boolean deaf = (Boolean) data.get(DEAF_KEY);
        Boolean mute = (Boolean) data.get(MUTE_KEY);
        Boolean selfDeaf = (Boolean) data.get(SELF_DEAF_KEY);
        Boolean selfMute = (Boolean) data.get(SELF_MUTE_KEY);
        Boolean selfStream = (Boolean) data.get(SELF_STREAM_KEY);
        Boolean selfVideo = (Boolean) data.get(SELF_VIDEO_KEY);
        Boolean suppress = (Boolean) data.get(SUPPRESS_KEY);
        String requestToSpeakTimestamp = (String) data.get(REQUEST_TO_SPEAK_TIMESTAMP_KEY);

        if(userId == null || sessionId == null || deaf == null || mute == null || selfDeaf == null || selfMute == null || selfVideo == null|| suppress == null){
            InvalidDataException.throwException(data, null, VoiceState.class,
                    new Object[]{userId, sessionId, deaf, mute, selfDeaf, selfMute, selfVideo, suppress},
                    new String[]{USER_ID_KEY, SESSION_ID_KEY, DEAF_KEY, MUTE_KEY, SELF_DEAF_KEY, SELF_MUTE_KEY, SELF_VIDEO_KEY, SUPPRESS_KEY});
        }

        return new VoiceState(lApi,
                Snowflake.fromString(guildId), Snowflake.fromString(channelId), Snowflake.fromString(userId),
                Member.fromData(lApi, member), sessionId, deaf, mute, selfDeaf, selfMute, selfStream, selfVideo, suppress,
                ISO8601Timestamp.fromString(requestToSpeakTimestamp)
                );
    }

    @Override
    public void updateSelfByData(@NotNull SOData data) throws InvalidDataException {
        //guildId will only be updated if it is null
        if(guildId == null) data.processIfContained(GUILD_ID_KEY, (String str) -> this.guildId = Snowflake.fromString(str));
        data.processIfContained(CHANNEL_ID_KEY, (String str) -> this.channelId = Snowflake.fromString(str));
        //userId will not be updated
        //member will only be updated if it is null
        if(member == null) member = Member.fromData(lApi, (SOData) data.get(MEMBER_KEY));

        data.processIfContained(SESSION_ID_KEY, (String str) -> this.sessionId = str);
        data.processIfContained(DEAF_KEY, (Boolean bool) -> this.deaf = bool);
        data.processIfContained(MUTE_KEY, (Boolean bool) -> this.mute = bool);
        data.processIfContained(SELF_DEAF_KEY, (Boolean bool) -> this.selfDeaf = bool);
        data.processIfContained(SELF_MUTE_KEY, (Boolean bool) -> this.selfMute = bool);
        data.processIfContained(SELF_STREAM_KEY, (Boolean bool) -> this.selfStream = bool);
        data.processIfContained(SELF_VIDEO_KEY, (Boolean bool) -> this.selfVideo = bool);
        data.processIfContained(SUPPRESS_KEY, (Boolean bool) -> this.suppress = bool);
        data.processIfContained(REQUEST_TO_SPEAK_TIMESTAMP_KEY, (String str) -> this.requestToSpeakTimestamp = ISO8601Timestamp.fromString(str));
    }

    /**
     * the guild id as {@link Snowflake} this voice state is for
     */
    public @NotUpdatable @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * the guild id as {@link String} this voice state is for
     */
    public @NotUpdatable @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
    }

    /**
     * the channel id as {@link Snowflake} this user is connected to
     */
    public @IsUpdatable @Nullable Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the channel id as {@link String} this user is connected to
     */
    public @IsUpdatable @Nullable String getChannelId() {
        if(channelId == null) return null;
        return channelId.asString();
    }

    /**
     * the user id as {@link Snowflake} this voice state is for
     */
    public @IsUpdatable @NotNull Snowflake getUserIdAsSnowflake() {
        return userId;
    }

    /**
     * the user id as {@link String} this voice state is for
     */
    public @IsUpdatable @NotNull String getUserId() {
        return userId.asString();
    }

    /**
     * the guild member this voice state is for
     */
    public @NotUpdatable @Nullable Member getMember() {
        return member;
    }

    /**
     * the session id for this voice state
     */
    public @IsUpdatable @NotNull String getSessionId() {
        return sessionId;
    }

    /**
     * whether this user is deafened by the server
     */
    public @IsUpdatable boolean isDeaf() {
        return deaf;
    }

    /**
     * whether this user is muted by the server
     */
    public @IsUpdatable boolean isMute() {
        return mute;
    }

    /**
     * whether this user is locally deafened
     */
    public @IsUpdatable boolean isSelfDeaf() {
        return selfDeaf;
    }

    /**
     * whether this user is locally muted
     */
    public @IsUpdatable boolean isSelfMute() {
        return selfMute;
    }

    /**
     * whether this user is streaming using "Go Live"
     */
    public @Nullable Boolean getSelfStream() {
        return selfStream;
    }

    /**
     * whether this user's camera is enabled
     */
    public @IsUpdatable boolean isSelfVideo() {
        return selfVideo;
    }

    /**
     * whether this user is muted by the current user
     */
    public @IsUpdatable boolean isSuppress() {
        return suppress;
    }

    /**
     * the time at which the user requested to speak
     */
    public @IsUpdatable @Nullable ISO8601Timestamp getRequestToSpeakTimestamp() {
        return requestToSpeakTimestamp;
    }

    /**
     *
     * @return {@link SOData} for this {@link VoiceState}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(12);

        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(USER_ID_KEY, userId);
        data.addIfNotNull(MEMBER_KEY, member);
        data.add(SESSION_ID_KEY, sessionId);
        data.add(DEAF_KEY, deaf);
        data.add(MUTE_KEY, mute);
        data.add(SELF_DEAF_KEY, selfDeaf);
        data.add(SELF_MUTE_KEY, selfMute);
        data.addIfNotNull(SELF_STREAM_KEY, selfStream);
        data.add(SELF_VIDEO_KEY, selfVideo);
        data.add(SUPPRESS_KEY, suppress);
        data.add(REQUEST_TO_SPEAK_TIMESTAMP_KEY, requestToSpeakTimestamp);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public @NotNull VoiceState copy() {
        return new VoiceState(
                lApi,
                Copyable.copy(guildId),
                Copyable.copy(channelId),
                Copyable.copy(userId),
                member, //member object is not copied, because this voice state will always be for the same person.
                Copyable.copy(sessionId),
                deaf, mute, selfDeaf, selfMute, selfStream, selfVideo, suppress, Copyable.copy(requestToSpeakTimestamp));
    }
}
