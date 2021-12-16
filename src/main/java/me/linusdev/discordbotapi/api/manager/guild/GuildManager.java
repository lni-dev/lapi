package me.linusdev.discordbotapi.api.manager.guild;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class GuildManager implements GuildManagerAbstract{

    private final @NotNull LApi lApi;

    private final @NotNull HashMap<String, Guild> guilds;

    public GuildManager(@NotNull LApi lApi){
        this.lApi = lApi;
        guilds = new HashMap<>();
    }

    public void addGuild(Guild guild){
        guilds.put(guild.getId(), guild);
    }

    @Override
    public @Nullable Guild getGuildById(@Nullable String guildId) {
        if(guildId == null) return null;
        return guilds.get(guildId);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
