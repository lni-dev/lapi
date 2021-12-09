package me.linusdev.discordbotapi.api.communication.gateway.resume;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#resume-resume-structure" target="_top">Resume Structure</a>
 */
public class Resume implements Datable {

    public static final String TOKEN_KEY = "token";
    public static final String SESSION_ID_KEY = "session_id";
    public static final String SEQUENCE_KEY = "seq";

    private final @NotNull String token;
    private final @NotNull String sessionId;
    private final long sequence;

    /**
     *
     * @param token session token
     * @param sessionId session id
     * @param sequence last sequence number received
     */
    public Resume(@NotNull String token, @NotNull String sessionId, long sequence) {
        this.token = token;
        this.sessionId = sessionId;
        this.sequence = sequence;
    }

    @Override
    public Data getData() {
        Data data = new Data(3);

        data.add(TOKEN_KEY, token);
        data.add(SESSION_ID_KEY, sessionId);
        data.add(SEQUENCE_KEY, sequence);

        return data;
    }
}
