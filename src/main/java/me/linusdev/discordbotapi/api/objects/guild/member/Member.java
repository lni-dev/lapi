package me.linusdev.discordbotapi.api.objects.guild.member;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.enums.Permissions;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.toodo.Avatar;
import me.linusdev.discordbotapi.api.objects.toodo.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-member-object" target="_top">Guild Member Object</a>
 */
public class Member implements Datable, HasLApi {

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

    private @Nullable final User user;
    private @Nullable final String nick;
    private @Nullable final Avatar avatar;
    private @NotNull final Snowflake[] roles;
    private @NotNull final String[] rolesAsString;
    private @NotNull final ISO8601Timestamp joinedAt;
    private @Nullable final ISO8601Timestamp premiumSince;
    private final boolean deaf;
    private final boolean mute;
    private @Nullable final Boolean pending;
    private @Nullable final String permissionsString;
    private @Nullable final List<Permissions> permissions;

    private @NotNull final LApi lApi;

    /**
     *
     * @param user the user this guild member represents
     * @param nick this users guild nickname
     * @param avatar the member's guild avatar hash
     * @param roles array of role object ids as {@link Snowflake Snowflake[]}
     * @param rolesAsString array of role object ids as {@link String String[]}
     * @param joinedAt when the user joined the guild
     * @param premiumSince when the user started boosting the guild
     * @param deaf whether the user is deafened in voice channels
     * @param mute whether the user is muted in voice channels
     * @param pending whether the user has not yet passed the guild's Membership Screening requirements
     * @param permissionsString total permissions of the member in the channel, including overwrites, returned when in the interaction object
     */
    public Member(@NotNull LApi lApi, @Nullable User user, @Nullable String nick, @Nullable Avatar avatar, @NotNull Snowflake[] roles, @NotNull String[] rolesAsString,
                  @NotNull ISO8601Timestamp joinedAt, @Nullable ISO8601Timestamp premiumSince, boolean deaf, boolean mute,
                  @Nullable Boolean pending, @Nullable String permissionsString){
        this.lApi = lApi;
        this.user = user;
        this.nick = nick;
        this.avatar = avatar;
        this.roles = roles;
        this.rolesAsString = rolesAsString;
        this.joinedAt = joinedAt;
        this.premiumSince = premiumSince;
        this.deaf = deaf;
        this.mute = mute;
        this.pending = pending;
        this.permissionsString = permissionsString;

        if(permissionsString != null)
            permissions = Permissions.getPermissionsFromBits(new BigInteger(permissionsString));
        else
            permissions = null;

    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link Member} or {@code null} if data was {@code null}
     * @throws InvalidDataException if {@link #ROLES_KEY}, {@link #JOINED_AT_KEY}, {@link #DEAF_KEY} or {@link #MUTE_KEY} are null or missing
     */
    public static @Nullable Member fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        Data userData = (Data) data.get(USER_KEY);
        String nick = (String) data.get(NICK_KEY);
        String avatar = (String) data.get(AVATAR_KEY);
        ArrayList<Object> rolesList = (ArrayList<Object>) data.get(ROLES_KEY);
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
        String[] rolesAsString = new String[rolesList.size()];
        int i = 0;
        for(Object o : rolesList) {
            rolesAsString[i] = (String) o;
            roles[i++] = Snowflake.fromString((String) o);
        }

        return new Member(lApi, userData == null ? null : User.fromData(lApi, userData), nick, Avatar.fromHashString(avatar), roles, rolesAsString, ISO8601Timestamp.fromString(joinedAt),
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
    public @Nullable Avatar getAvatar() {
        return avatar;
    }

    /**
     * array of role object ids as {@link Snowflake Snowflake[]}
     */
    public @NotNull Snowflake[] getRoleIdsAsSnowflakes() {
        return roles;
    }

    public @NotNull String[] getRoleIds(){
        return rolesAsString;
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
     * as {@link List} of {@link Permissions}
     */
    public @Nullable List<Permissions> getPermissions() {
        return permissions;
    }

    /**
     * Generate {@link Data} of this {@link Member}
     */
    @Override
    public @NotNull Data getData() {
        Data data = new Data(4);

        if(user != null) data.add(USER_KEY, user);
        if(nick != null) data.add(NICK_KEY, nick);
        if(avatar != null) data.add(AVATAR_KEY, avatar);
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
}
