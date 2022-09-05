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

package me.linusdev.lapi.api.objects.user;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.user.abstracts.BasicUserInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.linusdev.lapi.api.objects.user.User.*;

/**
 * This class only holds information and might not be up-to-date at all!
 * for example {@link #discriminator} could have been changed by the user.
 *
 */
public class Recipient implements BasicUserInformation, SnowflakeAble, Datable, HasLApi {

    private final @NotNull LApi lApi;

    private final @NotNull String username;
    private final @NotNull String discriminator;
    private final @NotNull Snowflake id;
    private final @Nullable String avatarHash;

    public Recipient(@NotNull LApi lApi, @NotNull String username, @NotNull String discriminator, @NotNull Snowflake id, @NotNull String avatarHash){
        this.lApi = lApi;
        this.username = username;
        this.discriminator = discriminator;
        this.id = id;
        this.avatarHash = avatarHash;
    }

    public Recipient(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        this.lApi = lApi;
        this.username = (String) data.getOrDefault(USERNAME_KEY, null);
        this.discriminator = (String) data.getOrDefault(DISCRIMINATOR_KEY, null);
        this.id = Snowflake.fromString((String) data.get(ID_KEY));
        this.avatarHash = ((String) data.get(AVATAR_KEY));

        if(this.username == null)
            throw new InvalidDataException(data, "field '" + USERNAME_KEY + "' missing or null in Recipient with id:" + getId()).addMissingFields(USERNAME_KEY);
        else if(this.discriminator == null)
            throw new InvalidDataException(data, "field '" + DISCRIMINATOR_KEY + "' missing or null in Recipient with id:" + getId()).addMissingFields(DISCRIMINATOR_KEY);
        else if(this.id == null)
            throw new InvalidDataException(data, "field '" + ID_KEY + "' missing or null in Recipient with id:" + getId()).addMissingFields(ID_KEY);

    }

    @Override
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public @NotNull String getDiscriminator() {
        return discriminator;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @Nullable String getAvatarHash() {
        return avatarHash;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(USERNAME_KEY, username);
        data.add(DISCRIMINATOR_KEY, discriminator);
        data.add(ID_KEY, id);
        data.add(AVATAR_KEY, avatarHash);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
