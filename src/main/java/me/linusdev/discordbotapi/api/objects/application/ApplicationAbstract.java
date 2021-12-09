package me.linusdev.discordbotapi.api.objects.application;

import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

public interface ApplicationAbstract extends HasLApi, Datable {

    /**
     * 	the id as {@link Snowflake} of the app
     */
    @NotNull Snowflake getIdAsSnowflake();

    /**
     * 	the id as {@link String} of the app
     */
    @NotNull default String getId() {
        return getIdAsSnowflake().asString();
    }

}
