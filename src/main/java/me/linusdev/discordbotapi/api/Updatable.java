package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;

/**
 * Objects implementing this can be updated by the api
 */
public interface Updatable {
    public void updateSelfByData(Data data);
}
