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

package me.linusdev.lapi.api.objects.guild.member;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-member-object" target="_top">GuildImpl Member Object</a>
 */
public class Member implements Datable, HasLApi, CopyAndUpdatable<Member> {

    public static final String USER_KEY = "user";
    public static final String NICK_KEY = "nick";
    public static final String AVATAR_KEY = "avatar";
    public static final String ROLES_KEY = "roles";
    public static final String JOINED_AT_KEY = "joined_at";
    public static final String PREMIUM_SINCE_KEY = "premium_since";
    public static final String DEAF_KEY = "deaf";
    public static final String MUTE_KEY = "mute";
    public static final String PENDING_KEY = "pending";
    public static final String PERMISSIONS_KEY = "permissions";

    private @Nullable User user;
    private @Nullable String nick;
    private @Nullable String avatarHash;
    private @NotNull Snowflake[] roles;
    private @NotNull final ISO8601Timestamp joinedAt;
    private @Nullable ISO8601Timestamp premiumSince;
    private boolean deaf;
    private boolean mute;
    private @Nullable Boolean pending;
    private @Nullable String permissionsString;

    private @NotNull final LApi lApi;

    /**
     *
     * @param user the user this guild member represents
     * @param nick this users guild nickname
     * @param avatarHash the member's guild avatar hash
     * @param roles array of role object ids as {@link Snowflake Snowflake[]}
     * @param joinedAt when the user joined the guild
     * @param premiumSince when the user started boosting the guild
     * @param deaf whether the user is deafened in voice channels
     * @param mute whether the user is muted in voice channels
     * @param pending whether the user has not yet passed the guild's Membership Screening requirements
     * @param permissionsString total permissions of the member in the channel, including overwrites, returned when in the interaction object
     */
    public Member(@NotNull LApi lApi, @Nullable User user, @Nullable String nick, @Nullable String avatarHash, @NotNull Snowflake[] roles,
                  @NotNull ISO8601Timestamp joinedAt, @Nullable ISO8601Timestamp premiumSince, boolean deaf, boolean mute,
                  @Nullable Boolean pending, @Nullable String permissionsString){
        this.lApi = lApi;
        this.user = user;
        this.nick = nick;
        this.avatarHash = avatarHash;
        this.roles = roles;
        this.joinedAt = joinedAt;
        this.premiumSince = premiumSince;
        this.deaf = deaf;
        this.mute = mute;
        this.pending = pending;
        this.permissionsString = permissionsString;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link Member} or {@code null} if data was {@code null}
     * @throws InvalidDataException if {@link #ROLES_KEY}, {@link #JOINED_AT_KEY}, {@link #DEAF_KEY} or {@link #MUTE_KEY} are null or missing
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable Member fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        SOData userData = (SOData) data.get(USER_KEY);
        String nick = (String) data.get(NICK_KEY);
        String avatarHash = (String) data.get(AVATAR_KEY);
        List<Object> rolesList = data.getList(ROLES_KEY);
        String joinedAt = (String) data.get(JOINED_AT_KEY);
        String premiumSince = (String) data.get(PREMIUM_SINCE_KEY);
        Boolean deaf = (Boolean) data.get(DEAF_KEY);
        Boolean mute = (Boolean) data.get(MUTE_KEY);
        Boolean pending = (Boolean) data.get(PENDING_KEY);
        String permissions = (String) data.get(PERMISSIONS_KEY);

        if(rolesList == null || joinedAt == null || deaf == null || mute == null){
            InvalidDataException exception = new InvalidDataException(data, "Cannot create " + Member.class.getSimpleName() + " because one or more required fields are missing");
            if(rolesList == null) exception.addMissingFields(ROLES_KEY);
            if(joinedAt == null) exception.addMissingFields(JOINED_AT_KEY);
            if(deaf == null) exception.addMissingFields(DEAF_KEY);
            if(mute == null) exception.addMissingFields(MUTE_KEY);

            throw exception;
        }

        Snowflake[] roles = new Snowflake[rolesList.size()];
        int i = 0;
        for(Object o : rolesList) {
            roles[i++] = Snowflake.fromString((String) o);
        }

        return new Member(lApi, userData == null ? null : User.fromData(lApi, userData), nick, avatarHash, roles, ISO8601Timestamp.fromString(joinedAt),
                ISO8601Timestamp.fromString(premiumSince), deaf, mute, pending, permissions);
    }

