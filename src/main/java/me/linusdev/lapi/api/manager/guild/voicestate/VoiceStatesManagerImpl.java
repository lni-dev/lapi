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

package me.linusdev.lapi.api.manager.guild.voicestate;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class VoiceStatesManagerImpl implements VoiceStateManager {

    private final @NotNull LApiImpl lApi;
    private boolean initialized = false;

    private @Nullable ConcurrentHashMap<String, VoiceState> voiceStates;

    public VoiceStatesManagerImpl(@NotNull LApiImpl lApi) {
        this.lApi = lApi;

    }

    @Override
    public void init(int initialCapacity) {
        this.voiceStates = new ConcurrentHashMap<>(initialCapacity);
        this.initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public @Nullable VoiceState get(@Nullable String userId) {
        if (voiceStates == null) throw new UnsupportedOperationException("init() not yet called!");
        return voiceStates.get(userId);
    }

    @Override
    public VoiceState add(@NotNull VoiceState voiceState) {
        if (voiceStates == null) throw new UnsupportedOperationException("init() not yet called!");
        voiceStates.put(voiceState.getUserId(), voiceState);
        return voiceState;
    }

    @Override
    public @NotNull Update<VoiceState, VoiceState> update(@NotNull SOData voiceStateData) throws InvalidDataException {
        String userId = (String) voiceStateData.get(VoiceState.USER_ID_KEY);

        if (userId == null)
            throw new InvalidDataException(voiceStateData, "missing user id.", null, VoiceState.USER_ID_KEY);

        VoiceState state = get(userId);

        if (state == null){
            if (!lApi.getConfig().isFlagSet(ConfigFlag.COPY_VOICE_STATE_ON_UPDATE_EVENT)) {
                return new Update<>(null, add(VoiceState.fromData(getLApi(), voiceStateData)));
            } else {
                //we have no old state yet.
                return new Update<>(add(VoiceState.fromData(getLApi(), voiceStateData)), true);
            }
        }


        if (!lApi.getConfig().isFlagSet(ConfigFlag.COPY_VOICE_STATE_ON_UPDATE_EVENT)) {
            state.updateSelfByData(voiceStateData);
            return new Update<>(null, state);
        }

        return new Update<>(state, voiceStateData);

    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
