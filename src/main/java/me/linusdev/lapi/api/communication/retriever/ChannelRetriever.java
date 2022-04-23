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

package me.linusdev.lapi.api.communication.retriever;


import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to retrieve a {@link Channel}.
 * You might want to cast this {@link Channel} depending on its {@link Channel#getType() type}
 * @see Channel
 * @see me.linusdev.lapi.api.objects.enums.ChannelType
 */
public class ChannelRetriever extends DataRetriever<Channel>{

    public ChannelRetriever(@NotNull LApi lApi, @NotNull String id){
        super(lApi, new GetLinkQuery(lApi, GetLinkQuery.Links.GET_CHANNEL, new PlaceHolder(PlaceHolder.CHANNEL_ID, id)));
    }

    @Override
    protected @Nullable Channel processData(@NotNull SOData data) throws InvalidDataException {
        return Channel.fromData(lApi, data);
    }
}
