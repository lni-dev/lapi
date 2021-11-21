package me.linusdev.discordbotapi.api.objects.invite;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.guild.member.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

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
     * @param data {@link Data} with required fields
     * @return {@link InviteStageInstance}
     * @throws InvalidDataException if {@link #MEMBERS_KEY}, {@link #PARTICIPANT_COUNT_KEY}, {@link #SPEAKER_COUNT_KEY} or {@link #TOPIC_KEY} are missing or {@code null}
     */
    public static @Nullable InviteStageInstance fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        @SuppressWarnings("unchecked")
        ArrayList<Object> membersData = (ArrayList<Object>) data.get(MEMBERS_KEY);
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
            members[i++] = Member.fromData(lApi, (Data) o);

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
     * @return {@link Data} for this {@link InviteStageInstance}
     */
    @Override
    public Data getData() {
        Data data = new Data(4);

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
