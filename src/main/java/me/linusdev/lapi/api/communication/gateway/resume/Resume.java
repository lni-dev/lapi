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

package me.linusdev.lapi.api.communication.gateway.resume;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#resume-resume-structure" target="_top">Resume Structure</a>
 */
public class Resume implements Datable {

    public static final String TOKEN_KEY = "token";
    public static final String SESSION_ID_KEY = "session_id";
    public static final String SEQUENCE_KEY = "seq";

    private final @NotNull String token;
    private final @NotNull String sessionId;
    private final long sequence;

    /**
     *
     * @param token session token
     * @param sessionId session id
     * @param sequence last sequence number received
     */
    public Resume(@NotNull String token, @NotNull String sessionId, long sequence) {
        this.token = token;
        this.sessionId = sessionId;
        this.sequence = sequence;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(TOKEN_KEY, token);
        data.add(SESSION_ID_KEY, sessionId);
        data.add(SEQUENCE_KEY, sequence);

        return data;
    }
}
