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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-types" target="_top">Activity Types</a>
 */
public enum ActivityType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(-1, "Unknown", ""),

    GAME(0,	"Game",	"Playing {name}"),
    STREAMING(1,	"Streaming", "Streaming {details}"),
    LISTENING(2,	"Listening",	"Listening to {name}"),
    WATCHING(3, "Watching",	"Watching {name}"),
    CUSTOM(4, "Custom", "{emoji} {name}"),
    COMPETING(5, "Competing", "Competing in {name}"),

    ;

    private final int id;
    private final @NotNull String name;
    private final @NotNull String format;

    ActivityType(int id, @NotNull String name, @NotNull String format) {
        this.id = id;
        this.name = name;
        this.format = format;
    }

    /**
     *
     * @param value int
     * @return {@link ActivityType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ActivityType fromValue(int value){
        for(ActivityType type : ActivityType.values()){
            if(type.id == value) return type;
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public Object simplify() {
        return id;
    }
}
