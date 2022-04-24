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

package me.linusdev.lapi.api.objects.invite;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/invite#invite-stage-instance-object" target="_top">Invite Stage Instance Object</a>
 */
public class InviteStageInstance implements Datable, HasLApi {

    public static final String MEMBERS_KEY = "members";
    public static final String PARTICIPANT_COUNT_KEY = "participant_count";
    public static final String SPEAKER_COUNT_KEY = "speaker_count";
    public static final String TOPIC_KEY = "topic";

    private final @NotNull LApi lApi;

    private final @NotNull Member[] members;
    private final int participantsCount;
    private final int speakerCount;
    private final @NotNull String topic;

    /**
     *
     * @param lApi {@link LApi}
     * @param members the members speaking in the Stage
     * @param participantsCount the number of users in the Stage
     * @param speakerCount the number of users speaking in the Stage
     * @param topic the topic of the Stage instance (1-120 characters)
     */
    public InviteStageInstance(@NotNull LApi lApi, @NotNull Member[] members, int participantsCount, int speakerCount, @NotNull String topic) {
        this.lApi = lApi;
        this.members = members;
        this.participantsCount = participantsCount;
        this.speakerCount = speakerCount;
        this.topic = topic;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData} with required fields
     * @return {@link InviteStageInstance}
     * @throws InvalidDataException if {@link #MEMBERS_KEY}, {@link #PARTICIPANT_COUNT_KEY}, {@link #SPEAKER_COUNT_KEY} or {@link #TOPIC_KEY} are missing or {@code null}
     */
    public static @Nullable InviteStageInstance fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        List<Object> membersData = data.getList(MEMBERS_KEY);
        Number participantsCount = (Number) data.get(PARTICIPANT_COUNT_KEY);
        Number speakerCount = (Number) data.get(SPEAKER_COUNT_KEY);
        String topic = (String) data.get(TOPIC_KEY);

        if(membersData == null || participantsCount == null || speakerCount == null || topic == null){
            InvalidDataException.throwException(data, null, InviteStageInstance.class,
                    new Object[]{membersData, participantsCount, speakerCount, topic},
                    new String[]{MEMBERS_KEY, PARTICIPANT_COUNT_KEY, SPEAKER_COUNT_KEY, TOPIC_KEY});
            return null; //this will never happen, because above method will throw an exception
        }

        Member[] members = new Member[membersData.size()];
        int i = 0;
        for(Object o : membersData)
            members[i++] = Member.fromData(lApi, (SOData) o);

        return new InviteStageInstance(lApi, members, participantsCount.intValue(), speakerCount.intValue(), topic);
    }

    /**
     * array of partial {@link Member guild member} objects.
     * the members speaking in the Stage
     */
    public @NotNull Member[] getMembers() {
        return members;
    }

    /**
     * the number of users in the Stage
     */
    public int getParticipantsCount() {
        return participantsCount;
    }

    /**
     * the number of users speaking in the Stage
     */
    public int getSpeakerCount() {
        return speakerCount;
    }

    /**
     * the topic of the Stage instance (1-120 characters)
     */
    public @NotNull String getTopic() {
        return topic;
    }

    /**
     *
     * @return {@link SOData} for this {@link InviteStageInstance}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(MEMBERS_KEY, members);
        data.add(PARTICIPANT_COUNT_KEY, participantsCount);
        data.add(SPEAKER_COUNT_KEY, speakerCount);
        data.add(TOPIC_KEY, topic);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
