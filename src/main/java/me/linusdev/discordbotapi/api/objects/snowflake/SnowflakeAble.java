package me.linusdev.discordbotapi.api.objects.snowflake;

import org.jetbrains.annotations.NotNull;

public interface SnowflakeAble {

    /**
     * the snowflake-id as {@link Snowflake}, it's recommended to use {@link #getId()} instead
     */
    @NotNull
    Snowflake getIdAsSnowflake();

    /**
     * the snowflake-id as string
     */
    @NotNull
    default String getId(){
        return getIdAsSnowflake().asString();
    }
}
