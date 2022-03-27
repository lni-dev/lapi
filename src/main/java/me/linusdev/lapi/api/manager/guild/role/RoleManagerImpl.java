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

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cached {@link Role roles}. An instance of this class should only exist if
 * {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_ROLES} is enabled
 */
public class RoleManagerImpl implements RoleManager, HasLApi {

    private final @NotNull LApiImpl lApi;

    private final @NotNull ConcurrentHashMap<String, Role> roles;

    public RoleManagerImpl(@NotNull LApiImpl lApi){
        this.lApi = lApi;
        this.roles = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull Collection<Role> getRoles(){
        return roles.values();
    }

    @Override
    public void addRole(@NotNull Role role){
        this.roles.put(role.getId(), role);
    }

    @Override
    public Role removeRole(@NotNull String roleId){
        return this.roles.remove(roleId);
    }

    @Override
    public @Nullable Update<Role, Role> updateRole(@NotNull Data updateData) throws InvalidDataException {
        String id = (String) updateData.get(Role.ID_KEY);
        Role role = this.roles.get(id);

        if(role == null) {
            //This should never happen...
            Logger.getLogger(this).warning("Trying to update role that does not exist...");
            return null;
        }

        if(lApi.isCopyOldRolesOnUpdateEventEnabled()){
            return new Update<Role, Role>(role, updateData);
        }else {
            return new Update<Role, Role>(null, role);
        }
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
