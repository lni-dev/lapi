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

package me.linusdev.lapi.api.manager.guild;

import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.AbstractVoiceStateManager;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class VoiceStatesManager implements AbstractVoiceStateManager {

    private final @NotNull LApi lApi;

    private final @NotNull HashMap<String, VoiceState> voiceStates;

    public VoiceStatesManager(@NotNull LApi lApi, int initialCapacity){
        this.lApi = lApi;
        this.voiceStates = new HashMap<>(initialCapacity);
    }

    @Override
    public @Nullable VoiceState get(@Nullable String userId) {
        return voiceStates.get(userId);
    }

    @Override
    public AbstractVoiceStateManager add(@NotNull VoiceState voiceState) {
        voiceStates.put(voiceState.getUserId(), voiceState);
        return this;
    }

}
