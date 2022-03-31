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

package me.linusdev.lapi.api.manager;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.log.Logger;
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
        try {
            get(userId).updateSelfByData(voiceStateData);
        } catch (me.linusdev.lapi.api.communication.exceptions.InvalidDataException e) {
            Logger.getLogger(this.getClass()).error(e);
        }
        return true;
    }
}
