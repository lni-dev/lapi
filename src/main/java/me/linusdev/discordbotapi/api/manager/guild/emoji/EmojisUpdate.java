package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;

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

    public List<EmojiObject> getOld() {
        return old;
    }

    public List<EmojiObject> getUpdated() {
        return updated;
    }

    public List<EmojiObject> getAdded() {
        return added;
    }

    public List<EmojiObject> getRemoved() {
        return removed;
    }
}
