package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Nullable;

/**
 * The Compression Discord should use, when sending us {@link me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract payloads}.<br>
 * This is not support by LApi as standard. You will have to implement your own converter.
 */
public enum GatewayCompression implements SimpleDatable {

    NONE(null),

    ZLIB_STREAM("zlib-stream"),

    ;

    private final String value;

    GatewayCompression(String value) {
        this.value = value;
    }

    /**
     *
     * @param value string
     * @return {@link GatewayCompression} matching given string (ignores case) or {@code null} if none matches
     */
    public static @Nullable GatewayCompression fromValue(String value){
        if(value == null) return null;
        for(GatewayCompression compression : GatewayCompression.values()){
            if(compression.value.equalsIgnoreCase(value)) return compression;
        }

        return null;
    }

    public @Nullable String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
