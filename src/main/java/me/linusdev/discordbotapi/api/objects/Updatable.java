package me.linusdev.discordbotapi.api.objects;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;

/**
 * Objects implementing this can be updated by {@link LApi}
 */
public interface Updatable {
    public void updateSelfByData(Data data);
}
