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

package me.linusdev.lapi.api.objects.application;

import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/application#application-object-application-flags" target="_top">Application Flags</a>
 */
public enum ApplicationFlag{
    GATEWAY_PRESENCE                    (1 << 12),
    GATEWAY_PRESENCE_LIMITED            (1 << 13),
    GATEWAY_GUILD_MEMBERS               (1 << 14),
    GATEWAY_GUILD_MEMBERS_LIMITED       (1 << 15),
    VERIFICATION_PENDING_GUILD_LIMIT    (1 << 16),
    EMBEDDED                            (1 << 17),
    GATEWAY_MESSAGE_CONTENT             (1 << 18),
    GATEWAY_MESSAGE_CONTENT_LIMITED     (1 << 19),
    ;

    private final int value;

    ApplicationFlag(int value){
        this.value = value;
    }

    /**
     *
     * @param flags int with set flags
     * @return {@link ApplicationFlag}[] with corresponding {@link ApplicationFlag application flags}
     */
    public static @NotNull ApplicationFlag[] getFlagsFromInteger(int flags){
        ApplicationFlag[] flagsArray = new ApplicationFlag[Integer.bitCount(flags)];

        int i = 0;
        for(ApplicationFlag flag : ApplicationFlag.values()){
            if((flags & flag.getValue()) == flag.getValue())
                flagsArray[i++] = flag;
        }

        return flagsArray;
    }

    /**
     * value of this flag as {@link Integer} with 1 bit set
     */
    public int getValue() {
        return value;
    }
}
