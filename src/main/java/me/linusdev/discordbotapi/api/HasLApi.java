package me.linusdev.discordbotapi.api;

import org.jetbrains.annotations.NotNull;

public interface HasLApi {

    /**
     *
     * @return {@link LApi} used to create this Object
     */
    public @NotNull LApi getLApi();
}
