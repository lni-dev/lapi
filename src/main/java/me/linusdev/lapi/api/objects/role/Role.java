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

package me.linusdev.lapi.api.objects.role;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.updatable.IsUpdatable;
import me.linusdev.lapi.api.interfaces.updatable.NotUpdatable;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * <p>
 *     Roles represent a set of permissions attached to a group of users. Roles have unique names, colors,
 *     and can be "pinned" to the side bar, causing their members to be listed separately. Roles are unique
 *     per guild, and can have separate permission profiles for the global context (guild) and channel context.
 *     The @everyone role has the same ID as the guild it belongs to.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/topics/permissions#role-object" target="_top">Role Object</a>
 */
public class Role implements Datable, SnowflakeAble, HasLApi, Updatable, CopyAndUpdatable<Role> {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String COLOR_KEY = "color";
    public static final String HOIST_KEY = "hoist";
    public static final String ICON_KEY = "icon";
    public static final String UNICODE_EMOJI_KEY = "unicode_emoji";
    public static final String POSITION_KEY = "position";
    public static final String PERMISSIONS_KEY = "permissions";
    public static final String MANAGED_KEY = "managed";
    public static final String MENTIONABLE_KEY = "mentionable";
    public static final String TAGS_KEY = "tags";

    @NotUpdatable
    private final @NotNull LApi lApi;

    @NotUpdatable
    private final @NotNull Snowflake id;

    private @IsUpdatable @NotNull String name;
    private @IsUpdatable int color;
    private @IsUpdatable boolean hoist;
    private @IsUpdatable @Nullable String iconHash;
    private @IsUpdatable @Nullable String unicodeEmoji;
    private @IsUpdatable int position;
    private @IsUpdatable @NotNull String permissions;
    private @IsUpdatable boolean managed;
    private @IsUpdatable boolean mentionable;
    private @IsUpdatable @Nullable RoleTags tags;
    private @IsUpdatable @Nullable Permissions permissionsAsList;

