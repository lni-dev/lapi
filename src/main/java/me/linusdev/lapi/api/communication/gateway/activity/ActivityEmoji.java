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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-emoji" target="_top">Activity Emoji</a>
 */
public class ActivityEmoji implements Emoji, Datable {

    public static final String NAME_KEY = "name";
    public static final String ID_KEY = "id";
    public static final String ANIMATED_KEY = "animated";

    private final @NotNull String name;
    private final @Nullable Snowflake id;
    private final @Nullable Boolean animated;

    /**
     *
     * @param name the name of the emoji
     * @param id the id of the emoji
     * @param animated whether this emoji is animated
     */
    public ActivityEmoji(@NotNull String name, @Nullable Snowflake id, @Nullable Boolean animated) {
        this.name = name;
        this.id = id;
        this.animated = animated;
    }

    /**
     *
     * @param data {@link SOData}
     * @return {@link ActivityEmoji}
     * @throws InvalidDataException if {@link #NAME_KEY} is missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ActivityEmoji fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String name = (String) data.get(NAME_KEY);
        String id = (String) data.get(ID_KEY);
        Boolean animated = (Boolean) data.get(ANIMATED_KEY);

        if(name == null){
            InvalidDataException.throwException(data, null, ActivityEmoji.class,
                    new Object[]{name}, new String[]{NAME_KEY});
        }

        //noinspection ConstantConditions
        return new ActivityEmoji(name, Snowflake.fromString(id), animated);
    }

    /**
     *
     * @param emoji {@link Emoji}
     * @return {@link ActivityEmoji}
     * @throws InvalidDataException if {@link Emoji#getName()} is {@code null}
     */
    public static @NotNull ActivityEmoji fromEmoji(@NotNull Emoji emoji) throws InvalidDataException {
        if(emoji.getName() == null) throw new InvalidDataException(null, "cannot crate an animated emoji with a name, that is null.");
        return new ActivityEmoji(emoji.getName(), emoji.getIdAsSnowflake(), emoji.isAnimated());
    }

    @Override
    public @Nullable Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * whether this emoji is animated
     */
    public @Nullable Boolean getAnimated() {
        return animated;
    }

    @Override
    public boolean isAnimated() {
        return !(animated == null) && animated;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(NAME_KEY, name);
        data.addIfNotNull(ID_KEY, id);
        data.addIfNotNull(ANIMATED_KEY, animated);

        return data;
    }
}
