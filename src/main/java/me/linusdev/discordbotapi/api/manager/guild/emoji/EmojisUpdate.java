package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmojisUpdate {

    private final List<EmojiObject> old;
    private final List<EmojiObject> updated;
    private final List<EmojiObject> added;
    private final List<EmojiObject> removed;

    public EmojisUpdate(List<EmojiObject> old, List<EmojiObject> updated, List<EmojiObject> added, List<EmojiObject> removed) {
        this.old = old;
        this.updated = updated;
        this.added = added;
        this.removed = removed;
    }

    /**
     * if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS}
     * and {@link me.linusdev.discordbotapi.api.config.ConfigFlag#COPY_EMOJI_ON_UPDATE_EVENT COPY_EMOJI_ON_UPDATE_EVENT}
     * is enabled, emojis will be copied into this {@link List} before they are updated.<br><br>
     *
     * If no {@link EmojiObject emojis} have been updated or
     * {@link me.linusdev.discordbotapi.api.config.ConfigFlag#COPY_EMOJI_ON_UPDATE_EVENT COPY_EMOJI_ON_UPDATE_EVENT} is
     * disabled this will return {@code null}
     */
    public @Nullable List<EmojiObject> getOld() {
        return old;
    }

    /**
     * if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS}
     * is enabled, updated emojis will be in this {@link List}<br><br>
     * If no {@link EmojiObject emojis} have been updated, this will return {@code null}
     */
    public @Nullable List<EmojiObject> getUpdated() {
        return updated;
    }

    /**
     * If no {@link EmojiObject emojis} have been added, this will return {@code null}
     */
    public @Nullable List<EmojiObject> getAdded() {
        return added;
    }

    /**
     * if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS}
     * is enabled, updated emojis will be in this {@link List}<br><br>
     * If no {@link EmojiObject emojis} have been removed or
     * {@link me.linusdev.discordbotapi.api.config.ConfigFlag#COPY_EMOJI_ON_UPDATE_EVENT COPY_EMOJI_ON_UPDATE_EVENT} is
     * disabled this will return {@code null}
     */
    public @Nullable List<EmojiObject> getRemoved() {
        return removed;
    }
}
