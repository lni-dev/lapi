/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.config;

import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.ManagerFactory;
import me.linusdev.lapi.api.manager.guild.member.MemberManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStateManager;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.sticker.Sticker;
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
    private final @NotNull ManagerFactory<ListManager<EmojiObject>> emojiManagerFactory;
    private final @NotNull ManagerFactory<ListManager<Sticker>> stickerManagerFactory;
    private final @NotNull ManagerFactory<VoiceStateManager> voiceStateManagerFactory;
    private final @NotNull ManagerFactory<MemberManager> memberManagerFactory;
    private final @NotNull ManagerFactory<ListManager<Channel<?>>> channelManagerFactory;

    public Config(long flags, @NotNull Supplier<Queue<Future<?>>> queueSupplier, @NotNull String token,
                  @NotNull GatewayConfig gatewayConfig,
                  @NotNull ManagerFactory<GuildManager> guildManagerFactory,
                  @NotNull ManagerFactory<RoleManager> roleManagerFactory,
                  @NotNull ManagerFactory<ListManager<EmojiObject>> emojiManagerFactory, @NotNull ManagerFactory<ListManager<Sticker>> stickerManagerFactory, @NotNull ManagerFactory<VoiceStateManager> voiceStateManagerFactory, @NotNull ManagerFactory<MemberManager> memberManagerFactory, @NotNull ManagerFactory<ListManager<Channel<?>>> channelManagerFactory){
        this.flags = flags;
        this.token = token;

        this.queueSupplier = queueSupplier;
        this.gatewayConfig = gatewayConfig;
        this.guildManagerFactory = guildManagerFactory;
        this.roleManagerFactory = roleManagerFactory;
        this.emojiManagerFactory = emojiManagerFactory;
        this.stickerManagerFactory = stickerManagerFactory;
        this.voiceStateManagerFactory = voiceStateManagerFactory;
        this.memberManagerFactory = memberManagerFactory;
        this.channelManagerFactory = channelManagerFactory;
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

    public @NotNull ManagerFactory<ListManager<EmojiObject>> getEmojiManagerFactory() {
        return emojiManagerFactory;
    }

    public @NotNull ManagerFactory<ListManager<Sticker>> getStickerManagerFactory() {
        return stickerManagerFactory;
    }

    public @NotNull ManagerFactory<VoiceStateManager> getVoiceStateManagerFactory() {
        return voiceStateManagerFactory;
    }

    public @NotNull ManagerFactory<MemberManager> getMemberManagerFactory() {
        return memberManagerFactory;
    }

    public @NotNull ManagerFactory<ListManager<Channel<?>>> getChannelManagerFactory() {
        return channelManagerFactory;
    }
}
