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

package me.linusdev.lapi.api.objects.channel.abstracts;

import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GroupDirectMessageChannelAbstract extends TextChannel, DirectMessageChannelAbstract{

    /**
     * group dm icon
     */
    @Nullable
    String getIconHash();

    /**
     * snowflake-id of the creator of the group DM
     */
    @NotNull
    Snowflake getOwnerIdAsSnowflake();

    /**
     * id of the creator of the group DM
     */
    @NotNull
    default String getOwnerId(){
        return getOwnerIdAsSnowflake().asString();
    }

    /**
     * application snowflake id of the group DM creator if it is bot-created
     */
    @Nullable
    Snowflake getApplicationID();
}
