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

package me.linusdev.lapi.api.communication.gateway.events.interaction;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.interaction.InteractionData;
import me.linusdev.lapi.api.objects.interaction.InteractionType;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractionCreateEvent extends Event {

    private final @NotNull Interaction interaction;

    public InteractionCreateEvent(@NotNull LApi lApi, @NotNull GatewayPayloadAbstract payload,
                                  @Nullable Snowflake guildId, @NotNull Interaction interaction) {
        super(lApi, payload, guildId);
        this.interaction = interaction;
    }

    public @NotNull Interaction getInteraction() {
        return interaction;
    }

    /**
     * Will always be present for
     * {@link InteractionType#APPLICATION_COMMAND APPLICATION_COMMAND},
     * {@link InteractionType#APPLICATION_COMMAND_AUTOCOMPLETE APPLICATION_COMMAND_AUTOCOMPLETE}
     * {@link InteractionType#MESSAGE_COMPONENT MESSAGE_COMPONENT},
     * and {@link InteractionType#MODAL_SUBMIT MODAL_SUBMIT}.
     * @return custom id of this interaction
     */
    public @Nullable String getCustomId() {
        InteractionData data = interaction.getInteractionData();
        if(data == null) return null;
        return data.getCustomId();
    }

    /**
     * @return {@link InteractionType type} of the interaction
     */
    public @NotNull InteractionType getType(){
        return interaction.getType();
    }


}
