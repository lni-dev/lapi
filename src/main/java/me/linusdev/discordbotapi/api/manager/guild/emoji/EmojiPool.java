package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface EmojiPool extends HasLApi {

    /**
     * Get {@link EmojiObject} with given id
     * @param id of the emoji
     * @return {@link EmojiObject} with given id or {@code null} if no such {@link EmojiObject} exists.
     */
    @Nullable EmojiObject getEmoji(@NotNull String id);

    /**
     * Get {@link EmojiObject} with given id
     * @param id of the emoji
     * @return {@link EmojiObject} with given id or {@code null} if no such {@link EmojiObject} exists.
     */
    default @Nullable EmojiObject getEmoji(@NotNull Snowflake id){
        return getEmoji(id.asString());
    }

    /**
     * The Collection, may change at any time, but should never be changed manually.
     * @return {@link Collection} of Emojis, which is possibly backed by this {@link EmojiPool}.
     */
    @NotNull Collection<EmojiObject> getEmojis();
}