    /**
     * the user this guild member represents
     */
    public @Nullable User getUser() {
        return user;
    }

    /**
     * this users guild nickname
     */
    public @Nullable String getNick() {
        return nick;
    }

    /**
     * the member's guild avatar hash
     */
    public @Nullable String getAvatarHash() {
        return avatarHash;
    }

    /**
     *
     * TODO docs
     *
     * @see CDNImageRetriever
     * @see CDNImage
     * @see me.linusdev.lapi.api.communication.cdn.image.ImageQuery
     */
    public @NotNull CDNImageRetriever getAvatar(int desiredSize, @NotNull String guildId, @Nullable String userId, @NotNull AbstractFileType fileType){

        if(userId == null && user == null) throw new IllegalArgumentException("No userId given");
        if(getAvatarHash() == null) throw new IllegalArgumentException("This member object has no avatar hash");

        return new CDNImageRetriever(CDNImage.ofGuildMemberAvatar(lApi, guildId, userId == null ? user.getId() : userId, getAvatarHash(), fileType));
    }

    /**
     * array of role object ids as {@link Snowflake Snowflake[]}
     */
    public @NotNull Snowflake[] getRoleIds() {
        return roles;
    }

    /**
     * when the user joined the guild
     */
    public @NotNull ISO8601Timestamp getJoinedAt() {
        return joinedAt;
    }

    /**
     * when the user started boosting the guild
     */
    public @Nullable ISO8601Timestamp getPremiumSince() {
        return premiumSince;
    }

    /**
     * whether the user is deafened in voice channels
     */
    public boolean isDeaf() {
        return deaf;
    }

    /**
     * whether the user is muted in voice channels
     */
    public boolean isMute() {
        return mute;
    }

    /**
     * whether the user has not yet passed the guild's Membership Screening requirements
     */
    public @Nullable Boolean getPending() {
        return pending;
    }

    /**
     * total permissions of the member in the channel, including overwrites, returned when in the interaction object
     */
    public @Nullable String getPermissionsString() {
        return permissionsString;
    }

    /**
     * total permissions of the member in the channel, including overwrites, returned when in the interaction object
     * as {@link Permissions}
     */
    public @Nullable Permissions getPermissions() {
        return Permissions.ofString(permissionsString);
    }

    /**
     * Generate {@link SOData} of this {@link Member}
     */
    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(10);

        if(user != null) data.add(USER_KEY, user);
        if(nick != null) data.add(NICK_KEY, nick);
        if(avatarHash != null) data.add(AVATAR_KEY, avatarHash);
        data.add(ROLES_KEY, roles);
        data.add(JOINED_AT_KEY, joinedAt);
        if(premiumSince != null) data.add(PREMIUM_SINCE_KEY, premiumSince);
        data.add(DEAF_KEY, deaf);
        data.add(MUTE_KEY, mute);
        if(pending != null) data.add(PENDING_KEY, pending);
        if(permissionsString != null) data.add(PERMISSIONS_KEY, permissionsString);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public @NotNull Member copy() {
        return new Member(
                lApi,
                user, //User will not be copied
                Copyable.copy(nick),
                Copyable.copy(avatarHash),
                Copyable.copyArrayFlat(roles),
                Copyable.copy(joinedAt),
                Copyable.copy(premiumSince),
                deaf, mute, pending,
                Copyable.copy(permissionsString)
        );
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {

        if(user == null) this.user = User.fromData(lApi, (SOData) data.get(USER_KEY));
        data.processIfContained(NICK_KEY, (String str) -> this.nick = str);
        data.processIfContained(AVATAR_KEY, (String str) -> this.avatarHash = str);
        data.processIfContained(ROLES_KEY, (List<Object> array) -> {
            this.roles = new Snowflake[array.size()];

            int i = 0;
            for(Object o : array) {
                roles[i++] = Snowflake.fromString((String) o);
            }
        });
        data.processIfContained(PREMIUM_SINCE_KEY, (String str) -> this.premiumSince = ISO8601Timestamp.fromString(str));
        data.processIfContained(DEAF_KEY, (Boolean deaf) -> this.deaf = deaf);
        data.processIfContained(MUTE_KEY, (Boolean mute) -> this.mute = deaf);
        data.processIfContained(PENDING_KEY, (Boolean pending) -> this.pending = deaf);
        data.processIfContained(PERMISSIONS_KEY, (String str) -> this.permissionsString = str);
    }
}
