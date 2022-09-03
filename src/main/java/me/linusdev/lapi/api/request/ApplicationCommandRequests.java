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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.command.LocalizationDictionary;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.linusdev.lapi.api.request.RequestFactory.*;

public interface ApplicationCommandRequests extends HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Global Application Command                  *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     *
     * Fetch all the global commands for your application.
     *
     * @param applicationId id of your application
     * @param withLocalizations {@code true} to include {@link LocalizationDictionary localizations} in the response {@link ApplicationCommand}s.
     *                                      {@code false} to include only the requesters (your) localization.
     *                                      see <a href="https://discord.com/developers/docs/interactions/application-commands#retrieving-localized-commands">here</a>
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of {@link ApplicationCommand}
     * @see Queueable#queue()
     * @see Link#GET_GLOBAL_APPLICATION_COMMANDS
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> getGlobalApplicationCommands(@NotNull String applicationId, boolean withLocalizations) {
        SOData queryStringsData = SOData.newOrderedDataWithKnownSize(1);
        queryStringsData.add(WITH_LOCALIZATIONS_KEY, withLocalizations);

        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GLOBAL_APPLICATION_COMMANDS, queryStringsData,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId));
        return new ArrayRetriever<>(getLApi(), query, ApplicationCommand::fromData);
    }

    default @NotNull Queueable<ApplicationCommand> createGlobalApplicationCommand(@NotNull String applicationId,
                                                                                  @NotNull ApplicationCommandTemplate template){
        LinkQuery query = new LinkQuery(getLApi(), Link.CREATE_GLOBAL_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                    Guild Application Command                  *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Global Application Command                  *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                 Application Command Permissions               *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

}
