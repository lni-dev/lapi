package me.linusdev.discordbotapi.api.objects.role;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/permissions#role-object" target="_top">Role Object</a>
 */
public class Role implements Datable, SnowflakeAble {

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
    //todo

    public static @NotNull Role fromData(@NotNull Data data){
        return new Role();
    }

    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }
}
