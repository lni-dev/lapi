package me.linusdev.discordbotapi.api.communication.gateway.websocket;

import me.linusdev.data.converter.ExceptionConverter;

/**
 * Encoding, which discord should use, when sending us a {@link me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract payload}
 */
public enum GatewayEncoding {
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

    public String getValue() {
        return value;
    }
}
