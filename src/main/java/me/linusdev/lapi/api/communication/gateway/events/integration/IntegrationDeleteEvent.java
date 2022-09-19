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

package me.linusdev.lapi.api.communication.gateway.events.integration;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntegrationDeleteEvent extends Event implements GuildEvent {

    public static final String APPLICATION_ID_KEY = "application_id";

    private final @NotNull Snowflake integrationId;
    private final @Nullable Snowflake applicationId;

    public IntegrationDeleteEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                                  @NotNull Snowflake integrationId, @Nullable Snowflake applicationId) {
        super(lApi, payload, guildId);
        this.integrationId = integrationId;
        this.applicationId = applicationId;
    }

    /**
     * integration id as {@link Snowflake}
     */
    public @NotNull Snowflake getIntegrationIdAsSnowflake() {
        return integrationId;
    }

    /**
     * integration id as {@link String}
     */
    public @NotNull String getIntegrationId() {
        return integrationId.asString();
    }

    /**
     * id as {@link Snowflake} of the bot/OAuth2 application for this discord integration
     */
    public @Nullable Snowflake getApplicationIdAsSnowflake() {
        return applicationId;
    }

    /**
     * id as {@link String} of the bot/OAuth2 application for this discord integration
     */
    public @Nullable String getApplicationId() {
        if(applicationId == null) return null;
        return applicationId.asString();
    }
}
