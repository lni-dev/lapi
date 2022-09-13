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

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum CommandScope {

    GLOBAL(new CommandEditor() {
        @Override
        public void create(@NotNull MatchingInformation info, @NotNull BaseCommand command, @Nullable List<String> guildIds) {
            if (command.getTemplate() == null)
                throw new LApiIllegalStateException("command template cannot be null");

            info.getLApi().getRequestFactory()
                    .createGlobalApplicationCommand(command.getTemplate()).queue((created, error) -> {
                        if (error != null) {
                            info.getLog().error(String.format("Could not create command %s.", command.getClass().getCanonicalName()));
                            command.onError(error.getThrowable());

                        } else {
                            command.linkWith(created);
                            info.getConnectedCommands().put(created.getId(), command);

                        }
                    });
        }

        @Override
        public void delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches, @NotNull BaseCommand command) {
            if(matches == null || matches.isEmpty()) return;
            if(matches.size() != 1)
                throw new LApiIllegalStateException("multiple matches for a global command.");

            info.getLApi().getRequestFactory().deleteGlobalApplicationCommand(matches.get(0).getId()).queue(
                    (lApiHttpResponse, error) -> {
                        if (error != null) {
                            info.getLog().error(String.format("Could not delete command %s.", command.getClass().getCanonicalName()));
                            command.onError(error.getThrowable());
                        }
                    });

            //TODO: maybe remove?
            command.linkWith(matches.get(0));
        }
    }),

    GUILD(new CommandEditor() {
        @Override
        public void create(@NotNull MatchingInformation info, @NotNull BaseCommand command, @Nullable List<String> guildIds) {
            if (command.getTemplate() == null)
                throw new LApiIllegalStateException("command template cannot be null");

            if (guildIds == null || guildIds.isEmpty()) return;

            for (String guildId : guildIds) {
                info.getLApi().getRequestFactory()
                        .createGuildApplicationCommand(guildId, command.getTemplate()).queue((created, error) -> {
                            if (error != null) {
                                info.getLog().error(String.format("Could not create command %s.", command.getClass().getCanonicalName()));
                                command.onError(error.getThrowable());
                            } else {
                                command.linkWith(created);
                                info.getConnectedCommands().put(created.getId(), command);
                            }
                        });
            }
        }

        @Override
        public void delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches, @NotNull BaseCommand command) {

            if(matches == null || matches.isEmpty()) return;

            for(ApplicationCommand match : matches) {

                if(match.getGuildId() == null){
                    info.getLog().error("Match is missing guild id.");
                    throw new LApiIllegalStateException("match missing guild id.");
                }

                info.getLApi().getRequestFactory().deleteGuildApplicationCommand(match.getId(), match.getGuildId()).queue(
                        (lApiHttpResponse, error) -> {
                            if (error != null) {
                                info.getLog().error(String.format("Could not delete command %s.", command.getClass().getCanonicalName()));
                                command.onError(error.getThrowable());
                            }
                        });
                //TODO: maybe remove?
                command.linkWith(match);
            }
        }
    }),

    ;

    private final CommandEditor editor;

    CommandScope(CommandEditor editor) {
        this.editor = editor;
    }

    @ApiStatus.Internal
    public void create(@NotNull MatchingInformation info, @NotNull BaseCommand command,
                       @Nullable List<String> guildIds) {
        editor.create(info, command, guildIds);
    }

    @ApiStatus.Internal
    public void delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches,
                       @NotNull BaseCommand command) {
        editor.delete(info, matches, command);
    }


    @ApiStatus.Internal
    private static interface CommandEditor {
        void create(@NotNull MatchingInformation info, @NotNull BaseCommand command, @Nullable List<String> guildIds);

        void delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches, @NotNull BaseCommand command);
    }

}
