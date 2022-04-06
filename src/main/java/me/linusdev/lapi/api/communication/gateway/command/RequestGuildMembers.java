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

package me.linusdev.lapi.api.communication.gateway.command;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class RequestGuildMembers extends GatewayCommand{

    public static final String GUILD_ID_KEY = "guild_id";
    public static final String QUERY_KEY = "query";
    public static final String LIMIT_KEY = "limit";
    public static final String PRESENCES_KEY = "presences";
    public static final String USER_IDS_KEY = "user_ids";
    public static final String NONCE_KYE = "nonce";

    private final @NotNull Snowflake guildId;
    private final @Nullable String query;
    private final int limit;
    private final @NotNull Boolean presences;
    private final @Nullable ArrayList<Snowflake> userIds;
    private final @Nullable String nonce;

    public RequestGuildMembers(@NotNull GatewayCommandType type, @Nullable Data data, @NotNull Snowflake guildId,
                               @Nullable String query, int limit, @NotNull Boolean presences,
                               @Nullable ArrayList<Snowflake> userIds, @Nullable String nonce) {
        super(type, data);
        this.guildId = guildId;
        this.query = query;
        this.limit = limit;
        this.presences = presences;
        this.userIds = userIds;
        this.nonce = nonce;
    }
}
