package me.linusdev.discordbotapi.api.objects.user;

import me.linusdev.discordbotapi.api.objects.Avatar;
import me.linusdev.discordbotapi.api.objects.Snowflake;
import me.linusdev.discordbotapi.api.objects.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.user.abstracts.BasicUserInformation;
import org.jetbrains.annotations.NotNull;

public class User implements BasicUserInformation, SnowflakeAble {

    public static final String USERNAME_KEY = "username";
    public static final String DISCRIMINATOR_KEY = "discriminator";
    public static final String USER_ID_KEY = "id";
    public static final String AVATAR_KEY = "avatar";

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
}
