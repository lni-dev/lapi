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

package me.linusdev.lapi.api.objects.invite;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.application.Application;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     Represents a code that when used, adds a user to a guild or group DM channel.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/invite#invite-resource" target="_top">Invite Resource</a>
 */
public class Invite implements Datable, HasLApi {

    public static final String CODE_KEY = "code";
    public static final String GUILD_KEY = "guild";
    public static final String CHANNEL_KEY = "channel";
    public static final String INVITER_KEY = "inviter";
    public static final String TARGET_TYPE_KEY = "target_type";
    public static final String TARGET_USER_KEY = "target_user";
    public static final String TARGET_APPLICATION_KEY = "target_application";
    public static final String APPROXIMATE_PRESENCE_COUNT_KEY = "approximate_presence_count";
    public static final String APPROXIMATE_MEMBER_COUNT_KEY = "approximate_member_count";
    public static final String EXPIRES_AT_KEY = "expires_at";
    public static final String STAGE_INSTANCE_KEY = "stage_instance";
    public static final String GUILD_SCHEDULED_EVENT_KEY = "guild_scheduled_event";

    private final @NotNull LApi lApi;

    private final @NotNull String code;
    private final @Nullable Guild guild;
    private final @NotNull Channel channel;
    private final @Nullable User inviter;
    private final @Nullable TargetType targetType;
    private final @Nullable User targetUser;
    private final @Nullable Application targetApplication;
    private final @Nullable Integer approximatePresenceCount;
    private final @Nullable Integer approximateMemberCount;
    private final @Nullable ISO8601Timestamp expiresAt;
    private final @Nullable InviteStageInstance stageInstance;
    private final @Nullable GuildScheduledEvent guildScheduledEvent;

    //this is not in the object, but extends it instead
    private final @Nullable InviteMetadata inviteMetadata;

    /**
     *
     * @param lApi {@link LApi}
     * @param code the invite code (unique ID)
     * @param guild the guild this invite is for
     * @param channel the channel this invite is for
     * @param inviter the user who created the invite
     * @param targetType the {@link TargetType type of target} for this voice channel invite
     * @param targetUser the user whose stream to display for this voice channel stream invite
     * @param targetApplication the embedded application to open for this voice channel embedded application invite
     * @param approximatePresenceCount approximate count of online members, returned from the GET /invites/&lt;code&gt; endpoint when with_counts is true TODO add @link
     * @param approximateMemberCount approximate count of total members, returned from the GET /invites/&lt;code&gt; endpoint when with_counts is true TODO add @link
     * @param expiresAt the expiration date of this invite, returned from the GET /invites/&lt;code&gt; endpoint when with_expiration is true TODO add @link
     * @param stageInstance stage instance data if there is a public Stage instance in the Stage channel this invite is for TODO add link public stage instance
     * @param guildScheduledEvent guild scheduled event data, only included if guild_scheduled_event_id contains a valid guild scheduled event id
     * @param inviteMetadata Extra information about an {@link Invite}, will "extend" the invite object.
     */
    public Invite(@NotNull LApi lApi, @NotNull String code, @Nullable Guild guild, @NotNull Channel channel, @Nullable User inviter, @Nullable TargetType targetType, @Nullable User targetUser, @Nullable Application targetApplication, @Nullable Integer approximatePresenceCount, @Nullable Integer approximateMemberCount, @Nullable ISO8601Timestamp expiresAt, @Nullable InviteStageInstance stageInstance, @Nullable GuildScheduledEvent guildScheduledEvent, @Nullable InviteMetadata inviteMetadata) {
        this.lApi = lApi;
        this.code = code;
        this.guild = guild;
        this.channel = channel;
        this.inviter = inviter;
        this.targetType = targetType;
        this.targetUser = targetUser;
        this.targetApplication = targetApplication;
        this.approximatePresenceCount = approximatePresenceCount;
        this.approximateMemberCount = approximateMemberCount;
        this.expiresAt = expiresAt;
        this.stageInstance = stageInstance;
        this.guildScheduledEvent = guildScheduledEvent;
        this.inviteMetadata = inviteMetadata;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData} with required fields
     * @return {@link Invite}
     * @throws InvalidDataException if {@link #CODE_KEY} or {@link #CHANNEL_KEY} are missing or {@code null}
     */
    public static @Nullable Invite fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String code = data.getAsAndRequireNotNull(CODE_KEY, InvalidDataException.SUPPLIER);
        SOData guildData = (SOData) data.get(GUILD_KEY);
        SOData channelData = data.getAsAndRequireNotNull(CHANNEL_KEY, InvalidDataException.SUPPLIER);
        SOData inviterData = (SOData) data.get(INVITER_KEY);
        Number targetType = (Number) data.get(TARGET_TYPE_KEY);
        SOData targetUserData = (SOData) data.get(TARGET_USER_KEY);
        SOData targetApplication = (SOData) data.get(TARGET_APPLICATION_KEY);
        Number approximatePresenceCount = (Number) data.get(APPROXIMATE_PRESENCE_COUNT_KEY);
        Number approximateMemberCount = (Number) data.get(APPROXIMATE_MEMBER_COUNT_KEY);
        String expiresAt = (String) data.get(EXPIRES_AT_KEY);
        SOData stageInstance = (SOData) data.get(STAGE_INSTANCE_KEY);
        SOData guildScheduledEvent = (SOData) data.get(GUILD_SCHEDULED_EVENT_KEY);

        //This extends the Invite object
        InviteMetadata inviteMetadata = InviteMetadata.fromData(data);


        return new Invite(lApi, code, Guild.fromData(lApi, guildData), Channel.channelFromData(lApi, channelData),
                inviterData == null ? null : User.fromData(lApi, inviterData),
                targetType == null ? null : TargetType.fromValue(targetType.intValue()),
                targetUserData == null ? null : User.fromData(lApi, targetUserData), Application.fromData(lApi, targetApplication),
                approximatePresenceCount == null ? null : approximatePresenceCount.intValue(),
                approximateMemberCount == null ? null : approximateMemberCount.intValue(),
                ISO8601Timestamp.fromString(expiresAt), InviteStageInstance.fromData(lApi, stageInstance),
                GuildScheduledEvent.fromData(lApi, guildScheduledEvent), inviteMetadata);
    }

