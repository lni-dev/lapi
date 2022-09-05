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

package me.linusdev.lapi.api.objects.guild;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#welcome-screen-object" target="_top">https://discord.com/developers/docs/resources/guild#welcome-screen-object</a>
 */
public class WelcomeScreen implements Datable {

    public static final String DESCRIPTION_KEY = "description";
    public static final String WELCOME_CHANNELS_KEY = "welcome_channels";

    private final @Nullable String description;
    private final @NotNull WelcomeScreenChannelStructure[] welcomeChannels;

    /**
     *
     * @param description the server description shown in the welcome screen
     * @param welcomeChannels the channels shown in the welcome screen, up to 5
     */
    public WelcomeScreen(@Nullable String description, @NotNull WelcomeScreenChannelStructure[] welcomeChannels) {
        this.description = description;
        this.welcomeChannels = welcomeChannels;
    }

    public static @Nullable WelcomeScreen fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String description = (String) data.get(DESCRIPTION_KEY);
        ArrayList<WelcomeScreenChannelStructure> channels = data.getListAndConvertWithException(WELCOME_CHANNELS_KEY, WelcomeScreenChannelStructure::fromData);


        return new WelcomeScreen(description, channels == null ? new WelcomeScreenChannelStructure[0] : channels.toArray(new WelcomeScreenChannelStructure[0]));
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(DESCRIPTION_KEY, description);
        data.add(WELCOME_CHANNELS_KEY, welcomeChannels);

        return data;
    }
}
