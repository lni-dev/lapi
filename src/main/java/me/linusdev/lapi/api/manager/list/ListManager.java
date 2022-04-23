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

package me.linusdev.lapi.api.manager.list;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.other.LApiImplConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ListManager<T extends SnowflakeAble & CopyAndUpdatable<? extends T>> implements HasLApi, ListPool<T>, Manager {

    private boolean initialized = false;

    private final @NotNull LApiImpl lApi;
    private @Nullable ConcurrentHashMap<String, T> objects;

    private final @NotNull String idKey;
    private final @NotNull LApiImplConverter<SOData, T, InvalidDataException> converter;
    private final @NotNull Supplier<Boolean> doCopy;

    public ListManager(@NotNull LApiImpl lApi, @NotNull String idKey,
                       @NotNull LApiImplConverter<SOData, T, InvalidDataException> converter,
                       @NotNull Supplier<Boolean> doCopy) {
        this.lApi = lApi;
        this.idKey = idKey;
        this.converter = converter;
        this.doCopy = doCopy;
    }

    @Override
    public void init(int initialCapacity) {
        this.objects = new ConcurrentHashMap<>(initialCapacity);
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    public void add(T obj){
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");
        objects.put(obj.getId(), obj);
    }

    /**
     * Adds given data to the current list. If an old entry with the same id exists, it is overwritten.
     * 
     * @param data data of {@link T} to add
     * @return added {@link T}
     * @throws InvalidDataException in {@link LApiImplConverter#convert(LApiImpl, Object)}
     */
    public @NotNull T onAdd(@NotNull SOData data) throws InvalidDataException {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");
        T obj = converter.convert(lApi, data);
        objects.put(obj.getId(), obj);
        return obj;
    }

    /**
     * Updates {@link T} with the same id as given {@link Data}. If no such {@link T} exists, {@code null} is returned.
     * @param data data of {@link T} to {@link CopyAndUpdatable#updateSelfByData(Data) updated}
     * @return {@link Update}
     * @throws InvalidDataException in {@link LApiImplConverter#convert(LApiImpl, Object)}
     */
    public @Nullable Update<T, T> onUpdate(@NotNull SOData data) throws InvalidDataException {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");

        String id = (String) data.get(idKey);
        if(id == null) throw new InvalidDataException(data, "id missing.", null, idKey);
        
        T obj = objects.get(id);
        
        if(obj == null) {
            //cannot update, because given object does not exist
            return null;
        }

        if(!doCopy.get()) {
            obj.updateSelfByData(data);
            return new Update<>(null, obj);
        }

        T old = obj.copy();
        obj.updateSelfByData(data);
        return new Update<>(old, obj);
    }

    /**
     *
     * @param id id of the {@link T} to remove
     * @return {@link T} which was removed, or {@code null} if there was no {@link T} with given id
     */
    public @Nullable T onDelete(@NotNull String id) {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");
        return objects.remove(id);
    }

    /**
     * This will check the current list, with the given array.<br>
     * If some items of the current list are missing in the given array, they are removed from the current list.<br>
     * If some items are in both lists, they are {@link CopyAndUpdatable#updateSelfByData(Data) updated}.<br>
     * If some items of the given array are missing in the current list, they are added to the current list.<br>
     * @param data Array of data
     * @return {@link ListUpdate}
     * @throws InvalidDataException in {@link LApiImplConverter#convert(LApiImpl, Object)}
     */
    public ListUpdate<T> onUpdate(List<SOData> data) throws InvalidDataException {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");
        ArrayList<T> old = null;
        ArrayList<T> updated = null;
        ArrayList<T> added = null;
        ArrayList<T> removed = null;

        for(SOData d : data){
            String id = (String) d.get(idKey);
            if(id == null) throw new InvalidDataException(d, "id missing.", null, idKey);

            T obj = objects.get(id);

            if(obj == null){
                //new emoji
                obj = converter.convert(lApi, d);
                if(added == null) added = new ArrayList<>(1);
                added.add(obj);
                objects.put(obj.getId(), obj);

            } else {
                if(obj.checkIfChanged(d)){
                    if(doCopy.get()){
                        if(old == null) old = new ArrayList<>(1);
                        old.add(obj.copy());
                    }
                    obj.updateSelfByData(d);
                    if(updated == null) updated = new ArrayList<>(1);
                    updated.add(obj);
                }
            }
        }

        //check if an emoji was removed
        if(objects.size() != data.size() - (added != null ? added.size() : 0)){
            first: for(T obj : objects.values()){
                for(SOData d : data){
                    if(obj.getId().equals(d.get(EmojiObject.ID_KEY))){
                        continue first;
                    }
                }

                if(removed == null) removed = new ArrayList<>(1);
                removed.add(obj);
                objects.remove(obj.getId());
            }
        }


        return new ListUpdate<>(old, updated, added, removed);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public @Nullable T get(@NotNull String id) {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");
        return objects.get(id);
    }

    @Override
    public @NotNull Collection<T> getAll() {
        if(objects == null) throw new UnsupportedOperationException("init not yet called!");
        return objects.values();
    }
}
