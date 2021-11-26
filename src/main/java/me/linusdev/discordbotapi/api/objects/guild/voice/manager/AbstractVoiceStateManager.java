package me.linusdev.discordbotapi.api.objects.guild.voice.manager;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.objects.guild.member.Member;
import me.linusdev.discordbotapi.api.objects.guild.voice.VoiceState;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;

public interface AbstractVoiceStateManager {

    VoiceState get(String userId);

    default VoiceState get(User user){
        return get(user.getId());
    }

    default VoiceState get(Member member){
        if(member.getUser() == null) return null;
        return get(member.getUser());
    }

    AbstractVoiceStateManager add(VoiceState voiceState);

    default boolean update(@NotNull Data voiceStateData){
        String userId = (String) voiceStateData.get(VoiceState.USER_ID_KEY);

        if(userId == null) return false; //TODO throw exception?
        get(userId).updateSelfByData(voiceStateData);
        return true;
    }
}
