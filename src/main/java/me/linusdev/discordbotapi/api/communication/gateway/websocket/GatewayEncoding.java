package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import me.linusdev.data.SimpleDatable;
import me.linusdev.data.converter.ExceptionConverter;
import org.jetbrains.annotations.Nullable;

/**
 * Encoding, which discord should use, when sending us a {@link me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract payload}
 */
public enum GatewayEncoding implements SimpleDatable {
    /**
     * Supported by LApi as standard.<br>
     * You can implement {@link GatewayWebSocket#setJsonToPayloadConverter(ExceptionConverter)}
     */
    JSON("json"),

    /**
     * Not supported by LApi.<br>
     * You <b>must</b> implement {@link GatewayWebSocket#setEtfToPayloadConverter(ExceptionConverter)}
     */
    ETF("etf"),
    ;

    private final String value;

    GatewayEncoding(String value) {
        this.value = value;
    }

    /**
     *
     * @param value string
     * @return {@link GatewayEncoding} matching given string (ignores case) or {@code null} if none matches
     */
    public static @Nullable GatewayEncoding fromValue(@Nullable String value){
        if(value == null) return null;
        for(GatewayEncoding e : GatewayEncoding.values()){
            if(e.value.equalsIgnoreCase(value)) return e;
        }

        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
