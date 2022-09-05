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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.NoContentRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.lapiandqueue.Queueable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import me.linusdev.lapi.api.templates.commands.EditApplicationCommandTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.linusdev.lapi.api.request.RequestFactory.*;

public interface ApplicationCommandRequests extends HasLApi {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                   Global Application Command                  *
     *                                                               *
     *  Done: 04.09.2022                                             *
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
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Create a new global command. Returns 201 and an {@link ApplicationCommand application command object}.
     * </p>
     * <p>
     *     Creating a command with the same name as an existing command for your application will overwrite the old command.
     * </p>
     * @param applicationId id of your application
     * @param template {@link ApplicationCommandTemplate} of the new command
     * @return {@link Queueable} which can create and retrieve the new command
     * @see Queueable#queue()
     * @see Link#CREATE_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> createGlobalApplicationCommand(@NotNull String applicationId,
                                                                                  @NotNull ApplicationCommandTemplate template){
        LinkQuery query = new LinkQuery(getLApi(), Link.CREATE_GLOBAL_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Fetch a global command for your application.
     * </p>
     * @param applicationId id of your application
     * @param commandId id of the {@link ApplicationCommand application command} to retrieve
     * @return {@link Queueable} which can retrieve the {@link ApplicationCommand}
     * @see Queueable#queue()
     * @see Link#GET_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> getGlobalApplicationCommand(@NotNull String applicationId,
                                                                               @NotNull String commandId) {
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GLOBAL_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Edit a global command. Returns 200 and an {@link ApplicationCommand application command object}.
     *     All fields are optional, but any fields provided will entirely overwrite the existing values of those fields.
     * </p>
     * @param applicationId id of your application
     * @param commandId id of the {@link ApplicationCommand application command} to edit
     * @param template {@link EditApplicationCommandTemplate} with all fields to edit
     * @return {@link Queueable} which can edit and retrieve the {@link ApplicationCommand}
     * @see Queueable#queue()
     * @see Link#EDIT_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> editGlobalApplicationCommand(@NotNull String applicationId,
                                                                                @NotNull String commandId,
                                                                                @NotNull EditApplicationCommandTemplate template){
        LinkQuery query = new LinkQuery(getLApi(), Link.EDIT_GLOBAL_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Deletes a global command. Returns 204 No Content on success.
     * </p>
     * @param applicationId id of your application
     * @param commandId id of the {@link ApplicationCommand application command} to edit
     * @return {@link Queueable} which can delete the {@link ApplicationCommand}
     * @see Queueable#queue()
     * @see Link#DELETE_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<LApiHttpResponse> deleteGlobalApplicationCommand(@NotNull String applicationId,
                                                                                @NotNull String commandId) {
        LinkQuery query = new LinkQuery(getLApi(), Link.DELETE_GLOBAL_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new NoContentRetriever(query);
    }

    /**
     * <p>
     *     Takes a list of application commands, overwriting the existing global command list for this application.
     *     Returns 200 and a list of application command objects. Commands that do not already exist will count toward
     *     daily application command create limits.
     * </p>
     * <p>
     *     This will overwrite all types of application commands: slash commands, user commands, and message commands.
     * </p>
     * @param applicationId id of your application
     * @param templates Array of {@link ApplicationCommandTemplate} containing the commands, that should be overwritten
     * @return {@link Queueable} which can overwrite the commands and retrieve the new {@link ApplicationCommand}s
     * @see Queueable#queue()
     * @see Link#BULK_OVERWRITE_GLOBAL_APPLICATION_COMMANDS
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(@NotNull String applicationId,
                                                                                               @NotNull ApplicationCommandTemplate[] templates) {

        LinkQuery query = new LinkQuery(getLApi(), Link.BULK_OVERWRITE_GLOBAL_APPLICATION_COMMANDS,
                new LApiHttpBody(SOData.wrap(templates)),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId));
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
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
