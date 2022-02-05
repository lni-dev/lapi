package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.guild.enums.*;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Guild implements GuildAbstract{

    protected @NotNull LApi lApi;

    protected @NotNull Snowflake id;
    protected @NotNull String name;
    protected @Nullable String iconHash;
    protected @Nullable String splashHash;
    protected @Nullable String discoverySplashHash;
    protected @Nullable Boolean owner;
    protected @NotNull Snowflake ownerId;
    protected @Nullable String permissions; //TODO PermissionsList
    protected @Nullable @Deprecated VoiceRegion region;
    protected @Nullable Snowflake afkChannelId;
    protected int afkTimeout;
    protected @Nullable Boolean widgetEnabled;
    protected @Nullable Snowflake widgetChannelId;
    protected @NotNull VerificationLevel verificationLevel;
    protected @NotNull DefaultMessageNotificationLevel defaultMessageNotifications;
    protected @NotNull ExplicitContentFilterLevel explicitContentFilter;
    protected @NotNull Role[] roles;
    protected @NotNull EmojiObject[] emojis;
    protected @Nullable String[] features; //see GuildFeature
    protected @NotNull MFALevel mfaLevel;
    protected @Nullable Snowflake applicationId;
    protected @NotNull Snowflake systemChannelId;
    protected int systemChannelFlagsAsInt;
    protected @NotNull SystemChannelFlag[] systemChannelFlags;
    protected @Nullable Snowflake rulesChannelId;
    protected @Nullable Integer maxPresences;
    protected @Nullable Integer maxMembers;
    protected @Nullable String vanityUrlCode;
    protected @Nullable String description;
    protected @Nullable String bannerHash;
    protected @Nullable PremiumTier premiumTier;
    protected @Nullable Integer premiumSubscriptionCount;
    protected @NotNull String preferredLocale;
    protected @Nullable Snowflake publicUpdatesChannelId;
    protected @Nullable Integer maxVideoChannelUsers;
    protected @Nullable Integer approximateMemberCount;
    protected @Nullable Integer approximatePresenceCount;
    protected @Nullable WelcomeScreen welcomeScreen;
    protected @NotNull GuildNsfwLevel nsfwLevel;
    protected @Nullable Sticker[] stickers;

    public Guild(
            @NotNull LApi lApi, @NotNull Snowflake id, @NotNull String name,
            @Nullable String iconHash, @Nullable String splashHash, @Nullable String discoverySplashHash,
            @Nullable Boolean owner, @NotNull Snowflake ownerId, @Nullable String permissions,
            @Nullable VoiceRegion region, @Nullable Snowflake afkChannelId, int afkTimeout,
            @Nullable Boolean widgetEnabled, @Nullable Snowflake widgetChannelId,
            @NotNull VerificationLevel verificationLevel,
            @NotNull DefaultMessageNotificationLevel defaultMessageNotifications,
            @NotNull ExplicitContentFilterLevel explicitContentFilter, @NotNull Role[] roles,
            @NotNull EmojiObject[] emojis, @Nullable String[] features, @NotNull MFALevel mfaLevel,
            @Nullable Snowflake applicationId, @NotNull Snowflake systemChannelId, int systemChannelFlagsAsInt,
            @NotNull SystemChannelFlag[] systemChannelFlags, @Nullable Snowflake rulesChannelId,
            @Nullable Integer maxPresences, @Nullable Integer maxMembers, @Nullable String vanityUrlCode,
            @Nullable String description, @Nullable String bannerHash, @Nullable PremiumTier premiumTier,
            @Nullable Integer premiumSubscriptionCount, @NotNull String preferredLocale,
            @Nullable Snowflake publicUpdatesChannelId, @Nullable Integer maxVideoChannelUsers,
            @Nullable Integer approximateMemberCount, @Nullable Integer approximatePresenceCount,
            @Nullable WelcomeScreen welcomeScreen, @NotNull GuildNsfwLevel nsfwLevel, @Nullable Sticker[] stickers
    ) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.iconHash = iconHash;
        this.splashHash = splashHash;
        this.discoverySplashHash = discoverySplashHash;
        this.owner = owner;
        this.ownerId = ownerId;
        this.permissions = permissions;
        this.region = region;
        this.afkChannelId = afkChannelId;
        this.afkTimeout = afkTimeout;
        this.widgetEnabled = widgetEnabled;
        this.widgetChannelId = widgetChannelId;
        this.verificationLevel = verificationLevel;
        this.defaultMessageNotifications = defaultMessageNotifications;
        this.explicitContentFilter = explicitContentFilter;
        this.roles = roles;
        this.emojis = emojis;
        this.features = features;
        this.mfaLevel = mfaLevel;
        this.applicationId = applicationId;
        this.systemChannelId = systemChannelId;
        this.systemChannelFlagsAsInt = systemChannelFlagsAsInt;
        this.systemChannelFlags = systemChannelFlags;
        this.rulesChannelId = rulesChannelId;
        this.maxPresences = maxPresences;
        this.maxMembers = maxMembers;
        this.vanityUrlCode = vanityUrlCode;
        this.description = description;
        this.bannerHash = bannerHash;
        this.premiumTier = premiumTier;
        this.premiumSubscriptionCount = premiumSubscriptionCount;
        this.preferredLocale = preferredLocale;
        this.publicUpdatesChannelId = publicUpdatesChannelId;
        this.maxVideoChannelUsers = maxVideoChannelUsers;
        this.approximateMemberCount = approximateMemberCount;
        this.approximatePresenceCount = approximatePresenceCount;
        this.welcomeScreen = welcomeScreen;
        this.nsfwLevel = nsfwLevel;
        this.stickers = stickers;
    }

    static @Nullable Guild fromData(@NotNull LApi lApi, @Nullable Data data){
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        //TODO
        return new Guild(lApi,
                Snowflake.fromString(id),
                name,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public Data getData() {

        Data data = new Data(1);
        //TODO
        return data;
    }
}
