package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import me.linusdev.discordbotapi.api.objects.toodo.Icon;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GroupDirectMessageChannelAbstract extends TextChannel, DirectMessageChannelAbstract{

    /**
     * group dm icon
     */
    @Nullable
    Icon getIcon();

    /**
     * snowflake-id of the creator of the group DM
     */
    @NotNull
    Snowflake getOwnerIdAsSnowflake();

    /**
     * id of the creator of the group DM
     */
    @NotNull
    default String getOwnerId(){
        return getOwnerIdAsSnowflake().asString();
    }

    /**
     * application snowflake id of the group DM creator if it is bot-created
     */
    @Nullable
    Snowflake getApplicationID();
}
