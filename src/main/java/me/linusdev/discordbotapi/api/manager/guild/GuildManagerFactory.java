package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import org.jetbrains.annotations.NotNull;

public interface GuildManagerFactory<M extends GuildManager>{

    @NotNull
    M newInstance(LApiImpl lApi);
}
