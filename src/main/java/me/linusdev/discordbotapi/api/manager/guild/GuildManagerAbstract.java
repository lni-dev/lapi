package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.manager.Manager;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import org.jetbrains.annotations.Nullable;

public interface GuildManagerAbstract extends Manager {

    @Nullable Guild getGuildById(@Nullable String guildId);



}
