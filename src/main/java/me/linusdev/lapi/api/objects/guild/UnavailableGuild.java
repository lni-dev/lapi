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

package me.linusdev.lapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnavailableGuild implements Datable {

    private final @Nullable Boolean unavailable;
    private final @NotNull Snowflake id;


    public UnavailableGuild(@Nullable Boolean unavailable, @NotNull Snowflake id) {
        this.unavailable = unavailable;
        this.id = id;
    }

    @Contract("!null -> !null; null -> null")
    public static @Nullable UnavailableGuild fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        String id = (String) data.get(GuildImpl.ID_KEY);
        Boolean unavailable = (Boolean) data.get(GuildImpl.UNAVAILABLE_KEY);

        if(id == null) {
            InvalidDataException.throwException(data, null, UnavailableGuild.class, new Object[]{null}, new String[]{GuildImpl.ID_KEY});
        }

        //noinspection ConstantConditions
        return new UnavailableGuild(unavailable, Snowflake.fromString(id));
    }

    public @Nullable Boolean getUnavailable() {
        return unavailable;
    }

    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    public @NotNull String getId() {
        return id.asString();
    }

    @Override
    public Data getData() {
        Data data = new Data(2);
        data.add(GuildImpl.ID_KEY, id);
        data.add(GuildImpl.UNAVAILABLE_KEY, unavailable);
        return data;
    }
}
