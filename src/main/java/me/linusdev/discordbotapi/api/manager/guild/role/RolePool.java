package me.linusdev.discordbotapi.api.manager.guild.role;

import me.linusdev.discordbotapi.api.objects.role.Role;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RolePool {

    /**
     * @return A Collection of {@link Role roles}, which are managed by this {@link RoleManager}.
     * This collection should be backed by the {@link RoleManager}, meaning that changes on the {@link RoleManager} will
     * have direct effect to the returned {@link Collection}.
     */
    @Contract(pure = false)
    @NotNull Collection<Role> getRoles();
}
