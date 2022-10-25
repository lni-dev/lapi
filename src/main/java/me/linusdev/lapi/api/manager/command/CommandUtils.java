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

package me.linusdev.lapi.api.manager.command;

import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.Task;
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.command.ApplicationCommandType;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@ApiStatus.Internal
public class CommandUtils {

    static void matchCommand(@NotNull MatchingInformation info,
                                    @NotNull BaseCommand command) {
        try {
            info.getLog().debug("Matching command '" + command.getClass().getCanonicalName() + "'.");
            matchCommand0(info, command, false);

        } catch (Throwable t) {
            info.getLog().error("Exception while trying to match command");
            info.getLog().error(t);
            command.onError(t);

        }
    }

    /**
     *
     * @param info {@link MatchingInformation}
     * @param command {@link BaseCommand} to match
     * @param ignoreRefactor {@code false} will match with the old value. {@code true} will match with the new value.
     */
    private static void matchCommand0(@NotNull MatchingInformation info, @NotNull BaseCommand command,
                                           boolean ignoreRefactor) {
        List<ApplicationCommand> matches = null;

        Refactor<?> refactor = command.refactor();
        if (!ignoreRefactor && refactor != null) {
            info.getLog().debug("Command requires refactoring: " + refactor);
            matches = refactor.match(info);

            if (matches == null) {
                info.getLog().debug("No match found for old command. Trying to match again with 'ignoreRefactor' set to true");
                matchCommand0(info, command, true);
                return;
            }

        } else {
            info.getLog().debug("Normal Matching...");
            matches = match(info, command.getScope(), command.getId(), command.getTemplate(), command.getType0(), command.getName0());
        }


        if (matches == null) {

            if(command.getScope() == CommandScope.GUILD) {
                //Guild Commands have to be enabled per guild. there is no need to call createCommand.
                info.getLog().debug("No matches found for a GUILD command.");
                return;
            }

            //This is a new command
            info.getLog().debug("No matches found. Creating new command...");
            createCommand(info, command, null, true);

        } else {
            info.getLog().debug(String.format("%d matche%s found.", matches.size(), matches.size() == 1 ? "" : "s"));
            if (command.delete()) {
                info.getLog().debug("Command requested deletion. deleting Command...");
                command.getScope().delete(info, matches, command);

            } else if (!ignoreRefactor && command.refactor() != null) {
                info.getLog().debug("Command requested refactoring. refactoring Command...");
                command.refactor().refactor(info, matches);

            } else {
                info.getLog().debug("Linking command.");
                for(ApplicationCommand match : matches) {

                    if(checkIfTemplateChanged(command, match)) {
                        editCommand(info, matches, command);
                        break;
                    } else {
                        info.getCommandLinks().put(match.getId(), command);
                        command.linkWith(match);

                    }


                }
                info.getLog().debug("Command linked.");

            }

        }
    }

    /**
     * Checks if the template of the {@link BaseCommand} is different of the {@link ApplicationCommand}.<br>
     * Scope, type and name will not be checked.
     * @param command {@link BaseCommand}
     * @param match {@link ApplicationCommand}
     * @return {@code true} if the template has changed.
     */
    static boolean checkIfTemplateChanged(@NotNull BaseCommand command, @NotNull ApplicationCommand match) {

        ApplicationCommandTemplate template = command.create();
        if(template == null) return false;

        //scope, type and name and  will not be checked

        if(!ApplicationCommandOption.optionsEquals(template.getOptions(), match.getOptions())) return false;

        return !Objects.equals(template.getDescription(), match.getDescription()) &&
                !Objects.equals(template.getDefaultMemberPermissions(), match.getDefaultMemberPermissions()) &&
                !LocalizationDictionary.equalsContent(template.getDescriptionLocalisations(), match.getDescriptionLocalizations()) &&
                !LocalizationDictionary.equalsContent(template.getNameLocalisations(), match.getNameLocalizations()) &&
                !Objects.equals(template.getDmPermissions(), match.getDmPermission());

    }



