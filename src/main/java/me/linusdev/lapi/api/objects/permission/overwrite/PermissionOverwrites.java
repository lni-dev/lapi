/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.objects.permission.overwrite;

import me.linusdev.data.SimpleDatable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PermissionOverwrites implements Copyable<PermissionOverwrites>, SimpleDatable {
    private ArrayList<PermissionOverwrite> overwrites;

    private PermissionOverwrites() {}

    public PermissionOverwrites(PermissionOverwrite... overwrites){
        this.overwrites = new ArrayList<>(overwrites.length);
        Collections.addAll(this.overwrites, overwrites);
    }

    public PermissionOverwrites(List<Object> overwrites) throws InvalidDataException {
        PermissionOverwrite[] array = new PermissionOverwrite[overwrites.size()];

        int i = 0;
        for(Object o : overwrites){
            array[i++] = new PermissionOverwrite((SOData) o);
        }

        this.overwrites = new ArrayList<>(array.length);
        Collections.addAll(this.overwrites, array);
    }

    public List<PermissionOverwrite> getOverwrites() {
        return overwrites;
    }

    @Override
    public Object simplify() {
        return overwrites;
    }

    @Override
    public @NotNull PermissionOverwrites copy() {
        PermissionOverwrites copy = new PermissionOverwrites();
        //noinspection unchecked

        copy.overwrites = (ArrayList<PermissionOverwrite>) this.overwrites.clone();
        return copy;
    }
}
