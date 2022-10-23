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

package me.linusdev.lapi.api.objects.channel.concrete;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.channel.AbstractChannel;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.channel.ChannelType;
import me.linusdev.lapi.api.objects.channel.PartialChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @see Channel
 * @see PartialChannel
 */
public class PartialChannelImpl extends AbstractChannel implements PartialChannel {

    private final @NotNull SOData data;

    public PartialChannelImpl(@NotNull ChannelType type, @NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        super(type, lApi, data);
        this.data = data;
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public @NotNull Channel copy() {
        try {
            return Channel.partialChannelFromData(lApi, data);
        } catch (InvalidDataException ignored) {
            return this; //Will not happen, as this channel was created with the same data...
        }
    }
}
