package me.linusdev.discordbotapi.api.objects.emoji.abstracts;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.linusdev.discordbotapi.api.objects.emoji.EmojiObject.*;

/**
 * @see <a href="https://discord.com/developers/docs/resources/emoji#emoji-resource" target="_top">Emoji Resource</a>
 */
public interface Emoji extends Datable {

    /**
     * emoji id as {@link Snowflake}
     * <br>
     * Can be {@code null} if this is a Standard Emoji, see {@link #isStandardEmoji()}
     */
    @Nullable Snowflake getIdAsSnowflake();

    /**
     * emoji id as {@link String}
     * <br>
     * Can be {@code null} if this is a Standard Emoji, see {@link #isStandardEmoji()}
     */
    default @Nullable String getId(){
        Snowflake snowflake = getIdAsSnowflake();
        if(snowflake == null) return null;
        return snowflake.asString();
    }


    /**
     * emoji name or the unicode emoji for {@link me.linusdev.discordbotapi.api.objects.emoji.StandardEmoji standard emojis}
     * <br>
     * (can be null only in {@link me.linusdev.discordbotapi.api.objects.message.Reaction} emoji objects) <br>
     * In MESSAGE_REACTION_ADD and MESSAGE_REACTION_REMOVE gateway events name may be null when custom emoji data is not available (for example, if it was deleted from the guild). <br>
     * todo add @links
     * These sentences seem rather contradictory to me, but they are directly from discords docs
     */
    @Nullable String getName();

    /**
     * the id of standard emojis is always {@code null}
     * <br><br>
     * Example Standard emoji: 🔥
     *
     * More Standard Emojis: {@link me.linusdev.discordbotapi.api.objects.emoji.StandardEmoji}
     *
     * @return {@code true} if this is a standard (Unicode) Emoji
     */
    default boolean isStandardEmoji(){
        return getIdAsSnowflake() == null;
    }

    boolean isAnimated();

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull Emoji of(@NotNull String id, @NotNull String name, boolean animated){
        return new Emoji() {
            @Override
            public @NotNull Snowflake getIdAsSnowflake() {
                return Snowflake.fromString(id);
            }

            @Override
            public @NotNull String getId() {
                return id;
            }

            @Override
            public @NotNull String getName() {
                return name;
            }

            @Override
            public boolean isAnimated() {
                return animated;
            }

            @Override
            public Data getData() {
                Data data = new Data(3);
                data.add(ID_KEY, id);
                data.add(NAME_KEY, name);
                data.add(ANIMATED_KEY, animated);
                return data;
            }
        };
    }
}
