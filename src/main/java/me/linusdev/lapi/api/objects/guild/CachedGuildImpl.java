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
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.guild.member.MemberManager;
import me.linusdev.lapi.api.manager.guild.PresencesManager;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStateManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.Thread;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.guild.enums.*;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import me.linusdev.lapi.api.objects.local.Locale;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.stage.StageInstance;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.log.Logger;
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
    protected @Nullable ListManager<EmojiObject> emojiManager = null;
    protected @Nullable ListManager<Sticker> stickerManager = null;

    //Create GuildImpl
    protected @Nullable VoiceStateManager voiceStatesManager;

    protected @Nullable ISO8601Timestamp joinedAt;
    protected @Nullable Boolean large;
    protected @Nullable Integer memberCount;
    protected @Nullable MemberManager memberManager;
    protected @Nullable ListManager<Channel<?>> channelManager;
    protected @Nullable ListManager<Thread<?>> threadsManager;
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
            this.roleManager = lApi.getConfig().getRoleManagerFactory().newInstance(lApi);
        }

        if(lApi.isCacheEmojisEnabled()){
            this.emojiManager = lApi.getConfig().getEmojiManagerFactory().newInstance(lApi);
        }

        if(lApi.isCacheStickersEnabled()){
            this.stickerManager = lApi.getConfig().getStickerManagerFactory().newInstance(lApi);
        }

        if(lApi.isCacheVoiceStatesEnabled()) {
            this.voiceStatesManager = lApi.getConfig().getVoiceStateManagerFactory().newInstance(lApi);
        }

        if(lApi.isCacheMembersEnabled()) {
            this.memberManager = lApi.getConfig().getMemberManagerFactory().newInstance(lApi);
        }

        if(lApi.isCacheChannelsEnabled()) {
            this.channelManager = lApi.getConfig().getChannelManagerFactory().newInstance(lApi);
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
            Logger.getLogger(CachedGuildImpl.class).error(e);
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
     * set by {@link me.linusdev.lapi.api.manager.guild.GuildManager}, when the current user left (or was removed from) this guild
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
            if(!roleManager.isInitialized()) {
                @SuppressWarnings("unchecked")
                ArrayList<Object> rolesData = (ArrayList<Object>) data.get(ROLES_KEY);
                if(rolesData != null){
                    roleManager.init(rolesData.size());
                    for(Object o : rolesData){
                        roleManager.addRole(Role.fromData(lApi, (Data) o));
                    }
                } else {
                    roleManager.init(1);
                }
            }

        }

        //Emojis
        if(emojiManager != null){
            if(!emojiManager.isInitialized()){
                @SuppressWarnings("unchecked")
                ArrayList<Object> emojisData = (ArrayList<Object>) data.get(EMOJIS_KEY);
                if(emojisData != null){
                    emojiManager.init(emojisData.size());
                    for(Object o : emojisData){
                        emojiManager.add(EmojiObject.fromData(lApi, (Data) o));
                    }
                } else {
                    emojiManager.init(1);
                }
            }

        }

        data.processIfContained(FEATURES_KEY, o -> {
            @SuppressWarnings("unchecked")
            ArrayList<Object> arr = (ArrayList<Object>) o;
            if(this.features != null) this.features.clear();
            else this.features = new ArrayList<>(arr.size());

            for(Object str : arr){
                features.add(GuildFeature.fromValue((String) str));
            }
        });

        if(stickerManager != null){
            if(!stickerManager.isInitialized()){
                @SuppressWarnings("unchecked")
                ArrayList<Object> stickersData = (ArrayList<Object>) data.get(STICKERS_KEY);
                if(stickersData != null){
                    stickerManager.init(stickersData.size());
                    for(Object o : stickersData){
                        stickerManager.add(Sticker.fromData(lApi, (Data) o));
                    }
                } else {
                    stickerManager.init(1);
                }
            }
        }

        //GuildImpl Create
        data.processIfContained(JOINED_AT_KEY, (String joinedAt) -> this.joinedAt = ISO8601Timestamp.fromString(joinedAt));
        data.processIfContained(LARGE_KEY, (Boolean large) -> this.large = large);
        data.processIfContained(MEMBER_COUNT_KEY, (Long count) -> this.memberCount = count == null ? null : count.intValue());

        if(voiceStatesManager != null) {
            if(!voiceStatesManager.isInitialized()) {
                @SuppressWarnings("unchecked")
                ArrayList<Object> voiceStatesData = (ArrayList<Object>) data.get(VOICE_STATES_KEY);
                if(voiceStatesData != null) {
                    voiceStatesManager.init(voiceStatesData.size());
                    for(Object o : voiceStatesData){
                        //the data won't contain a guild_id field, because this data was contained, in the GUILD_CREATE event
                        //of this guild. -> add it here
                        //TODO: Maybe do the same with member field
                        Data d = (Data) o;
                        d.add(VoiceState.GUILD_ID_KEY, this.id.asString());
                        voiceStatesManager.add(VoiceState.fromData(lApi, d));
                    }
                } else {
                    voiceStatesManager.init(1);
                }
            }
        }

        if(memberManager != null){
            if(!memberManager.isInitialized()) {
                @SuppressWarnings("unchecked")
                ArrayList<Object> membersData = (ArrayList<Object>) data.get(MEMBERS_KEY);
                if(membersData != null){
                    memberManager.init(membersData.size());
                    for(Object o : membersData){
                        memberManager.addMember((Data) o);
                    }
                } else {
                    memberManager.init(1);
                }
            }
        }

        if(channelManager != null){
            if(!channelManager.isInitialized()) {
                @SuppressWarnings("unchecked")
                ArrayList<Object> channelsData = (ArrayList<Object>) data.get(CHANNELS_KEY);
                if(channelsData != null){
                    channelManager.init(channelsData.size());
                    for(Object o : channelsData){
                        Data channelData = (Data) o;
                        //add guildId to channel data
                        channelData.add(Channel.GUILD_ID_KEY, id.asString());
                        channelManager.add(Channel.fromData(lApi, (Data) o));
                    }
                } else {
                    channelManager.init(1);
                }
            }

        }

        //TODO: threads, ...
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

    @ApiStatus.Internal
    public @Nullable ListManager<EmojiObject> getEmojiManager() {
        return emojiManager;
    }

    @ApiStatus.Internal
    public @Nullable ListManager<Sticker> getStickerManager() {
        return stickerManager;
    }

    public @Nullable VoiceStateManager getVoiceStatesManager() {
        return voiceStatesManager;
    }

    public @Nullable MemberManager getMemberManager() {
        return memberManager;
    }

    public @Nullable ListManager<Channel<?>> getChannelManager() {
        return channelManager;
    }

    public @Nullable ListManager<Thread<?>> getThreadsManager() {
        return threadsManager;
    }

    @NotNull
    @Override
    public Guild copy() {
        //TODO: implement copy method
        return null;
    }
}
