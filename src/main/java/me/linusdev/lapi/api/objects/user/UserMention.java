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

package me.linusdev.lapi.api.objects.user;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * used in {@link me.linusdev.lapi.api.objects.message.abstracts.Message}
 */
public class UserMention implements Datable, HasLApi {

    public static final String MEMBER_KEY = "member";

    private final @NotNull LApi lApi;

    private final @NotNull User user;
    private final @Nullable Member member;

    /**
     *
     * @param lApi {@link LApi}
     * @param user the user mentioned
     * @param member additional partial member field
     */
    public UserMention(@NotNull LApi lApi, @NotNull User user, @Nullable Member member) {
        this.lApi = lApi;
        this.user = user;
        this.member = member;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data} with required fields
     * @return {@link UserMention}
     * @throws InvalidDataException see {@link User#fromData(LApi, Data)}
     */
    public static @NotNull UserMention fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {

        Data memberData = (Data) data.get(MEMBER_KEY);

        return new UserMention(lApi, User.fromData(lApi, data), Member.fromData(lApi, memberData));
    }

    /**
     * mentioned {@link User}
     */
    public @NotNull User getUser() {
        return user;
    }

    /**
     * additional partial member
     */
    public @Nullable Member getMember() {
        return member;
    }

    /**
     *
     * @return {@link Data} for this {@link UserMention}
     */
    @Override
    public Data getData() {
        Data data = user.getData();

        if(member != null) data.add(MEMBER_KEY, member);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}