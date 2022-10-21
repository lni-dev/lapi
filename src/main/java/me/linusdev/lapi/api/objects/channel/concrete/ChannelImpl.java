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
import org.jetbrains.annotations.NotNull;

/**
 * @see Channel
 */
public class ChannelImpl extends AbstractChannel {

    public ChannelImpl(@NotNull ChannelType type, @NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        super(type, lApi, data);
    }

    @Override
    public boolean isPartial() {
        return false;
    }

    @Override
    public boolean isCached() {
        return false;
    }
}
