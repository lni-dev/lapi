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

package me.linusdev.lapi.api.manager.guild.role;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.objects.role.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RoleManager extends RolePool, Manager {

    /**
     *
     * @param updateData the updated {@link SOData} for the role to update.
     * @return {@link Update} contain the updated {@link Role}. {@link Update#getCopy() a copy of the role before it was updated} will be contained if
     * {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_ROLE_ON_UPDATE_EVENT COPY_ROLE_ON_UPDATE_EVENT} is set.
     * @throws InvalidDataException if given updateData is Invalid.
     */
    @Nullable Update<Role, Role> updateRole(@NotNull SOData updateData) throws InvalidDataException;

    /**
     * Adds given {@link Role} to this {@link RoleManager}
     * @param role the {@link Role} to add
     */
    void addRole(@NotNull Role role);

    /**
     *
     * @param roleId the id of the {@link Role}, that should be removed
     * @return the {@link Role}, that has been removed or {@code null} if no role with given id exists.
     */
    @Nullable Role removeRole(@NotNull String roleId);
}
