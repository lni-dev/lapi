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

package me.linusdev.lapi.api.objects.enums;

import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-video-quality-modes" target="_top">Video Quality Modes</a>
 */
public enum VideoQuality {

    /**
     * This is LApi specific, in case discord adds another quality type
     */
    UNKNOWN(-2),

    /**
     * Discord chooses the quality for optimal performance
     */
    AUTO(1),

    /**
     * 720p
     */
    FULL(2),
    ;

    private final int id;

    VideoQuality(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * @return the VideoQuality representing the id. If Id is {@code null}, {@link #AUTO} is returned. This is the Default
     */
    public static VideoQuality fromId(@Nullable Number id) {
        if(id == null) return AUTO;
        for (VideoQuality type : VideoQuality.values())
            if (type.getId() == id.intValue()) return type;
        return UNKNOWN;
    }
}
