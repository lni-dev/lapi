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

package me.linusdev.lapi.api.objects.presence;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.gateway.presence.StatusType;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#client-status-object">Client Status Object</a>
 */
public class ClientStatus implements Datable, Copyable<ClientStatus> {
    public static final String DESKTOP_KEY = "desktop";
    public static final String MOBILE_KEY = "mobile";
    public static final String WEB_KEY = "web";

    private final @Nullable StatusType desktop;
    private final @Nullable StatusType mobile;
    private final @Nullable StatusType web;

    /**
     *
     * @param desktop the user's status set for an active desktop (Windows, Linux, Mac) application session
     * @param mobile the user's status set for an active mobile (iOS, Android) application session
     * @param web the user's status set for an active web (browser, bot account) application session
     */
    public ClientStatus(@Nullable StatusType desktop, @Nullable StatusType mobile, @Nullable StatusType web) {
        this.desktop = desktop;
        this.mobile = mobile;
        this.web = web;
    }

    /**
     * @param data {@link Data}
     * @return {@link ClientStatus}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ClientStatus fromData(@Nullable Data data) {
        if(data == null) return null;
        return new ClientStatus(
                StatusType.fromValue((String) data.get(DESKTOP_KEY)),
                StatusType.fromValue((String) data.get(MOBILE_KEY)),
                StatusType.fromValue((String) data.get(WEB_KEY))
        );
    }

    /**
     * the user's status set for an active desktop (Windows, Linux, Mac) application session
     */
    public @Nullable StatusType getDesktop() {
        return desktop;
    }

    /**
     * the user's status set for an active mobile (iOS, Android) application session
     */
    public @Nullable StatusType getMobile() {
        return mobile;
    }

    /**
     * the user's status set for an active web (browser, bot account) application session
     */
    public @Nullable StatusType getWeb() {
        return web;
    }

    /**
     * @return The first of {@link #getDesktop()}, {@link #getMobile()}, {@link #getWeb()} or {@link StatusType#UNKNOWN}
     * which is not null.
     */
    public @NotNull StatusType getAny() {
        return Objects.requireNonNullElse(getDesktop(),
                Objects.requireNonNullElse(getMobile(),
                        Objects.requireNonNullElse(getWeb(),
                                StatusType.UNKNOWN)));
    }

    @Override
    public Data getData() {
        Data data = new Data(1);

        data.addIfNotNull(DESKTOP_KEY, desktop);
        data.addIfNotNull(MOBILE_KEY, mobile);
        data.addIfNotNull(WEB_KEY, web);

        return data;
    }

    @Override
    public @NotNull ClientStatus copy() {
        return new ClientStatus(desktop, mobile, web);
    }
}
