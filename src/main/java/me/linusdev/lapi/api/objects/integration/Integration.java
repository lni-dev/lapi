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

package me.linusdev.lapi.api.objects.integration;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#integration-object" target="_top">Integration Object</a>
 */
public class Integration implements Datable, SnowflakeAble, HasLApi {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String ENABLED_KEY = "enabled";
    public static final String SYNCING_KEY = "syncing";
    public static final String ROLE_ID_KEY = "role_id";
    public static final String ENABLE_EMOTICONS_KEY = "enable_emoticons";
    public static final String EXPIRE_BEHAVIOR_KEY = "expire_behavior";
    public static final String EXPIRE_GRACE_PERIOD_KEY = "expire_grace_period";
    public static final String USER_KEY = "user";
    public static final String ACCOUNT_KEY = "account";
    public static final String SYNCED_AT_KEY = "synced_at";
    public static final String SUBSCRIBER_COUNT_KEY = "subscriber_count";
    public static final String REVOKED_KEY = "revoked";
    public static final String APPLICATION_KEY = "application";

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @NotNull String name;
    private final @NotNull String type;
    private final boolean enabled;
    private final @Nullable Boolean syncing;
    private final @Nullable Snowflake roleId;
    private final @Nullable Boolean enableEmoticons;
    private final @Nullable IntegrationExpireBehavior expireBehavior;
    private final @Nullable Integer expireGracePeriod;
    private final @Nullable User user;
    private final @NotNull IntegrationAccount account;
    private final @Nullable ISO8601Timestamp syncedAt;
    private final @Nullable Integer subscriberCount;
    private final @Nullable Boolean revoked;
    private final @Nullable IntegrationApplication application;

    public Integration(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull String name, @NotNull String type, boolean enabled, @Nullable Boolean syncing, @Nullable Snowflake roleId, @Nullable Boolean enableEmoticons, @Nullable IntegrationExpireBehavior expireBehavior, @Nullable Integer expireGracePeriod, @Nullable User user, @NotNull IntegrationAccount account, @Nullable ISO8601Timestamp syncedAt, @Nullable Integer subscriberCount, @Nullable Boolean revoked, @Nullable IntegrationApplication application) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.syncing = syncing;
        this.roleId = roleId;
        this.enableEmoticons = enableEmoticons;
        this.expireBehavior = expireBehavior;
        this.expireGracePeriod = expireGracePeriod;
        this.user = user;
        this.account = account;
        this.syncedAt = syncedAt;
        this.subscriberCount = subscriberCount;
        this.revoked = revoked;
        this.application = application;
    }

    @Contract("_, !null -> !null; _, null -> null")
    public static @Nullable Integration fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String)  data.get(NAME_KEY);
        String type = (String) data.get(TYPE_KEY);
        Boolean enabled = (Boolean) data.get(ENABLED_KEY);
        Boolean syncing = (Boolean) data.get(SYNCING_KEY);
        String roleId = (String) data.get(ROLE_ID_KEY);
        Boolean enabledEmoticons = (Boolean) data.get(ENABLE_EMOTICONS_KEY);
        Number expireBehavior = (Number) data.get(EXPIRE_BEHAVIOR_KEY);
        Number expireGracePeriod = (Number) data.get(EXPIRE_GRACE_PERIOD_KEY);
        Data user = (Data) data.get(USER_KEY);
        Data account = (Data) data.get(ACCOUNT_KEY);
        String syncedAt = (String) data.get(SYNCED_AT_KEY);
        Number subscriberCount = (Number) data.get(SUBSCRIBER_COUNT_KEY);
        Boolean revoked = (Boolean) data.get(REVOKED_KEY);
        Data application = (Data) data.get(APPLICATION_KEY);

        if(id == null || name == null || type == null || enabled == null || account == null){
            InvalidDataException.throwException(data, null, Integration.class,
                    new Object[]{id, name, type, enabled, account},
                    new String[]{ID_KEY, NAME_KEY, TYPE_KEY, ENABLED_KEY, ACCOUNT_KEY});
            return null;
        }

        return new Integration(lApi, Snowflake.fromString(id), name, type, enabled, syncing, Snowflake.fromString(roleId), enabledEmoticons,
                expireBehavior == null ? null : IntegrationExpireBehavior.fromValue(expireBehavior.intValue()),
                expireGracePeriod == null ? null : expireGracePeriod.intValue(), User.fromData(lApi, user), IntegrationAccount.fromData(account),
                ISO8601Timestamp.fromString(syncedAt), subscriberCount == null ? null : subscriberCount.intValue(), revoked,
                IntegrationApplication.fromData(lApi, application));
    }

    /**
     * integration id as {@link Snowflake}
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * integration name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * integration type (twitch, youtube, or discord)
     */
    public @NotNull String getType() {
        return type;
    }

    /**
     * is this integration enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * is this integration syncing.<br> This field is not provided for discord bot integrations
     */
    public @Nullable Boolean getSyncing() {
        return syncing;
    }

    /**
     * id that this integration uses for "subscribers".<br> This field is not provided for discord bot integrations
     */
    public @Nullable Snowflake getRoleId() {
        return roleId;
    }

    /**
     * whether emoticons should be synced for this integration (twitch only currently).<br> This field is not provided for discord bot integrations
     */
    public @Nullable Boolean getEnableEmoticons() {
        return enableEmoticons;
    }

    /**
     * the behavior of expiring subscribers.<br> This field is not provided for discord bot integrations
     */
    public @Nullable IntegrationExpireBehavior getExpireBehavior() {
        return expireBehavior;
    }

    /**
     * the grace period (in days) before expiring subscribers.<br> This field is not provided for discord bot integrations
     */
    public @Nullable Integer getExpireGracePeriod() {
        return expireGracePeriod;
    }

    /**
     * user for this integration.<br> This field is not provided for discord bot integrations
     */
    public @Nullable User getUser() {
        return user;
    }

    /**
     * integration account information
     */
    public @NotNull IntegrationAccount getAccount() {
        return account;
    }

    /**
     * when this integration was last synced.<br> This field is not provided for discord bot integrations
     */
    public @Nullable ISO8601Timestamp getSyncedAt() {
        return syncedAt;
    }

    /**
     * how many subscribers this integration has.<br> This field is not provided for discord bot integrations
     */
    public @Nullable Integer getSubscriberCount() {
        return subscriberCount;
    }

    /**
     * has this integration been revoked.<br> This field is not provided for discord bot integrations
     */
    public @Nullable Boolean getRevoked() {
        return revoked;
    }

    /**
     * The bot/OAuth2 application for discord integrations
     */
    public @Nullable IntegrationApplication getApplication() {
        return application;
    }

    @Override
    public Data getData() {
        Data data = new Data(5);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(TYPE_KEY, type);
        data.add(ENABLED_KEY, enabled);
        data.addIfNotNull(SYNCING_KEY, syncing);
        data.addIfNotNull(ROLE_ID_KEY, roleId);
        data.addIfNotNull(ENABLE_EMOTICONS_KEY, enableEmoticons);
        data.addIfNotNull(EXPIRE_BEHAVIOR_KEY, expireBehavior);
        data.addIfNotNull(EXPIRE_GRACE_PERIOD_KEY, expireGracePeriod);
        data.addIfNotNull(USER_KEY, user);
        data.add(ACCOUNT_KEY, account);
        data.addIfNotNull(SYNCED_AT_KEY, syncedAt);
        data.addIfNotNull(SUBSCRIBER_COUNT_KEY, subscriberCount);
        data.addIfNotNull(REVOKED_KEY, revoked);
        data.addIfNotNull(APPLICATION_KEY, application);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

}
