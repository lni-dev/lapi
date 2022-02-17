package me.linusdev.discordbotapi.api.manager.guild.role;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.gateway.update.Update;
import me.linusdev.discordbotapi.api.objects.role.Role;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface RoleManager extends RolePool{

    /**
     *
     * @param updateData the updated {@link Data} for the role to update.
     * @return {@link Update} contain the updated {@link Role}. {@link Update#getCopy() a copy of the role before it was updated} will be contained if
     * {@link me.linusdev.discordbotapi.api.config.ConfigFlag#COPY_ROLE_ON_UPDATE_EVENT COPY_ROLE_ON_UPDATE_EVENT} is set.
     * @throws InvalidDataException if given updateData is Invalid.
     */
    @Nullable Update<Role, Role> updateRole(@NotNull Data updateData) throws InvalidDataException;

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
