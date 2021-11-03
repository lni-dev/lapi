package me.linusdev.discordbotapi.api.objects.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum MessageFlag {

    /**
     * this message has been published to subscribed channels (via Channel Following)
     */
    CROSSPOSTED             (1L << 0, "cross-posted"),

    /**
     * this message originated from a message in another channel (via Channel Following)
     */
    IS_CROSSPOST            (1L << 1, "is crosspost"),

    /**
     * do not include any embeds when serializing this message
     */
    SUPPRESS_EMBEDS         (1L << 2, "suppress embeds"),

    /**
     * the source message for this crosspost has been deleted (via Channel Following)
     */
    SOURCE_MESSAGE_DELETED  (1L << 3, "source message deleted"),

    /**
     * this message came from the urgent message system
     */
    URGENT                  (1 << 4, "urgent"),

    /**
     * this message has an associated thread, with the same id as the message
     */
    HAS_THREAD              (1 << 5, "has thread"),

    /**
     * this message is only visible to the user who invoked the Interaction
     */
    EPHEMERAL               (1 << 6, "ephemeral"),

    /**
     * this message is an Interaction Response and the bot is "thinking"
     */
    LOADING                 (1 << 7, "loading"),
    ;

    private final long value;
    private final String name;

    MessageFlag(long value, String name){
        this.value = value;
        this.name = name;
    }

    /**
     *
     * @return the value of this Flag (1l left shifted by some value)
     */
    public long getValue() {
        return value;
    }

    /**
     * Idk why I added this, it's utterly useless
     */
    public String getName() {
        return name;
    }

    /**
     * reads the set flags in given {@code long} bits
     * @param bits the set flags as long
     * @return Empty array if, bits == null or 0L.
     * Array with {@link MessageFlag} corresponding to bits
     */
    public static @NotNull List<MessageFlag> getFlagsFromBits(@Nullable Long bits){
        ArrayList<MessageFlag> flags = new ArrayList<>();

        if(bits == null || bits == 0L) return flags;

        for(MessageFlag flag : MessageFlag.values())
            if((bits & flag.getValue()) == flag.getValue())
                flags.add(flag);

        return flags;
    }

    /**
     * converts a {@link List} of {@link MessageFlag} into a corresponding long
     * @param flags the List
     * @return long with set bits
     */
    public static long getBitsFromFlags(List<MessageFlag> flags){
        long bits = 0L;

        for(MessageFlag flag : flags)
            bits = bits | flag.getValue();

        return bits;
    }
}
