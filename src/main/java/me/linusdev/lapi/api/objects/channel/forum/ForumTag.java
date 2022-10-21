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

package me.linusdev.lapi.api.objects.channel.forum;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.channel.ChannelType;
import me.linusdev.lapi.api.objects.permission.Permission;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link ForumTag} retrieved in {@link ChannelType#GUILD_FORUM forum} {@link Channel channels}.
 * @see <a href="https://discord.com/developers/docs/resources/channel#forum-tag-object">Discord Documentation</a>
 */
public class ForumTag implements Datable {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String MODERATED_KEY = "moderated";
    public static final String EMOJI_ID_KEY = "emoji_id";
    public static final String EMOJI_NAME_KEY = "emoji_name";

    private final @NotNull String id;
    private final @NotNull String name;
    private final boolean moderated;
    private final @Nullable String emojiId; //TODO should be nullable, but discord docs says otherwise see https://discord.com/developers/docs/resources/channel#forum-tag-object-forum-tag-structure
    private final @Nullable String emojiName;

    public ForumTag(@NotNull String id, @NotNull String name, boolean moderated, @Nullable String emojiId, @Nullable String emojiName) {
        this.id = id;
        this.name = name;
        this.moderated = moderated;
        this.emojiId = emojiId;
        this.emojiName = emojiName;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable ForumTag fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = data.getAs(ID_KEY);
        String name = data.getAs(NAME_KEY);
        Boolean moderated = data.getAs(MODERATED_KEY);

        if(id == null || name == null || moderated == null) {
            InvalidDataException.throwException(data, null, ForumTag.class,
                    new Object[]{id, name, moderated},
                    new String[]{ID_KEY, NAME_KEY, MODERATED_KEY});
            return null; //unreachable
        }

        return new ForumTag(id, name, moderated, data.getAs(EMOJI_ID_KEY), data.getAs(EMOJI_NAME_KEY));
    }

    /**
     * The id of the tag
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * The id as {@link Snowflake} of the tag
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return Snowflake.fromString(id);
    }

    /**
     * The name of the tag (0-20 characters)
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Whether this tag can only be added to or removed from threads by a member with the {@link Permission#MANAGE_THREADS MANAGE_THREADS} permission.
     */
    public boolean isModerated() {
        return moderated;
    }

    /**
     * the id of a guild's custom emoji
     */
    public @Nullable String getEmojiId() {
        return emojiId;
    }

    /**
     * the id as {@link Snowflake} of a guild's custom emoji
     */
    public @Nullable Snowflake getEmojiIdAsSnowflake() {
        return Snowflake.fromString(emojiId);
    }


    /**
     * the unicode character of the emoji
     */
    public @Nullable String getEmojiName() {
        return emojiName;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(MODERATED_KEY, moderated);
        data.add(EMOJI_ID_KEY, emojiId);
        data.add(EMOJI_NAME_KEY, emojiName);

        return data;
    }
}
