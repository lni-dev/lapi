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

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * <p>
 *     The allowed mention field allows for more granular control over mentions without various hacks to the message
 *     content. This will always validate against message content to avoid phantom pings (e.g. to ping everyone, you
 *     must still have @everyone in the message content), and check against user/bot permissions.
 * </p>
 *
 * <p>
 *     If allowed mentions in a {@link MessageTemplate message template} is {@code null} the mentions
 *     will be parsed via the content (Discord does this)
 * </p>
 *
 * <p>
 *     If {@link #parse} array is empty (and {@link #users} and {@link #roles} is {@code null}), all mentions will be suppressed. Even if the content
 *     would mention a user, role, @everyone or @here
 * </p>
 *
 * <p>
 *     If {@link #users} array is not {@code null}, the parse array may not contain {@link AllowedMentionType#USER_MENTIONS}.<br>
 *     If {@link #roles} array is not {@code null}, the parse array may not contain {@link AllowedMentionType#ROLE_MENTIONS}.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mentions-reference" target="_top">Allowed Mentions Examples and Rules</a>
 * @see <a href="https://discord.com/developers/docs/resources/channel#allowed-mentions-object" target="_top">Allowed Mentions Object</a>
 */
public class AllowedMentions implements Datable {

    public static final String PARSE_KEY = "parse";
    public static final String ROLES_KEY = "roles";
    public static final String USERS_KEY = "users";
    public static final String REPLIED_USER_KEY = "replied_user";

    public static final int MAX_ROLE_MENTIONS = 100;
    public static final int MAX_USER_MENTIONS = 100;

    private final @Nullable AllowedMentionType[] parse;
    private final @Nullable String[] roles;
    private final @Nullable String[] users;
    private final boolean repliedUser;

    /**
     *
     * @param parse An array of {@link AllowedMentionType allowed mention types} to parse from the content.
     * @param roles Array of role_ids to mention (Max size of {@value #MAX_ROLE_MENTIONS})
     * @param users Array of user_ids to mention (Max size of {@value #MAX_USER_MENTIONS})
     * @param repliedUser For replies, whether to mention the author of the message being replied to (default false)
     */
    public AllowedMentions(@Nullable AllowedMentionType[] parse, @Nullable String[] roles, @Nullable String[] users, boolean repliedUser) {
        this.parse = parse;
        this.roles = roles;
        this.users = users;
        this.repliedUser = repliedUser;
    }

    /**
     * Creates a new {@link AllowedMentions} object, with an empty parse array. Thereby not allowing any mentions
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public static AllowedMentions noneAllowed(){
        return new AllowedMentions(new AllowedMentionType[0], null, null, false);
    }


    /**
     *
     *
     * @return {@link SOData} for this {@link AllowedMentions}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        //an empty parse array, means no mentions are allowed
        data.addIfNotNull(PARSE_KEY, parse);

        //an empty user or roles array is a "falsy" value
        if(roles != null && roles.length > 0) data.add(ROLES_KEY, roles);
        if(users != null && users.length > 0) data.add(USERS_KEY, users);


        if(repliedUser) data.add(REPLIED_USER_KEY, true);

        return data;
    }
}
