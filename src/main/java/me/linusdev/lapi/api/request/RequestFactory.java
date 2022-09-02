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

package me.linusdev.lapi.api.request;

import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.objects.HasLApi;

import org.jetbrains.annotations.NotNull;

/**
 * The {@link RequestFactory} can create {@link Queueable Queueables} to send HTTP-requests to discord.
 */
public class RequestFactory implements HasLApi,
        ChannelRequests {

    private final @NotNull LApiImpl lApi;

    public RequestFactory(@NotNull LApiImpl lApi) {
        this.lApi = lApi;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }


}
