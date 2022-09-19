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

package me.linusdev.lapi.api.communication.gateway.events.thread;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <a href="https://discord.com/developers/docs/topics/gateway#thread-members-update-thread-members-update-event-fields" target="_top">Thread Members Update Event Fields</a>
 */
public class ExtraThreadMember extends ThreadMember implements HasLApi {

    public static final String PRESENCE_KEY = "presence";
    public static final String MEMBER_KEY = "member";

    private final @NotNull LApi lApi;

    private final @NotNull Member member;
    private final @Nullable PresenceUpdate presence;

    /**
     * @param id            the id of the thread
     * @param userId        the id of the user
     * @param joinTimestamp the time the current user last joined the thread
     * @param flags         any user-thread settings, currently only used for notifications
     * @param member        guild member
     * @param presence      presence
     */
    public ExtraThreadMember(@NotNull LApi lApi, @Nullable Snowflake id, @Nullable Snowflake userId, @NotNull ISO8601Timestamp joinTimestamp, int flags, @NotNull Member member, @Nullable PresenceUpdate presence) {
        super(id, userId, joinTimestamp, flags);

        this.lApi = lApi;
        this.member = member;
        this.presence = presence;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link ExtraThreadMember}
     */
    @Contract(value = "_, null -> null; _, !null -> !null", pure = true)
    public static @Nullable ExtraThreadMember fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String userId = (String) data.get(USER_ID_KEY);
        String joinTimestamp = (String) data.get(JOIN_TIMESTAMP_KEY);
        Number flags = (Number) data.get(FLAGS_KEY);
        SOData memberData = (SOData) data.get(MEMBER_KEY);
        SOData presenceData = (SOData) data.get(PRESENCE_KEY);

        if(joinTimestamp == null || flags == null || memberData == null){
            InvalidDataException.throwException(data, null, ThreadMember.class,
                    new Object[]{joinTimestamp, flags, memberData},
                    new String[]{JOIN_TIMESTAMP_KEY, FLAGS_KEY, MEMBER_KEY});
            return null; //this will never happen, because above method will throw an Exception
        }

        return new ExtraThreadMember(lApi, Snowflake.fromString(id), Snowflake.fromString(userId), ISO8601Timestamp.fromString(joinTimestamp),
                flags.intValue(), Member.fromData(lApi, memberData), PresenceUpdate.fromData(presenceData));
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    /**
     * guild member for the added thread member
     */
    public @NotNull Member getMember() {
        return member;
    }

    /**
     * presence for the added thread member
     */
    public @Nullable PresenceUpdate getPresence() {
        return presence;
    }

    @Override
    public SOData getData() {
        SOData data = super.getData();

        data.add(MEMBER_KEY, member);
        data.add(PRESENCE_KEY, presence);

        return data;
    }
}
