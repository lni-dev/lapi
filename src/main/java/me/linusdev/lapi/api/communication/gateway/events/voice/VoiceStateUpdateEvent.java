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

package me.linusdev.lapi.api.communication.gateway.events.voice;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class VoiceStateUpdateEvent extends Event {

    private final @NotNull Update<VoiceState, VoiceState> update;

    public VoiceStateUpdateEvent(@NotNull LApi lApi,
                                 @Nullable GatewayPayloadAbstract payload, @Nullable Snowflake guildId,
                                 @NotNull Update<VoiceState, VoiceState> update) {
        super(lApi, payload, guildId);

        this.update = update;
    }

    /**
     * @return The {@link Update}
     * @see #getVoiceState()
     * @see #getOldVoiceState()
     * @see #isNewVoiceState()
     */
    public @NotNull Update<VoiceState, VoiceState> getUpdate() {
        return update;
    }

    /**
     *
     * @return the updated {@link VoiceState}
     */
    public @NotNull VoiceState getVoiceState(){
        return update.getObj();
    }

    /**
     * A copy of the {@link VoiceState} before it was updated. Only available if
     * {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_VOICE_STATE_ON_UPDATE_EVENT COPY_VOICE_STATE_ON_UPDATE_EVENT}
     * is enabled.<br>
     * Note: even if above criteria are met, this method may still be {@code null}, if {@link #isNewVoiceState()}
     * returns {@code true}
     * @return copy of the {@link #getVoiceState()} before it was updated
     */
    public @Nullable VoiceState getOldVoiceState(){
        return update.getCopy();
    }

    /**
     * If this returns {@code true}, {@link #getOldVoiceState()} will always be {@code null}.
     * @return {@code true} if this is a new {@link VoiceState}
     */
    public boolean isNewVoiceState(){
        return update.isNew();
    }
}
