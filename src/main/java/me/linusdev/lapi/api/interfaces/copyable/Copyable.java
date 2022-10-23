/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.interfaces.copyable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     *
     * @param string the string to be copied
     * @return the same string, because string is immutable.
     */
    @Contract("null -> null; !null -> !null")
    static String copy(@Nullable String string) {
        return string;
    }

    /**
     *
     * @param array the array to be copied
     * @param <T> {@link Copyable}
     * @return A copy of given array. Every element of the array will be copied with {@link #copy(Copyable)}
     */
    @Contract("null -> null; !null -> !null")
    @SuppressWarnings("unchecked")
    static <T extends Copyable<T>> @Nullable T[] copyArrayDeep(@Nullable T @Nullable[] array){
        if(array == null) return null;
        T[] copy = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);

        for(int i = 0; i < copy.length; i++){
            copy[i] = Copyable.copy(array[i]);
        }

        return copy;
    }

    /**
     *
     * @param array the array to be copied
     * @param <T> {@link Copyable}
     * @return A copy of given array. Every element of the array will be the same as element in the input array (not copied)
     */
    static <T> T[] copyArrayFlat(@Nullable T[] array){
        return Arrays.copyOf(array, array.length);
    }

    /**
     *
     * @param list the {@link List} to be copied
     * @return An {@link ArrayList} with the elements copied with {@link Copyable#copy(Copyable)} from {@code list}.
     * @param <T> {@link Copyable}
     */
    @Contract("null -> null; !null -> !null")
    static <T extends Copyable<T>> @Nullable List<T> copyListDeep(@Nullable List<T> list) {
        if(list == null) return null;
        ArrayList<T> copy = new ArrayList<>(list.size());

        for(T item : list) {
            copy.add(Copyable.copy(item));
        }

        return copy;
    }

    /**
     *
     * @param list the {@link List} to be copied
     * @return An {@link ArrayList} with the same elements as given {@code list}.
     * @param <T> {@link Copyable}
     */
    @Contract("null -> null; !null -> !null")
    static <T> @Nullable List<T> copyListFlat(@Nullable List<T> list) {
        if(list == null) return null;
        return new ArrayList<>(list);
    }
}
