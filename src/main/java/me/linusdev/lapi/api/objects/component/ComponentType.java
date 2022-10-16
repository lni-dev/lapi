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
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.component.actionrow.ActionRow;
import me.linusdev.lapi.api.objects.component.button.Button;
import me.linusdev.lapi.api.objects.component.selectmenu.SelectMenu;
import me.linusdev.lapi.api.objects.component.textinput.TextInput;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#component-object-component-types" target="_top">ComponentType</a>
 * @updated 16.10.2022
 */
public enum ComponentType implements SimpleDatable {

    /**
     * LApi specific, not in Discord!
     */
    UNKNOWN(0) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return UnknownComponent.fromData(data);
        }
    },

    /**
     * A container for other components
     */
    ACTION_ROW(1) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return ActionRow.fromData(lApi, data);
        }
    },

    /**
     * A button object
     */
    BUTTON(2) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return Button.fromData(lApi, data);
        }
    },

    /**
     * A select menu for picking from choices
     */
    SELECT_MENU(3) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return SelectMenu.fromData(lApi, data);
        }
    },

    /**
     * Text input object
     */
    TEXT_INPUT(4) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return TextInput.fromData(lApi, data);
        }
    },

    /**
     * Select menu for users
     */
    USER_SELECT(5) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return SelectMenu.fromData(lApi, data);
        }
    },

    /**
     * Select menu for roles
     */
    ROLE_SELECT(6) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return SelectMenu.fromData(lApi, data);
        }
    },

    /**
     * Select menu for mentionables (users and roles)
     */
    MENTIONABLE(7) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return SelectMenu.fromData(lApi, data);
        }
    },

    /**
     * Select menu for channels
     */
    CHANNEL_SELECT(8) {
        @Override
        @Nullable
        Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
            return SelectMenu.fromData(lApi, data);
        }
    },
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

    @Contract("_, null -> null; _, !null -> !null")
    abstract @Nullable Component fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException;

    @Override
    public Object simplify() {
        return value;
    }
}
