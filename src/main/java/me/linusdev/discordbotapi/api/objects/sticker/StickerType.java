package me.linusdev.discordbotapi.api.objects.sticker;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/sticker#sticker-object-sticker-types" target="_top"> Sticker Types</a>
 */
public enum StickerType implements SimpleDatable {

    UNKNOWN(0),
    STANDARD(1),
    GUILD(2),
    ;

    private final int value;

    StickerType(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link StickerType} with given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull StickerType fromValue(int value){
        for(StickerType type : StickerType.values()){
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
