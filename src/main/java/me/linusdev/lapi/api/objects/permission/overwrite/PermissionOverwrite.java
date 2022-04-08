/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.objects.permission.overwrite;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.math.BigInteger;
import java.util.List;

/**
 * <a href="https://discord.com/developers/docs/resources/channel#overwrite-object">Overwrite Object</a>
 *
 *
 */
public class PermissionOverwrite implements Copyable<PermissionOverwrite>, SnowflakeAble, Datable {

    public static final String ID_KEY = "id";
    public static final String TYPE_KEY = "type";
    public static final String ALLOW_KEY = "allow";
    public static final String DENY_KEY = "deny";

    public static final int ROLE = 0;
    public static final int MEMBER = 1;

    /**
     * role or user id
     */
    private @NotNull final Snowflake id;

    /**
     * either 0 ({@link #ROLE}) or 1 ({@link #MEMBER})
     */
    private final int type;

    /**
     * permission bit set
     */
    private @NotNull final String allow;

    /**
     * permission bit set
     */
    private @NotNull final String deny;


    private @Nullable Data data;


    /**
     *
     * @param id role or user id
     * @param type either 0 ({@link #ROLE}) or 1 ({@link #MEMBER})
     * @param allow {@link BigInteger} serialized into a string
     * @param deny {@link BigInteger} serialized into a string
     */
    public PermissionOverwrite(@NotNull Snowflake id, @Range(from=ROLE, to=MEMBER) int type, @NotNull String allow, @NotNull String deny){
        this.data = null;
        this.id = id;
        this.type = type;
        this.allow = allow;
        this.deny = deny;
    }

    /**
     * This creates a {@link PermissionOverwrite} from given {@link Data}
     * @param data to read from
     * @throws InvalidDataException if given {@link Data} does not match a {@link PermissionOverwrite}
     */
    public PermissionOverwrite(@NotNull Data data) throws InvalidDataException {
        this.data = data;
        this.id = Snowflake.fromString((String) data.getOrDefault(ID_KEY, null));
        this.type = ((Number)data.getOrDefault(TYPE_KEY, -1)).intValue();
        this.allow = (String) data.getOrDefault(ALLOW_KEY, "0");
        this.deny = (String) data.getOrDefault(DENY_KEY, "0");

        if(this.id == null) throw new InvalidDataException(data, "id in permission override may not be null").addMissingFields(ID_KEY);
        if(this.type == -1) throw new InvalidDataException(data, "type in permission override us unknown or unset").addMissingFields(TYPE_KEY);
    }

    @Override
    @NotNull
    public Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * either 0 ({@link #ROLE}) or 1 ({@link #MEMBER})
     */
    public int getType() {
        return type;
    }

    /**
     * @return true if this {@link PermissionOverwrite} is for a Role, false otherwise
     */
    public boolean isForRole(){
        return type == ROLE;
    }

    /**
     * @return true if this {@link PermissionOverwrite} is for a Member, false otherwise
     */
    public boolean isForMember(){
        return type == MEMBER;
    }

    @NotNull
    public String getAllowPermissionBitsAsString() {
        return allow;
    }

    @NotNull
    public String getDenyPermissionBitsAsString() {
        return deny;
    }

    /**
     * @return a {@link List} of {@link Permission} which are allowed by this {@link PermissionOverwrite}
     */
    @NotNull
    public Permissions getAllowPermissions() {
        return Permissions.ofString(allow);
    }

    /**
     * @return a {@link List} of {@link Permission} which are denied by this {@link PermissionOverwrite}
     */
    @NotNull
    public Permissions getDenyPermissions() {
        return Permissions.ofString(deny);
    }

    @Override
    public Data getData() {
        if(data != null) return data;
        data = new Data(4);

        data.add(ID_KEY, id);
        data.add(TYPE_KEY, type);
        data.add(ALLOW_KEY, allow);
        data.add(DENY_KEY, deny);

        return data;
    }

    @Override
    public @NotNull PermissionOverwrite copy() {
        return this;
    }
}