    /**
     * the invite code (unique ID)
     */
    public @NotNull String getCode() {
        return code;
    }

    /**
     * the guild this invite is for
     * @return partial {@link Guild guild} object
     */
    public @Nullable Guild getGuild() {
        return guild;
    }

    /**
     * the channel this invite is for
     * @return partial {@link Channel channel} object
     */
    public @NotNull Channel getChannel() {
        return channel;
    }

    /**
     * the user who created the invite
     */
    public @Nullable User getInviter() {
        return inviter;
    }

    /**
     * the {@link TargetType type of target} for this voice channel invite
     */
    public @Nullable TargetType getTargetType() {
        return targetType;
    }

    /**
     * the user whose stream to display for this voice channel stream invite
     */
    public @Nullable User getTargetUser() {
        return targetUser;
    }

    /**
     * the embedded application to open for this voice channel embedded application invite
     */
    public @Nullable Application getTargetApplication() {
        return targetApplication;
    }

    /**
     * approximate count of online members, returned from the GET /invites/&lt;code&gt; endpoint when with_counts is true TODO add @link
     */
    public @Nullable Integer getApproximatePresenceCount() {
        return approximatePresenceCount;
    }

    /**
     * approximate count of total members, returned from the GET /invites/&lt;code&gt; endpoint when with_counts is true TODO add @link
     */
    public @Nullable Integer getApproximateMemberCount() {
        return approximateMemberCount;
    }

    /**
     * the expiration date of this invite, returned from the GET /invites/&lt;code&gt; endpoint when with_expiration is true TODO add @link
     */
    public @Nullable ISO8601Timestamp getExpiresAt() {
        return expiresAt;
    }

    /**
     * stage instance data if there is a public Stage instance in the Stage channel this invite is for TODO add link public stage instance
     */
    public @Nullable InviteStageInstance getStageInstance() {
        return stageInstance;
    }

    /**
     * guild scheduled event data, only included if {@link GuildScheduledEvent#getEntityId() guild_scheduled_event_id} contains a valid guild scheduled event id
     */
    public @Nullable GuildScheduledEvent getGuildScheduledEvent() {
        return guildScheduledEvent;
    }

    /**
     * This extends the normal {@link Invite invite} object
     */
    public @Nullable InviteMetadata getInviteMetadata() {
        return inviteMetadata;
    }

    /**
     *
     * @return {@link SOData} for this {@link Invite}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(12);

        data.add(CODE_KEY, code);
        if(guild != null) data.add(GUILD_KEY, guild);
        data.add(CHANNEL_KEY, channel);
        if(inviter != null) data.add(INVITER_KEY, inviter);
        if(targetType != null) data.add(TARGET_TYPE_KEY, targetType);
        if(targetUser != null) data.add(TARGET_USER_KEY, targetUser);
        if(targetApplication != null) data.add(TARGET_APPLICATION_KEY, targetApplication);
        if(approximatePresenceCount != null) data.add(APPROXIMATE_PRESENCE_COUNT_KEY, approximatePresenceCount);
        if(approximateMemberCount != null) data.add(APPROXIMATE_MEMBER_COUNT_KEY, approximateMemberCount);
        if(expiresAt != null) data.add(EXPIRES_AT_KEY, expiresAt);
        if(stageInstance != null) data.add(STAGE_INSTANCE_KEY, stageInstance);
        if(guildScheduledEvent != null) data.add(GUILD_SCHEDULED_EVENT_KEY, guildScheduledEvent);

        if(inviteMetadata != null) inviteMetadata.extendData(data);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
