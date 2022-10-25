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

package me.linusdev.lapi.api.objects.message.impl;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.message.concrete.CreateEventMessage;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.UserMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateEventMessageImpl extends MessageImpl implements CreateEventMessage {

    protected final @Nullable Snowflake guildId;
    protected final @Nullable Member member;
    protected final @NotNull List<UserMember> mentionsWithMember;

    @SuppressWarnings("ConstantConditions")
    public CreateEventMessageImpl(@NotNull LApi lApi, @NotNull SOData data, boolean doNullChecks) throws InvalidDataException {
        super(lApi, data, doNullChecks);

        guildId = data.getAndConvert(GUILD_ID_KEY, Snowflake::fromString);
        member = data.getAndConvertWithException(MEMBER_KEY, (SOData c) -> Member.fromData(lApi, c));
        mentionsWithMember = data.getListAndConvertWithException(MENTIONS_KEY, (SOData c) -> UserMember.fromData(lApi, c));

        if(doNullChecks && mentionsWithMember == null) {
            InvalidDataException.throwException(data, null, CreateEventMessageImpl.class,
                    new Object[]{null}, new String[]{MENTIONS_KEY});
            return; // unreachable
        }

    }

    @Override
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    @Override
    public @Nullable Member getMember() {
        return member;
    }

    @Override
    public @NotNull List<UserMember> getMentionsWithMember() {
        return mentionsWithMember;
    }

    @Override
    public SOData getData() {
        SOData data = super.getData();

        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.addIfNotNull(MEMBER_KEY, member);
        data.addOrReplace(MENTIONS_KEY, mentionsWithMember);

        return data;
    }
}
