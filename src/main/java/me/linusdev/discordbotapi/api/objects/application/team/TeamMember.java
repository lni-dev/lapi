package me.linusdev.discordbotapi.api.objects.application.team;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @see <a href="https://discord.com/developers/docs/topics/teams#data-models-team-member-object" target="_top"> Team Member Object</a>
 */
public class TeamMember implements Datable, HasLApi {

    public static final String MEMBERSHIP_STATE_KEY = "membership_state";
    public static final String PERMISSIONS_KEY = "permissions";
    public static final String TEAM_ID_KEY = "team_id";
    public static final String USER_KEY = "user";

    private @NotNull final MembershipState membershipState;
    private @NotNull final String[] permissions;
    private @NotNull final Snowflake teamId;
    private @NotNull final User user;

    private @NotNull final LApi lApi;

    /**
     *
     * @param membershipState the user's membership state on the team
     * @param permissions will always be ["*"]
     * @param teamId the id of the parent team of which they are a member
     * @param user partial {@link User user} object. the avatar, discriminator, id, and username of the user
     */
    public TeamMember(@NotNull LApi lApi, @NotNull MembershipState membershipState, @NotNull String[] permissions, @NotNull Snowflake teamId, @NotNull User user){
        this.lApi = lApi;

        this.membershipState = membershipState;
        this.permissions = permissions;
        this.teamId = teamId;
        this.user = user;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link TeamMember}
     * @throws InvalidDataException if {@link #MEMBERSHIP_STATE_KEY}, {@link #PERMISSIONS_KEY}, {@link #TEAM_ID_KEY} or {@link #USER_KEY} are missing or null
     */
    public static @NotNull TeamMember fromData(final @NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        Number memberShipStateNumber = (Number) data.get(MEMBERSHIP_STATE_KEY);
        ArrayList<Objects> permissionArray = (ArrayList<Objects>) data.get(PERMISSIONS_KEY);
        String teamId = (String) data.get(TEAM_ID_KEY);
        Data userData = (Data) data.get(USER_KEY);

        if(memberShipStateNumber == null || permissionArray == null || teamId == null || userData == null){
            InvalidDataException exception = new InvalidDataException(data, "Invalid Data in " + TeamMember.class.getSimpleName());

            if(memberShipStateNumber == null) exception.addMissingFields(MEMBERSHIP_STATE_KEY);
            if(permissionArray == null) exception.addMissingFields(PERMISSIONS_KEY);
            if(teamId == null) exception.addMissingFields(TEAM_ID_KEY);
            if(userData == null) exception.addMissingFields(USER_KEY);

            throw exception;
        }

        String[] permissions = new String[permissionArray.size()];
        int i = 0;
        for(Object o : permissionArray)
            permissions[i++] = (String) o;

        return new TeamMember(lApi, MembershipState.fromValue(memberShipStateNumber.intValue()), permissions, Snowflake.fromString(teamId), User.fromData(lApi, userData));
    }

    /**
     * the user's membership state on the team
     */
    public @NotNull MembershipState getMembershipState() {
        return membershipState;
    }

    /**
     * will always be ["*"]
     */
    public @NotNull String[] getPermissions() {
        return permissions;
    }

    /**
     * the id as {@link Snowflake} of the parent team of which they are a member
     */
    public @NotNull Snowflake getTeamIdAsSnowflake() {
        return teamId;
    }

    /**
     * the id as {@link String} of the parent team of which they are a member
     */
    public @NotNull String getTeamId() {
        return teamId.asString();
    }

    /**
     * partial user object. the avatar, discriminator, id, and username of the user
     */
    public @NotNull User getUser() {
        return user;
    }

    /**
     * Generate {@link Data} from this {@link TeamMember}
     */
    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(MEMBERSHIP_STATE_KEY, membershipState);
        data.add(PERMISSIONS_KEY, permissions);
        data.add(TEAM_ID_KEY, teamId);
        data.add(USER_KEY, user);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
