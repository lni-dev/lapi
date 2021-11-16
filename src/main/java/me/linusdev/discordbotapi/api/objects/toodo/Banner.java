package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO
 */
public class Banner implements SimpleDatable {
    private final @NotNull String hash;

    public Banner(@NotNull String hash){
        this.hash = hash;
    }

    public static @Nullable Banner fromHashString(@Nullable String hash){
        if(hash == null) return null;
        return new Banner(hash);
    }

    @Override
    public Object simplify() {
        return hash;
    }
}
