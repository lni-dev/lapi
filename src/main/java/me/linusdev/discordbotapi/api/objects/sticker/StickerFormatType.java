package me.linusdev.discordbotapi.api.objects.sticker;

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
