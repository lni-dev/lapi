/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.templates.guild;

import me.linusdev.data.OptionalValue;
import me.linusdev.data.refl.OptValue;
import me.linusdev.data.refl.AutoSODatable;
import me.linusdev.data.refl.Value;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.guild.enums.*;
import me.linusdev.lapi.api.objects.locale.Locale;
import me.linusdev.lapi.api.objects.other.ImageData;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#modify-guild">Discord Documentation</a>
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ModifyGuildTemplate implements Template, AutoSODatable {

    @Value(value = Guild.NAME_KEY, addIfNull = false)
    private final @Nullable String name;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @OptValue(Guild.REGION_KEY)
    @Deprecated
    private final @Nullable OptionalValue<String> region;

    @OptValue(Guild.VERIFICATION_LEVEL_KEY)
    private final @Nullable OptionalValue<VerificationLevel> verificationLevel;

    @OptValue(Guild.DEFAULT_MESSAGE_NOTIFICATIONS_KEY)
    private final @Nullable OptionalValue<DefaultMessageNotificationLevel> defaultMessageNotificationLevel;

    @OptValue(Guild.EXPLICIT_CONTENT_FILTER_KEY)
    private final @Nullable OptionalValue<ExplicitContentFilterLevel> explicitContentFilter;

    @OptValue(Guild.AFK_CHANNEL_ID_KEY)
    private final @Nullable OptionalValue<String> afkChannelId;

    @Value(value = Guild.AFK_TIMEOUT_KEY, addIfNull = false)
    private final @Nullable Integer afkTimeout;

    @OptValue(Guild.ICON_KEY)
    private final @Nullable OptionalValue<ImageData> icon;

    @Value(value = Guild.OWNER_ID_KEY, addIfNull = false)
    private final @Nullable String ownerId;

    @OptValue(Guild.SPLASH_KEY)
    private final @Nullable OptionalValue<ImageData> splash;

    @OptValue(Guild.DISCOVERY_SPLASH_KEY)
    private final @Nullable OptionalValue<ImageData> discoverySplash;

    @OptValue(Guild.BANNER_KEY)
    private final @Nullable OptionalValue<ImageData> banner;

    @OptValue(Guild.SYSTEM_CHANNEL_ID_KEY)
    private final @Nullable OptionalValue<String> systemChannelId;

    @Value(value = Guild.SYSTEM_CHANNEL_FLAGS_KEY, addIfNull = false)
    private final @Nullable List<SystemChannelFlag> systemChannelFlags;

    @OptValue(Guild.RULES_CHANNEL_ID_KEY)
    private final @Nullable OptionalValue<String> ruleChannelId;

    @OptValue(Guild.PUBLIC_UPDATES_CHANNEL_ID_KEY)
    private final @Nullable OptionalValue<String> publicUpdatesChannelId;

    @OptValue(Guild.PREFERRED_LOCALE_KEY)
    private final @Nullable OptionalValue<Locale> preferredLocale;

    @Value(value = Guild.FEATURES_KEY, addIfNull = false)
    private final @Nullable List<GuildFeature> features;

    @OptValue(Guild.DESCRIPTION_KEY)
    private final @Nullable OptionalValue<String> description;

    @OptValue(Guild.PREMIUM_PROGRESS_BAR_ENABLED)
    private final @Nullable Boolean premiumProgressbarEnabled;



    public ModifyGuildTemplate(@Nullable String name, @Deprecated @Nullable OptionalValue<String> region,
                               @Nullable OptionalValue<VerificationLevel> verificationLevel,
                               @Nullable OptionalValue<DefaultMessageNotificationLevel> defaultMessageNotificationLevel,
                               @Nullable OptionalValue<ExplicitContentFilterLevel> explicitContentFilter,
                               @Nullable OptionalValue<String> afkChannelId, @Nullable Integer afkTimeout,
                               @Nullable OptionalValue<ImageData> icon, @Nullable String ownerId,
                               @Nullable OptionalValue<ImageData> splash,
                               @Nullable OptionalValue<ImageData> discoverySplash,
                               @Nullable OptionalValue<ImageData> banner,
                               @Nullable OptionalValue<String> systemChannelId,
                               @Nullable List<SystemChannelFlag> systemChannelFlags,
                               @Nullable OptionalValue<String> ruleChannelId,
                               @Nullable OptionalValue<String> publicUpdatesChannelId,
                               @Nullable OptionalValue<Locale> preferredLocale, @Nullable List<GuildFeature> features,
                               @Nullable OptionalValue<String> description, @Nullable Boolean premiumProgressbarEnabled
    ) {
        this.name = name;
        this.region = region;
        this.verificationLevel = verificationLevel;
        this.defaultMessageNotificationLevel = defaultMessageNotificationLevel;
        this.explicitContentFilter = explicitContentFilter;
        this.afkChannelId = afkChannelId;
        this.afkTimeout = afkTimeout;
        this.icon = icon;
        this.ownerId = ownerId;
        this.splash = splash;
        this.discoverySplash = discoverySplash;
        this.banner = banner;
        this.systemChannelId = systemChannelId;
        this.systemChannelFlags = systemChannelFlags;
        this.ruleChannelId = ruleChannelId;
        this.publicUpdatesChannelId = publicUpdatesChannelId;
        this.preferredLocale = preferredLocale;
        this.features = features;
        this.description = description;
        this.premiumProgressbarEnabled = premiumProgressbarEnabled;
    }

    @Override
    public @NotNull SOData getData() {
        return AutoSODatable.super.getData();
    }
}
