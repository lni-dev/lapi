package me.linusdev.discordbotapi.api.manager;

import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import org.jetbrains.annotations.NotNull;

public interface ManagerFactory<M> {

    @NotNull M newInstance(@NotNull LApiImpl lApi);
}
