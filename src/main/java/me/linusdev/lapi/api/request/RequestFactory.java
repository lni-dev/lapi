/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.request;

import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.interfaces.HasLApi;

import me.linusdev.lapi.api.request.requests.*;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link RequestFactory} can create {@link Queueable Queueables} to send HTTP-requests to discord.
 */
public class RequestFactory implements HasLApi,
                                    ApplicationCommandRequests,
                                    ReceivingAndRespondingRequests,

                                    GuildRequests,

                                    ChannelRequests,

                                    UserRequests,

                                    GatewayRequests,
                                    OAuth2Requests
{

    public static final String WITH_LOCALIZATIONS_KEY = "with_localizations";

    public static final String AROUND_KEY = "around";
    public static final String BEFORE_KEY = "before";
    public static final String AFTER_KEY = "after";
    public static final String LIMIT_KEY = "limit";



    private final @NotNull LApiImpl lApi;

    public RequestFactory(@NotNull LApiImpl lApi) {
        this.lApi = lApi;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }


}
