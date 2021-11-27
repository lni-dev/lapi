package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.manager.*;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.guild.WelcomeScreen;
import me.linusdev.discordbotapi.api.objects.guild.enums.*;
import me.linusdev.discordbotapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.stage.StageInstance;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <a href="https://discord.com/developers/docs/resources/guild#guild-resource" target="_top">Guild Resource</a>
 */
public class Guild implements Datable, HasLApi, SnowflakeAble {

    private @NotNull LApi lApi;

    private @NotNull Snowflake id;
    private @NotNull String name;
    private @Nullable Icon icon;
    private @Nullable Icon iconHash;
    private @Nullable Splash splash;
    private @Nullable Splash discoverySplash;
    private @Nullable Boolean owner;
    private @NotNull Snowflake ownerId;
    private @Nullable String permissions; //TODO PermissionsList
    private @Nullable @Deprecated VoiceRegion region;
    private @Nullable Snowflake afkChannelId;
    private int afkTimeout;
    private @Nullable Boolean widgetEnabled;
    private @Nullable Snowflake widgetChannelId;
    private @NotNull VerificationLevel verificationLevel;
    private @NotNull DefaultMessageNotificationLevel defaultMessageNotifications;
    private @NotNull ExplicitContentFilterLevel explicitContentFilter;
    private @NotNull Role[] roles;
    private @NotNull EmojiObject[] emojis;
    private @Nullable String[] features; //see GuildFeature
    private @NotNull MFALevel mfaLevel;
    private @Nullable Snowflake applicationId;
    private @NotNull Snowflake systemChannelId;
    private int systemChannelFlagsAsInt;
    private @NotNull SystemChannelFlag[] systemChannelFlags;
    private @Nullable Snowflake rulesChannelId;

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

    //Other
    private @Nullable Integer maxPresences;
    private @Nullable Integer maxMembers;
    private @Nullable String vanityUrlCode;
    private @Nullable String description;
    private @Nullable Banner banner;
    private @Nullable PremiumTier premiumTier;
    private @Nullable Integer premiumSubscriptionCount;
    private @NotNull String preferredLocale;
    private @Nullable Snowflake publicUpdatesChannelId;
    private @Nullable Integer maxVideoChannelUsers;
    private @Nullable Integer approximateMemberCount;
    private @Nullable Integer approximatePresenceCount;
    private @Nullable WelcomeScreen welcomeScreen;
    private @NotNull GuildNsfwLevel nsfwLevel;
    private @Nullable Sticker[] stickers;



    public static @Nullable Guild fromData(@NotNull LApi lApi, @Nullable Data data){
        if(data == null) return null;

        return new Guild();
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public @NotNull LApi getLApi() {
        return null;
    }


}
