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

package me.linusdev.lapi.api.communication.gateway.events.invite;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.application.Application;
import me.linusdev.lapi.api.objects.invite.InviteMetadata;
import me.linusdev.lapi.api.objects.invite.TargetType;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#invite-create" target="_TOP">Discord Documentation</a>
 */
public class InviteCreateEvent extends Event {

    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String CODE_KEY = "code";
    public static final String INVITER_KEY = "inviter";
    public static final String TARGET_TYPE_KEY = "target_type";
    public static final String TARGET_USER_KEY = "target_user";
    public static final String TARGET_APPLICATION_KEY = "target_application";

    private final @NotNull Snowflake channelId;
    private final @NotNull String code;
    private final @Nullable User inviter;
    private final @Nullable TargetType targetType;
    private final @Nullable User targetUser;

    //TODO: discord docs says partial application, maybe a new class has to be created: PartialApplication. Check this.
    private final @Nullable Application targetApplication;

    private final @NotNull InviteMetadata inviteMetadata;


    public InviteCreateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                             @NotNull Snowflake channelId, @NotNull String code, @Nullable User inviter,
                             @Nullable TargetType targetType, @Nullable User targetUser,
                             @Nullable Application targetApplication, @NotNull InviteMetadata inviteMetadata) {
        super(lApi, payload, guildId);
        this.channelId = channelId;
        this.code = code;
        this.inviter = inviter;
        this.targetType = targetType;
        this.targetUser = targetUser;
        this.targetApplication = targetApplication;
        this.inviteMetadata = inviteMetadata;
    }

    public static @NotNull InviteCreateEvent fromData(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                                      @Nullable Snowflake guildId, @NotNull SOData data) throws InvalidDataException {

        Snowflake channelId = data.getAndConvert(CHANNEL_ID_KEY, Snowflake::fromString);
        String code = (String) data.get(CODE_KEY);
        User inviter = data.getAndConvertWithException(INVITER_KEY, (SOData convertible) -> User.fromData(lApi, convertible), null);
        Number targetType = (Number) data.get(TARGET_TYPE_KEY);
        User targetUser = data.getAndConvertWithException(TARGET_USER_KEY, (SOData convertible) -> User.fromData(lApi, data), null);
        Application targetApplication = data.getAndConvertWithException(TARGET_APPLICATION_KEY, (SOData convertible) -> Application.fromData(lApi, convertible), null);

        InviteMetadata inviteMetadata = InviteMetadata.fromData(data);

        if(channelId == null || code == null) {
           InvalidDataException.throwException(data, null, InviteCreateEvent.class,
                   new Object[]{channelId, code}, new String[]{CHANNEL_ID_KEY, CODE_KEY});
           return null; //unreachable Statement
        }

        return new InviteCreateEvent(lApi, payload, guildId, channelId, code, inviter,
                targetType == null ? null : TargetType.fromValue(targetType.intValue()),
                targetUser, targetApplication, inviteMetadata);
    }

    /**
     * the id as {@link Snowflake} of the channel the invite is for
     */
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the id as {@link Snowflake} of the channel the invite is for
     */
    public @NotNull String getChannelId() {
        return channelId.asString();
    }

    /**
     * 	the unique invite code
     */
    public @NotNull String getCode() {
        return code;
    }

    /**
     * the user that created the invite
     */
    public @Nullable User getInviter() {
        return inviter;
    }

    /**
     * the type of target for this voice channel invite
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
     * More invite information
     */
    public @NotNull InviteMetadata getInviteMetadata() {
        return inviteMetadata;
    }
}
