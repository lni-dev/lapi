package me.linusdev.discordbotapi.api.manager.guild.emoji;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class EmojiManagerImpl implements EmojiManager{

    private final @NotNull LApiImpl lApi;
    private final @NotNull ConcurrentHashMap<String, EmojiObject> emojis;

    public EmojiManagerImpl(@NotNull LApiImpl lApi){
        this.lApi = lApi;
        this.emojis = new ConcurrentHashMap<>(32);
    }

    @Override
    public void addEmoji(@NotNull EmojiObject emoji){
        emojis.put(emoji.getId(), emoji);
    }

    public @NotNull EmojisUpdate onEmojiUpdate(@NotNull ArrayList<Data> emojisData) throws InvalidDataException {
        ArrayList<EmojiObject> old = null;
        ArrayList<EmojiObject> updated = null;
        ArrayList<EmojiObject> added = null;
        ArrayList<EmojiObject> removed = null;

        for(Data data : emojisData){
            String emojiId = (String) data.get(EmojiObject.ID_KEY);
            if(emojiId == null) throw new InvalidDataException(data, "emoji id missing.", null, EmojiObject.ID_KEY);

            EmojiObject emoji = emojis.get(emojiId);

            if(emoji == null){
                //new emoji
                emoji = EmojiObject.fromData(lApi, data);
                if(added == null) added = new ArrayList<>(1);
                added.add(emoji);
                emojis.put(emoji.getId(), emoji);

            } else {
                if(emoji.checkIfChanged(data)){
                    if(lApi.isCopyOldEmojiOnUpdateEventEnabled()){
                        if(old == null) old = new ArrayList<>(1);
                        old.add(emoji.copy());
                    }
                    emoji.updateSelfByData(data);
                    if(updated == null) updated = new ArrayList<>(1);
                    updated.add(emoji);
                }
            }
        }

        //check if an emoji was removed
        if(emojis.size() != emojisData.size() - (added != null ? added.size() : 0)){
            first: for(EmojiObject emojiObject : emojis.values()){
                for(Data data : emojisData){
                    if(emojiObject.getId().equals(data.get(EmojiObject.ID_KEY))){
                        continue first;
                    }
                }

                if(removed == null) removed = new ArrayList<>(1);
                removed.add(emojiObject);
                emojis.remove(emojiObject.getId());
            }
        }


        return new EmojisUpdate(old, updated, added, removed);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public @Nullable EmojiObject getEmoji(@NotNull String id) {
        return emojis.get(id);
    }

    @Override
    public @NotNull Collection<EmojiObject> getEmojis() {
        return emojis.values();
    }
}
