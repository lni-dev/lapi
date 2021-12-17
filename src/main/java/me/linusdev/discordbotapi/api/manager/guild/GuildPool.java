package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.objects.guild.GuildAbstract;
import org.jetbrains.annotations.Nullable;

public interface GuildPool {

    @Nullable GuildAbstract getGuildById(@Nullable String guildId);
}
