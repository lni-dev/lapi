package me.linusdev.discordbotapi.api.objects.user;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.toodo.Avatar;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.user.abstracts.BasicUserInformation;
import org.jetbrains.annotations.NotNull;

public class User implements BasicUserInformation, SnowflakeAble, Datable {
    //todo
    public static final String USERNAME_KEY = "username";
    public static final String DISCRIMINATOR_KEY = "discriminator";
    public static final String USER_ID_KEY = "id";
    public static final String AVATAR_KEY = "avatar";

    public static @NotNull User fromData(@NotNull Data data){
        return new User();
    }

    @Override
    public @NotNull String getUsername() {
        return null;
    }

    @Override
    public @NotNull String getDiscriminator() {
        return null;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }

    @Override
    public @NotNull Avatar getAvatar() {
        return null;
    }

    @Override
    public @NotNull Data getData() {
        Data data = new Data(0);
        return data;
    }
}
