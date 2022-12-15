/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.data.Datable;
import me.linusdev.data.entry.Entry;
import me.linusdev.data.implemantations.SAODataListImpl;
import me.linusdev.data.so.SAOData;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.attachment.Attachment;
import me.linusdev.lapi.api.objects.channel.PartialChannel;
import me.linusdev.lapi.api.objects.guild.member.PartialMember;
import me.linusdev.lapi.api.objects.message.AnyMessage;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @updated 15.12.2022
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-resolved-data-structure" target="_top">Resolved Data Strcuture</a>
 */
public class ResolvedData implements Datable, HasLApi {

    public static final String USERS_KEY = "users";
    public static final String MEMBERS_KEY = "members";
    public static final String ROLES_KEY = "roles";
    public static final String CHANNELS_KEY = "channels";
    public static final String MESSAGES_KEY = "messages";
    public static final String ATTACHMENTS_KEY = "attachments";

    private final @NotNull LApi lApi;

    private final @Nullable SAOData<User> users;
    private final @Nullable SAOData<PartialMember> members;
    private final @Nullable SAOData<Role> roles;
    private final @Nullable SAOData<PartialChannel> channels;
    private final @Nullable SAOData<AnyMessage> messages;
    private final @Nullable SAOData<Attachment> attachments;

    /**
     * @param lApi        {@link LApi}
     * @param users       the ids and User objects
     * @param members     the ids and partial Member objects
     * @param roles       the ids and Role objects
     * @param channels    the ids and partial Channel objects
     * @param messages    the ids and partial Message objects
     * @param attachments the ids and attachment objects
     */
    public ResolvedData(@NotNull LApi lApi, @Nullable SAOData<User> users, @Nullable SAOData<PartialMember> members,
                        @Nullable SAOData<Role> roles, @Nullable SAOData<PartialChannel> channels,
                        @Nullable SAOData<AnyMessage> messages, @Nullable SAOData<Attachment> attachments) {
        this.lApi = lApi;
        this.users = users;
        this.members = members;
        this.roles = roles;
        this.channels = channels;
        this.messages = messages;
        this.attachments = attachments;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link ResolvedData}
     * @throws InvalidDataException if fields are missing
     */
    public static @Nullable ResolvedData fromData(@NotNull LApi lApi, SOData data) throws InvalidDataException {
        if(data == null) return null;

        //users
        SOData usersData = data.getAs(USERS_KEY);
        SAOData<User> users = null;

        if(usersData != null) {
            users = new SAODataListImpl<>(new ArrayList<>(usersData.size()));

            for(Entry<String, Object> entry : usersData)
                users.add(entry.getKey(), User.fromData(lApi, (SOData) entry.getValue()));
        }

        //members
        SOData membersData = data.getAs(MEMBERS_KEY);
        SAOData<PartialMember> members = null;

        if(membersData != null) {
            members = new SAODataListImpl<>(new ArrayList<>(membersData.size()));

            for(Entry<String, Object> entry : membersData)
                members.add(entry.getKey(), PartialMember.fromData(lApi, (SOData) entry.getValue()));
        }

        //roles
        SOData rolesData = data.getAs(ROLES_KEY);
        SAOData<Role> roles = null;

        if(rolesData != null) {
            roles = new SAODataListImpl<>(new ArrayList<>(rolesData.size()));

            for(Entry<String, Object> entry : rolesData)
                roles.add(entry.getKey(), Role.fromData(lApi, (SOData) entry.getValue()));
        }

        //channels
        SOData channelsData = data.getAs(CHANNELS_KEY);
        SAOData<PartialChannel> channels = null;

        if(channelsData != null) {
            channels = new SAODataListImpl<>(new ArrayList<>(channelsData.size()));

            for(Entry<String, Object> entry : channelsData)
                channels.add(entry.getKey(), Channel.partialChannelFromData(lApi, (SOData) entry.getValue()));
        }

        //messages
        SOData messagesData = data.getAs(MESSAGES_KEY);
        SAOData<AnyMessage> messages = null;

        if(messagesData != null) {
            messages = new SAODataListImpl<>(new ArrayList<>(messagesData.size()));

            for(Entry<String, Object> entry : messagesData)
                messages.add(entry.getKey(), AnyMessage.fromData(lApi, (SOData) entry.getValue()));
        }

        //attachments
        SOData attachmentsData = data.getAs(MESSAGES_KEY);
        SAOData<Attachment> attachments = null;

        if(attachmentsData != null) {
            attachments = new SAODataListImpl<>(new ArrayList<>(attachmentsData.size()));

            for(Entry<String, Object> entry : attachmentsData)
                attachments.add(entry.getKey(), Attachment.fromData((SOData) entry.getValue()));
        }

        return new ResolvedData(lApi, users, members, roles, channels, messages, attachments);
    }

    /**
     * the ids and User objects
     */
    public @Nullable SAOData<User> getUsers() {
        return users;
    }

    /**
     * the ids and partial Member objects
     */
    public @Nullable SAOData<PartialMember> getMembers() {
        return members;
    }

    /**
     * the ids and Role objects
     */
    public @Nullable SAOData<Role> getRoles() {
        return roles;
    }

    /**
     * 	the ids and partial Channel objects
     */
    public @Nullable SAOData<PartialChannel> getChannels() {
        return channels;
    }

    /**
     * the ids and partial Message objects
     */
    public @Nullable SAOData<AnyMessage> getMessages() {
        return messages;
    }

    public @Nullable SAOData<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(5);

        data.addIfNotNull(USERS_KEY, users);
        data.addIfNotNull(MEMBERS_KEY, members);
        data.addIfNotNull(ROLES_KEY, roles);
        data.addIfNotNull(CHANNELS_KEY, channels);
        data.addIfNotNull(MESSAGES_KEY, messages);
        data.addIfNotNull(ATTACHMENTS_KEY, attachments);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
