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

package me.linusdev.lapi.api.request.requests;

import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.application.Application;
import org.jetbrains.annotations.NotNull;

public interface OAuth2Requests extends HasLApi {


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                   Get Current Bot Application Information                                 *
     *                                                                                                           *
     *  Done:       08.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * @return {@link Queueable} that can retrieve the bot's {@link Application application} object.
     */
    default @NotNull Queueable<Application> getCurrentBotApplicationInformation() {
        return new ConvertingRetriever<>(new LinkQuery(getLApi(), Link.GET_CURRENT_BOT_APPLICATION_INFORMATION), Application::fromData);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                    Get Current Authorization Information                                  *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Not yet implemented
     * @return nothing
     */
    default @NotNull Queueable<?> getCurrentAuthorizationInformation() {
        throw new UnsupportedOperationException("Not yet implemented");

        //TODO: implement. Requires authentication with a bearer token!

    }

}
