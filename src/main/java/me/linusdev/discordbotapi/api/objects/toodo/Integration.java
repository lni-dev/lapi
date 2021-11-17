package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.HasLApi;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#integration-object" target="_top">Integration Object</a>
 */
public class Integration implements Datable, SnowflakeAble, HasLApi {

    //TODO

    public static @NotNull Integration fromData(@NotNull LApi lApi, @NotNull Data data){
        return null;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public @NotNull LApi getLApi() {
        return null;
    }

}
