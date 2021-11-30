package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.guild.enums.*;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <a href="https://discord.com/developers/docs/resources/guild#guild-resource" target="_top">Guild Resource</a>
 */
public class Guild implements Datable, HasLApi, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String ICON_KEY = "icon";
    public static final String ICON_HASH_KEY = "icon_hash";
    public static final String SPLASH_KEY = "splash";
    public static final String DISCOVERY_SPLASH_KEY = "discovery_splash";
    public static final String OWNER_KEY = "owner";
    public static final String OWNER_ID_KEY = "owner_id";
    public static final String PERMISSIONS_KEY = "permissions";
    public static final String REGION_KEY = "region";
    public static final String AFK_CHANNEL_ID_KEY = "afk_channel_id";
    public static final String AFK_TIMEOUT_KEY = "afk_timeout";
    public static final String WIDGET_ENABLED_KEY = "widget_enabled";
    public static final String WIDGET_CHANNEL_ID_KEY = "widget_channel_id";
    public static final String VERIFICATION_LEVEL_KEY = "verification_level";
    public static final String DEFAULT_MESSAGE_NOTIFICATIONS_KEY = "default_message_notifications";
    public static final String EXPLICIT_CONTENT_FILTER_KEY = "explicit_content_filter";
    public static final String ROLES_KEY = "roles";
    public static final String EMOJIS_KEY = "emojis";
    public static final String FEATURES_KEY = "features";
    public static final String MFA_LEVEL_KEY = "mfa_level";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String SYSTEM_CHANNEL_ID_KEY = "system_channel_id";
    public static final String SYSTEM_CHANNEL_FLAGS_KEY = "system_channel_flags";
    public static final String RULES_CHANNEL_ID_KEY = "rules_channel_id";
    public static final String JOINED_AT_KEY = "joined_at";
    public static final String LARGE_KEY = "large";
    public static final String UNAVAILABLE_KEY = "unavailable";
    public static final String MEMBER_COUNT_KEY = "member_count";
    public static final String VOICE_STATES_KEY = "voice_states";
    public static final String MEMBERS_KEY = "members";
    public static final String CHANNELS_KEY = "channels";
    public static final String THREADS_KEY = "threads";
    public static final String PRESENCES_KEY = "presences";
    public static final String MAX_PRESENCES_KEY = "max_presences";
    public static final String MAX_MEMBERS_KEY = "max_members";
    public static final String VANITY_URL_CODE_KEY = "vanity_url_code";
    public static final String DESCRIPTION_KEY = "description";
    public static final String BANNER_KEY = "banner";
    public static final String PREMIUM_TIER_KEY = "premium_tier";
    public static final String PREMIUM_SUBSCRIPTION_COUNT_KEY = "premium_subscription_count";
    public static final String PREFERRED_LOCALE_KEY = "preferred_locale";
    public static final String PUBLIC_UPDATES_CHANNEL_ID_KEY = "public_updates_channel_id";
    public static final String MAX_VIDEO_CHANNEL_USERS_KEY = "max_video_channel_users";
    public static final String APPROXIMATE_MEMBER_COUNT_KEY = "approximate_member_count";
    public static final String APPROXIMATE_PRESENCE_COUNT_KEY = "approximate_presence_count";
    public static final String WELCOME_SCREEN_KEY = "welcome_screen";
    public static final String NSFW_LEVEL_KEY = "nsfw_level";
    public static final String STAGE_INSTANCES_KEY = "stage_instances";
    public static final String STICKERS_KEY = "stickers";
    public static final String GUILD_SCHEDULED_EVENTS_KEY = "guild_scheduled_events";


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

    public static @Nullable Guild fromData(@NotNull LApi lApi, @Nullable Data data){
        if(data == null) return null;

        return null;
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
