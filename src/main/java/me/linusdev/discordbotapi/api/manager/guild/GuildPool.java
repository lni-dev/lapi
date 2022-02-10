package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.objects.guild.GuildAbstract;
import me.linusdev.discordbotapi.api.objects.guild.UpdatableGuild;
import org.jetbrains.annotations.Nullable;

public interface GuildPool extends Iterable<UpdatableGuild> {

    @Nullable GuildAbstract getGuildById(@Nullable String guildId);
}
