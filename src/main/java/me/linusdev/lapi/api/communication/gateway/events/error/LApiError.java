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

package me.linusdev.lapi.api.communication.gateway.events.error;

import org.jetbrains.annotations.Nullable;

public class LApiError {

    public enum ErrorCode{
        UNKNOWN_GUILD,
        UNKNOWN_ROLE,
        UNKNOWN_MEMBER,
        UNKNOWN_CHANNEL,
        UNKNOWN_STAGE_INSTANCE,
        UNKNOWN_GUILD_SCHEDULED_EVENT,
    }

    private final @Nullable ErrorCode code;
    private final @Nullable String message;

    public LApiError(@Nullable ErrorCode code, @Nullable String message){
        this.code = code;
        this.message = message;
    }

    public @Nullable ErrorCode getCode() {
        return code;
    }

    public @Nullable String getMessage() {
        return message;
    }
}
