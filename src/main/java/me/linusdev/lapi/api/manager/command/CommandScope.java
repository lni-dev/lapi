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
import me.linusdev.lapi.api.async.queue.QResponse;
import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum CommandScope {

    GLOBAL(new CommandEditor() {
        @Override
        public @Nullable Future<ApplicationCommand, QResponse> create(@NotNull MatchingInformation info, @NotNull BaseCommand command, @Nullable List<String> guildIds) {
            if (command.getTemplate() == null)
                throw new LApiIllegalStateException("command template cannot be null");

            return info.getLApi().getRequestFactory()
                    .createGlobalApplicationCommand(command.getTemplate()).queue((created, response, error) -> {
                        if (error != null) {
                            info.getLog().error(String.format("Could not create command %s.", command.getClass().getCanonicalName()));
                            command.onError(error.asThrowable());

                        } else {
                            command.linkWith(created);
                            info.getCommandLinks().put(created.getId(), command);

                        }
                    });
        }

        @Override
        public @Nullable Future<LApiHttpResponse, QResponse> delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches, @NotNull BaseCommand command) {
            if(matches == null || matches.isEmpty()) return null;
            if(matches.size() != 1)
                throw new LApiIllegalStateException("multiple matches for a global command.");

            return info.getLApi().getRequestFactory().deleteGlobalApplicationCommand(matches.get(0).getId()).queue(
                    (lApiHttpResponse, response, error) -> {
                        if (error != null) {
                            info.getLog().error(String.format("Could not delete command %s.", command.getClass().getCanonicalName()));
                            command.onError(error.asThrowable());
                        }
                    });
        }
    }),

    GUILD(new CommandEditor() {
        @Override
        public @Nullable Future<ApplicationCommand, QResponse> create(@NotNull MatchingInformation info, @NotNull BaseCommand command, @Nullable List<String> guildIds) {
            if (command.getTemplate() == null)
                throw new LApiIllegalStateException("command template cannot be null");

            if (guildIds == null || guildIds.isEmpty()) return null;

            for (String guildId : guildIds) {
                return info.getLApi().getRequestFactory()
                        .createGuildApplicationCommand(guildId, command.getTemplate()).queue((created, response, error) -> {
                            if (error != null) {
                                info.getLog().error(String.format("Could not create command %s.", command.getClass().getCanonicalName()));
                                command.onError(error.asThrowable());
                            } else {
                                command.linkWith(created);
                                info.getCommandLinks().put(created.getId(), command);
                            }
                        });
            }
            return null;
        }

        @Override
        public @Nullable Future<LApiHttpResponse, QResponse> delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches, @NotNull BaseCommand command) {

            if(matches == null || matches.isEmpty()) return null;

            for(ApplicationCommand match : matches) {

                if(match.getGuildId() == null){
                    info.getLog().error("Match is missing guild id.");
                    throw new LApiIllegalStateException("match missing guild id.");
                }

                return info.getLApi().getRequestFactory().deleteGuildApplicationCommand(match.getId(), match.getGuildId()).queue(
                        (lApiHttpResponse, response, error) -> {
                            if (error != null) {
                                info.getLog().error(String.format("Could not delete command %s.", command.getClass().getCanonicalName()));
                                command.onError(error.asThrowable());
                            }
                        });
            }
            return null;
        }
    }),

    ;

    private final CommandEditor editor;

    CommandScope(CommandEditor editor) {
        this.editor = editor;
    }

    @ApiStatus.Internal
    public @Nullable Future<ApplicationCommand, QResponse> create(@NotNull MatchingInformation info, @NotNull BaseCommand command,
                                                        @Nullable List<String> guildIds) {
        return editor.create(info, command, guildIds);
    }

    @ApiStatus.Internal
    public @Nullable Future<LApiHttpResponse, QResponse> delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches,
                                                      @NotNull BaseCommand command) {
        return editor.delete(info, matches, command);
    }


    @ApiStatus.Internal
    private static interface CommandEditor {
        @Nullable Future<ApplicationCommand, QResponse> create(@NotNull MatchingInformation info, @NotNull BaseCommand command, @Nullable List<String> guildIds);

        @Nullable Future<LApiHttpResponse, QResponse> delete(@NotNull MatchingInformation info, @Nullable List<ApplicationCommand> matches, @NotNull BaseCommand command);
    }

}
