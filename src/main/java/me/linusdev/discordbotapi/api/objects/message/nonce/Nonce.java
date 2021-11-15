package me.linusdev.discordbotapi.api.objects.message.nonce;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Nullable;

/**
 * used in {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message}
 * "used for validating a message was sent"
 * TODO what is this?
 */
public class Nonce implements SimpleDatable {

    private final @Nullable String string;
    private final @Nullable Integer integer;

    public Nonce(@Nullable String s, @Nullable Integer i){
        this.string = s;
        this.integer = i;
    }

    /**
     *
     * @param stringOrInt {@link String} or {@link Integer} or {@code null}
     * @return {@link Nonce} or {@code null} if stringOrInt was {@code null}
     */
    public static @Nullable Nonce fromStringOrInteger(@Nullable Object stringOrInt){
        if(stringOrInt == null) return null;
        if(stringOrInt instanceof Number){
            return new Nonce(null, ((Number) stringOrInt).intValue());
        }else if(stringOrInt instanceof String){
            return new Nonce((String) stringOrInt, null);
        }

        return null;
    }


    public @Nullable String getString() {
        return string;
    }

    public @Nullable Integer getInteger() {
        return integer;
    }

    @Override
    public Object simplify() {
        return string == null ? integer : string;
    }
}
