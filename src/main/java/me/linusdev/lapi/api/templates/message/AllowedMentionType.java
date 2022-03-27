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

package me.linusdev.lapi.api.templates.message;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mention-types
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mention-types" target="_top">Allowed Mention Types</a>
 */
public enum AllowedMentionType implements SimpleDatable {

    /**
     * Controls role mentions
     */
    ROLE_MENTIONS("roles"),

    /**
     * Controls user mentions
     */
    USER_MENTIONS("users"),

    /**
     * Controls @everyone and @here mentions
     */
    EVERYONE_MENTIONS("everyone"),
    ;

    private final @NotNull String value;

    AllowedMentionType(@NotNull String value){
        this.value = value;
    }

    public @NotNull String getValue() {
        return value;
    }

    @Override
    @NotNull
    public Object simplify() {
        return value;
    }
}
