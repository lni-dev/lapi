package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbstractLink {

    /**
     *
     * @return the {@link Method} for this Link
     */
    @NotNull Method getMethod();

    /**
     * The url, may still be missing placeholders!
     */
    @NotNull String getLink();
}
