package me.linusdev.discordbotapi.api.objects.emoji;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.toodo.Role;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/resources/emoji#emoji-resource" target="_top">Emoji Resource</a>
 */
public class EmojiObject implements Datable, me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji, HasLApi {

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
    private final @Nullable String name;

    /**
     * @see #getRoles()
     */
    private final @Nullable Role[] roles;

    /**
     * @see #getUser()
     */
    private final @Nullable User user;

    /**
     * @see #getRequireColons()
     */
    private final @Nullable Boolean requireColons;

    /**
     * @see #getManaged()
     */
    private final @Nullable Boolean managed;

    /**
     * @see #isAnimated()
     * @see #getAnimated()
     */
    private final @Nullable Boolean animated;

    /**
     * @see #isAvailable()
     * @see #getAvailable()
     */
    private final @Nullable Boolean available;

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
    public static @NotNull EmojiObject fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);

        //id and name may not both be null at the same time
        if(id == null && name == null)
            throw new InvalidDataException(data, "Cannot create a Emoji, where " + ID_KEY + " and " + NAME_KEY + " are missing or null.");

        Role[] roles = null;
        ArrayList<Object> rolesData = (ArrayList<Object>) data.get(ROLES_KEY);

        if(rolesData != null){
            roles = new Role[rolesData.size()];
            int i = 0;
            for(Object o : rolesData) roles[i++] = Role.fromData((Data) o);
        }

        Data userData = (Data) data.get(USER_KEY);
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
     * Generates a {@link Data} from this {@link EmojiObject}
     * <br><br>
     * This is probably useless, but it's here anyways
     */
    @Override
    public Data getData() {
        Data data = new Data(0);

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

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
