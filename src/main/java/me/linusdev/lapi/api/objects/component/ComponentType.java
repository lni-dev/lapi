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

package me.linusdev.lapi.api.objects.component;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#component-object-component-types" target="_top">ComponentType</a>
 */
public enum ComponentType implements SimpleDatable {

    /**
     * LApi specific, not in Discord!
     */
    UNKNOWN(0),

    /**
     * A container for other components
     */
    ACTION_ROW(1),

    /**
     * A button object
     */
    BUTTON(2),

    /**
     * A select menu for picking from choices
     */
    SELECT_MENU(3),
    ;

    private final int value;

    ComponentType(int value){
        this.value = value;
    }

    /**
     *
     * @param value to get corresponding {@link ComponentType}
     * @return {@link ComponentType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ComponentType fromValue(int value){
        for(ComponentType type : ComponentType.values()){
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
