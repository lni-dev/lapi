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

import me.linusdev.lapi.api.communication.gateway.other.GetGatewayResponse;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;

public interface GatewayRequests extends HasLApi {


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                          Get Gateway                          *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                        Get Gateway Bot                        *
     *                                                               *
     *  Done: 05.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Returns an object based on the information in Get Gateway, plus additional metadata that can help during the
     *     operation of large or sharded bots. Unlike the Get Gateway, this route should not be cached for extended
     *     periods of time as the value is not guaranteed to be the same per-call, and changes as the bot joins/leaves
     *     guilds.
     * </p>
     * @return {@link Queueable} to get a {@link GetGatewayResponse}
     * @see Link#GET_GATEWAY_BOT
     */
    default @NotNull Queueable<GetGatewayResponse> getGatewayBot(){
        Query query = new LinkQuery(getLApi(), Link.GET_GATEWAY_BOT);
        return new ConvertingRetriever<>(query, (lApi, data) -> GetGatewayResponse.fromData(data));
    }

}
