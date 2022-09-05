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

package me.linusdev.lapi.api.communication.gateway.events.message.reaction;

import me.linusdev.data.AbstractData;
import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#message-reaction-add-message-reaction-add-event-fields" target="_TOP">
 *     Discord Documenation</a>
 */
@ApiStatus.Internal
public class MessageReactionEventFields implements Datable {

    public static final String USER_ID_KEY = "user_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String MESSAGE_ID_KEY = "message_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String MEMBER_KEY = "member";
    public static final String EMOJI_KEY = "emoji";

    private final @NotNull Snowflake userId;
    private final @NotNull Snowflake channelId;
    private final @NotNull Snowflake messageId;
    private final @Nullable Snowflake guildId;
    private final @Nullable Member member;
    private final @NotNull EmojiObject emoji;

    public MessageReactionEventFields(@NotNull Snowflake userId, @NotNull Snowflake channelId,
                                      @NotNull Snowflake messageId, @Nullable Snowflake guildId,
                                      @Nullable Member member, @NotNull EmojiObject emoji) {
        this.userId = userId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.guildId = guildId;
        this.member = member;
        this.emoji = emoji;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable MessageReactionEventFields fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {

        if (data == null) return null;

        Snowflake userId = data.getAndConvert(USER_ID_KEY, Snowflake::fromString);
        Snowflake channelId = data.getAndConvert(CHANNEL_ID_KEY, Snowflake::fromString);
        Snowflake messageId = data.getAndConvert(MESSAGE_ID_KEY, Snowflake::fromString);
        Snowflake guildId = data.getAndConvert(GUILD_ID_KEY, Snowflake::fromString);
        Member member = data.getAndConvertWithException(MEMBER_KEY, (SOData c) -> Member.fromData(lApi, c), null);
        EmojiObject emoji = data.getAndConvertWithException(EMOJI_KEY, (SOData c) -> EmojiObject.fromData(lApi, c), null);

        if (userId == null || channelId == null || messageId == null || emoji == null) {
            InvalidDataException.throwException(data, null, MessageReactionEventFields.class,
                    new Object[]{userId, channelId, messageId, emoji},
                    new String[]{USER_ID_KEY, CHANNEL_ID_KEY, MESSAGE_ID_KEY, EMOJI_KEY});
            return null; //unreachable statement
        }

        return new MessageReactionEventFields(userId, channelId, messageId, guildId, member, emoji);
    }

    public @NotNull Snowflake getUserId() {
        return userId;
    }

    public @NotNull Snowflake getChannelId() {
        return channelId;
    }

    public @NotNull Snowflake getMessageId() {
        return messageId;
    }

    public @Nullable Snowflake getGuildId() {
        return guildId;
    }

    public @Nullable Member getMember() {
        return member;
    }

    public @NotNull EmojiObject getEmoji() {
        return emoji;
    }

    @Override
    public AbstractData<?, ?> getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(USER_ID_KEY, userId);
        data.add(CHANNEL_ID_KEY, channelId);
        data.add(MESSAGE_ID_KEY, messageId);
        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.addIfNotNull(MEMBER_KEY, member);
        data.add(EMOJI_KEY, emoji);

        return data;
    }
}
