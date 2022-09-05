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

package me.linusdev.lapi.api.objects.channel.abstracts;

import me.linusdev.lapi.api.objects.enums.VideoQuality;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VoiceChannel {

    /**
     * the bitrate (in bits) of the voice channel
     */
    int getBitRate();

    /**
     * the user limit of the voice channel
     *
     * Possibly 0, this means there is no limit set
     */
    int getUserLimit();

    /**
     * <a href="https://discord.com/developers/docs/resources/voice#voice-region-object" target="_top">voice region</a> id for the voice channel, automatic when set to null
     */
    @Nullable
    String getRTCRegion();

    /**
     * the camera {@link VideoQuality} of the voice channel, 1 when not present
     */
    @NotNull
    VideoQuality getVideoQualityMode();
}
