package me.linusdev.discordbotapi.api.communication;


import org.jetbrains.annotations.NotNull;

/**
 * The different api versions of discord. As of writing this enum, the api version {@link me.linusdev.discordbotapi.api.lapiandqueue.LApi LApi}
 * uses for its HttpRequests is always {@link #V9}. So this enum is only useful for the {@link me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket Gateway}
 * @see <a href="https://discord.com/developers/docs/reference#api-versioning-api-versions" target="_top">API Versions</a>
 */
public enum ApiVersion {

    /**
     * LApi specific
     */
    UNKNOWN("9", -1),

    /**
     * Available
     */
    V9("9", 9),

    /**
     * Available
     */
    V8("8", 8),

    /**
     * 	Doesn't look like anything to me
     */
    V7("7", 7),

    /**
     * Deprecated
     */
    @Deprecated
    V6("6", 6),

    /**
     * Discontinued
     */
    V5("5", 5),

    /**
     * Discontinued
     */
    V4("4", 4),

    /**
     * Discontinued
     */
    V3("3", 3),

    ;

    private final @NotNull String versionNumberString;
    private final int versionNumber;

    ApiVersion(@NotNull String versionNumberString, int versionNumber) {
        this.versionNumberString = versionNumberString;
        this.versionNumber = versionNumber;
    }

    /**
     *
     * @param version int
     * @return {@link ApiVersion} matching given version or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ApiVersion fromInt(int version){
        for(ApiVersion api : ApiVersion.values()){
            if(api.versionNumber == version)
                return api;
        }

        return UNKNOWN;
    }

    public @NotNull String getVersionNumber() {
        return versionNumberString;
    }
}
