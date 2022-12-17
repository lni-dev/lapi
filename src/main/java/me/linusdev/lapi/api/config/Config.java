/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitedQueueCheckerFactory;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.manager.command.provider.CommandProvider;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.ManagerFactory;
import me.linusdev.lapi.api.manager.guild.member.MemberManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.guild.scheduledevent.GuildScheduledEventManager;
import me.linusdev.lapi.api.manager.guild.thread.ThreadManager;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStateManager;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.manager.presence.PresenceManager;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.stage.StageInstance;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.function.Supplier;

public class Config {

    private final long flags;

    private final @NotNull String token;
    private final @Nullable Snowflake applicationId;
    private final @NotNull ApiVersion apiVersion;
    private final long maxShutdownTime;
    private final @NotNull GatewayConfig gatewayConfig;

    //Queue
    private final boolean debugRateLimitBuckets;
    private final long globalHttpRateLimitRetryLimit;
    private final long httpRateLimitAssumedBucketLimit;
    private final int bucketsCheckAmount;
    private final long assumedBucketMaxLifeTime;
    private final long bucketMaxLastUsedTime;
    private final long minTimeBetweenChecks;
    private final int bucketQueueCheckSize;

    private final @NotNull RateLimitedQueueCheckerFactory bucketQueueCheckerFactory;
    private final @NotNull Supplier<Queue<QueueableFuture<?>>> queueSupplier;

    //Other
    private final @NotNull CommandProvider commandProvider;
    private final @NotNull ManagerFactory<GuildManager> guildManagerFactory;
    private final @NotNull ManagerFactory<RoleManager> roleManagerFactory;
    private final @NotNull ManagerFactory<ListManager<EmojiObject>> emojiManagerFactory;
    private final @NotNull ManagerFactory<ListManager<Sticker>> stickerManagerFactory;
    private final @NotNull ManagerFactory<VoiceStateManager> voiceStateManagerFactory;
    private final @NotNull ManagerFactory<MemberManager> memberManagerFactory;
    private final @NotNull ManagerFactory<ListManager<Channel>> channelManagerFactory;
    private final @NotNull ManagerFactory<ThreadManager> threadsManagerFactory;
    private final @NotNull ManagerFactory<PresenceManager> presenceManagerFactory;
    private final @NotNull ManagerFactory<ListManager<StageInstance>> stageInstanceManagerFactory;
    private final @NotNull ManagerFactory<GuildScheduledEventManager> guildScheduledEventManagerFactory;

    public Config(long flags, long globalHttpRateLimitRetryLimit, long httpRateLimitAssumedBucketLimit, @NotNull Supplier<Queue<QueueableFuture<?>>> queueSupplier, @NotNull String token,
                  @Nullable Snowflake applicationId, @NotNull ApiVersion apiVersion, long maxShutdownTime, @NotNull GatewayConfig gatewayConfig,
                  boolean debugRateLimitBuckets, int bucketsCheckAmount, long assumedBucketMaxLifeTime, long bucketMaxLastUsedTime, long minTimeBetweenChecks, int bucketQueueCheckSize, @NotNull RateLimitedQueueCheckerFactory bucketQueueCheckerFactory, @NotNull CommandProvider commandProvider, @NotNull ManagerFactory<GuildManager> guildManagerFactory,
                  @NotNull ManagerFactory<RoleManager> roleManagerFactory,
                  @NotNull ManagerFactory<ListManager<EmojiObject>> emojiManagerFactory, @NotNull ManagerFactory<ListManager<Sticker>> stickerManagerFactory, @NotNull ManagerFactory<VoiceStateManager> voiceStateManagerFactory, @NotNull ManagerFactory<MemberManager> memberManagerFactory, @NotNull ManagerFactory<ListManager<Channel>> channelManagerFactory, @NotNull ManagerFactory<ThreadManager> threadsManagerFactory, @NotNull ManagerFactory<PresenceManager> presenceManagerFactory, @NotNull ManagerFactory<ListManager<StageInstance>> stageInstanceManagerFactory, @NotNull ManagerFactory<GuildScheduledEventManager> guildScheduledEventManagerFactory){
        this.flags = flags;
        this.globalHttpRateLimitRetryLimit = globalHttpRateLimitRetryLimit;
        this.httpRateLimitAssumedBucketLimit = httpRateLimitAssumedBucketLimit;
        this.token = token;

        this.queueSupplier = queueSupplier;
        this.applicationId = applicationId;
        this.apiVersion = apiVersion;
        this.maxShutdownTime = maxShutdownTime;
        this.gatewayConfig = gatewayConfig;
        this.debugRateLimitBuckets = debugRateLimitBuckets;
        this.bucketsCheckAmount = bucketsCheckAmount;
        this.assumedBucketMaxLifeTime = assumedBucketMaxLifeTime;
        this.bucketMaxLastUsedTime = bucketMaxLastUsedTime;
        this.minTimeBetweenChecks = minTimeBetweenChecks;
        this.bucketQueueCheckSize = bucketQueueCheckSize;
        this.bucketQueueCheckerFactory = bucketQueueCheckerFactory;
        this.commandProvider = commandProvider;
        this.guildManagerFactory = guildManagerFactory;
        this.roleManagerFactory = roleManagerFactory;
        this.emojiManagerFactory = emojiManagerFactory;
        this.stickerManagerFactory = stickerManagerFactory;
        this.voiceStateManagerFactory = voiceStateManagerFactory;
        this.memberManagerFactory = memberManagerFactory;
        this.channelManagerFactory = channelManagerFactory;
        this.threadsManagerFactory = threadsManagerFactory;
        this.presenceManagerFactory = presenceManagerFactory;
        this.stageInstanceManagerFactory = stageInstanceManagerFactory;
        this.guildScheduledEventManagerFactory = guildScheduledEventManagerFactory;
    }

