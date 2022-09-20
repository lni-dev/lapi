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
import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.communication.http.request.body.LApiHttpBody;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.ConvertingRetriever;
import me.linusdev.lapi.api.communication.retriever.NoContentRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.async.queue.Queueable;
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
     * Fetch all global commands for your application.
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
     *
     * Fetch all global commands for your application.
     *
     * @param withLocalizations {@code true} to include {@link LocalizationDictionary localizations} in the response {@link ApplicationCommand}s.
     *                                      {@code false} to include only the requesters (your) localization.
     *                                      see <a href="https://discord.com/developers/docs/interactions/application-commands#retrieving-localized-commands">here</a>
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of {@link ApplicationCommand}
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#GET_GLOBAL_APPLICATION_COMMANDS
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> getGlobalApplicationCommands(boolean withLocalizations) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        return getGlobalApplicationCommands(getLApi().getCache().getCurrentApplicationId(), withLocalizations);
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
     *     Create a new global command. Returns 201 and an {@link ApplicationCommand application command object}.
     * </p>
     * <p>
     *     Creating a command with the same name as an existing command for your application will overwrite the old command.
     * </p>
     * @param template {@link ApplicationCommandTemplate} of the new command
     * @return {@link Queueable} which can create and retrieve the new command
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#CREATE_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> createGlobalApplicationCommand(@NotNull ApplicationCommandTemplate template){
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.CREATE_GLOBAL_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()));
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
     *     Fetch a global command for your application.
     * </p>
     * @param commandId id of the {@link ApplicationCommand application command} to retrieve
     * @return {@link Queueable} which can retrieve the {@link ApplicationCommand}
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#GET_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> getGlobalApplicationCommand(@NotNull String commandId) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GLOBAL_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
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
     *     Edit a global command. Returns 200 and an {@link ApplicationCommand application command object}.
     *     All fields are optional, but any fields provided will entirely overwrite the existing values of those fields.
     * </p>
     * @param commandId id of the {@link ApplicationCommand application command} to edit
     * @param template {@link EditApplicationCommandTemplate} with all fields to edit
     * @return {@link Queueable} which can edit and retrieve the {@link ApplicationCommand}
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#EDIT_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> editGlobalApplicationCommand(@NotNull String commandId,
                                                                                @NotNull EditApplicationCommandTemplate template){
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.EDIT_GLOBAL_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Deletes a global command. Returns 204 No Content on success.
     * </p>
     * @param applicationId id of your application
     * @param commandId id of the {@link ApplicationCommand application command} to delete
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
     *     Deletes a global command. Returns 204 No Content on success.
     * </p>
     * @param commandId id of the {@link ApplicationCommand application command} to delete
     * @return {@link Queueable} which can delete the {@link ApplicationCommand}
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#DELETE_GLOBAL_APPLICATION_COMMAND
     */
    default @NotNull Queueable<LApiHttpResponse> deleteGlobalApplicationCommand(@NotNull String commandId) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.DELETE_GLOBAL_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
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

    /**
     * <p>
     *     Takes a list of application commands, overwriting the existing global command list for this application.
     *     Returns 200 and a list of application command objects. Commands that do not already exist will count toward
     *     daily application command create limits.
     * </p>
     * <p>
     *     This will overwrite all types of application commands: slash commands, user commands, and message commands.
     * </p>
     * @param templates Array of {@link ApplicationCommandTemplate} containing the commands, that should be overwritten
     * @return {@link Queueable} which can overwrite the commands and retrieve the new {@link ApplicationCommand}s
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#BULK_OVERWRITE_GLOBAL_APPLICATION_COMMANDS
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> bulkOverwriteGlobalApplicationCommands(@NotNull ApplicationCommandTemplate[] templates) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.BULK_OVERWRITE_GLOBAL_APPLICATION_COMMANDS,
                new LApiHttpBody(SOData.wrap(templates)),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()));
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                    Guild Application Command                  *
     *                                                               *
     *  Done: 11.09.2022                                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * <p>
     *     Fetch all of the guild commands for your application for a specific guild.
     * </p>
     * @param applicationId id of your application
     * @param guildId id of the guild
     * @param withLocalizations {@code true} to include {@link LocalizationDictionary localizations} in the response {@link ApplicationCommand}s.
     *                                      {@code false} to include only the requesters (your) localization.
     *                                      see <a href="https://discord.com/developers/docs/interactions/application-commands#retrieving-localized-commands">here</a>
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of guild {@link ApplicationCommand}
     * @see Queueable#queue()
     * @see Link#GET_GUILD_APPLICATION_COMMANDS
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> getGuildApplicationCommands(@NotNull String applicationId,
                                                                                          @NotNull String guildId,
                                                                                          boolean withLocalizations) {
        SOData queryStringsData = SOData.newOrderedDataWithKnownSize(1);
        queryStringsData.add(WITH_LOCALIZATIONS_KEY, withLocalizations);

        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GUILD_APPLICATION_COMMANDS, queryStringsData,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId));
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Fetch all of the guild commands for your application for a specific guild.
     * </p>
     * @param guildId id of the guild
     * @param withLocalizations {@code true} to include {@link LocalizationDictionary localizations} in the response {@link ApplicationCommand}s.
     *                                      {@code false} to include only the requesters (your) localization.
     *                                      see <a href="https://discord.com/developers/docs/interactions/application-commands#retrieving-localized-commands">here</a>
     * @return {@link Queueable} which can retrieve an {@link ArrayList} of guild {@link ApplicationCommand}
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#GET_GUILD_APPLICATION_COMMANDS
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> getGuildApplicationCommands(@NotNull String guildId,
                                                                                          boolean withLocalizations) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        SOData queryStringsData = SOData.newOrderedDataWithKnownSize(1);
        queryStringsData.add(WITH_LOCALIZATIONS_KEY, withLocalizations);

        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GUILD_APPLICATION_COMMANDS, queryStringsData,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId));
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Create a new guild command. New guild commands will be available in the guild immediately.
     *     Returns 201 and an {@link ApplicationCommand application command object}.
     *     If the command did not already exist, it will count toward daily application command create limits.
     * </p>
     * <p>
     *     Creating a command with the same name as an existing command for your application will overwrite the old command.
     * </p>
     * @param applicationId id of your application
     * @param guildId id of the guild
     * @param template {@link ApplicationCommandTemplate} of the new command
     * @return {@link Queueable} which can create and retrieve the new command
     * @see Queueable#queue()
     * @see Link#CREATE_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> createGuildApplicationCommand(@NotNull String applicationId,
                                                                                 @NotNull String guildId,
                                                                                 @NotNull ApplicationCommandTemplate template) {
        LinkQuery query = new LinkQuery(getLApi(), Link.CREATE_GUILD_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Create a new guild command. New guild commands will be available in the guild immediately.
     *     Returns 201 and an {@link ApplicationCommand application command object}.
     *     If the command did not already exist, it will count toward daily application command create limits.
     * </p>
     * <p>
     *     Creating a command with the same name as an existing command for your application will overwrite the old command.
     * </p>
     * @param guildId id of the guild
     * @param template {@link ApplicationCommandTemplate} of the new command
     * @return {@link Queueable} which can create and retrieve the new command
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#CREATE_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> createGuildApplicationCommand(@NotNull String guildId, @NotNull ApplicationCommandTemplate template) {

        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.CREATE_GUILD_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Fetch a guild command for your application. Returns an {@link ApplicationCommand application command object}.
     * </p>
     * @param applicationId id of your application
     * @param guildId id of the guild
     * @param commandId id of the command
     * @return {@link Queueable} which can retrieve the command
     * @see Queueable#queue()
     * @see Link#GET_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> getGuildApplicationCommand(@NotNull String applicationId,
                                                                              @NotNull String guildId,
                                                                              @NotNull String commandId) {
        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GUILD_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Fetch a guild command for your application. Returns an {@link ApplicationCommand application command object}.
     * </p>
     * @param guildId id of the guild
     * @param commandId id of the command
     * @return {@link Queueable} which can retrieve the command
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#GET_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> getGuildApplicationCommand(@NotNull String guildId,
                                                                              @NotNull String commandId) {

        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.GET_GUILD_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Edit a guild command. Updates for guild commands will be available immediately.
     *     Returns 200 and an {@link ApplicationCommand application command object}. All fields are optional,
     *     but any fields provided will entirely overwrite the existing values of those fields.
     * </p>
     * @param applicationId id of your application
     * @param guildId id of the guild
     * @param commandId id of the command to edit
     * @param template {@link EditApplicationCommandTemplate} template with all fields to edit
     * @return {@link Queueable} that can edit and retrieve the {@link ApplicationCommand}
     * @see Queueable#queue()
     * @see Link#EDIT_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> editGuildApplicationCommand(@NotNull String applicationId,
                                                                               @NotNull String guildId,
                                                                               @NotNull String commandId,
                                                                               @NotNull EditApplicationCommandTemplate template) {
        LinkQuery query = new LinkQuery(getLApi(), Link.EDIT_GUILD_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Edit a guild command. Updates for guild commands will be available immediately.
     *     Returns 200 and an {@link ApplicationCommand application command object}. All fields are optional,
     *     but any fields provided will entirely overwrite the existing values of those fields.
     * </p>
     * @param guildId id of the guild
     * @param commandId id of the command to edit
     * @param template {@link EditApplicationCommandTemplate} template with all fields to edit
     * @return {@link Queueable} that can edit and retrieve the {@link ApplicationCommand}
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#EDIT_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ApplicationCommand> editGuildApplicationCommand(@NotNull String guildId,
                                                                               @NotNull String commandId,
                                                                               @NotNull EditApplicationCommandTemplate template) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.EDIT_GUILD_APPLICATION_COMMAND, template.getBody(),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new ConvertingRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Delete a guild command. Returns 204 No Content on success.
     * </p>
     * @param applicationId id of your application
     * @param guildId id of the guild
     * @param commandId id of the command
     * @return {@link Queueable} to delete the command
     * @see Queueable#queue()
     * @see Link#DELETE_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<LApiHttpResponse> deleteGuildApplicationCommand(@NotNull String applicationId,
                                                                               @NotNull String guildId,
                                                                               @NotNull String commandId) {
        LinkQuery query = new LinkQuery(getLApi(), Link.DELETE_GUILD_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new NoContentRetriever(query);
    }

    /**
     * <p>
     *     Delete a guild command. Returns 204 No Content on success.
     * </p>
     * @param guildId id of the guild
     * @param commandId id of the command
     * @return {@link Queueable} to delete the command
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#DELETE_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<LApiHttpResponse> deleteGuildApplicationCommand(@NotNull String guildId,
                                                                               @NotNull String commandId) {
        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.DELETE_GUILD_APPLICATION_COMMAND,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.COMMAND_ID, commandId));
        return new NoContentRetriever(query);
    }

    /**
     * <p>
     *     Takes a list of application commands, overwriting the existing command list for this application for the
     *     targeted guild. Returns 200 and a list of {@link ApplicationCommand application command objects}.
     * </p>
     * @param applicationId id of your application
     * @param guildId id of the guild
     * @param templates array of {@link ApplicationCommandTemplate}s
     * @return {@link Queueable} to overwrite and retrieve all commands for given guild
     * @see Queueable#queue()
     * @see Link#DELETE_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> bulkOverwriteGuildApplicationCommands(
            @NotNull String applicationId, @NotNull String guildId, @NotNull ApplicationCommandTemplate[] templates) {
        LinkQuery query = new LinkQuery(getLApi(), Link.BULK_OVERWRITE_GUILD_APPLICATION_COMMANDS,
                new LApiHttpBody(SOData.wrap(templates)),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId));
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
    }

    /**
     * <p>
     *     Takes a list of application commands, overwriting the existing command list for this application for the
     *     targeted guild. Returns 200 and a list of {@link ApplicationCommand application command objects}.
     * </p>
     * @param guildId id of the guild
     * @param templates array of {@link ApplicationCommandTemplate}s
     * @return {@link Queueable} to overwrite and retrieve all commands for given guild
     * @throws LApiIllegalStateException if {@link ConfigFlag#BASIC_CACHE} is disabled or {@link EventIdentifier#CACHE_READY} has not yet been triggered.
     * @see Queueable#queue()
     * @see Link#DELETE_GUILD_APPLICATION_COMMAND
     */
    default @NotNull Queueable<ArrayList<ApplicationCommand>> bulkOverwriteGuildApplicationCommands(
            @NotNull String guildId, @NotNull ApplicationCommandTemplate[] templates) {

        if(getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        LinkQuery query = new LinkQuery(getLApi(), Link.BULK_OVERWRITE_GUILD_APPLICATION_COMMANDS,
                new LApiHttpBody(SOData.wrap(templates)),
                new PlaceHolder(PlaceHolder.APPLICATION_ID, getLApi().getCache().getCurrentApplicationId()),
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId));
        return new ArrayRetriever<>(query, ApplicationCommand::fromData);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                 Application Command Permissions               *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

}
