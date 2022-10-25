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

package me.linusdev.lapi.api.objects.message;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import org.jetbrains.annotations.NotNull;

/**
 * <a href="https://discord.com/developers/docs/resources/channel#reaction-object" target="_top">Reaction Object</a>
 */
public class Reaction implements Datable, HasLApi {

    public static final String COUNT_KEY = "count";
    public static final String ME_KEY = "me";
    public static final String EMOJI_KEY = "emoji";

    /**
     * @see #getCount()
     */
    private final int count;

    /**
     * @see #currentUserHasReacted()
     */
    private final boolean mee;

    /**
     * @see #getEmoji()
     */
    private final @NotNull Emoji emoji;

    private final @NotNull LApi lApi;

    /**
     * @param count times this emoji has been used to react
     * @param me whether the current user reacted using this emoji
     * @param emoji emoji information
     */
    public Reaction(@NotNull LApi lApi, int count, boolean me, @NotNull Emoji emoji){
        this.lApi = lApi;
        this.count = count;
        this.mee = me;
        this.emoji = emoji;
    }

    /**
     *
     * @param data with required fields
     * @return {@link Reaction}
     * @throws InvalidDataException if {@link #COUNT_KEY}, {@link #ME_KEY} or {@link #EMOJI_KEY} field are missing
     */
    public static @NotNull Reaction fromData(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        Number count = (Number) data.get(COUNT_KEY);
        Boolean mee = (Boolean) data.get(ME_KEY);
        SOData emojiData = (SOData) data.get(EMOJI_KEY);

        if(count == null || mee == null || emojiData == null){
            InvalidDataException exception = new InvalidDataException(data, "One or more required fields are null or missing in " + Reaction.class.getSimpleName());
            if(count == null) exception.addMissingFields(COUNT_KEY);
            if(mee == null) exception.addMissingFields(ME_KEY);
            if(emojiData == null) exception.addMissingFields(EMOJI_KEY);
            throw exception;
        }

        return new Reaction(lApi, count.intValue(), mee, EmojiObject.fromData(lApi, emojiData));
    }

    /**
     * times this emoji has been used to react
     */
    public int getCount() {
        return count;
    }

    /**
     * whether the current user (your bot) reacted using this emoji
     */
    public boolean currentUserHasReacted() {
        return mee;
    }

    /**
     * 	partial emoji object with emoji information
     *
     * This {@link Emoji} has {@link Emoji#getId()}, {@link Emoji#getName()} and {@link EmojiObject#isAnimated()} if it is an animated Emoji
     * <br> {@link Emoji#getId()} may be {@code null} for Standard Emojis ({@link EmojiObject#isStandardEmoji()})
     */
    public @NotNull Emoji getEmoji() {
        return emoji;
    }

    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(COUNT_KEY, count);
        data.add(ME_KEY, mee);
        data.add(EMOJI_KEY, emoji);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