    /**
     *
     * @param flag to check, can also be more than one flag
     * @return true if all bits in flag are also set int {@link #flags}
     */
    public boolean isFlagSet(ConfigFlag flag){
        return flag.isSet(flags);
    }

    public boolean isDebugRateLimitBucketsEnabled() {
        return debugRateLimitBuckets;
    }

    public long getGlobalHttpRateLimitRetryLimit() {
        return globalHttpRateLimitRetryLimit;
    }

    public long getHttpRateLimitAssumedBucketLimit() {
        return httpRateLimitAssumedBucketLimit;
    }

    /**
     * The Queue used by {@link LApi} to queue any {@link Queueable}
     */
    public Queue<QueueableFuture<?>> getNewQueue() {
        return queueSupplier.get();
    }

    public int getBucketsCheckAmount() {
        return bucketsCheckAmount;
    }

    public long getAssumedBucketMaxLifeTime() {
        return assumedBucketMaxLifeTime;
    }

    public long getBucketMaxLastUsedTime() {
        return bucketMaxLastUsedTime;
    }

    public long getMinTimeBetweenChecks() {
        return minTimeBetweenChecks;
    }

    public int getBucketQueueCheckSize() {
        return bucketQueueCheckSize;
    }

    public @NotNull RateLimitedQueueCheckerFactory getBucketQueueCheckerFactory() {
        return bucketQueueCheckerFactory;
    }

    public @NotNull String getToken() {
        return token;
    }

    public @Nullable Snowflake getApplicationId() {
        return applicationId;
    }

    public @NotNull ApiVersion getApiVersion() {
        return apiVersion;
    }

    public @NotNull GatewayConfig getGatewayConfig() {
        return gatewayConfig;
    }

    public @NotNull CommandProvider getCommandProvider() {
        return commandProvider;
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

    public @NotNull ManagerFactory<ListManager<Channel>> getChannelManagerFactory() {
        return channelManagerFactory;
    }

    public @NotNull ManagerFactory<ThreadManager> getThreadsManagerFactory() {
        return threadsManagerFactory;
    }

    public @NotNull ManagerFactory<PresenceManager> getPresenceManagerFactory() {
        return presenceManagerFactory;
    }

    public @NotNull ManagerFactory<ListManager<StageInstance>> getStageInstanceManagerFactory() {
        return stageInstanceManagerFactory;
    }

    public @NotNull ManagerFactory<GuildScheduledEventManager> getGuildScheduledEventManagerFactory() {
        return guildScheduledEventManagerFactory;
    }

    public long getMaxShutdownTime() {
        return maxShutdownTime;
    }
}
