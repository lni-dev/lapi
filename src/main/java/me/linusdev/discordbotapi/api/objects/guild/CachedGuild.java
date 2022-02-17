package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.discordbotapi.api.interfaces.updatable.Updatable;

/**
 * A cached guild.
 */
public interface CachedGuild extends Guild, Updatable {

    boolean isUnavailable();

    /**
     * {@code true} if the current user left (or was removed from) this guild
     */
    boolean isRemoved();
}
