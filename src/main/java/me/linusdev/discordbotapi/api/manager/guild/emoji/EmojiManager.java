package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface EmojiManager extends EmojiPool{

    void addEmoji(@NotNull EmojiObject emoji);

    @NotNull EmojisUpdate onEmojiUpdate(@NotNull ArrayList<Data> emojisData) throws InvalidDataException;
}
