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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-secrets" target="_top">Activity Secrets</a>
 */
public class ActivitySecrets implements Datable {

    public static final String JOIN_KEY = "join";
    public static final String SPECTATE_KEY = "spectate";
    public static final String MATCH_KEY = "match";

    private final @Nullable String join;
    private final @Nullable String spectate;
    private final @Nullable String match;

    /**
     *
     * @param join the secret for joining a party
     * @param spectate the secret for spectating a game
     * @param match the secret for a specific instanced match
     */
    public ActivitySecrets(@Nullable String join, @Nullable String spectate, @Nullable String match) {
        this.join = join;
        this.spectate = spectate;
        this.match = match;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ActivitySecrets}
     */
    public static @Nullable ActivitySecrets fromData(@Nullable Data data){
        if(data == null) return null;
        String join = (String) data.get(JOIN_KEY);
        String spectate = (String) data.get(SPECTATE_KEY);
        String match = (String) data.get(MATCH_KEY);

        return new ActivitySecrets(join, spectate, match);
    }

    /**
     * the secret for joining a party
     */
    public @Nullable String getJoin() {
        return join;
    }

    /**
     * the secret for spectating a game
     */
    public @Nullable String getSpectate() {
        return spectate;
    }

    /**
     * the secret for a specific instanced match
     */
    public @Nullable String getMatch() {
        return match;
    }

    @Override
    public Data getData() {
        Data data = new Data(3);

        data.addIfNotNull(JOIN_KEY, join);
        data.addIfNotNull(SPECTATE_KEY, spectate);
        data.addIfNotNull(MATCH_KEY, match);

        return data;
    }
}
