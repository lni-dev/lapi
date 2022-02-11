package me.linusdev.discordbotapi.api.lapiandqueue.updatable;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;

/**
 * Objects implementing this can be updated by {@link LApi}
 */
public interface Updatable {
    void updateSelfByData(Data data) throws InvalidDataException;
}