    /**
     * Will edit the given matches and add the edited {@link ApplicationCommand}'s {@link ApplicationCommand#getId() ids}
     * to {@link MatchingInformation#getCommandLinks()}.
     *
     * @param info    {@link MatchingInformation}
     * @param matches {@link ApplicationCommand}s to edit
     * @param command {@link BaseCommand} to get the new {@link ApplicationCommandTemplate}
     * @return {@link Future} of the editing {@link Task} or {@code null} if no command was edited.
     */
    static @Nullable Future<ApplicationCommand, QResponse> editCommand(@NotNull MatchingInformation info, @NotNull List<ApplicationCommand> matches, @NotNull BaseCommand command) {

        if(command.getTemplate() == null) {
            info.getLog().error("Command is missing a template. Cannot edit.");
            command.onError(new LApiIllegalStateException("Command missing template. Cannot edit."));
        }

        if(command.getScope() == CommandScope.GLOBAL) {
            if(matches.isEmpty()) return null;
            if(matches.size() != 1)
                throw new LApiIllegalStateException("multiple matches for a global command.");

            return info.getLApi().getRequestFactory().editGlobalApplicationCommand(matches.get(0).getId(), command.getTemplate().toEditTemplate()).queue(
                    (returnCommand, response, error) -> {
                        if (error != null) {
                            info.getLog().error(String.format("Could not edit command %s.", command.getClass().getCanonicalName()));
                            command.onError(error.asThrowable());

                        } else {
                            command.linkWith(returnCommand);
                            info.getCommandLinks().put(returnCommand.getId(), command);

                        }
                    });

        } else if (command.getScope() == CommandScope.GUILD) {
            if(matches.isEmpty()) return null;

            for(ApplicationCommand match : matches) {

                if(match.getGuildId() == null){
                    info.getLog().error("Match is missing guild id. Cannot edit.");
                    throw new LApiIllegalStateException("Match missing guild id. Cannot edit.");
                }

                return info.getLApi().getRequestFactory().editGuildApplicationCommand(match.getGuildId(), match.getId(), command.getTemplate().toEditTemplate()).queue(
                        (returnCommand, response, error) -> {
                            if (error != null) {
                                info.getLog().error(String.format("Could not edit command %s.", command.getClass().getCanonicalName()));
                                command.onError(error.asThrowable());

                            } else {
                                command.linkWith(returnCommand);
                                info.getCommandLinks().put(returnCommand.getId(), command);

                            }
                        });
            }

        } else {
            throw new LApiIllegalStateException("Unknown command scope.");

        }
        return null;
    }

    /**
     * @param info          {@link MatchingInformation}
     * @param command       {@link BaseCommand} to create
     * @param checkRefactor whether to check if this could command should be refactored, not created.
     *                      Will call {@link BaseCommand#onError(Throwable)} if it should be refactored and not created.
     * @return {@link Future} of the editing {@link Task} or {@code null} if no command was created.
     */
    static @Nullable Future<ApplicationCommand, QResponse> createCommand(@NotNull MatchingInformation info, @NotNull BaseCommand command,
                                                               @Nullable List<String> guildIds, boolean checkRefactor) {
        //check if this command should be deleted. If so, we do not need to do anything
        if (command.delete()) return null;

        if (checkRefactor && command.refactor() != null) {
            //This is bad: we cant find a match for the old or the new command...
            info.getLog().error(String.format("command '%s' cannot be found on discord." +
                    " Cannot refactor command.", command.getClass().getCanonicalName()));
            command.onError(new LApiIllegalStateException("Your command cannot be found on discord" +
                    " and that's why it cannot be refactored."));
            return null;
        }

        if (command.getTemplate() == null) {
            info.getLog().error(String.format("command '%s' cannot be found on discord and template returns null." +
                    " Cannot create command.", command.getClass().getCanonicalName()));
            command.onError(new LApiIllegalStateException("Your command cannot be found on discord" +
                    " and that's why it needs a template to be created."));
            return null;
        }

        return command.getScope().create(info, command, guildIds);
    }

    /**
     * Checks if this command meets the {@link BaseCommand requirements}.
     *
     * @param command {@link BaseCommand} to check
     * @param log     {@link LogInstance} to log to if check fails
     * @return {@code false} if check fails, {@code true} otherwise.
     */
    static boolean checkCommand(@NotNull BaseCommand command, @NotNull LogInstance log) {
        if (command.getId() == null
                && command.getTemplate() == null
                && (command.getType0() == null || command.getName0() == null)) {
            //This command cannot be matched
            log.error(String.format("command '%s' does not meet the requirements.", command.getClass().getCanonicalName()));
            command.onError(new LApiIllegalStateException("Your command does not meet the requirements."));
            return false;
        }

        return true;
    }

