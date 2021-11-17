package me.linusdev.discordbotapi.api.objects.message;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import org.jetbrains.annotations.NotNull;

/**
 * <a href="https://discord.com/developers/docs/resources/channel#reaction-object" target="_top">Reaction Object</a>
 */
public class Reaction implements Datable, HasLApi {

    public static final String COUNT_KEY = "count";
    public static final String ME_KEY = "me";
    public static final String EMOJI_KEY = "emoji";

    /**
     * @see #getCount()
     */
    private final int count;

    /**
     * @see #currentUserHasReacted()
     */
    private final boolean mee;

    /**
     * @see #getEmoji()
     */
    private final @NotNull Emoji emoji;

    private final @NotNull LApi lApi;

    /**
     * @param count times this emoji has been used to react
     * @param me whether the current user reacted using this emoji
     * @param emoji emoji information
     */
    public Reaction(@NotNull LApi lApi, int count, boolean me, @NotNull Emoji emoji){
        this.lApi = lApi;
        this.count = count;
        this.mee = me;
        this.emoji = emoji;
    }

    /**
     *
     * @param data with required fields
     * @return {@link Reaction}
     * @throws InvalidDataException if {@link #COUNT_KEY}, {@link #ME_KEY} or {@link #EMOJI_KEY} field are missing
     */
    public static @NotNull Reaction fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        Number count = (Number) data.get(COUNT_KEY);
        Boolean mee = (Boolean) data.get(ME_KEY);
        Data emojiData = (Data) data.get(EMOJI_KEY);

        if(count == null || mee == null || emojiData == null){
            InvalidDataException exception = new InvalidDataException(data, "One or more required fields are null or missing in " + Reaction.class.getSimpleName());
            if(count == null) exception.addMissingFields(COUNT_KEY);
            if(mee == null) exception.addMissingFields(ME_KEY);
            if(emojiData == null) exception.addMissingFields(EMOJI_KEY);
            throw exception;
        }

        return new Reaction(lApi, count.intValue(), mee, me.linusdev.discordbotapi.api.objects.emoji.Emoji.fromData(lApi, emojiData));
    }

    /**
     * times this emoji has been used to react
     */
    public int getCount() {
        return count;
    }

    /**
     * whether the current user (your bot) reacted using this emoji
     */
    public boolean currentUserHasReacted() {
        return mee;
    }

    /**
     * 	partial emoji object with emoji information
     *
     * This {@link Emoji} has {@link Emoji#getId()}, {@link Emoji#getName()} and {@link me.linusdev.discordbotapi.api.objects.emoji.Emoji#isAnimated()} if it is an animated Emoji
     * <br> {@link Emoji#getId()} may be {@code null} for Standard Emojis ({@link me.linusdev.discordbotapi.api.objects.emoji.Emoji#isStandardEmoji()})
     */
    public @NotNull Emoji getEmoji() {
        return emoji;
    }

    @Override
    public @NotNull Data getData() {
        Data data = new Data(3);

        data.add(COUNT_KEY, count);
        data.add(ME_KEY, mee);
        data.add(EMOJI_KEY, emoji);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
