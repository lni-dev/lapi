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

package me.linusdev.lapi.api.objects.message.abstracts;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.attachment.Attachment;
import me.linusdev.lapi.api.objects.enums.MessageType;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.objects.user.UserMention;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * @see Message
 */
public abstract class AbstractMessage implements Datable, Message, HasLApi {

    public static final String ID_KEY = "id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String AUTHOR_KEY = "author";
    public static final String MEMBER_KEY = "member";
    public static final String CONTENT_KEY = "content";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String EDITED_TIMESTAMP_KEY = "edited_timestamp";
    public static final String TTS_KEY = "tts";
    public static final String MENTION_EVERYONE_KEY = "mention_everyone";
    public static final String MENTIONS_KEY = "mentions";
    public static final String MENTION_ROLES_KEY = "mention_roles";
    public static final String MENTION_CHANNELS_KEY = "mention_channels";
    public static final String ATTACHMENTS_KEY = "attachments";
    public static final String EMBEDS_KEY = "embeds";
    public static final String REACTIONS_KEY = "reactions";
    public static final String NONCE_KEY = "nonce";
    public static final String PINNED_KEY = "pinned";
    public static final String WEBHOOK_ID_KEY = "webhook_id";
    public static final String TYPE_KEY = "type";
    public static final String ACTIVITY_KEY = "activity";
    public static final String APPLICATION_KEY = "application";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String MESSAGE_REFERENCE_KEY = "message_reference";
    public static final String FLAGS_KEY = "flags";
    public static final String REFERENCED_MESSAGE_KEY = "referenced_message";
    public static final String INTERACTION_KEY = "interaction";
    public static final String THREAD_KEY = "thread";
    public static final String COMPONENTS_KEY = "components";
    public static final String STICKER_ITEMS_KEY = "sticker_items";
    public static final String STICKERS_KEY = "stickers";


    protected @NotNull Snowflake id;
    protected @NotNull Snowflake channelId;
    protected @NotNull User author;
    protected @NotNull String content;
    protected @NotNull ISO8601Timestamp timestamp;
    protected @Nullable ISO8601Timestamp editedTimestamp;
    protected boolean tts;
    protected boolean mentionEveryone;
    protected @NotNull UserMention[] mentions;
    protected @NotNull Role[] mentionRoles;
    protected @NotNull Attachment[] attachments;
    protected @NotNull Embed[] embeds;
    protected boolean pinned;
    protected @NotNull MessageType type;
    protected int typeAsInt;

    protected final @NotNull LApi lApi;

