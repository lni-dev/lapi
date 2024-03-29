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

package me.linusdev.lapi.api.objects.guild;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.guild.enums.DefaultMessageNotificationLevel;
import me.linusdev.lapi.api.objects.guild.enums.ExplicitContentFilterLevel;
import me.linusdev.lapi.api.objects.guild.enums.GuildFeature;
import me.linusdev.lapi.api.objects.guild.enums.VerificationLevel;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 *
 * A temporary guild object.
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-resource" target="_top">GuildImpl Resource</a>
 */
@SuppressWarnings({"UnnecessaryInterfaceModifier", "unused"})
public interface Guild extends Datable, HasLApi, SnowflakeAble {

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
    public static final String PREMIUM_PROGRESS_BAR_ENABLED = "premium_progress_bar_enabled";


    static @Nullable Guild fromData(@NotNull LApi lApi, @Nullable SOData data){
        if(data == null) return null;
        //TODO
        return GuildImpl.fromData((LApiImpl) lApi, data);
    }

    /**
     * guild name
     */
    @Nullable String getName();

    /**
     * icon hash
     */
    @Nullable String getIconHash();

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofGuildIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the banner
     */
    default @NotNull CDNImageRetriever getIcon(int desiredSize, @NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This guild object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofGuildIcon(getLApi(), getId(), getIconHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofGuildIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the banner
     */
    default @NotNull CDNImageRetriever getIcon(@NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This guild object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofGuildIcon(getLApi(), getId(), getIconHash(), fileType));
    }

    @Nullable String getSplashHash();

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofGuildSplash(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the banner
     */
    default @NotNull CDNImageRetriever getSplash(int desiredSize, @NotNull AbstractFileType fileType){
        if(getSplashHash() == null) throw new IllegalArgumentException("This guild object has no splash hash");
        return new CDNImageRetriever(CDNImage.ofGuildSplash(getLApi(), getId(), getSplashHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofGuildSplash(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the banner
     */
    default @NotNull CDNImageRetriever getSplash(@NotNull AbstractFileType fileType) {
        if (getSplashHash() == null) throw new IllegalArgumentException("This guild object has no splash hash");
        return new CDNImageRetriever(CDNImage.ofGuildSplash(getLApi(), getId(), getSplashHash(), fileType));
    }

    @Nullable String getDiscoverySplashHash();

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofGuildDiscoverySplash(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the banner
     */
    default @NotNull CDNImageRetriever getDiscoverySplash(int desiredSize, @NotNull AbstractFileType fileType){
        if(getDiscoverySplashHash() == null) throw new IllegalArgumentException("This guild object has no discovery splash hash");
        return new CDNImageRetriever(CDNImage.ofGuildDiscoverySplash(getLApi(), getId(), getDiscoverySplashHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofGuildDiscoverySplash(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the banner
     */
    default @NotNull CDNImageRetriever getDiscoverySplash(@NotNull AbstractFileType fileType) {
        if (getDiscoverySplashHash() == null) throw new IllegalArgumentException("This guild object has no discovery splash hash");
        return new CDNImageRetriever(CDNImage.ofGuildDiscoverySplash(getLApi(), getId(), getDiscoverySplashHash(), fileType));
    }

    /**
     * Whether the current user is owner of this guild.
     */
    @Nullable Boolean getOwner();

    /**
     * Id as {@link Snowflake} of the owner of this guild.
     */
    @NotNull Snowflake getOwnerIdAsSnowflake();

    /**
     * Id as {@link String} of the owner of this guild.
     */
    @SuppressWarnings("ConstantConditions")
    default @NotNull String getOwnerId(){
        if(getOwnerIdAsSnowflake() == null) return null;
        return getOwnerIdAsSnowflake().asString();
    }

    /**
     * total permissions for the user in the guild (excludes overwrites).<br>
     * <p>
     *     This field is only sent when using the {@link Link#GET_CURRENT_USER_GUILDS GET Current User Guilds} endpoint and are relative to the requested user!
     * </p>
     * @see #getPermissions()
     */
    @Nullable String getPermissionsAsString();

    /**
     * total permissions for the user in the guild (excludes overwrites).<br>
     * <p>
     *     This field is only sent when using the {@link Link#GET_CURRENT_USER_GUILDS GET Current User Guilds} endpoint and are relative to the requested user!
     * </p>
     * <p>
     *     Changes on the {@link Permissions} object will change the {@link String} returned by {@link #getPermissionsAsString()} as well.
     * </p>
     */
    @Nullable Permissions getPermissions();

    /**
     * 	voice region for the guild (deprecated).
     *  <br><br>
     *  All {@link VoiceRegion VoiceRegions} are managed by the {@link VoiceRegionManager VoiceRegionManager}.
     *  See {@link VoiceRegionManager#getVoiceRegionById(LApi, String) getVoiceRegionById()} for more information.
     */
    @Deprecated
    @Nullable VoiceRegion getRegion();

    /**
     * id as {@link Snowflake} of afk channel.
     * <p>
     *     returns {@code null} if no afk channel is set
     * </p>
     */
    @Nullable Snowflake getAfkChannelIdAsSnowflake();

    /**
     * id as {@link String} of the afk channel.
     * <p>
     *     returns {@code null} if no afk channel is set
     * </p>
     */
    @Nullable default String getAfkChannelId(){
        if(getAfkChannelIdAsSnowflake() == null) return null;
        return getAfkChannelIdAsSnowflake().asString();
    }

    /**
     * afk timeout in seconds.<br>
     * TODO: what means no timeout?
     */
    int getAfkTimeout();

    /**
     * {@code true} if the server widget is enabled. <br>
     * {@code null} probably means, that the widget is disabled.
     */
    @Nullable Boolean getWidgetEnabled();

    /**
     * {@code true} if the server widget is enabled.
     * @return {@code false} if {@link #getWidgetEnabled()} is {@code null}. Otherwise returns {@link #getWidgetEnabled()}.
     */
    default boolean isWidgetEnabled(){
        return getWidgetEnabled() != null && getWidgetEnabled();
    }

    /**
     * check if {@link #isWidgetEnabled()} is {@code true} first!<br>
     *
     * the channel id as {@link Snowflake} that the widget will generate an invite to, or {@code null} if set to no invite.
     * @see #getWidgetChannelId()
     */
    @Nullable Snowflake getWidgetChannelIdAsSnowflake();

    /**
     * check if {@link #isWidgetEnabled()} is {@code true} first!<br>
     *
     * the channel id as {@link String} that the widget will generate an invite to, or {@code null} if set to no invite.
     * @see #getWidgetChannelIdAsSnowflake()
     */
    @Nullable default String getWidgetChannelId(){
        if(getWidgetChannelIdAsSnowflake() == null) return null;
        return getWidgetChannelIdAsSnowflake().asString();
    }

    /**
     * {@link VerificationLevel} required for the guild
     */
    @NotNull VerificationLevel getVerificationLevel();

    /**
     * 	{@link DefaultMessageNotificationLevel default message notifications level}
     */
    @NotNull DefaultMessageNotificationLevel getDefaultMessageNotifications();

    /**
     * {@link ExplicitContentFilterLevel explicit content filter level}
     */
    @NotNull ExplicitContentFilterLevel getExplicitContentFilter();

    /**
     * roles in the guild
     */
    @NotNull Collection<Role> getRoles();

    @NotNull List<GuildFeature> getFeatures();

    //TODO: emojis, features, ...

    @Override
    @NotNull Snowflake getIdAsSnowflake();

    @Override
    @NotNull
    default String getId() {
        return SnowflakeAble.super.getId();
    }

    @Override
    SOData getData();
}
