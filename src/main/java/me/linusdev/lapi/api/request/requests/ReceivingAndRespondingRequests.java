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

import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.retriever.NoContentRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponse;
import org.jetbrains.annotations.NotNull;

public interface ReceivingAndRespondingRequests extends HasLApi {


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Create Interaction Response                 *
     *                                                               *
     *  Done: 05.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * TODO: add docs
     * @param interactionId
     * @param interactionToken
     * @param response
     * @return
     */
    default @NotNull Queueable<LApiHttpResponse> createInteractionResponse(@NotNull String interactionId,
                                                                          @NotNull String interactionToken,
                                                                          @NotNull InteractionResponse response) {
        Query query = new LinkQuery(getLApi(), Link.CREATE_INTERACTION_RESPONSE, response.getBody(),
                new PlaceHolder(PlaceHolder.INTERACTION_ID, interactionId),
                new PlaceHolder(PlaceHolder.INTERACTION_TOKEN, interactionToken));

        return new NoContentRetriever(query);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      Original Interaction                     *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                        Followup Message                       *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