    /**
     *
     * @param data {@link SOData} with required fields
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #CHANNEL_ID_KEY}, {@link #CONTENT_KEY}, {@link #TIMESTAMP_KEY}, {@link #TTS_KEY}, {@link #MENTION_EVERYONE_KEY}, {@link #MENTIONS_KEY}, {@link #MENTION_ROLES_KEY}, {@link #ATTACHMENTS_KEY}, {@link #EMBEDS_KEY}, {@link #PINNED_KEY} or {@link #TYPE_KEY} are missing or null
     */
    @SuppressWarnings("unchecked cast")
    protected AbstractMessage(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException{
        this.lApi = lApi;

        String id = (String) data.get(ID_KEY);
        String channelId = (String) data.get(CHANNEL_ID_KEY);
        SOData author = (SOData) data.get(AUTHOR_KEY);
        String content = (String) data.get(CONTENT_KEY);
        String timestamp = (String) data.get(TIMESTAMP_KEY);
        String editedTimestamp = (String) data.get(EDITED_TIMESTAMP_KEY);
        Boolean tts = (Boolean) data.get(TTS_KEY);
        Boolean mentionEveryone = (Boolean) data.get(MENTION_EVERYONE_KEY);
        List<Object> mentionsData = data.getList(MENTIONS_KEY);
        List<Object> mentionRolesData = data.getList(MENTION_ROLES_KEY);
        List<Object> attachmentsData = data.getList(ATTACHMENTS_KEY);
        List<Object> embedsData = data.getList(EMBEDS_KEY);
        Boolean pinned = (Boolean) data.get(PINNED_KEY);
        Number type = (Number) data.get(TYPE_KEY);

        if(id == null || channelId == null || author == null || content == null || timestamp == null || tts == null || mentionEveryone == null
                || mentionsData == null || mentionRolesData == null || attachmentsData == null || embedsData == null
                || pinned == null || type == null){
            InvalidDataException.throwException(data, null, AbstractMessage.class,
                    new Object[]{id, channelId, author, content, timestamp, tts, mentionEveryone, mentionsData, mentionRolesData, attachmentsData, embedsData, pinned, type},
                    new String[]{ID_KEY, CHANNEL_ID_KEY, AUTHOR_KEY, CONTENT_KEY, TIMESTAMP_KEY, TTS_KEY, MENTION_EVERYONE_KEY, MENTIONS_KEY, MENTION_ROLES_KEY, ATTACHMENTS_KEY, EMBEDS_KEY, PINNED_KEY, TYPE_KEY});
            throw new RuntimeException(); //this will never happen, because above method will throw an exception
        }

        this.id = Snowflake.fromString(id);
        this.channelId = Snowflake.fromString(channelId);
        this.author = User.fromData(lApi, author);
        this.content = content;
        this.timestamp = ISO8601Timestamp.fromString(timestamp);
        this.editedTimestamp = ISO8601Timestamp.fromString(editedTimestamp);
        this.tts = tts;
        this.mentionEveryone = mentionEveryone;

        this.mentions = new UserMention[mentionsData.size()];
        int i = 0;
        for(Object o : mentionsData)
            this.mentions[i++] = UserMention.fromData(lApi, (SOData) o);

        this.mentionRoles = new Role[mentionRolesData.size()];
        i = 0;
        for(Object o : mentionRolesData)
            this.mentionRoles[i++] = Role.fromData(lApi, (SOData) o);

        this.attachments = new Attachment[attachmentsData.size()];
        i = 0;
        for(Object o : attachmentsData)
            this.attachments[i++] = new Attachment((SOData) o);

        this.embeds = new Embed[embedsData.size()];
        i = 0;
        for(Object o : embedsData)
            this.embeds[i++] = Embed.fromData((SOData) o);

        this.pinned = pinned;
        this.type = MessageType.fromValue(type.intValue());
        this.typeAsInt = type.intValue();
    }


    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @NotNull Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    @Override
    public @NotNull User getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public @NotNull ISO8601Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public @Nullable ISO8601Timestamp getEditedTimestamp() {
        return editedTimestamp;
    }

    @Override
    public boolean isTTS() {
        return tts;
    }

    @Override
    public boolean mentionsEveryone() {
        return mentionEveryone;
    }


    @Override
    public @NotNull UserMention[] getMentions() {
        return mentions;
    }

    @Override
    public @NotNull Role[] getRoleMentions() {
        return mentionRoles;
    }

    @Override
    public @NotNull Attachment[] getAttachments() {
        return attachments;
    }

    @Override
    public @NotNull Embed[] getEmbeds() {
        return embeds;
    }

    @Override
    public boolean isPinned() {
        return pinned;
    }

    @Override
    public int getTypeAsValue() {
        return typeAsInt;
    }

    @Override
    public @Nullable MessageType getTypeAsMessageType() {
        return type;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    /**
     *
     * @return {@link SOData} for this {@link AbstractMessage}
     */
    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(20);

        data.add(ID_KEY, id);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(AUTHOR_KEY, author);
        data.add(CONTENT_KEY, content);
        data.add(TIMESTAMP_KEY, timestamp);
        data.add(EDITED_TIMESTAMP_KEY, editedTimestamp);
        data.add(TTS_KEY, tts);
        data.add(MENTION_EVERYONE_KEY, mentionEveryone);
        data.add(MENTIONS_KEY, mentions);
        data.add(MENTION_ROLES_KEY, mentionRoles);
        data.add(ATTACHMENTS_KEY, attachments);
        data.add(EMBEDS_KEY, embeds);
        data.add(PINNED_KEY, pinned);
        data.add(TYPE_KEY, type);

        return data;
    }

}
