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

package me.linusdev.lapi.api.objects.permission.overwrite;

import me.linusdev.data.Data;
import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;

import java.util.ArrayList;
import java.util.Collections;

public class PermissionOverwrites implements SimpleDatable {
    private ArrayList<PermissionOverwrite> overwrites;

    public PermissionOverwrites(PermissionOverwrite... overwrites){
        this.overwrites = new ArrayList<>(overwrites.length);
        Collections.addAll(this.overwrites, overwrites);
    }

    public PermissionOverwrites(ArrayList<Data> overwrites) throws InvalidDataException {
        PermissionOverwrite[] array = new PermissionOverwrite[overwrites.size()];

        int i = 0;
        for(Data data : overwrites){
            array[i++] = new PermissionOverwrite(data);
        }

        this.overwrites = new ArrayList<>(array.length);
        Collections.addAll(this.overwrites, array);
    }

    public ArrayList<PermissionOverwrite> getOverwrites() {
        return overwrites;
    }

    @Override
    public Object simplify() {
        return overwrites;
    }
}
