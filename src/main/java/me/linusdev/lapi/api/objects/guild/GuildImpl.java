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

package me.linusdev.lapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.guild.enums.*;
import me.linusdev.lapi.api.objects.local.Locale;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildImpl implements Guild {

    protected @NotNull LApiImpl lApi;

    protected @NotNull Snowflake id;
    protected @NotNull String name;
    protected @Nullable String iconHash;
    protected @Nullable String splashHash;
    protected @Nullable String discoverySplashHash;
    protected @Nullable Boolean owner;
    protected @NotNull Snowflake ownerId;
    protected @Nullable String permissions;
    protected @Nullable @Deprecated VoiceRegion region;
    protected @Nullable Snowflake afkChannelId;
    protected int afkTimeout;
    protected @Nullable Boolean widgetEnabled;
    protected @Nullable Snowflake widgetChannelId;
    protected @NotNull VerificationLevel verificationLevel;
    protected @NotNull DefaultMessageNotificationLevel defaultMessageNotifications;
    protected @NotNull ExplicitContentFilterLevel explicitContentFilter;
    protected @NotNull List<Role> roles;
    protected @NotNull EmojiObject[] emojis;
    protected @Nullable ArrayList<GuildFeature> features; //see GuildFeature
    protected @NotNull MFALevel mfaLevel;
    protected @Nullable Snowflake applicationId;
    protected @NotNull Snowflake systemChannelId;
    protected int systemChannelFlagsAsInt;
    protected @NotNull SystemChannelFlag[] systemChannelFlags; //TODO convert to array in get method!
    protected @Nullable Snowflake rulesChannelId;
    protected @Nullable Integer maxPresences;
    protected @Nullable Integer maxMembers;
    protected @Nullable String vanityUrlCode;
    protected @Nullable String description;
    protected @Nullable String bannerHash;
    protected @Nullable PremiumTier premiumTier;
    protected @Nullable Integer premiumSubscriptionCount;
    protected @NotNull Locale preferredLocale;
    protected @Nullable Snowflake publicUpdatesChannelId;
    protected @Nullable Integer maxVideoChannelUsers;
    protected @Nullable Integer approximateMemberCount;
    protected @Nullable Integer approximatePresenceCount;
    protected @Nullable WelcomeScreen welcomeScreen;
    protected @NotNull GuildNsfwLevel nsfwLevel;
    protected @Nullable Sticker[] stickers;

    protected @Nullable Permissions permissionsAsList;

    public GuildImpl(
            @NotNull LApiImpl lApi, @NotNull Snowflake id, @NotNull String name,
            @Nullable String iconHash, @Nullable String splashHash, @Nullable String discoverySplashHash,
            @Nullable Boolean owner, @NotNull Snowflake ownerId, @Nullable String permissions,
            @Nullable VoiceRegion region, @Nullable Snowflake afkChannelId, int afkTimeout,
            @Nullable Boolean widgetEnabled, @Nullable Snowflake widgetChannelId,
            @NotNull VerificationLevel verificationLevel,
            @NotNull DefaultMessageNotificationLevel defaultMessageNotifications,
            @NotNull ExplicitContentFilterLevel explicitContentFilter, @NotNull List<Role> roles,
            @NotNull EmojiObject[] emojis, ArrayList<GuildFeature> features, @NotNull MFALevel mfaLevel,
            @Nullable Snowflake applicationId, @NotNull Snowflake systemChannelId, int systemChannelFlagsAsInt,
            @NotNull SystemChannelFlag[] systemChannelFlags, @Nullable Snowflake rulesChannelId,
            @Nullable Integer maxPresences, @Nullable Integer maxMembers, @Nullable String vanityUrlCode,
            @Nullable String description, @Nullable String bannerHash, @Nullable PremiumTier premiumTier,
            @Nullable Integer premiumSubscriptionCount, @NotNull Locale preferredLocale,
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

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable GuildImpl fromData(@NotNull LApiImpl lApi, @Nullable Data data){
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        //TODO
        return new GuildImpl(lApi,
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

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable String getIconHash() {
        return iconHash;
    }

    @Override
    public @Nullable String getSplashHash() {
        return splashHash;
    }

    @Override
    public @Nullable String getDiscoverySplashHash() {
        return discoverySplashHash;
    }

    @Override
    public @Nullable Boolean getOwner() {
        return owner;
    }

    @Override
    public @NotNull Snowflake getOwnerIdAsSnowflake() {
        return ownerId;
    }

    @Override
    public @Nullable String getPermissionsAsString() {
        return permissionsAsList == null ? permissions : permissionsAsList.getValueAsString();
    }

    @Override
    public @Nullable Permissions getPermissions() {
        if(permissionsAsList != null) return permissionsAsList;
        if(getPermissionsAsString() == null) return null;
        permissionsAsList = Permissions.ofString(getPermissionsAsString());
        return permissionsAsList;
    }

    @Override
    public @Nullable VoiceRegion getRegion() {
        return region;
    }

    @Override
    public @Nullable Snowflake getAfkChannelIdAsSnowflake() {
        return afkChannelId;
    }

    @Override
    public int getAfkTimeout() {
        return afkTimeout;
    }

    @Override
    public @Nullable Boolean getWidgetEnabled() {
        return widgetEnabled;
    }

    @Override
    public @Nullable Snowflake getWidgetChannelIdAsSnowflake() {
        return widgetChannelId;
    }

    @Override
    public @NotNull VerificationLevel getVerificationLevel() {
        return verificationLevel;
    }

    @Override
    public @NotNull DefaultMessageNotificationLevel getDefaultMessageNotifications() {
        return defaultMessageNotifications;
    }

    @Override
    public @NotNull ExplicitContentFilterLevel getExplicitContentFilter() {
        return explicitContentFilter;
    }

    @Override
    public @NotNull Collection<Role> getRoles() {
        return roles;
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
