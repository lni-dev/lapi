package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.objects.guild.Guild;
import org.jetbrains.annotations.Nullable;

public interface GuildPool {

    @Nullable Guild getGuildById(@Nullable String guildId);
}
