package me.linusdev.discordbotapi.api.manager;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.guild.voice.VoiceState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class VoiceStatesManager implements AbstractVoiceStateManager{

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
