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

package me.linusdev.lapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.objects.permission.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-guild-features" target="_top">GuildImpl Features</a>
 * @updated 25.10.2022
 */
public enum GuildFeature implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN("-1"),

    /**
     * guild has access to set an animated guild banner image
     */
    ANIMATED_BANNER("ANIMATED_BANNER"),

    /**
     * guild has access to set an animated guild icon
     */
    ANIMATED_ICON("ANIMATED_ICON"),

    /**
     * guild has set up auto moderation rules
     */
    AUTO_MODERATION("AUTO_MODERATION"),

    /**
     * guild has access to set a guild banner image
     */
    BANNER("BANNER"),

    /**
     * guild has access to use commerce features (i.e. create store channels)
     */
    @Deprecated
    COMMERCE("COMMERCE"),

    /**
     * guild can enable welcome screen, Membership Screening, stage channels and discovery, and receives community updates
     */
    COMMUNITY("COMMUNITY", Permission.ADMINISTRATOR),

    /**
     * guild has been set as a support server on the App Directory
     */
    DEVELOPER_SUPPORT_SERVER("DEVELOPER_SUPPORT_SERVER"),

    /**
     * guild is able to be discovered in the directory
     */
    DISCOVERABLE("DISCOVERABLE", Permission.ADMINISTRATOR),

    /**
     * guild is able to be featured in the directory
     */
    FEATURABLE("FEATURABLE"),

    /**
     * guild has paused invites, preventing new users from joining
     */
    INVITES_DISABLED("INVITES_DISABLED", Permission.MANAGE_GUILD),

    /**
     * guild has access to set an invite splash background
     */
    INVITE_SPLASH("INVITE_SPLASH"),

    /**
     * guild has enabled Membership Screening
     */
    MEMBER_VERIFICATION_GATE_ENABLED("MEMBER_VERIFICATION_GATE_ENABLED"),

    /**
     * guild has enabled monetization
     */
    MONETIZATION_ENABLED("MONETIZATION_ENABLED"),

    /**
     * guild has increased custom sticker slots
     */
    MORE_STICKERS("MORE_STICKERS"),

    /**
     * guild has access to create news channels
     */
    NEWS("NEWS"),

    /**
     * guild is partnered
     */
    PARTNERED("PARTNERED"),

    /**
     * guild can be previewed before joining via Membership Screening or the directory
     */
    PREVIEW_ENABLED("PREVIEW_ENABLED"),

    /**
     * guild has access to create private threads
     */
    PRIVATE_THREADS("PRIVATE_THREADS"),

    /**
     * guild is able to set role icons
     */
    ROLE_ICONS("ROLE_ICONS"),

    /**
     * guild has access to the seven day archive time for threads
     */
    @Deprecated
    SEVEN_DAY_THREAD_ARCHIVE("SEVEN_DAY_THREAD_ARCHIVE"),

    /**
     * guild has access to the three day archive time for threads
     */
    @Deprecated
    THREE_DAY_THREAD_ARCHIVE("THREE_DAY_THREAD_ARCHIVE"),

    /**
     * guild has enabled ticketed events
     */
    TICKETED_EVENTS_ENABLED("TICKETED_EVENTS_ENABLED"),

    /**
     * guild has access to set a vanity URL
     */
    VANITY_URL("VANITY_URL"),

    /**
     * guild is verified
     */
    VERIFIED("VERIFIED"),

    /**
     * guild has access to set 384kbps bitrate in voice (previously VIP voice servers)
     */
    VIP_REGIONS("VIP_REGIONS"),

    /**
     * guild has enabled the welcome screen
     */
    WELCOME_SCREEN_ENABLED("WELCOME_SCREEN_ENABLED"),
    ;

    private final @NotNull String value;
    private final boolean mutable;
    private final @Nullable Permission requiredPermission;

    GuildFeature(@NotNull String value, boolean mutable, @Nullable Permission requiredPermission) {
        this.value = value;
        this.mutable = mutable;
        this.requiredPermission = requiredPermission;
    }

    GuildFeature(@NotNull String value, @NotNull Permission requiredPermission) {
        this(value, true, requiredPermission);
    }

    GuildFeature(String value) {
        this(value, false, null);
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable GuildFeature fromValue(@Nullable String value){
        if(value == null) return null;
        for(GuildFeature feature : GuildFeature.values()){
            if(feature.value.equals(value)) return feature;
        }

        return UNKNOWN;
    }

    /**
     *
     * @param values String array of GuildFeatures
     * @return {@link GuildFeature} array
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable GuildFeature[] fromValues(@Nullable String... values){
        if(values == null) return null;
        GuildFeature[] array = new GuildFeature[values.length];
        GuildFeature[] allFeatures = values();


        loop:
        for(int j = 0; j < values.length; j++){
            for(int i = 0; i < allFeatures.length; i++){
                if(allFeatures[i].value.equals(values[j])){
                    array[j] = allFeatures[i];

                    //move found feature to last, cause there are usually no duplicates in values.
                    //So we can find features, that would usually be at the end faster
                    GuildFeature temp = allFeatures[i];
                    allFeatures[i] = allFeatures[allFeatures.length-j-1];
                    allFeatures[allFeatures.length-j-1] = temp;
                    continue loop;
                }
            }
        }

        return array;
    }

    public boolean isMutable() {
        return mutable;
    }

    public @Nullable Permission getRequiredPermission() {
        return requiredPermission;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
