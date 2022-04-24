/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.gateway.update;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.updatable.Updatable;
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
    private final boolean isNew;

    /**
     *
     * @param copy a copy of the object, before it was updated.
     * @param obj the updated object.
     */
    public Update(@Nullable COPY copy, @NotNull OBJ obj){
        this(copy, obj, false);
    }

    /**
     *
     * @param obj the updated object.
     * @param isNew {@code true} if obj is a new object and had no entry before (but caching is enabled), {@code false} otherwise.
     */
    public Update( @NotNull OBJ obj, boolean isNew){
        this(null, obj, isNew);
    }


    /**
     *
     * @param copy a copy of the object, before it was updated.
     * @param obj the updated object.
     * @param isNew {@code true} if obj is a new object and had no entry before (but caching is enabled), {@code false} otherwise.
     */
    private Update(@Nullable COPY copy, @NotNull OBJ obj, boolean isNew){
        this.copy = copy;
        this.obj = obj;
        this.isNew = isNew;
    }

    /**
     *
     * @param obj the <b>not updated</b> object, which must implement {@link Updatable} and {@link me.linusdev.lapi.api.interfaces.copyable.Copyable Copyable}
     * @param data the {@link SOData} to update the object. (calls {@link Updatable#updateSelfByData(Data)})
     * @param <C> class that implements {@link Updatable} and {@link me.linusdev.lapi.api.interfaces.copyable.Copyable Copyable}
     * @throws InvalidDataException thrown by {@link Updatable#updateSelfByData(Data)}
     */
    public <C extends CopyAndUpdatable<COPY>> Update(@NotNull C obj, @NotNull SOData data) throws InvalidDataException {
        this.copy = obj.copy();
        //noinspection unchecked
        this.obj = (OBJ) obj;
        this.obj.updateSelfByData(data);
        this.isNew = false;
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

    /**
     *
     * @return {@code true} if obj is a new object and had no entry before (but caching is enabled), {@code false} otherwise.
     */
    public boolean isNew() {
        return isNew;
    }
}
