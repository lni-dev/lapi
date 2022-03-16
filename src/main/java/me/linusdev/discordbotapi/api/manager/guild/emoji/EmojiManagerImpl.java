package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EmojiManagerImpl implements EmojiManager{

    private final @NotNull LApiImpl lApi;

    public EmojiManagerImpl(@NotNull LApiImpl lApi){
        this.lApi = lApi;
    }

    public @NotNull EmojisUpdate onEmojiUpdate(@NotNull ArrayList<Data> emojisData){
        //TODO
        return null;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
