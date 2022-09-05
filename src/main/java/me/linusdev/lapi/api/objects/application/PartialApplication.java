/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.objects.application;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is only used in the {@link ReadyEvent ReadyEvent}
 *
 * @see ReadyEvent ReadyEvent
 * @see Application
 */
public class PartialApplication implements ApplicationAbstract {

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @Nullable Integer flags;
    private final @Nullable ApplicationFlag[] flagsArray;

    /**
     *
     * @param lApi {@link LApi}
     * @param id the id of the app
     * @param flags the application's public flags
     */
    public PartialApplication(@NotNull LApi lApi, @NotNull Snowflake id, @Nullable Integer flags) {
        this.lApi = lApi;
        this.id = id;
        this.flags = flags;
        this.flagsArray = flags == null ? null : ApplicationFlag.getFlagsFromInteger(flags);
    }

    /**
     *
     * @param data with required fields
     * @return {@link Application} or {@code null} if data was {@code null}
     * @throws InvalidDataException if (id == null) == true
     */
    public static @Nullable PartialApplication fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if (data == null) return null;

        String id = (String) data.get(Application.ID_KEY);
        Number flags = (Number) data.get(Application.FLAGS_KEY);

        if(id == null){
            InvalidDataException.throwException(data, null, Application.class, new Object[]{id}, new String[]{Application.ID_KEY});
        }

        //noinspection ConstantConditions
        return new PartialApplication(lApi, Snowflake.fromString(id), flags == null ? null : flags.intValue());
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the application's public flags
     * @see #getFlagsArray()
     */
    public @Nullable Integer getFlags() {
        return flags;
    }

    /**
     * the application's public flags
     * @see ApplicationFlag
     */
    public ApplicationFlag[] getFlagsArray() {
        return flagsArray;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(Application.ID_KEY, id);
        data.addIfNotNull(Application.FLAGS_KEY, flags);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
