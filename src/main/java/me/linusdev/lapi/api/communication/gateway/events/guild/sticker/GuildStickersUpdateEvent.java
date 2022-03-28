/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.events.guild.sticker;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.events.Event;
import me.linusdev.lapi.api.communication.gateway.events.GuildEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.list.ListPool;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GuildStickersUpdateEvent extends Event implements GuildEvent {

    protected final @NotNull ArrayList<Data> stickersData;
    protected final @Nullable ListUpdate<Sticker> update;
    private final @Nullable ListPool<Sticker> stickerPool;

    public GuildStickersUpdateEvent(@NotNull LApi lApi, @Nullable GatewayPayloadAbstract payload,
                                    @Nullable Snowflake guildId, @NotNull ArrayList<Data> stickersData,
                                    @Nullable ListUpdate<Sticker> update,
                                    @Nullable ListPool<Sticker> stickerPool) {
        super(lApi, payload, guildId);
        this.stickersData = stickersData;
        this.update = update;
        this.stickerPool = stickerPool;
    }

    /**
     *
     * @return {@link ListUpdate<Sticker>} or {@code null} if
     * {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_STICKERS CACHE_STICKERS} is disabled
     */
    public @Nullable ListUpdate<Sticker> getUpdate() {
        return update;
    }

    /**
     *
     * @return {@link ListPool<Sticker>} or {@code null} if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_STICKERS CACHE_STICKERS} is disabled
     */
    public @Nullable ListPool<Sticker> getStickerPool() {
        return stickerPool;
    }

    /**
     * {@link ArrayList} of {@link Sticker stickers}, which has been retrieved from Discord in the update event
     * @return {@link ArrayList} of {@link Sticker stickers}
     * @throws InvalidDataException if a {@link Data} was invalid
     */
    @Contract(value="-> new",pure = true)
    public @NotNull ArrayList<Sticker> getStickers() throws InvalidDataException {
        ArrayList<Sticker> stickers = new ArrayList<>(stickersData.size());

        for(Data d : stickersData){
            stickers.add(Sticker.fromData(lApi, d));
        }

        return stickers;
    }
}

