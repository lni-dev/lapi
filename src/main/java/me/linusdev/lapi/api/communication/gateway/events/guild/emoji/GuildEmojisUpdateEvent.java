/*
 * Copyright (c) 2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.gateway.events.guild.emoji;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.list.ListPool;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GuildEmojisUpdateEvent extends Event implements GuildEvent {

    protected final @NotNull ArrayList<SOData> emojisData;
    protected final @Nullable ListUpdate<EmojiObject> update;
    private final @Nullable ListPool<EmojiObject> emojiPool;

    public GuildEmojisUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                  @Nullable Snowflake guildId, @NotNull ArrayList<SOData> emojisData, @Nullable ListUpdate<EmojiObject> update,
                                  @Nullable ListPool<EmojiObject> emojiPool) {
        super(lApi, payload, guildId);
        this.emojisData = emojisData;
        this.update = update;
        this.emojiPool = emojiPool;
    }

    /**
     *
     * @return {@link ListUpdate} or {@code null} if
     * {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS} is disabled
     */
    public @Nullable ListUpdate<EmojiObject> getUpdate() {
        return update;
    }

    /**
     *
     * @return {@link ListPool<EmojiObject>} or {@code null} if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS} is disabled
     */
    public @Nullable ListPool<EmojiObject> getEmojiPool() {
        return emojiPool;
    }

    /**
     * {@link ArrayList} of {@link EmojiObject emojis}, which has been retrieved from Discord in the update event
     * @return {@link ArrayList} of {@link EmojiObject emojis}
     * @throws InvalidDataException if a {@link SOData} was invalid
     */
    @Contract(value="-> new",pure = true)
    public @NotNull ArrayList<EmojiObject> getEmojis() throws InvalidDataException {
        ArrayList<EmojiObject> emojis = new ArrayList<>(emojisData.size());

        for(SOData d : emojisData){
            emojis.add(EmojiObject.fromData(lApi, d));
        }

        return emojis;
    }
}
