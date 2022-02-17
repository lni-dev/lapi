package me.linusdev.discordbotapi.api.manager.guild.role;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.update.Update;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

/**
 * Manages cached {@link Role roles}. An instance of this class should only exist if
 * {@link me.linusdev.discordbotapi.api.config.ConfigFlag#CACHE_ROLES} is enabled
 */
public class RoleManager implements HasLApi {

    private final @NotNull LApiImpl lApi;

    private final @NotNull HashMap<String, Role> roles;

    public RoleManager(@NotNull LApiImpl lApi){
        this.lApi = lApi;
        this.roles = new HashMap<>();
    }

    public @NotNull Collection<Role> getRoles(){
        return roles.values();
    }

    public void addRole(@NotNull Role role){
        this.roles.put(role.getId(), role);
    }

    public Role removeRole(@NotNull String roleId){
        return this.roles.remove(roleId);
    }

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
