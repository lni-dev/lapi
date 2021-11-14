package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.NotNull;

public class Role implements Datable {

    //todo

    public static @NotNull Role fromData(@NotNull Data data){
        return new Role();
    }

    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }
}
