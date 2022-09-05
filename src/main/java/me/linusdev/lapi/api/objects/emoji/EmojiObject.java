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

package me.linusdev.lapi.api.objects.emoji;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class should only be used for emojis, that do have an id.<br>
 *
 * @see <a href="https://discord.com/developers/docs/resources/emoji#emoji-resource" target="_top">Emoji Resource</a>
 */
public class EmojiObject implements SnowflakeAble, CopyAndUpdatable<EmojiObject>, Updatable, Datable, me.linusdev.lapi.api.objects.emoji.abstracts.Emoji, HasLApi {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String ROLES_KEY = "roles";
    public static final String USER_KEY = "user";
    public static final String REQUIRE_COLONS_KEY = "require_colons";
    public static final String MANAGED_KEY = "managed";
    public static final String ANIMATED_KEY = "animated";
    public static final String AVAILABLE_KEY = "available";

    /**
     * @see #getId()
     */
    private final @Nullable Snowflake id;

    /**
     * @see #getName()
     */
    private @Nullable String name;

    /**
     * @see #getRoles()
     */
    private @Nullable Role[] roles;

    /**
     * @see #getUser()
     */
    private @Nullable User user;

    /**
     * @see #getRequireColons()
     */
    private @Nullable Boolean requireColons;

    /**
     * @see #getManaged()
     */
    private @Nullable Boolean managed;

    /**
     * @see #isAnimated()
     * @see #getAnimated()
     */
    private @Nullable Boolean animated;

    /**
     * @see #isAvailable()
     * @see #getAvailable()
     */
    private @Nullable Boolean available;

    private final @NotNull LApi lApi;

    /**
     *
     * @param id emoji id
     * @param name emoji name
     * @param roles roles allowed to use this emoji
     * @param user user that created this emoji
     * @param requireColons whether this emoji must be wrapped in colons
     * @param managed whether this emoji is managed
     * @param animated whether this emoji is animated
     * @param available whether this emoji can be used, may be false due to loss of Server Boosts
     */
    public EmojiObject(@NotNull LApi lApi, @Nullable Snowflake id, @Nullable String name, @Nullable Role[] roles, @Nullable User user, @Nullable Boolean requireColons,
                       @Nullable Boolean managed, @Nullable Boolean animated, @Nullable Boolean available){
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.roles = roles;
        this.user = user;
        this.requireColons = requireColons;
        this.managed = managed;
        this.animated = animated;
        this.available = available;
    }

    /**
     *
     * @param data to create {@link EmojiObject}
     * @return {@link EmojiObject}
     * @throws InvalidDataException if {@link #ID_KEY} and {@link #NAME_KEY} are both {@code null} or missing
     */
    public static @NotNull EmojiObject fromData(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);

        //id and name may not both be null at the same time
        if(id == null && name == null)
            throw new InvalidDataException(data, "Cannot create a Emoji, where " + ID_KEY + " and " + NAME_KEY + " are missing or null.");

        Role[] roles = null;
        List<Object> rolesData = data.getList(ROLES_KEY);

        if(rolesData != null){
            roles = new Role[rolesData.size()];
            int i = 0;
            for(Object o : rolesData) roles[i++] = Role.fromData(lApi, (SOData) o);
        }

        SOData userData = (SOData) data.get(USER_KEY);
        User user = userData == null ? null : User.fromData(lApi, userData);

