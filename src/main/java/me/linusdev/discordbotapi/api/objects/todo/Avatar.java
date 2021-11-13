package me.linusdev.discordbotapi.api.objects.todo;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class holds information about an avatar of a user
 *
 * todo animated avatar, link to img, etc difference between icon and avatar? default avatar
 * todo https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints
 */
public class Avatar implements SimpleDatable {
    private final @NotNull String hash;

    public Avatar(@NotNull String hash){
        this.hash = hash;
    }

    public static @Nullable Avatar fromHashString(@Nullable String hash){
        if(hash == null) return null;
        return new Avatar(hash);
    }

    @Override
    public Object simplify() {
        return hash;
    }
}
