package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import org.jetbrains.annotations.Nullable;

/**
 * The Compression Discord should use, when sending us {@link me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract payloads}.<br>
 * This is not support by LApi as standard. You will have to implement your own converter.
 */
public enum GatewayCompression {

    NONE(null),

    ZLIB_STREAM("zlib-stream"),

    ;

    private final String value;

    GatewayCompression(String value) {
        this.value = value;
    }

    public @Nullable String getValue() {
        return value;
    }
}
