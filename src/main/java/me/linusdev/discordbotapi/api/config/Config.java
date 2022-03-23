package me.linusdev.discordbotapi.api.config;

import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.manager.guild.GuildManager;
import me.linusdev.discordbotapi.api.manager.ManagerFactory;
import me.linusdev.discordbotapi.api.manager.guild.emoji.EmojiManager;
import me.linusdev.discordbotapi.api.manager.guild.role.RoleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.function.Supplier;

public class Config {

    private final long flags;
    private final @NotNull Supplier<Queue<Future<?>>> queueSupplier;
    private final @NotNull String token;
    private final @NotNull GatewayConfig gatewayConfig;
    private final @NotNull ManagerFactory<GuildManager> guildManagerFactory;
    private final @NotNull ManagerFactory<RoleManager> roleManagerFactory;
    private final @NotNull ManagerFactory<EmojiManager> emojiManagerFactory;

    public Config(long flags, @NotNull Supplier<Queue<Future<?>>> queueSupplier, @NotNull String token,
                  @NotNull GatewayConfig gatewayConfig,
                  @NotNull ManagerFactory<GuildManager> guildManagerFactory,
                  @NotNull ManagerFactory<RoleManager> roleManagerFactory, @NotNull ManagerFactory<EmojiManager> emojiManagerFactory){
        this.flags = flags;
        this.token = token;

        this.queueSupplier = queueSupplier;
        this.gatewayConfig = gatewayConfig;
        this.guildManagerFactory = guildManagerFactory;
        this.roleManagerFactory = roleManagerFactory;
        this.emojiManagerFactory = emojiManagerFactory;
    }

    /**
     *
     * @param flag to check, can also be more than one flag
     * @return true if all bits in flag are also set int {@link #flags}
     */
    public boolean isFlagSet(ConfigFlag flag){
        return flag.isSet(flags);
    }

    /**
     * The Queue used by {@link LApi} to queue any {@link Queueable}
     */
    public Queue<Future<?>> getNewQueue() {
        return queueSupplier.get();
    }

    public @NotNull String getToken() {
        return token;
    }

    public @NotNull GatewayConfig getGatewayConfig() {
        return gatewayConfig;
    }

    public @NotNull ManagerFactory<GuildManager> getGuildManagerFactory() {
        return guildManagerFactory;
    }

    public @NotNull ManagerFactory<RoleManager> getRoleManagerFactory() {
        return roleManagerFactory;
    }

    public @NotNull ManagerFactory<EmojiManager> getEmojiManagerFactory() {
        return emojiManagerFactory;
    }
}
