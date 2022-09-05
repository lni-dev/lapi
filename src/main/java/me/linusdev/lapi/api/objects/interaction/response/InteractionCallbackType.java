/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.objects.interaction.response;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-callback-type" target="_top">
 *     Interaction Callback Type</a>
 */
public enum InteractionCallbackType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    /**
     * ACK a Ping
     */
    PONG(1),

    /**
     * respond to an interaction with a message
      */
    CHANNEL_MESSAGE_WITH_SOURCE(4),

    /**
     * ACK an interaction and edit a response later, the user sees a loading state
     */
    DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5),

    /**
     * for components, ACK an interaction and edit the original message later; the user does not see a loading state
     */
    DEFERRED_UPDATE_MESSAGE(6),

    /**
     * for components, edit the message the component was attached to.<br>
     * Only valid for {@link me.linusdev.lapi.api.objects.message.component.Component component-based} interactions
     */
    UPDATE_MESSAGE(7),

    /**
     * respond to an autocomplete interaction with suggested choices
     */
    APPLICATION_COMMAND_AUTOCOMPLETE_RESULT(8),

    /**
     * respond to an interaction with a popup modal.<br>
     * Not available for MODAL_SUBMIT and PING interactions.
     */
    MODAL(9),
    ;

    private final int value;

    InteractionCallbackType(int value) {
        this.value = value;
    }

    /**
     *
     * @param value value
     * @return {@link InteractionCallbackType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull InteractionCallbackType fromValue(int value){
        for(InteractionCallbackType type : InteractionCallbackType.values()){
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
