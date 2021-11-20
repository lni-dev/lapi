package me.linusdev.discordbotapi.api.objects;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;

public interface HasLApi {

    /**
     *
     * @return {@link LApi} used to create this Object
     */
    public @NotNull LApi getLApi();
}
