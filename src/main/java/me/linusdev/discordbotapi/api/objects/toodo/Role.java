package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/permissions#role-object" target="_top">Role Object</a>
 */
public class Role implements Datable, SnowflakeAble {

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
