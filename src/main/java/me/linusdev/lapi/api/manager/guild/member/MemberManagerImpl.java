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

package me.linusdev.lapi.api.manager.guild.member;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class MemberManagerImpl implements MemberManager{

    private final @NotNull LApi lApi;
    private boolean initialized = false;

    private @Nullable ConcurrentHashMap<String, Member> members;

    public MemberManagerImpl(@NotNull LApiImpl lApi) {
        this.lApi = lApi;
    }

    @Override
    public void init(int initialCapacity) {
        this.members = new ConcurrentHashMap<>(initialCapacity);
        this.initialized = true;
    }

    @Override
    public @NotNull Member addMember(@NotNull SOData data) throws InvalidDataException {
        if(members == null) throw new UnsupportedOperationException("init() not yet called");
        Member member = Member.fromData(lApi, data);
        User user = member.getUser();
        if(user == null)
            throw new InvalidDataException(data, "member data missing user field, but required for MemberManager");

        members.put(user.getId(), member);
        return member;
    }

    @Override
    public @NotNull Member onMemberAdd(@NotNull SOData data) throws InvalidDataException {
        if(members == null) throw new UnsupportedOperationException("init() not yet called");
        return addMember(data);
    }

    @Override
    public @Nullable Update<Member, Member> onMemberUpdate(@NotNull String userId, @NotNull SOData data) throws InvalidDataException {
        if(members == null) throw new UnsupportedOperationException("init() not yet called");

        Member member = members.get(userId);

        if(member == null) {
            Logger.getLogger(this).warning("Trying to update member that does not exist...");
            return null;
        }

        if(!lApi.isCopyOldMemberOnUpdateEventEnabled()){
            member.updateSelfByData(data);
            return new Update<>(null, member);
        }

        return new Update<>(member, data);
    }

    @Override
    public @Nullable Member onMemberRemove(@NotNull String userId) {
        if(members == null) throw new UnsupportedOperationException("init() not yet called");
        return members.remove(userId);
    }

    @Override
    public void onGuildMemberChunk(@NotNull SOData data) {
        if(members == null) throw new UnsupportedOperationException("init() not yet called");
        //TODO: implement
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public Member get(@NotNull String userId) {
        if(members == null) throw new UnsupportedOperationException("init() not yet called");
        return members.get(userId);
    }
}