    /**
     *
     * @param lApi {@link LApi}
     * @param id role id
     * @param name role name
     * @param color integer representation of hexadecimal color code
     * @param hoist if this role is pinned in the user listing
     * @param iconHash role icon hash
     * @param unicodeEmoji role unicode emoji
     * @param position position of this role
     * @param permissions permission bit set
     * @param managed whether this role is managed by an integration
     * @param mentionable whether this role is mentionable
     * @param tags the tags this role has
     */
    public Role(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull String name, int color, boolean hoist, @Nullable String iconHash, @Nullable String unicodeEmoji, int position, @NotNull String permissions, boolean managed, boolean mentionable, @Nullable RoleTags tags) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.color = color;
        this.hoist = hoist;
        this.iconHash = iconHash;
        this.unicodeEmoji = unicodeEmoji;
        this.position = position;
        this.permissions = permissions;
        this.managed = managed;
        this.mentionable = mentionable;
        this.tags = tags;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData} with required fields
     * @return {@link Role}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY}, {@link #COLOR_KEY}, {@link #HOIST_KEY}, {@link #POSITION_KEY}, {@link #PERMISSIONS_KEY}, {@link #MANAGED_KEY} or {@link #MENTIONABLE_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null")
    @SuppressWarnings("ConstantConditions")
    public static @Nullable Role fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        Number color = (Number) data.get(COLOR_KEY);
        Boolean hoist = (Boolean) data.get(HOIST_KEY);
        String iconHash = (String) data.get(ICON_KEY);
        String unicodeEmoji = (String) data.get(UNICODE_EMOJI_KEY);
        Number position = (Number) data.get(POSITION_KEY);
        String permissions = (String) data.get(PERMISSIONS_KEY);
        Boolean managed = (Boolean) data.get(MANAGED_KEY);
        Boolean mentionable = (Boolean) data.get(MENTIONABLE_KEY);
        SOData tagsData = (SOData) data.get(TAGS_KEY);

        if(id == null || name == null || color == null
                || hoist == null || position == null || permissions == null || managed == null || mentionable == null){
            InvalidDataException.throwException(data, null, Role.class,
                    new Object[]{id, name, color, hoist, position, permissions, managed, mentionable},
                    new String[]{ID_KEY, NAME_KEY, COLOR_KEY, HOIST_KEY, POSITION_KEY, PERMISSIONS_KEY, MANAGED_KEY, MENTIONABLE_KEY});
        }

        return new Role(lApi, Snowflake.fromString(id), name, color.intValue(), hoist, iconHash, unicodeEmoji, position.intValue(), permissions, managed, mentionable, RoleTags.fromData(tagsData));
    }

    /**
     * role id as {@link Snowflake}
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * role name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * integer representation of hexadecimal color code
     */
    public int getColor() {
        return color;
    }

    /**
     * if this role is pinned in the user listing
     */
    public boolean isHoist() {
        return hoist;
    }

    /**
     * 	role icon hash
     */
    public @Nullable String getIconHash() {
        return iconHash;
    }

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofRoleIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the icon
     */
    public @NotNull CDNImageRetriever getIcon(int desiredSize, @NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This role object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofRoleIcon(lApi, getId(), getIconHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofRoleIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the icon
     */
    public @NotNull CDNImageRetriever getIcon(@NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This role object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofRoleIcon(lApi, getId(), getIconHash(), fileType));
    }

    /**
     * role unicode emoji
     */
    public @Nullable String getUnicodeEmoji() {
        return unicodeEmoji;
    }

    /**
     * position of this role
     */
    public int getPosition() {
        return position;
    }

    /**
     * permission bit set. String representation of a number.
     * @see #getPermissions()
     */
    public String getPermissionsAsString() {
        return permissionsAsList == null ? permissions : permissionsAsList.getValueAsString();
    }

    /**
     * <p>
     *     returned {@link Permissions} is backed by this {@link Role} object.
     *     Changes on the returned {@link Permissions} will also change the return value of {@link #getPermissionsAsString()}.
     * </p>
     *
     * @return the permissions as {@link Permissions}
     */
    public Permissions getPermissions() {
        if(permissionsAsList == null)
            permissionsAsList = Permissions.ofString(permissions);
        return permissionsAsList;
    }

    /**
     * whether this role is managed by an integration
     */
    public boolean isManaged() {
        return managed;
    }

    /**
     * whether this role is mentionable
     */
    public boolean isMentionable() {
        return mentionable;
    }

    /**
     * the tags this role has
     */
    public @Nullable RoleTags getTags() {
        return tags;
    }

    /**
     *
     * @return {@link SOData} for this {@link Role}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(11);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(COLOR_KEY, color);
        data.add(HOIST_KEY, hoist);
        data.addIfNotNull(ICON_KEY, iconHash);
        data.addIfNotNull(UNICODE_EMOJI_KEY, unicodeEmoji);
        data.add(POSITION_KEY, position);
        data.add(PERMISSIONS_KEY, permissions);
        data.add(MANAGED_KEY, managed);
        data.add(MENTIONABLE_KEY, mentionable);
        data.addIfNotNull(TAGS_KEY, tags);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        try{
            data.processIfContained(NAME_KEY, (String name) -> this.name = Objects.requireNonNull(name));
            data.processIfContained(COLOR_KEY, (Number color) -> this.color = Objects.requireNonNull(color).intValue());
            data.processIfContained(HOIST_KEY, (Boolean hoist) -> this.hoist = Objects.requireNonNull(hoist));
            data.processIfContained(ICON_KEY, (String hash) -> this.iconHash = hash);
            data.processIfContained(UNICODE_EMOJI_KEY, (String emoji) -> this.unicodeEmoji = emoji);
            data.processIfContained(POSITION_KEY, (Number pos) -> this.position = Objects.requireNonNull(pos).intValue());
            data.processIfContained(PERMISSIONS_KEY, (String permissions) -> {
                this.permissions = Objects.requireNonNull(permissions);
                if(permissionsAsList != null) permissionsAsList.set(permissions);
            });
            data.processIfContained(MANAGED_KEY, (Boolean managed) -> this.managed = Objects.requireNonNull(managed));
            data.processIfContained(MENTIONABLE_KEY, (Boolean mentionable) -> this.mentionable = Objects.requireNonNull(mentionable));
            data.processIfContained(TAGS_KEY, (SOData d) -> this.tags = RoleTags.fromData(Objects.requireNonNull(d)));

        }catch (NullPointerException e){
            throw new InvalidDataException(data, "NotNull field is set to null!", e);
        }
    }

    @Override
    public @NotNull Role copy() {
        return new Role(lApi, id, name, color, hoist, iconHash, unicodeEmoji, position, permissions, managed, mentionable, Copyable.copy(tags));
    }
}
