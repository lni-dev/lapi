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

package me.linusdev.lapi.api.objects.nchannel.forum;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#default-reaction-object">Discord Documentation</a>
 */
public class DefaultReaction implements Datable {

    public static final String EMOJI_ID_KEY = "emoji_id";
    public static final String EMOJI_NAME_KEY = "emoji_name";

    private final @Nullable String emojiId;
    private final @Nullable String emojiName;

    public DefaultReaction(@Nullable String emojiId, @Nullable String emojiName) {
        this.emojiId = emojiId;
        this.emojiName = emojiName;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable DefaultReaction fromData(@Nullable SOData data) {
        if(data == null) return null;
        return new DefaultReaction(data.getAs(EMOJI_ID_KEY), data.getAs(EMOJI_NAME_KEY));
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
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(EMOJI_ID_KEY, emojiId);
        data.add(EMOJI_NAME_KEY, emojiName);

        return data;
    }
}
