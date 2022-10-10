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

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.objects.application.Application;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.message.Message;
import me.linusdev.lapi.api.communication.retriever.NoContentRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponse;
import me.linusdev.lapi.api.templates.message.MessageTemplate;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.other.placeholder.Name.*;

public interface ReceivingAndRespondingRequests extends HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                         Create Interaction Response                                       *
     *                                                                                                           *
     *  Done:       05.09.2022                                                                                   *
     *  Updated:    08.10.2022                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Create a response to an Interaction from the gateway. Body is an interaction response. Returns 204 No Content.
     *     This endpoint also supports file attachments.
     * </p>
     * @param interactionId id of the {@link Interaction}
     * @param interactionToken token of the {@link Interaction}
     * @param response {@link  InteractionResponse} to send
     * @return {@link Queueable} which can create an interaction response.
     */
    default @NotNull Queueable<LApiHttpResponse> createInteractionResponse(@NotNull String interactionId,
                                                                          @NotNull String interactionToken,
                                                                          @NotNull InteractionResponse response) {
        Query query = new LinkQuery(getLApi(), Link.CREATE_INTERACTION_RESPONSE, response.getBody(),
                INTERACTION_ID.withValue(interactionId),
                INTERACTION_TOKEN.withValue(interactionToken));

        return new NoContentRetriever(query);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                             Original Interaction                                          *
     *                                                                                                           *
     *  Done:       08.10.2022                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Returns the initial Interaction response.
     * </p>
     * @param applicationId id of your bot's {@link Application}
     * @param interactionToken token of the {@link Interaction}
     * @return {@link Queueable} which can get the original interaction response
     */
    default @NotNull Queueable<Message> getOriginalInteractionResponse(@NotNull String applicationId, @NotNull String interactionToken) {
        Query query = new LinkQuery(getLApi(), Link.GET_ORIGINAL_INTERACTION_RESPONSE,
                APPLICATION_ID.withValue(applicationId),
                INTERACTION_ID.withValue(interactionToken));

        return new ConvertingRetriever<>(query, Message::fromData);
    }

    /**
     * <p>
     *     Returns the initial Interaction response.
     * </p>
     * @param interactionToken token of the {@link Interaction}
     * @return {@link Queueable} which can get the original interaction response
     * @throws LApiIllegalStateException if the application id is not cached.
     */
    default @NotNull Queueable<Message> getOriginalInteractionResponse(@NotNull String interactionToken) {
        return getOriginalInteractionResponse(RequestUtils.getApplicationIdFromCache(this), interactionToken);
    }

    /**
     * <p>
     *     Edits the initial Interaction response.
     * </p>
     * @param applicationId id of your bot's {@link Application}
     * @param interactionToken token of the {@link Interaction}
     * @param template {@link MessageTemplate} to edit your message
     * @return {@link Queueable} which can edit the original interaction response
     */
    default @NotNull Queueable<Message> editOriginalInteractionResponse(@NotNull String applicationId, @NotNull String interactionToken, @NotNull MessageTemplate template) {
        Query query = new LinkQuery(getLApi(), Link.EDIT_ORIGINAL_INTERACTION_RESPONSE, template.getBody(),
                APPLICATION_ID.withValue(applicationId),
                INTERACTION_ID.withValue(interactionToken));

        return new ConvertingRetriever<>(query, Message::fromData);
    }

    /**
     * <p>
     *     Edits the initial Interaction response.
     * </p>
     * @param interactionToken token of the {@link Interaction}
     * @param template {@link MessageTemplate} to edit your message
     * @return {@link Queueable} which can edit the original interaction response
     * @throws LApiIllegalStateException if the application id is not cached.
     */
    default @NotNull Queueable<Message> editOriginalInteractionResponse(@NotNull String interactionToken, @NotNull MessageTemplate template) {
        return editOriginalInteractionResponse(RequestUtils.getApplicationIdFromCache(this), interactionToken, template);
    }

    /**
     * <p>
     *     Deletes the initial Interaction response.
     * </p>
     * @param applicationId id of your bot's {@link Application}
     * @param interactionToken token of the {@link Interaction}
     * @return {@link Queueable} which can delete the original interaction response
     */
    default @NotNull Queueable<LApiHttpResponse> deleteOriginalInteractionResponse(@NotNull String applicationId, @NotNull String interactionToken) {
        Query query = new LinkQuery(getLApi(), Link.EDIT_ORIGINAL_INTERACTION_RESPONSE,
                APPLICATION_ID.withValue(applicationId),
                INTERACTION_ID.withValue(interactionToken));

        return new NoContentRetriever(query);
    }

    /**
     * <p>
     *     Deletes the initial Interaction response.
     * </p>
     * @param interactionToken token of the {@link Interaction}
     * @return {@link Queueable} which can delete the original interaction response
     * @throws LApiIllegalStateException if the application id is not cached.
     */
    default @NotNull Queueable<LApiHttpResponse> deleteOriginalInteractionResponse(@NotNull String interactionToken) {
       return deleteOriginalInteractionResponse(RequestUtils.getApplicationIdFromCache(this), interactionToken);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                                                                           *
     *                                                                                                           *
     *                                               Followup Message                                            *
     *                                                                                                           *
     *  Done:       00.00.0000                                                                                   *
     *  Updated:    00.00.0000                                                                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * @param applicationId id of your bot's {@link Application}
     * @param interactionToken token of the {@link Interaction}
     * @param template {@link MessageTemplate} to create the message
     * @return {@link Queueable} that can create the followup message.
     */
    default @NotNull Queueable<Message> createFollowupMessage(@NotNull String applicationId, @NotNull String interactionToken, @NotNull MessageTemplate template) {
        Query query = new LinkQuery(getLApi(), Link.CREATE_FOLLOWUP_MESSAGE, template.getBody(),
                APPLICATION_ID.withValue(applicationId),
                INTERACTION_ID.withValue(interactionToken));

        return new ConvertingRetriever<>(query, Message::fromData);
    }

    /**
     *
     * @param interactionToken token of the {@link Interaction}
     * @param template {@link MessageTemplate} to create the message
     * @return {@link Queueable} that can create the followup message.
     * @throws LApiIllegalStateException if the application id is not cached.
     */
    default @NotNull Queueable<Message> createFollowupMessage(@NotNull String interactionToken, @NotNull MessageTemplate template) {
        return createFollowupMessage(RequestUtils.getApplicationIdFromCache(this), interactionToken, template);
    }


}
