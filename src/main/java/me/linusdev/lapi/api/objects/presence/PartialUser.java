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

package me.linusdev.lapi.api.objects.presence;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;

public class PartialUser implements Datable {

    private final @NotNull Data data;
    private final @NotNull Snowflake id;

    public PartialUser(@NotNull Data data) throws InvalidDataException {
        this.data = data;

        Object id = (Object) data.get(User.ID_KEY);
        if(id instanceof Number) id = String.valueOf(id);
        if(id == null) throw new InvalidDataException(data, "Id may not be null", null, User.ID_KEY);
        this.id = Snowflake.fromString((String) id);
    }

    /**
     * 	the user's id {@link Snowflake}
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * 	the user's id a {@link String}
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * raw {@link Data} of the retrieved user object.
     */
    @Override
    public @NotNull Data getData() {
        return data;
    }
}
