package me.linusdev.discordbotapi.api.objects.user.abstracts;

import me.linusdev.discordbotapi.api.objects.Avatar;
import me.linusdev.discordbotapi.api.objects.Snowflake;
import me.linusdev.discordbotapi.api.objects.SnowflakeAble;
import org.jetbrains.annotations.NotNull;

public interface BasicUserInformation extends SnowflakeAble {

    /**
     * the username (in front of the '#')
     */
    @NotNull
    String getUsername();

    /**
     * the Discriminator (the numbers after the '#')
     */
    @NotNull
    String getDiscriminator();

    /**
     * snowflake id of the user
     * It's easier to use {@link #getId()}
     */
    @NotNull
    Snowflake getIdAsSnowflake();

    /**
     * the id of the user as string
     */
    default @NotNull String getId(){
        return getIdAsSnowflake().asString();
    }

    /**
     * the {@link Avatar} of the user
     */
    @NotNull
    Avatar getAvatar();

}
