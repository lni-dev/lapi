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

package me.linusdev.lapi.api.objects.application.team;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.User;
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
     * @param data {@link SOData} with required fields
     * @return {@link TeamMember}
     * @throws InvalidDataException if {@link #MEMBERSHIP_STATE_KEY}, {@link #PERMISSIONS_KEY}, {@link #TEAM_ID_KEY} or {@link #USER_KEY} are missing or null
     */
    public static @NotNull TeamMember fromData(final @NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        Number memberShipStateNumber = (Number) data.get(MEMBERSHIP_STATE_KEY);
        ArrayList<Objects> permissionArray = (ArrayList<Objects>) data.get(PERMISSIONS_KEY);
        String teamId = (String) data.get(TEAM_ID_KEY);
        SOData userData = (SOData) data.get(USER_KEY);

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
     * Generate {@link SOData} from this {@link TeamMember}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

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
