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

package me.linusdev.lapi.api.objects.sticker;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     The smallest amount of data required to render a sticker. A partial sticker object.
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/sticker#sticker-item-object" target="_top"> Sticker Item Object</a>
 * @see Sticker
 */
public class StickerItem implements Datable {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String FORMAT_TYPE_KEY = "format_type";

    private final @NotNull Snowflake id;
    private final @NotNull String name;
    private final @NotNull StickerFormatType formatType;

    /**
     *
     * @param id id of the sticker
     * @param name name of the sticker
     * @param formatType {@link StickerFormatType 	type of sticker format}
     */
    public StickerItem(@NotNull Snowflake id, @NotNull String name, @NotNull StickerFormatType formatType){
        this.id = id;
        this.name = name;
        this.formatType = formatType;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link StickerItem}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY} or {@link #FORMAT_TYPE_KEY} are missing
     */
    public static @NotNull StickerItem fromData(SOData data) throws InvalidDataException {
        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        Number type = (Number) data.get(FORMAT_TYPE_KEY);

        if(id == null || name == null || type == null){
            InvalidDataException.throwException(data, null, StickerItem.class,
                    new Object[]{id, name, type}, new String[]{ID_KEY, NAME_KEY, FORMAT_TYPE_KEY});
            return null; //this will never happen, because above method will throw an exception
        }

        return new StickerItem(Snowflake.fromString(id), name, StickerFormatType.fromValue(type.intValue()));
    }

    /**
     * id as {@link Snowflake} of the sticker
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * id as {@link String} of the sticker
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * 	name of the sticker
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * {@link StickerFormatType type of sticker format}
     */
    public @NotNull StickerFormatType getFormatType() {
        return formatType;
    }

    /**
     *
     * @return {@link SOData} for this {@link StickerItem}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(FORMAT_TYPE_KEY, formatType);

        return data;
    }
}
