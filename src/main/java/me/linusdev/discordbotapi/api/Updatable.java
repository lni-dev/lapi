package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;

/**
 * Objects implementing this can be updated by {@link LApi}
 */
public interface Updatable {
    public void updateSelfByData(Data data);
}
