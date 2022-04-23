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

package me.linusdev.lapi.api.objects.guild;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#welcome-screen-object-welcome-screen-channel-structure" target="_top">Welcome Screen Channel Structure</a>
 */
public class WelcomeScreenChannelStructure implements Datable {

    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String DESCRIPTION_KEY = "description";
    public static final String EMOJI_ID_KEY = "emoji_id";
    public static final String EMOJI_NAME_KEY = "emoji_name";

    private final @NotNull Snowflake channelId;
    private final @NotNull String description;
    private final @Nullable Snowflake emojiId;
    private final @Nullable String emojiName;

    /**
     *
     * @param channelId the channel's id
     * @param description the description shown for the channel
     * @param emojiId the emoji id, if the emoji is custom
     * @param emojiName the emoji name if custom, the unicode character if standard, or null if no emoji is set
     */
    public WelcomeScreenChannelStructure(@NotNull Snowflake channelId, @NotNull String description, @Nullable Snowflake emojiId, @Nullable String emojiName) {
        this.channelId = channelId;
        this.description = description;
        this.emojiId = emojiId;
        this.emojiName = emojiName;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link WelcomeScreenChannelStructure}
     * @throws InvalidDataException if {@link #CHANNEL_ID_KEY} or {@link #DESCRIPTION_KEY} is missing or {@code null}
     */
    @SuppressWarnings("ConstantConditions")
    public static @NotNull WelcomeScreenChannelStructure fromData(@NotNull SOData data) throws InvalidDataException {
        String channelId = (String) data.get(CHANNEL_ID_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        String emojiId = (String) data.get(EMOJI_ID_KEY);
        String emojiName = (String) data.get(EMOJI_NAME_KEY);

        if(channelId == null || description == null){
            InvalidDataException.throwException(data, null, WelcomeScreenChannelStructure.class,
                    new Object[]{channelId, description},
                    new String[]{CHANNEL_ID_KEY, DESCRIPTION_KEY});
        }

        return new WelcomeScreenChannelStructure(Snowflake.fromString(channelId), description, Snowflake.fromString(emojiId), emojiName);
    }

    /**
     * the channel's id
     */
    public @NotNull Snowflake getChannelId() {
        return channelId;
    }

    /**
     * the description shown for the channel
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * the emoji id, if the emoji is custom
     */
    public @Nullable Snowflake getEmojiId() {
        return emojiId;
    }

    /**
     * the emoji name if custom, the unicode character if standard, or {@code null} if no emoji is set
     */
    public @Nullable String getEmojiName() {
        return emojiName;
    }

    /**
     *
     * @return {@link Data} for this {@link WelcomeScreenChannelStructure}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(CHANNEL_ID_KEY, channelId);
        data.add(DESCRIPTION_KEY, description);
        data.add(EMOJI_ID_KEY, emojiId);
        data.add(EMOJI_NAME_KEY, emojiName);

        return data;
    }
}
