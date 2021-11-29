package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.updatable.Updatable;
import me.linusdev.discordbotapi.api.manager.*;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.guild.enums.*;
import me.linusdev.discordbotapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.stage.StageInstance;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.toodo.Role;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdatableGuild extends Guild implements Datable, HasLApi, SnowflakeAble, Updatable {

    //Create Guild
    private @Nullable ISO8601Timestamp joinedAt;
    private @Nullable Boolean large;
    private @Nullable Boolean unavailable;
    private @Nullable Integer memberCount;
    private @Nullable VoiceStatesManager voiceStatesManager;
    private @Nullable MembersManager membersManager;
    private @Nullable ChannelsManager channelsManager;
    private @Nullable ThreadsManager threadsManager;
    private @Nullable PresencesManager presencesManager;
    private @Nullable StageInstance[] stageInstances;
    private @NotNull GuildScheduledEvent[] guildScheduledEvents;


    public UpdatableGuild(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull String name, @Nullable String iconHash, @Nullable String splashHash, @Nullable String discoverySplashHash, @Nullable Boolean owner, @NotNull Snowflake ownerId, @Nullable String permissions, @Nullable VoiceRegion region, @Nullable Snowflake afkChannelId, int afkTimeout, @Nullable Boolean widgetEnabled, @Nullable Snowflake widgetChannelId, @NotNull VerificationLevel verificationLevel, @NotNull DefaultMessageNotificationLevel defaultMessageNotifications, @NotNull ExplicitContentFilterLevel explicitContentFilter, @NotNull Role[] roles, @NotNull EmojiObject[] emojis, @Nullable String[] features, @NotNull MFALevel mfaLevel, @Nullable Snowflake applicationId, @NotNull Snowflake systemChannelId, int systemChannelFlagsAsInt, @NotNull SystemChannelFlag[] systemChannelFlags, @Nullable Snowflake rulesChannelId, @Nullable Integer maxPresences, @Nullable Integer maxMembers, @Nullable String vanityUrlCode, @Nullable String description, @Nullable String banner, @Nullable PremiumTier premiumTier, @Nullable Integer premiumSubscriptionCount, @NotNull String preferredLocale, @Nullable Snowflake publicUpdatesChannelId, @Nullable Integer maxVideoChannelUsers, @Nullable Integer approximateMemberCount, @Nullable Integer approximatePresenceCount, @Nullable WelcomeScreen welcomeScreen, @NotNull GuildNsfwLevel nsfwLevel, @Nullable Sticker[] stickers, @Nullable ISO8601Timestamp joinedAt, @Nullable Boolean large, @Nullable Boolean unavailable, @Nullable Integer memberCount, @Nullable VoiceStatesManager voiceStatesManager, @Nullable MembersManager membersManager, @Nullable ChannelsManager channelsManager, @Nullable ThreadsManager threadsManager, @Nullable PresencesManager presencesManager, @Nullable StageInstance[] stageInstances, @NotNull GuildScheduledEvent[] guildScheduledEvents) {
        super(lApi, id, name, iconHash, splashHash, discoverySplashHash, owner, ownerId, permissions, region, afkChannelId, afkTimeout, widgetEnabled, widgetChannelId, verificationLevel, defaultMessageNotifications, explicitContentFilter, roles, emojis, features, mfaLevel, applicationId, systemChannelId, systemChannelFlagsAsInt, systemChannelFlags, rulesChannelId, maxPresences, maxMembers, vanityUrlCode, description, banner, premiumTier, premiumSubscriptionCount, preferredLocale, publicUpdatesChannelId, maxVideoChannelUsers, approximateMemberCount, approximatePresenceCount, welcomeScreen, nsfwLevel, stickers);
        this.joinedAt = joinedAt;
        this.large = large;
        this.unavailable = unavailable;
        this.memberCount = memberCount;
        this.voiceStatesManager = voiceStatesManager;
        this.membersManager = membersManager;
        this.channelsManager = channelsManager;
        this.threadsManager = threadsManager;
        this.presencesManager = presencesManager;
        this.stageInstances = stageInstances;
        this.guildScheduledEvents = guildScheduledEvents;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public void updateSelfByData(Data data) {

    }

    @Override
    public @NotNull LApi getLApi() {
        return null;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }
}
