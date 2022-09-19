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

package me.linusdev.lapi.api.communication;


import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * The different api versions of discord. As of writing this enum, the api version {@link me.linusdev.lapi.api.lapi.LApi LApi}
 * uses for its HttpRequests is always {@link #V9}. So this enum is only useful for the {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket Gateway}
 * @see <a href="https://discord.com/developers/docs/reference#api-versioning-api-versions" target="_top">API Versions</a>
 */
public enum ApiVersion implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN("9", -1),

    /**
     * Available
     */
    V10("10", 10),

    /**
     * Available
     */
    V9("9", 9),

    /**
     * Available
     */
    V8("8", 8),

    /**
     * 	Doesn't look like anything to me
     */
    V7("7", 7),

    /**
     * Deprecated
     */
    @Deprecated
    V6("6", 6),

    /**
     * Discontinued
     */
    V5("5", 5),

    /**
     * Discontinued
     */
    V4("4", 4),

    /**
     * Discontinued
     */
    V3("3", 3),

    ;

    private final @NotNull String versionNumberString;
    private final int versionNumber;

    ApiVersion(@NotNull String versionNumberString, int versionNumber) {
        this.versionNumberString = versionNumberString;
        this.versionNumber = versionNumber;
    }

    /**
     *
     * @param version int
     * @return {@link ApiVersion} matching given version or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ApiVersion fromInt(int version){
        for(ApiVersion api : ApiVersion.values()){
            if(api.versionNumber == version)
                return api;
        }

        return UNKNOWN;
    }

    public @NotNull String getVersionNumber() {
        return versionNumberString;
    }

    @Override
    public Object simplify() {
        return versionNumber;
    }
}
