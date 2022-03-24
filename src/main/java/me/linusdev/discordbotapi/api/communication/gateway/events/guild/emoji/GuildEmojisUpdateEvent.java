package me.linusdev.discordbotapi.api.communication.gateway.events.guild.emoji;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.discordbotapi.api.communication.gateway.events.Event;
import me.linusdev.discordbotapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.manager.guild.emoji.EmojiPool;
import me.linusdev.discordbotapi.api.manager.guild.emoji.EmojisUpdate;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GuildEmojisUpdateEvent extends Event implements GuildEvent {

    protected final @NotNull ArrayList<Data> emojisData;
    protected final @Nullable EmojisUpdate update;
    private final @Nullable EmojiPool emojiPool;

    public GuildEmojisUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                  @Nullable Snowflake guildId, @NotNull ArrayList<Data> emojisData, @Nullable EmojisUpdate update,
                                  @Nullable EmojiPool emojiPool) {
        super(lApi, payload, guildId);
        //TODO:
        this.emojisData = emojisData;
        this.update = update;
        this.emojiPool = emojiPool;
    }

    /**
     *
     * @return {@link EmojisUpdate} or {@code null} if
     * {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS} is disabled
     */
    public @Nullable EmojisUpdate getUpdate() {
        return update;
    }

    /**
     *
     * @return {@link EmojiPool} or {@code null} if {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS} is disabled
     */
    public @Nullable EmojiPool getEmojiPool() {
        return emojiPool;
    }

    /**
     * {@link ArrayList} of {@link EmojiObject emojis}, which has been retrieved from Discord in the update event
     * @return {@link ArrayList} of {@link EmojiObject emojis}
     * @throws InvalidDataException if a {@link Data} was invalid
     */
    @Contract(value="-> new",pure = true)
    public @NotNull ArrayList<EmojiObject> getEmojis() throws InvalidDataException {
        ArrayList<EmojiObject> emojis = new ArrayList<>(emojisData.size());

        for(Data d : emojisData){
            emojis.add(EmojiObject.fromData(lApi, d));
        }

        return emojis;
    }
}
