package me.linusdev.discordbotapi.api.objects.sticker;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.HasLApi;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * <p>
 *     Represents a sticker that can be sent in messages
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/sticker#sticker-object" target="_top">
 *          Sticker Object
 *      </a>
 * @see StickerItem
 */
public class Sticker implements Datable, HasLApi {

    public static final String ID_KEY = "id";
    public static final String PACK_ID_KEY = "pack_id";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String TAGS_KEY = "tags";
    public static final String ASSET_KEY = "asset";
    public static final String TYPE_KEY = "type";
    public static final String FORMAT_TYPE_KEY = "format_type";
    public static final String AVAILABLE_KEY = "available";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String USER_KEY = "user";
    public static final String SORT_VALUE_KEY = "sort_value";

    private final @NotNull Snowflake id;
    private final @Nullable Snowflake packId;
    private final @NotNull String name;
    private final @Nullable String description;
    private final @NotNull String tags;
    private final @Deprecated String asset;
    private final @NotNull StickerType type;
    private final @NotNull StickerFormatType formatType;
    private final @Nullable Boolean available;
    private final @Nullable Snowflake guildId;
    private final @Nullable User user;
    private final @Nullable Integer sortValue;

    private final @NotNull LApi lApi;

    /**
     *
     * @param id id of the sticker
     * @param packId for standard stickers, id of the pack the sticker is from
     * @param name name of the sticker
     * @param description description of the sticker
     * @param tags autocomplete/suggestion tags for the sticker (max 200 characters)
     * @param asset <b>Deprecated</b> previously the sticker asset hash, now an empty string
     * @param type {@link StickerType type of sticker}
     * @param formatType {@link StickerFormatType type of sticker format}
     * @param available whether this guild sticker can be used, may be false due to loss of Server Boosts
     * @param guildId id of the guild that owns this sticker
     * @param user the user that uploaded the guild sticker
     * @param sort_value the standard sticker's sort order within its pack
     */
    public Sticker(@NotNull LApi lApi, @NotNull Snowflake id, @Nullable Snowflake packId, @NotNull String name, @Nullable String description, @NotNull String tags, String asset, @NotNull StickerType type, @NotNull StickerFormatType formatType, @Nullable Boolean available, @Nullable Snowflake guildId, @Nullable User user, @Nullable Integer sort_value) {
        this.lApi = lApi;
        this.id = id;
        this.packId = packId;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.asset = asset;
        this.type = type;
        this.formatType = formatType;
        this.available = available;
        this.guildId = guildId;
        this.user = user;
        this.sortValue = sort_value;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link Sticker}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY}, {@link #TAGS_KEY}, {@link #TYPE_KEY} or {@link #FORMAT_TYPE_KEY} is null or missing
     */
    public static @NotNull Sticker fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        String id = (String) data.get(ID_KEY);
        String packId = (String) data.get(PACK_ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        String tags = (String) data.get(TAGS_KEY);
        String asset = (String) data.get(ASSET_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        Number formatType = (Number) data.get(FORMAT_TYPE_KEY);
        Boolean available = (Boolean) data.get(AVAILABLE_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        Data user = (Data) data.get(USER_KEY);
        Number sortValue = (Number) data.get(SORT_VALUE_KEY);

        if(id == null || name == null || tags == null || type == null || formatType == null){
            InvalidDataException.throwException(data, null, Sticker.class,
                    new Object[]{id, name, tags, type, formatType},
                    new String[]{ID_KEY, NAME_KEY, TAGS_KEY, TYPE_KEY, FORMAT_TYPE_KEY});
            return null; //this will never happen, because above method will throw an exception
        }

        return new Sticker(lApi, Snowflake.fromString(id), Snowflake.fromString(packId), name, description,
                tags, asset, StickerType.fromValue(type.intValue()), StickerFormatType.fromValue(formatType.intValue()),
                available, Snowflake.fromString(guildId), user == null ? null : User.fromData(lApi, data),
                sortValue == null ? null : sortValue.intValue());
    }

    /**
     * 	id as {@link Snowflake} of the sticker
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * 	id as {@link String} of the sticker
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * for standard stickers, id as {@link Snowflake} of the pack the sticker is from
     */
    public @Nullable Snowflake getPackIdAsSnowflake() {
        return packId;
    }

    /**
     * for standard stickers, id as {@link String} of the pack the sticker is from
     */
    public @Nullable String getPackId() {
        if(packId == null) return null;
        return packId.asString();
    }

    /**
     * name of the sticker
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * 	description of the sticker
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * autocomplete/suggestion tags for the sticker (max 200 characters)
     * <p>
     *     A comma separated list of keywords is the format used in this field by standard stickers, but this is just a convention. Incidentally the client will always use a name generated from an emoji as the value of this field when creating or modifying a guild sticker.
     * </p>
     */
    public @NotNull String getTags() {
        return tags;
    }

    /**
     * 	<b>Deprecated</b> previously the sticker asset hash, now an empty string
     */
    @Deprecated
    public @Nullable String getAsset() {
        return asset;
    }

    /**
     * {@link StickerType type of sticker}
     */
    public @NotNull StickerType getType() {
        return type;
    }

    /**
     * {@link StickerFormatType type of sticker format}
     */
    public @NotNull StickerFormatType getFormatType() {
        return formatType;
    }

    /**
     * whether this guild sticker can be used, may be false due to loss of Server Boosts
     */
    public @Nullable Boolean getAvailable() {
        return available;
    }

    /**
     * id as {@link Snowflake} of the guild that owns this sticker
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * id as {@link String} of the guild that owns this sticker
     */
    public @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
    }

    /**
     * the user that uploaded the guild sticker
     */
    public @Nullable User getUser() {
        return user;
    }

    /**
     * the standard sticker's sort order within its pack
     */
    public @Nullable Integer getSortValue() {
        return sortValue;
    }

    /**
     *
     * @return {@link Data} for this {@link Sticker}
     */
    @Override
    public Data getData() {
        Data data = new Data(7);

        data.add(ID_KEY, id);
        if(packId != null) data.add(PACK_ID_KEY, packId);
        data.add(NAME_KEY, name);
        data.add(DESCRIPTION_KEY, description);
        data.add(TAGS_KEY, tags);
        data.add(ASSET_KEY, getClass() == null ? "" : asset);
        data.add(TYPE_KEY, type);
        data.add(FORMAT_TYPE_KEY, formatType);
        if(available != null) data.add(AVAILABLE_KEY, available);
        if(guildId != null) data.add(GUILD_ID_KEY, guildId);
        if(user != null) data.add(USER_KEY, user);
        if(sortValue != null) data.add(SORT_VALUE_KEY, sortValue);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
