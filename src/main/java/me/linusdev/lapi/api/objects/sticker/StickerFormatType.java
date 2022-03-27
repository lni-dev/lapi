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

package me.linusdev.lapi.api.objects.sticker;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/sticker#sticker-object-sticker-format-types" target="_top">
 *          Sticker Format Types
 *      </a>
 * @see StickerItem
 */
public enum StickerFormatType implements SimpleDatable {
    UNKNOWN(0),
    PNG(1),
    APNG(2),
    LOTTIE(3),
    ;

    private final int value;

    StickerFormatType(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link StickerFormatType} matching to given value or {@link #UNKNOWN} if none matches
     */
    public static final StickerFormatType fromValue(int value){
        for(StickerFormatType type : StickerFormatType.values()){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
