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

package me.linusdev.lapi.api.objects.interaction;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.message.abstracts.Message;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * TODO Discord says, the arrays would be maps. JSON does not support maps tho... will have to see what they mean
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-resolved-data-structure" target="_top">Resolved Data Strcuture</a>
 */
public class ResolvedData implements Datable, HasLApi {

    public static final String USERS_KEY = "users";
    public static final String MEMBERS_KEY = "members";
    public static final String ROLES_KEY = "roles";
    public static final String CHANNELS_KEY = "channels";
    public static final String MESSAGES_KEY = "messages";

    private final @NotNull LApi lApi;

    private final @Nullable ArrayList<User> users;
    private final @Nullable ArrayList<Member> members;
    private final @Nullable ArrayList<Role> roles;
    private final @Nullable ArrayList<Channel> channels;
    private final @Nullable ArrayList<Message> messages;

    /**
     *
     * @param lApi {@link LApi}
     * @param users the ids and User objects
     * @param members the ids and partial Member objects
     * @param roles the ids and Role objects
     * @param channels the ids and partial Channel objects
     * @param messages the ids and partial Message objects
     */
    public ResolvedData(@NotNull LApi lApi, @Nullable ArrayList<User> users, @Nullable ArrayList<Member> members, @Nullable ArrayList<Role> roles, @Nullable ArrayList<Channel> channels, @Nullable ArrayList<Message> messages) {
        this.lApi = lApi;
        this.users = users;
        this.members = members;
        this.roles = roles;
        this.channels = channels;
        this.messages = messages;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data}
     * @return {@link ResolvedData}
     * @throws InvalidDataException see {@link User#fromData(LApi, Data)}, {@link Member#fromData(LApi, Data)}, {@link Role#fromData(LApi, Data)}, {@link Channel#fromData(LApi, Data)}, {@link MessageImplementation#MessageImplementation(LApi, Data)}
     */
    public static @Nullable ResolvedData fromData(@NotNull LApi lApi, Data data) throws InvalidDataException {
        if(data == null) return null;
        ArrayList<User> users = data.getAndConvertArrayList(USERS_KEY,
                (ExceptionConverter<Data, User, InvalidDataException>) convertible -> User.fromData(lApi, convertible));

        ArrayList<Member> members = data.getAndConvertArrayList(MEMBERS_KEY,
                (ExceptionConverter<Data, Member, InvalidDataException>) convertible -> Member.fromData(lApi, convertible));

        ArrayList<Role> roles = data.getAndConvertArrayList(ROLES_KEY,
                (ExceptionConverter<Data, Role, InvalidDataException>) convertible -> Role.fromData(lApi, convertible));

        ArrayList<Channel> channels = data.getAndConvertArrayList(CHANNELS_KEY,
                (ExceptionConverter<Data, Channel, InvalidDataException>) convertible -> Channel.fromData(lApi, convertible));

        ArrayList<Message> messages = data.getAndConvertArrayList(MESSAGES_KEY,
                (ExceptionConverter<Data, Message, InvalidDataException>) convertible -> new MessageImplementation(lApi, convertible));


        return new ResolvedData(lApi, users, members, roles, channels, messages);
    }

    /**
     * the ids and User objects
     */
    public @Nullable ArrayList<User> getUsers() {
        return users;
    }

    /**
     * the ids and partial Member objects
     */
    public @Nullable ArrayList<Member> getMembers() {
        return members;
    }

    /**
     * the ids and Role objects
     */
    public @Nullable ArrayList<Role> getRoles() {
        return roles;
    }

    /**
     * 	the ids and partial Channel objects
     */
    public @Nullable ArrayList<Channel> getChannels() {
        return channels;
    }

    /**
     * the ids and partial Message objects
     */
    public @Nullable ArrayList<Message> getMessages() {
        return messages;
    }

    @Override
    public Data getData() {
        Data data = new Data(5);

        data.addIfNotNull(USERS_KEY, users);
        data.addIfNotNull(MEMBERS_KEY, members);
        data.addIfNotNull(ROLES_KEY, roles);
        data.addIfNotNull(CHANNELS_KEY, channels);
        data.addIfNotNull(MESSAGES_KEY, messages);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
