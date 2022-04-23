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

package me.linusdev.lapi.api.interfaces.updatable;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;

/**
 * Objects implementing this can be updated by {@link LApi}
 */
public interface Updatable {
    void updateSelfByData(SOData data) throws InvalidDataException;

    /**
     * checks whether given {@link Data} could represent this object.<br>
     * Note: Even if {@code false} is returned, given data may not contain exactly the same
     * information as this object.
     * @param data to compare with
     * @return {@code true} if given {@link Data} could represent this object, {@code false} otherwise
     */
    default boolean checkIfChanged(SOData data) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
