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
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/topics/teams#data-models-team-object" target="_top">Team Object</a>
 */
public class Team implements Datable, HasLApi {

    public static final String ICON_KEY = "icon";
    public static final String ID_KEY = "id";
    public static final String MEMBERS_KEY = "members";
    public static final String NAME_KEY = "name";
    public static final String OWNER_USER_ID_KEY = "owner_user_id";

    /**
     * @see #getIcon()
     */
    private final @Nullable String icon;

    /**
     * @see #getId()
     * @see #getIdAsSnowflake()
     */
    private final @NotNull Snowflake id;

    /**
     * @see #getMembers()
     */
    private final @NotNull TeamMember[] members;

    /**
     * @see #getName()
     */
    private final @NotNull String name;

    /**
     * @see #getOwnerUserId()
     * @see #getOwnerUserIdAsSnowflake()
     */
    private final @NotNull Snowflake ownerUserId;


    private final @NotNull LApi lApi;

    /**
     *
     * @param icon a hash of the image of the team's icon
     * @param id the unique id of the team
     * @param members the members of the team
     * @param name the name of the team
     * @param ownerUserId the user id of the current team owner
     */
    public Team(@NotNull LApi lApi, @Nullable String icon, @NotNull Snowflake id, @NotNull TeamMember[] members, @NotNull String name, @NotNull Snowflake ownerUserId){
        this.lApi = lApi;
        this.icon = icon;
        this.id = id;
        this.members = members;
        this.name = name;
        this.ownerUserId = ownerUserId;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link Team}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #MEMBERS_KEY}, {@link #NAME_KEY} or {@link #OWNER_USER_ID_KEY} are missing
     */
    public static @NotNull Team fromData(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        String icon = (String) data.get(ICON_KEY);
        String id = (String) data.get(ID_KEY);
        List<Object> membersData = data.getList(MEMBERS_KEY);
        String name = (String) data.get(NAME_KEY);
        String ownerUserId = (String) data.get(OWNER_USER_ID_KEY);

        if(id == null || membersData == null || name == null || ownerUserId == null){
            InvalidDataException exception = new InvalidDataException(data, "Missing or null fields in " + Team.class.getSimpleName());
            if(id == null) exception.addMissingFields(ID_KEY);
            if(membersData == null) exception.addMissingFields(MEMBERS_KEY);
            if(name == null) exception.addMissingFields(NAME_KEY);
            if(ownerUserId == null) exception.addMissingFields(OWNER_USER_ID_KEY);

            throw exception;
        }

        TeamMember[] members = new TeamMember[membersData.size()];
        int i = 0;
        for(Object o : membersData)
            members[i++] = TeamMember.fromData(lApi, (SOData) o);

        return new Team(lApi, icon, Snowflake.fromString(id), members, name, Snowflake.fromString(ownerUserId));
    }

    /**
     * a hash of the image of the team's icon
     */
    public @Nullable String getIcon() {
        return icon;
    }

    /**
     * the unique id as {@link Snowflake} of the team
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the unique id as {@link String} of the team
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * the members of the team
     */
    public @NotNull TeamMember[] getMembers() {
        return members;
    }

    /**
     * the name of the team
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * the user id as {@link Snowflake} of the current team owner
     */
    public @NotNull Snowflake getOwnerUserIdAsSnowflake() {
        return ownerUserId;
    }

    /**
     * the user id as {@link String} of the current team owner
     */
    public @NotNull String getOwnerUserId() {
        return ownerUserId.asString();
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(5);

        data.add(ICON_KEY, icon);
        data.add(ID_KEY, id);
        data.add(MEMBERS_KEY, members);
        data.add(NAME_KEY, name);
        data.add(OWNER_USER_ID_KEY, ownerUserId);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
