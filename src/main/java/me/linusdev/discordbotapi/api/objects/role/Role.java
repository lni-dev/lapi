package me.linusdev.discordbotapi.api.objects.role;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImage;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.discordbotapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.file.types.AbstractFileType;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.permission.Permission;
import me.linusdev.discordbotapi.api.objects.permission.Permissions;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
public class Role implements Datable, SnowflakeAble, HasLApi {

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

    private final @NotNull LApi lApi;
    private final @NotNull Snowflake id;
    private final @NotNull String name;
    private final int color;
    private final boolean hoist;
    private final @Nullable String iconHash;
    private final @Nullable String unicodeEmoji;
    private final  int position;
    private final @NotNull String permissions;
    private final boolean managed;
    private final boolean mentionable;
    private final @Nullable RoleTags tags;

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
     * @param data {@link Data} with required fields
     * @return {@link Role}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY}, {@link #COLOR_KEY}, {@link #HOIST_KEY}, {@link #POSITION_KEY}, {@link #PERMISSIONS_KEY}, {@link #MANAGED_KEY} or {@link #MENTIONABLE_KEY} are missing or {@code null}
     */
    @SuppressWarnings("ConstantConditions")
    public static @NotNull Role fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
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
        Data tagsData = (Data) data.get(TAGS_KEY);

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
     * @param fileType see {@link CDNImage#ofRoleIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.discordbotapi.api.communication.file.types.FileType FileType}
     * @return {@link me.linusdev.discordbotapi.api.lapiandqueue.Queueable Queueable} to retrieve the icon
     */
    public @NotNull CDNImageRetriever getIcon(int desiredSize, @NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This role object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofRoleIcon(lApi, getId(), getIconHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofRoleIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.discordbotapi.api.communication.file.types.FileType FileType}
     * @return {@link me.linusdev.discordbotapi.api.lapiandqueue.Queueable Queueable} to retrieve the icon
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
        return permissions;
    }

    /**
     * the permissions as {@link Permissions}
     */
    public Permissions getPermissions() {
        return Permissions.ofString(permissions);
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
     * @return {@link Data} for this {@link Role}
     */
    @Override
    public Data getData() {
        Data data = new Data(11);

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
}