    /**
     * Finds the first {@link ApplicationCommand} that matches given data and then removes it from the given list and
     * returns it.
     * <br><br>
     * Matching is tried in the following order:
     * <ol>
     *     <li>
     *         match by commandId. If matched by command, the match is only returned if given type and name also
     *         match the command's type and name or are {@code null}.
     *     </li>
     *     <li>
     *         match by template
     *     </li>
     *     <li>
     *         match by given type and name
     *     </li>
     * </ol>
     * That also means, that at least one of the above must be not {@code null}.
     *
     * @param info         list of {@link MatchingInformation} to match with
     * @param commandScope {@link CommandScope} to match in
     * @param commandId    id of the command
     * @param template     template of the command
     * @param type         type of the command
     * @param name         name of the command
     * @return List of {@link ApplicationCommand} matches or {@code null} if none matched.
     */
    static @Nullable List<ApplicationCommand> match(@NotNull MatchingInformation info,
                                                           @NotNull CommandScope commandScope,
                                                           @Nullable String commandId,
                                                           @Nullable ApplicationCommandTemplate template,
                                                           @Nullable ApplicationCommandType type, @Nullable String name) {
        if (commandScope == CommandScope.GLOBAL) {
            info.getLog().debug("Matching in command Scope: " + CommandScope.GLOBAL);
            if (commandId != null) {
                //match by id
                info.getLog().debug("Matching by command id=" + commandId);
                ApplicationCommand match = matchInList(info.getGlobalCommandsOnDiscord(), applicationCommand ->
                        applicationCommand.getId().equals(commandId));

                if (match == null) {
                    info.getLog().debug("No matches found...");
                    return null;

                }
                if (type != null && match.getType() != type) {
                    info.getLog().debug("match found but type is not correct.");
                    return null;

                }
                if (name != null && !match.getName().equals(name)){
                    info.getLog().debug("match found but name is not correct.");
                    return null;

                }
                info.getLog().debug("1 match found.");
                return List.of(match);

            } else if (template != null) {
                //match by template
                info.getLog().debug("Matching by template: type=" + template.getType() + ", name=" + template.getName());
                ApplicationCommand match = matchInList(info.getGlobalCommandsOnDiscord(), ac ->
                        ac.getType() == template.getType() && ac.getName().equals(template.getName())
                );

                if (match == null) {
                    info.getLog().debug("No matches found...");
                    return null;
                }
                info.getLog().debug("1 match found.");
                return List.of(match);

            } else if (type != null && name != null) {
                //match by type and name
                info.getLog().debug("Matching by type=" + type + " and name=" + name);
                ApplicationCommand match = matchInList(info.getGlobalCommandsOnDiscord(), applicationCommand ->
                        applicationCommand.getType() == type && applicationCommand.getName().equals(name));

                if (match == null) {
                    info.getLog().debug("No matches found...");
                    return null;
                }
                info.getLog().debug("1 match found.");
                return List.of(match);

            } else {
                //should be checked before calling this method!
                throw new LApiIllegalStateException("cannot match");
            }

        } else if (commandScope == CommandScope.GUILD) {
            info.getLog().debug("Matching in command Scope: " + CommandScope.GUILD);
            ArrayList<ApplicationCommand> matches = new ArrayList<>();

            //cannot match by command id! id changes for every guild...

            if (template != null) {
                //match by template
                info.getLog().debug("Matching by template: type=" + template.getType() + ", name=" + template.getName());
                ApplicationCommand match = matchInList(info.getGuildCommandsOnDiscord(), ac ->
                        ac.getType() == template.getType() && ac.getName().equals(template.getName()));
                if(match != null) matches.add(match);

                info.getLog().debug(matches.size() + " matches found.");
                return matches.isEmpty() ? null : matches;

            } else if (type != null && name != null) {
                //match by scope, type and name
                info.getLog().debug("Matching by type=" + type + " and name=" + name);
                ApplicationCommand match = matchInList(info.getGuildCommandsOnDiscord(), applicationCommand ->
                            applicationCommand.getType() == type && applicationCommand.getName().equals(name));
                if (match != null) matches.add(match);

                info.getLog().debug(matches.size() + " matches found.");
                return matches.isEmpty() ? null : matches;

            } else {
                //should be checked before calling this method!
                throw new LApiIllegalStateException("cannot match");
            }
        }

        throw new LApiIllegalStateException("unknown command scope.");
    }

    /**
     * removes and returns the first entry from the list, for which given match function returns {@code true}, if no such element exists, {@code null} is returned.
     *
     * @param list          list to find the match in
     * @param matchFunction function used to match
     * @return {@link ApplicationCommand} match or {@code null} if none matched.
     */
    private static @Nullable ApplicationCommand matchInList(@Nullable List<ApplicationCommand> list, @NotNull Predicate<ApplicationCommand> matchFunction) {
        if(list == null) return null;
        Iterator<ApplicationCommand> iterator = list.iterator();
        if (list.isEmpty()) return null;

        ApplicationCommand command;
        while (iterator.hasNext()) {
            command = iterator.next();
            if (matchFunction.test(command)) {
                iterator.remove();
                return command;
            }
        }

        return null;
    }




    static @NotNull List<String> readServices(@NotNull LApiImpl lApi) {
        ArrayList<String> list = new ArrayList<>();

        BufferedReader reader = null;
        try{
            InputStream inputStream = lApi.getCallerClass().getClassLoader().getResourceAsStream("META-INF/services/me.linusdev.lapi.api.manager.command.BaseCommand");
            if(inputStream == null) return list;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line =reader.readLine()) != null){
                list.add(line);
            }
        }catch (IOException exception) {
            Logger.getLogger(CommandUtils.class).error(exception);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }

        return list;
    }
}
