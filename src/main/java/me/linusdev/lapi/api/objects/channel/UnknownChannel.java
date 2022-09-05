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

package me.linusdev.lapi.api.objects.channel;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnknownChannel extends Channel<UnknownChannel> {

    protected @NotNull SOData data;

    public UnknownChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) {
        super(lApi, id, type);
        this.data = data;
    }

    @Override
    public @NotNull UnknownChannel copy() {
        return new UnknownChannel(lApi,
                Copyable.copy(id),
                type,
                data
        );
    }

    public @NotNull SOData getData() {
        return data;
    }

    @Override
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return null;
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        super.updateSelfByData(data);
        this.data = data;
    }
}
