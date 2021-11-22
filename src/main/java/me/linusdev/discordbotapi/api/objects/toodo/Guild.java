package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Guild implements Datable, HasLApi, SnowflakeAble {


    public static @Nullable Guild fromData(@NotNull LApi lApi, @Nullable Data data){
        if(data == null) return null;

        return new Guild();
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
