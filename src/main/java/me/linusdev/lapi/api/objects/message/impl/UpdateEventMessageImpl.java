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

package me.linusdev.lapi.api.objects.message.impl;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.message.AnyMessage;
import me.linusdev.lapi.api.objects.message.ImplementationType;
import me.linusdev.lapi.api.objects.message.concrete.UpdateEventMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class should only be accessed as {@link UpdateEventMessage} interface, because the {@link org.jetbrains.annotations.NotNull NotNull}
 * annotations in this class are possibly wrong. That is because it has the same fields as {@link CreateEventMessageImpl}, but different nullability.
 * The Nullability is specified in {@link UpdateEventMessage}.
 */
public class UpdateEventMessageImpl extends CreateEventMessageImpl implements UpdateEventMessage, AnyMessage {

    protected final @NotNull SOData originalData;

    @SuppressWarnings("ConstantConditions")
    protected UpdateEventMessageImpl(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        super(lApi, data, false);

        if(id == null || channelId == null) {
            InvalidDataException.throwException(data, null, this.getClass(),
                    new Object[]{id, channelId},
                    new String[]{ID_KEY, CHANNEL_ID_KEY});
        }

        this.originalData = data;
    }

    @Contract("_, _ -> new")
    public static @NotNull UpdateEventMessage newInstance(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        return new UpdateEventMessageImpl(lApi, data);
    }

    @Override
    public @NotNull SOData getOriginalData() {
        return originalData;
    }

    @Override
    public @NotNull ImplementationType getImplementationType() {
        return UpdateEventMessage.super.getImplementationType();
    }
}
