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

package me.linusdev.lapi.api.objects.invite;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

public enum TargetType implements SimpleDatable {
    /**
     * LApi specific
     */
    UNKNOWN(0),

    STREAM(1),

    EMBEDDED_APPLICATION(2),
    ;

    private final int value;

    TargetType(int value){
        this.value = value;
    }

    /**
     *
     * @param value to get {@link TargetType} for
     * @return {@link TargetType} matching given value, or {@link #UNKNOWN} if none matches
     */
    public static @NotNull TargetType fromValue(int value){
        for(TargetType type : TargetType.values()){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
