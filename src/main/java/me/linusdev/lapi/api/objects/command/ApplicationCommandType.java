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

package me.linusdev.lapi.api.objects.command;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-types" target="_top">Application Command Types</a>
 */
public enum ApplicationCommandType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    /**
     * Slash commands; a text-based command that shows up when a user types /
     */
    CHAT_INPUT(1),

    /**
     * A UI-based command that shows up when you right click or tap on a user
     */
    USER(2),

    /**
     * A UI-based command that shows up when you right click or tap on a message
     */
    MESSAGE(3),

    ;


    private final int value;

    ApplicationCommandType(int value) {
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link ApplicationCommandType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ApplicationCommandType fromValue(int value){
        for(ApplicationCommandType type : ApplicationCommandType.values()){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
