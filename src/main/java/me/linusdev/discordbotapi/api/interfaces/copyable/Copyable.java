package me.linusdev.discordbotapi.api.interfaces.copyable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface indicates, that given class can be copied using the {@link #copy()} method.
 * A copy of an object should be a new object with the same values. In case of constant objects, the {@link #copy()} method
 * may return the same object (Meaning {@code object == object.copy()} would evaluate to {@code true}). constant values that cannot be changed may also be shared by the original object and the copy.
 * @param <T> The class which should be copyable
 */
public interface Copyable<T> {
    @NotNull T copy();

    /**
     *
     * @param obj object to copy or {@code null}
     * @return a copy of obj or {@code null} if obj was {@code null}
     */
    @Contract("null -> null; !null -> !null")
    static <T extends Copyable<T>> T copy(@Nullable T obj){
        return obj == null ? null : obj.copy();
    }
}
