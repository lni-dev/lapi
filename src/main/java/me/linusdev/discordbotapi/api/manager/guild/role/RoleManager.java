package me.linusdev.discordbotapi.api.manager.guild.role;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;

public class RoleManager implements HasLApi {

    private final @NotNull LApiImpl lApi;

    private final @NotNull HashMap<String, Role> roles;

    public RoleManager(@NotNull LApiImpl lApi, @NotNull Collection<Role> roles){
        this.lApi = lApi;
        this.roles = new HashMap<>();

        for(Role role : roles)
            this.roles.put(role.getId(), role);
    }

    public @NotNull Collection<Role> getRoles(){
        return roles.values();
    }

    public void addRole(@NotNull Role role){
        this.roles.put(role.getId(), role);
    }

    public void removeRole(@NotNull Role role){
        this.roles.remove(role.getId());
    }

    public void updateRole(@NotNull String id, @NotNull Data updateData){
        Role role = this.roles.get(id);

        if(role == null) {
            Logger.getLogger(this).warning("Trying to update role that does not exist...");
            return;
        }

        //TODO update role
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
