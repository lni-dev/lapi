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

package me.linusdev.lapi.api.objects.integration;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#integration-account-object-integration-account-structure" target="_top"></a>
 */
public class IntegrationAccount implements Datable {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";


    private final @NotNull String id;
    private final @NotNull String name;

    /**
     *
     * @param id id of the account
     * @param name name of the account
     */
    public IntegrationAccount(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link IntegrationAccount}
     * @throws InvalidDataException if {@link #ID_KEY} or {@link #NAME_KEY} are missing or {@code null}
     */
    @Contract("!null -> !null")
    @SuppressWarnings("ConstantConditions")
    public static @Nullable IntegrationAccount fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);

        if(id == null || name == null){
            InvalidDataException.throwException(data, null, IntegrationAccount.class,
                    new Object[]{id, name},
                    new String[]{ID_KEY, NAME_KEY});
        }

        return new IntegrationAccount(id, name);
    }

    /**
     * id of the account
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * name of the account
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return {@link Data}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);

        return data;
    }
}
