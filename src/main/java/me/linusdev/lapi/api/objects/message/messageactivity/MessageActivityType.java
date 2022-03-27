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

package me.linusdev.lapi.api.objects.message.messageactivity;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-activity-types" target="">Message Activity Types</a>
 */
public enum MessageActivityType implements SimpleDatable {
    UNKNOWN(0),
    JOIN(1),
    SPECTATE(2),
    LISTEN(3),
    JOIN_REQUEST(5),
    ;

    private final int value;

    MessageActivityType(int value){
        this.value = value;
    }

    /**
     * @return {@link MessageActivityType} with given value or {@link #UNKNOWN} if no such type exists
     */
    public static @NotNull MessageActivityType fromValue(int value){
        for(MessageActivityType type : MessageActivityType.values()){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
