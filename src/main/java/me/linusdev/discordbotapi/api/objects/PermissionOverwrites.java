package me.linusdev.discordbotapi.api.objects;

import me.linusdev.data.Data;
import me.linusdev.data.SimpleDatable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;

import java.util.ArrayList;
import java.util.Collections;

public class PermissionOverwrites implements SimpleDatable {
    private ArrayList<PermissionOverwrite> overwrites;

    public PermissionOverwrites(PermissionOverwrite... overwrites){
        this.overwrites = new ArrayList<>(overwrites.length);
        Collections.addAll(this.overwrites, overwrites);
    }

    public PermissionOverwrites(ArrayList<Data> overwrites) throws InvalidDataException {
        PermissionOverwrite[] array = new PermissionOverwrite[overwrites.size()];

        int i = 0;
        for(Data data : overwrites){
            array[i++] = new PermissionOverwrite(data);
        }

        this.overwrites = new ArrayList<>(array.length);
        Collections.addAll(this.overwrites, array);
    }

    public ArrayList<PermissionOverwrite> getOverwrites() {
        return overwrites;
    }

    @Override
    public Object simplify() {
        return overwrites;
    }
}
