package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.objects.guild.Guild;
import me.linusdev.discordbotapi.api.objects.guild.CachedGuildImpl;
import org.jetbrains.annotations.Nullable;

public interface GuildPool extends Iterable<CachedGuildImpl> {

    @Nullable Guild getGuildById(@Nullable String guildId);
}
