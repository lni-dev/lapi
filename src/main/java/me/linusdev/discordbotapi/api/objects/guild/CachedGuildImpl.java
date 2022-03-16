package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.discordbotapi.api.interfaces.updatable.Updatable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import me.linusdev.discordbotapi.api.manager.*;
import me.linusdev.discordbotapi.api.manager.guild.MembersManager;
import me.linusdev.discordbotapi.api.manager.guild.PresencesManager;
import me.linusdev.discordbotapi.api.manager.guild.ThreadsManager;
import me.linusdev.discordbotapi.api.manager.guild.VoiceStatesManager;
import me.linusdev.discordbotapi.api.manager.guild.emoji.EmojiManager;
import me.linusdev.discordbotapi.api.manager.guild.role.RoleManager;
import me.linusdev.discordbotapi.api.manager.guild.role.RoleManagerImpl;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.guild.enums.*;
import me.linusdev.discordbotapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.discordbotapi.api.objects.local.Locale;
import me.linusdev.discordbotapi.api.objects.permission.Permissions;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.stage.StageInstance;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CachedGuildImpl extends GuildImpl implements CachedGuild, Datable, HasLApi, SnowflakeAble, CopyAndUpdatable<Guild>, Updatable {

    protected @Nullable Boolean unavailable;
    protected boolean awaitingEvent;
    protected boolean removed;

    protected @Nullable RoleManager roleManager = null;
    protected @Nullable EmojiManager emojiManager = null;

    //Create GuildImpl
    protected @Nullable ISO8601Timestamp joinedAt;
    protected @Nullable Boolean large;
    protected @Nullable Integer memberCount;
    protected @Nullable VoiceStatesManager voiceStatesManager;
    protected @Nullable MembersManager membersManager;
    protected @Nullable ChannelsManager channelsManager;
    protected @Nullable ThreadsManager threadsManager;
    protected @Nullable PresencesManager presencesManager;
    protected @Nullable StageInstance[] stageInstances;
    protected @Nullable GuildScheduledEvent[] guildScheduledEvents;


    public CachedGuildImpl(@NotNull LApiImpl lApi, @NotNull Snowflake id, @Nullable Boolean unavailable, boolean awaitingEvent){
        super(lApi,
                id,
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
        this.unavailable = unavailable;
        this.awaitingEvent = awaitingEvent;
        this.removed = unavailable == null;

        if(lApi.isCacheRolesEnabled()){
            this.roleManager = lApi.getNewRoleManager();
        }


    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable CachedGuildImpl fromData(@NotNull LApiImpl lApi, @Nullable Data data){
        if(data == null) return null;
        String id = (String) data.get(ID_KEY);
        Boolean unavailable = (Boolean) data.getOrDefaultIfNull(UNAVAILABLE_KEY, false);


        CachedGuildImpl guild = new CachedGuildImpl(lApi, Snowflake.fromString(id), unavailable, false);
        try {
            guild.updateSelfByData(data);
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }

        return guild;
    }


    public static @NotNull CachedGuildImpl fromUnavailableGuild(@NotNull LApiImpl lApi, @NotNull UnavailableGuild guild){
        return new CachedGuildImpl(lApi, guild.getIdAsSnowflake(), guild.getUnavailable(), true);
    }

    /**
     * total permissions for the current user in the guild (excludes overwrites).<br>
     * This field is probably {@code null} or already be outdated!
     */
    @Override
    public @Nullable String getPermissionsAsString() {
        return super.getPermissionsAsString();
    }

    /**
     * total permissions for the current user in the guild (excludes overwrites).<br>
     * This field is probably {@code null} or already be outdated!<br>
     * <br>
     * The returned {@link Permissions} object should not be changed.
     */
    @Override
    public @Nullable Permissions getPermissions() {
        return super.getPermissions();
    }

    @Override
    public boolean isUnavailable() {
        return (!(unavailable == null)) && unavailable;
    }

    @ApiStatus.Internal
    public boolean isAwaitingEvent() {
        return awaitingEvent;
    }

    /**
     * {@code true} if the current user left (or was removed from) this guild
     */
    @Override
    public boolean isRemoved() {
        return removed;
    }

    /**
     * set by {@link me.linusdev.discordbotapi.api.manager.guild.GuildManager}, when the current user left (or was removed from) this guild
     */
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public Data getData() {
        //TODO
        return null;
    }

    @Override
    public void updateSelfByData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return;
        this.awaitingEvent = false;
        this.unavailable = (Boolean) data.getOrDefault(UNAVAILABLE_KEY, false);

        data.processIfContained(NAME_KEY, (String name) -> this.name = name);
        data.processIfContained(ICON_HASH_KEY, (String hash) -> this.iconHash = hash);
        data.processIfContained(SPLASH_KEY, (String splash) -> this.splashHash = splash);
        data.processIfContained(DISCOVERY_SPLASH_KEY, (String splash) -> this.discoverySplashHash = splash);
        data.processIfContained(OWNER_KEY, (Boolean owner) -> this.owner = owner);
        data.processIfContained(OWNER_ID_KEY, (String id) -> this.ownerId = Snowflake.fromString(id));
        data.processIfContained(PERMISSIONS_KEY, (String permissions) -> {
            this.permissions = permissions;
            if(this.permissionsAsList != null) this.permissionsAsList.set(permissions);
        });
        data.processIfContained(REGION_KEY, (String region) -> this.region = lApi.getVoiceRegionManager().getVoiceRegionById(region));
        data.processIfContained(AFK_CHANNEL_ID_KEY, (String id) -> this.afkChannelId = Snowflake.fromString(id));
        data.processIfContained(AFK_TIMEOUT_KEY, (Long timeout) -> this.afkTimeout = timeout.intValue());
        data.processIfContained(WIDGET_ENABLED_KEY, (Boolean enabled) -> this.widgetEnabled = enabled);
        data.processIfContained(WIDGET_CHANNEL_ID_KEY, (String id) -> this.widgetChannelId = Snowflake.fromString(id));
        data.processIfContained(VERIFICATION_LEVEL_KEY, (Long level) -> this.verificationLevel = VerificationLevel.ofValue(level.intValue()));
        data.processIfContained(DEFAULT_MESSAGE_NOTIFICATIONS_KEY, (Long level) -> this.defaultMessageNotifications = DefaultMessageNotificationLevel.ofValue(level.intValue()));
        data.processIfContained(EXPLICIT_CONTENT_FILTER_KEY, (Long level) -> this.explicitContentFilter = ExplicitContentFilterLevel.fromValue(level.intValue()));
        data.processIfContained(MFA_LEVEL_KEY, (Long level) -> this.mfaLevel = MFALevel.fromValue(level.intValue()));
        data.processIfContained(APPLICATION_ID_KEY, (String id) -> this.applicationId = Snowflake.fromString(id));
        data.processIfContained(SYSTEM_CHANNEL_ID_KEY, (String id) -> this.systemChannelId = Snowflake.fromString(id));
        data.processIfContained(SYSTEM_CHANNEL_FLAGS_KEY, (Long flags) -> this.systemChannelFlagsAsInt = flags.intValue());
        data.processIfContained(RULES_CHANNEL_ID_KEY, (String id) -> this.rulesChannelId = Snowflake.fromString(id));
        data.processIfContained(MAX_PRESENCES_KEY, (Long max) -> this.maxPresences = max == null ? null : max.intValue());
        data.processIfContained(MAX_MEMBERS_KEY, (Long max) -> this.maxMembers = max == null ? null : max.intValue());
        data.processIfContained(VANITY_URL_CODE_KEY, (String code) -> this.vanityUrlCode = code);
        data.processIfContained(DESCRIPTION_KEY, (String description) -> this.description = description);
        data.processIfContained(BANNER_KEY, (String hash) -> this.bannerHash = hash);
        data.processIfContained(PREMIUM_TIER_KEY, (Long tier) -> this.premiumTier = PremiumTier.fromValue(tier.intValue()));
        data.processIfContained(PREMIUM_SUBSCRIPTION_COUNT_KEY, (Long count) -> this.premiumSubscriptionCount = count == null ? null : count.intValue());
        data.processIfContained(PREFERRED_LOCALE_KEY, (String locale) -> this.preferredLocale = Locale.fromString(locale));
        data.processIfContained(PUBLIC_UPDATES_CHANNEL_ID_KEY, (String id) -> this.publicUpdatesChannelId = Snowflake.fromString(id));
        data.processIfContained(MAX_VIDEO_CHANNEL_USERS_KEY, (Long max) -> this.maxVideoChannelUsers = max == null ? null : max.intValue());
        data.processIfContained(APPROXIMATE_MEMBER_COUNT_KEY, (Long count) -> this.approximateMemberCount = count == null ? null : count.intValue());
        data.processIfContained(APPROXIMATE_PRESENCE_COUNT_KEY, (Long count) -> this.approximatePresenceCount = count == null ? null : count.intValue());
        data.processIfContained(WELCOME_SCREEN_KEY, (Data screen) -> {
            try {
                this.welcomeScreen = WelcomeScreen.fromData(screen);
            } catch (InvalidDataException e) {
                //TODO: InvalidDataException in an updateSelf...
                //TODO: maybe add a throws InvalidDataException to the interface?
                Logger.getLogger(this).error(e);
            }
        });
        data.processIfContained(NSFW_LEVEL_KEY, (Long level) -> this.nsfwLevel = GuildNsfwLevel.fromValue(level.intValue()));

        //Roles
        if(roleManager != null){
            @SuppressWarnings("unchecked")
            ArrayList<Object> rolesData = (ArrayList<Object>) data.get(ROLES_KEY);
            if(rolesData != null){
                for(Object o : rolesData){
                    roleManager.addRole(Role.fromData(lApi, (Data) o));
                }
            }
        }

        // TODO: Emojis[], Features[], Sticker[]

        //GuildImpl Create
        data.processIfContained(JOINED_AT_KEY, (String joinedAt) -> this.joinedAt = ISO8601Timestamp.fromString(joinedAt));
        data.processIfContained(LARGE_KEY, (Boolean large) -> this.large = large);
        data.processIfContained(MEMBER_COUNT_KEY, (Long count) -> this.memberCount = count == null ? null : count.intValue());

        //TODO voice states, members, ...
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nCachedGuildImpl: \n");

        sb.append("\t").append("name: ").append(name).append("\n");
        sb.append("\t").append("owner-id: ").append(ownerId).append("\n");
        sb.append("\t").append("current user permissions: ").append(permissions).append("\n");
        sb.append("\t").append("approximate member count: ").append(approximateMemberCount).append("\n");
        sb.append("\t").append("member count: ").append(memberCount).append("\n");
        sb.append("\t").append("current user joined at: ").append(joinedAt).append("\n");

        return sb.toString();
    }

    @ApiStatus.Internal
    public @Nullable RoleManager getRoleManager() {
        return roleManager;
    }


    @NotNull
    @Override
    public Guild copy() {
        //TODO: implement copy method
        return null;
    }
}
