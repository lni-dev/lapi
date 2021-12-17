package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.discordbotapi.api.lapiandqueue.updatable.Updatable;
import org.jetbrains.annotations.Nullable;

public interface UpdatableGuildAbstract extends Updatable {

    boolean isUnavailable();
    
    @Nullable String getName();
}