        return new EmojiObject(lApi, Snowflake.fromString(id), name, roles, user,
                (Boolean) data.get(REQUIRE_COLONS_KEY), (Boolean) data.get(MANAGED_KEY),
                (Boolean) data.get(ANIMATED_KEY),(Boolean) data.get(AVAILABLE_KEY));
    }

    /**
     * The id of this {@link EmojiObject} or {@code null} if this is a Standard Emoji ({@link #isStandardEmoji()})
     */
    @Override
    public @Nullable Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @NotNull String getId() {
        return SnowflakeAble.super.getId();
    }

    /**
     * name of the Emoji, or it's corresponding unicode emoji, if it is a standard emoji ({@link #isStandardEmoji()})
     */
    @Override
    public @Nullable String getName() {
        return name;
    }

    /**
     * {@link Role}s allowed to use this emoji
     */
    public @Nullable Role[] getRoles() {
        return roles;
    }

    /**
     * {@link User} that created this emoji
     */
    public @Nullable User getUser() {
        return user;
    }

    /**
     * whether this emoji must be wrapped in colons
     *
     * todo get more information about this and add requiresColons(); method
     */
    public @Nullable Boolean getRequireColons() {
        return requireColons;
    }

    /**
     * whether this emoji is managed
     *
     * todo get more information about this and add isManaged(); method
     */
    public @Nullable Boolean getManaged() {
        return managed;
    }

    /**
     * whether this emoji is animated
     */
    public @Nullable Boolean getAnimated() {
        return animated;
    }

    /**
     * whether this emoji is animated
     *
     * <br><br>
     *
     * It should be {@code false} if {@link #getAnimated()} is {@code null}, but discord docs do not contain any information about this.
     * So message me if I am wrong
     *
     * @return {@code false} if {@link #getAnimated()} is {@code null}, {@link #getAnimated()} otherwise
     */
    public boolean isAnimated(){
        return !(getAnimated() == null) && getAnimated();
    }

    /**
     * whether this emoji can be used, may be false due to loss of Server Boosts
     */
    public @Nullable Boolean getAvailable() {
        return available;
    }

    /**
     * whether this emoji can be used, may be false due to loss of Server Boosts
     *
     * <br><br>
     *
     * It should be {@code true} if {@link #getAvailable()} is {@code null}, but discord docs do not contain any information about this.
     * So message me if I am wrong
     *
     * @return {@code true} if {@link #getAvailable()} is {@code null}, {@link #getAvailable()} otherwise
     */
    public boolean isAvailable(){
        return getAvailable() == null || getAvailable();
    }

    /**
     * Generates a {@link SOData} from this {@link EmojiObject}
     * <br><br>
     * This is probably useless, but it's here anyways
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(8);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        if(roles != null) data.add(ROLES_KEY, roles);
        if(user != null) data.add(USER_KEY, user);
        if(requireColons != null) data.add(REQUIRE_COLONS_KEY, requireColons);
        if(managed != null) data.add(MANAGED_KEY, managed);
        if(animated != null) data.add(ANIMATED_KEY, animated);
        if(available != null) data.add(ANIMATED_KEY, available);

        return data;
    }

    /**
     *
     * This will compare this emoji with given {@link SOData}.
     * {@link #ID_KEY ID} and {@link #USER_KEY USER} will not be checked,
     * because these fields should not change in the first place
     *
     * @param emojiData the {@link SOData}, which should be compared to this emoji
     * @return {@code false} if given data could represent this emoji,
     * {@code true} if given {@link SOData} would change this emoji.
     */
    @ApiStatus.Internal
    @Override
    public boolean checkIfChanged(@NotNull SOData emojiData){

        //id won't be checked here, because it should never change
        //user won't be checked here, because it should not change

        if(!Objects.equals(this.name, emojiData.get(NAME_KEY))){
           return true;
        }

        if(roles != null){
            List<Object> rolesData = emojiData.getList(ROLES_KEY);
            if(rolesData == null)
                return true;

            //if rolesData is longer than roles, a new role was added
            if(rolesData.size() > roles.length) return true;

            first: for(Role role : roles){
                for(Object roleData : rolesData){
                    if(role.getId().equals(((SOData)roleData).get(Role.ID_KEY))){
                        continue first;
                    }
                }

                //we are missing a role... -> emoji was changed
                return true;
            }
        }else{
            //roles must be null to be unchanged...
            if(emojiData.get(ROLES_KEY) != null)
                return true;
        }

        return !(Objects.equals(requireColons, emojiData.get(REQUIRE_COLONS_KEY)) ||
                Objects.equals(managed, emojiData.get(MANAGED_KEY)) ||
                Objects.equals(animated, emojiData.get(ANIMATED_KEY)) ||
                Objects.equals(available, emojiData.get(AVAILABLE_KEY)));
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        //id will never change and won't be updated here

        //user should never change, but it will still be updated here.
        this.user = User.fromData(lApi, (SOData) data.get(USER_KEY));

        this.name = (String) data.get(NAME_KEY);

        List<Object> rolesData = data.getList(ROLES_KEY);
        if(rolesData != null){
            roles = new Role[rolesData.size()];
            int i = 0;
            for(Object o : rolesData) roles[i++] = Role.fromData(lApi, (SOData) o);
        }

        this.requireColons = (Boolean) data.get(REQUIRE_COLONS_KEY);
        this.managed = (Boolean) data.get(MANAGED_KEY);
        this.animated = (Boolean) data.get(ANIMATED_KEY);
        this.available = (Boolean) data.get(AVAILABLE_KEY);

    }

    /**
     * {@link #user} is not copied properly. The copied {@link EmojiObject} may have the same {@link User} object as the original
     * {@link EmojiObject}.
     * @return
     */
    @Override
    public @NotNull EmojiObject copy() {
        Role[] cRoles = null;

        if(roles != null){
            cRoles = new Role[roles.length];
            int i = 0;
            for(Role role : roles){
                cRoles[i++] = role.copy();
            }
        }

        return new EmojiObject(lApi, id, name, cRoles, user, requireColons, managed, animated, available);
    }

    @Override
    public String toString() {
        return name + " : " + id;
    }
}
