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

import me.linusdev.data.Data;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.other.LApiImplConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ListManager<T extends SnowflakeAble & CopyAndUpdatable<T>> implements HasLApi, ListPool<T> {

    private final @NotNull LApiImpl lApi;
    private final @NotNull ConcurrentHashMap<String, T> objects;

    private final @NotNull String idKey;
    private final @NotNull LApiImplConverter<Data, T, InvalidDataException> converter;
    private final @NotNull Supplier<Boolean> doCopy;

    public ListManager(@NotNull LApiImpl lApi, @NotNull String idKey,
                       @NotNull LApiImplConverter<Data, T, InvalidDataException> converter,
                       @NotNull Supplier<Boolean> doCopy) {
        this.lApi = lApi;
        this.objects = new ConcurrentHashMap<>(10);
        this.idKey = idKey;
        this.converter = converter;
        this.doCopy = doCopy;
    }

    public void add(T obj){
        objects.put(obj.getId(), obj);
    }

    public ListUpdate<T> onUpdate(ArrayList<Data> data) throws InvalidDataException {
        ArrayList<T> old = null;
        ArrayList<T> updated = null;
        ArrayList<T> added = null;
        ArrayList<T> removed = null;

        for(Data d : data){
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
                    if(lApi.isCopyOldEmojiOnUpdateEventEnabled()){
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
                for(Data d : data){
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
        return objects.get(id);
    }

    @Override
    public @NotNull Collection<T> getAll() {
        return objects.values();
    }
}
