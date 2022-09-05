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

package me.linusdev.lapi.api.communication.gateway.identify;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#identify-identify-connection-properties" target="_top">Identify Connection Properties</a>
 */
public class ConnectionProperties implements Datable {

    public static final String OS_KEY = "$os";
    public static final String BROWSER_KEY = "$browser";
    public static final String DEVICE_KEY = "$device";

    private final @NotNull String os;
    private final @NotNull String browser;
    private final @NotNull String device;

    /**
     *
     * @param os your operating system
     * @param browser your library name
     * @param device your library name
     */
    public ConnectionProperties(@NotNull String os, @NotNull String browser, @NotNull String device) {
        this.os = os;
        this.browser = browser;
        this.device = device;
    }

    /**
     *
     * @param data {@link SOData}
     * @return {@link ConnectionProperties}
     * @throws InvalidDataException if {@link #OS_KEY}, {@link #BROWSER_KEY} or {@link #DEVICE_KEY} are missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ConnectionProperties fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        String os = (String) data.get(OS_KEY);
        String browser = (String) data.get(BROWSER_KEY);
        String device = (String) data.get(DEVICE_KEY);

        if(os == null || browser == null || device == null){
            InvalidDataException.throwException(data, null, ConnectionProperties.class,
                    new Object[]{os, null, device},
                    new String[]{OS_KEY, BROWSER_KEY, DEVICE_KEY});
        }

        //noinspection ConstantConditions
        return new ConnectionProperties(os, browser, device);
    }

    /**
     * your operating system
     */
    public @NotNull String getOs() {
        return os;
    }

    /**
     * your library name
     */
    public @NotNull String getBrowser() {
        return browser;
    }

    /**
     * your library name
     */
    public @NotNull String getDevice() {
        return device;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(OS_KEY, os);
        data.add(BROWSER_KEY, browser);
        data.add(DEVICE_KEY, device);

        return data;
    }
}
