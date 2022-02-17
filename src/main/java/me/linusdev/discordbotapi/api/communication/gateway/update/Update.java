package me.linusdev.discordbotapi.api.communication.gateway.update;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.discordbotapi.api.interfaces.updatable.Updatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an {@link Update} contains an {@link #obj object}, which was updated and a {@link #copy} of the same object, before
 * it was updated.
 * @param <OBJ> object
 */
public class Update<OBJ extends Updatable, COPY> {

    private final @Nullable COPY copy;
    private final @NotNull OBJ obj;

    /**
     *
     * @param copy a copy of the object, before it was updated.
     * @param obj the updated object.
     */
    public Update(@Nullable COPY copy, @NotNull OBJ obj){
        this.copy = copy;
        this.obj = obj;
    }

    /**
     *
     * @param obj the <b>not updated</b> object, which must implement {@link Updatable} and {@link me.linusdev.discordbotapi.api.interfaces.copyable.Copyable Copyable}
     * @param data the {@link Data} to update the object. (calls {@link Updatable#updateSelfByData(Data)})
     * @param <C> class that implements {@link Updatable} and {@link me.linusdev.discordbotapi.api.interfaces.copyable.Copyable Copyable}
     * @throws InvalidDataException thrown by {@link Updatable#updateSelfByData(Data)}
     */
    public <C extends CopyAndUpdatable<COPY>> Update(@NotNull C obj, @NotNull Data data) throws InvalidDataException {
        this.copy = obj.copy();
        //noinspection unchecked
        this.obj = (OBJ) obj;
        this.obj.updateSelfByData(data);
    }

    /**
     * The updated object.
     */
    public @NotNull OBJ getObj() {
        return obj;
    }

    /**
     * The old Object before it was updated.
     */
    public @Nullable COPY getCopy() {
        return copy;
    }
}
