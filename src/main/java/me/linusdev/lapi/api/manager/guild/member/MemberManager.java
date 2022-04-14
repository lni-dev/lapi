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

package me.linusdev.lapi.api.manager.guild.member;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MemberManager extends Manager, HasLApi, MemberPool {

    @NotNull Member addMember(@NotNull Data data) throws InvalidDataException;

    @NotNull Member onMemberAdd(@NotNull Data data) throws InvalidDataException;
    @Nullable Update<Member, Member> onMemberUpdate(@NotNull String userId, @NotNull Data data) throws InvalidDataException;
    @Nullable Member onMemberRemove(@NotNull String userId);

    void onGuildMemberChunk(@NotNull Data data);

}