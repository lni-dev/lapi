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

package me.linusdev.lapi.api.objects.guild.member;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PartialMember implements Datable, HasLApi {

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

    protected @Nullable String nick;
    protected @Nullable String avatarHash;
    protected @NotNull Snowflake[] roles;
    protected final @NotNull ISO8601Timestamp joinedAt;
    protected @Nullable ISO8601Timestamp premiumSince;
    protected @Nullable Boolean pending;
    protected @Nullable String permissionsString;

    protected final @NotNull LApi lApi;

    public PartialMember(@NotNull LApi lApi, @Nullable String nick, @Nullable String avatarHash, @NotNull Snowflake[] roles,
                         @NotNull ISO8601Timestamp joinedAt, @Nullable ISO8601Timestamp premiumSince,
                         @Nullable Boolean pending, @Nullable String permissionsString) {
        this.nick = nick;
        this.avatarHash = avatarHash;
        this.roles = roles;
        this.joinedAt = joinedAt;
        this.premiumSince = premiumSince;
        this.pending = pending;
        this.permissionsString = permissionsString;
        this.lApi = lApi;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link Member} or {@code null} if data was {@code null}
     * @throws InvalidDataException if {@link #ROLES_KEY}, {@link #JOINED_AT_KEY}, {@link #DEAF_KEY} or {@link #MUTE_KEY} are null or missing
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable PartialMember fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String nick = (String) data.get(NICK_KEY);
        String avatarHash = (String) data.get(AVATAR_KEY);
        List<Object> rolesList = data.getList(ROLES_KEY);
        String joinedAt = (String) data.get(JOINED_AT_KEY);
        String premiumSince = (String) data.get(PREMIUM_SINCE_KEY);
        Boolean pending = (Boolean) data.get(PENDING_KEY);
        String permissions = (String) data.get(PERMISSIONS_KEY);

        if(rolesList == null || joinedAt == null){
            InvalidDataException.throwException(data, null, PartialMember.class,
                    new Object[]{rolesList, joinedAt},
                    new String[]{ROLES_KEY, JOINED_AT_KEY});
            return null; //unreachable
        }

        Snowflake[] roles = new Snowflake[rolesList.size()];
        int i = 0;
        for(Object o : rolesList) {
            roles[i++] = Snowflake.fromString((String) o);
        }

        return new PartialMember(lApi, nick, avatarHash, roles, ISO8601Timestamp.fromString(joinedAt),
                ISO8601Timestamp.fromString(premiumSince), pending, permissions);
    }

    /**
     * the user this guild member represents
     */
    public @Nullable User getUser() {
        return null;
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
     * @param desiredSize see {@link CDNImageRetriever#CDNImageRetriever(CDNImage, int, boolean) here}
     * @param guildId id of the guild this member belongs to
     * @param userId id of the user this member belongs to
     * @param fileType see {@link CDNImage#ofGuildMemberAvatar(LApi, String, String, String, AbstractFileType) here}
     * @return {@link CDNImageRetriever} that can retrieve the image.
     * @see CDNImageRetriever#CDNImageRetriever(CDNImage, int, boolean)
     * @see CDNImage#ofGuildMemberAvatar(LApi, String, String, String, AbstractFileType)
     */
    public @NotNull CDNImageRetriever getAvatar(int desiredSize, @NotNull String guildId, @Nullable String userId, @NotNull AbstractFileType fileType){

        if(userId == null && getUser() == null) throw new IllegalArgumentException("No userId given");
        if(getAvatarHash() == null) throw new IllegalArgumentException("This member object has no avatar hash");

        return new CDNImageRetriever(CDNImage.ofGuildMemberAvatar(lApi, guildId, userId == null ? getUser().getId() : userId, getAvatarHash(), fileType), desiredSize, true);
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

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(10);

        if(nick != null) data.add(NICK_KEY, nick);
        if(avatarHash != null) data.add(AVATAR_KEY, avatarHash);
        data.add(ROLES_KEY, roles);
        data.add(JOINED_AT_KEY, joinedAt);
        if(premiumSince != null) data.add(PREMIUM_SINCE_KEY, premiumSince);
        if(pending != null) data.add(PENDING_KEY, pending);
        if(permissionsString != null) data.add(PERMISSIONS_KEY, permissionsString);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
