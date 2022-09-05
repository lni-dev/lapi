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

package me.linusdev.lapi.api.objects.role;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/permissions#role-object-role-tags-structure" target="_top">Role Tags Structure</a>
 */
public class RoleTags implements Datable, Copyable<RoleTags> {

    public static final String BOT_ID_KEY = "bot_id";
    public static final String INTEGRATION_ID_KEY = "integration_id";
    public static final String PREMIUM_SUBSCRIBER_KEY = "premium_subscriber";

    private final @Nullable Snowflake botId;
    private final @Nullable Snowflake integrationId;
    private final boolean isPremiumSubscriberRole; //This depends on whether a PREMIUM_SUBSCRIBER_KEY exists or not

    /**
     *
     * @param botId the id of the bot this role belongs to
     * @param integrationId the id of the integration this role belongs to
     * @param isPremiumSubscriberRole whether this is the guild's premium subscriber role
     */
    public RoleTags(@Nullable Snowflake botId, @Nullable Snowflake integrationId, boolean isPremiumSubscriberRole) {
        this.botId = botId;
        this.integrationId = integrationId;
        this.isPremiumSubscriberRole = isPremiumSubscriberRole;
    }

    /**
     *
     * @param data {@link SOData}
     * @return {@link RoleTags}
     */
    public static @Nullable RoleTags fromData(@Nullable SOData data){
        if(data == null) return null;
        String botId = (String) data.get(BOT_ID_KEY);
        String integrationId = (String) data.get(INTEGRATION_ID_KEY);

        Object premiumSubscriptionKey = data.get(PREMIUM_SUBSCRIBER_KEY, new Object(), false);
        boolean isPremiumSubscriberRole = false;
        if(premiumSubscriptionKey == null){ //it will only be null if the field was really null
            isPremiumSubscriberRole = true;
        }

        return new RoleTags(Snowflake.fromString(botId), Snowflake.fromString(integrationId), isPremiumSubscriberRole);
    }

    /**
     * the id as {@link Snowflake} of the bot this role belongs to
     */
    public @Nullable Snowflake getBotIdAsSnowflake() {
        return botId;
    }

    /**
     * the id as {@link String} of the bot this role belongs to
     */
    public @Nullable String getBotId() {
        if(botId == null) return null;
        return botId.asString();
    }

    /**
     * the id as {@link Snowflake} of the integration this role belongs to
     */
    public @Nullable Snowflake getIntegrationIdAsSnowflake() {
        return integrationId;
    }

    /**
     * the id as {@link String} of the integration this role belongs to
     */
    public @Nullable String getIntegrationId() {
        if(integrationId == null) return null;
        return integrationId.asString();
    }

    /**
     * whether this is the guild's premium subscriber role
     */
    public boolean isPremiumSubscriberRole() {
        return isPremiumSubscriberRole;
    }

    /**
     *
     * @return {@link SOData} for this {@link RoleTags}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(BOT_ID_KEY, botId);
        data.add(INTEGRATION_ID_KEY, integrationId);
        if(isPremiumSubscriberRole) data.add(PREMIUM_SUBSCRIBER_KEY, null);

        return data;
    }

    @Override
    public @NotNull RoleTags copy() {
        return new RoleTags(Copyable.copy(botId), Copyable.copy(integrationId), isPremiumSubscriberRole);
    }
}
