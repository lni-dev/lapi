package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;

public class EmojisUpdate {

    private final EmojiObject[] old;
    private final EmojiObject[] updated;
    private final EmojiObject[] added;
    private final EmojiObject[] removed;

    public EmojisUpdate(EmojiObject[] old, EmojiObject[] updated, EmojiObject[] added, EmojiObject[] removed) {
        this.old = old;
        this.updated = updated;
        this.added = added;
        this.removed = removed;
    }
}
