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
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RefactorType<T> {

    public static RefactorType<String> RENAME = new RefactorType<>(
            new Functions<>() {
                @Override
                public List<ApplicationCommand> match(@NotNull MatchingInformation info, @NotNull BaseCommand command, @NotNull Refactor<String> refactor) {
                    return CommandUtils.match(info, command.getScope(), command.getId(), null, command.getType(), refactor.getOldValue());
                }

                @Override
                public void refactor(@NotNull MatchingInformation info, @NotNull List<ApplicationCommand> matches,
                                                   @NotNull BaseCommand command, @NotNull Refactor<String> refactor) {
                    CommandUtils.editCommand(info, matches, command);
                }
            });

    public static RefactorType<CommandScope> CHANGE_SCOPE = new RefactorType<>(
            new Functions<>() {
                @Override
                public List<ApplicationCommand> match(@NotNull MatchingInformation info, @NotNull BaseCommand command, @NotNull Refactor<CommandScope> refactor) {
                    return CommandUtils.match(info, refactor.getOldValue(), command.getId(), null, command.getType(), command.getName());
                }

                @Override
                public void refactor(@NotNull MatchingInformation info, @NotNull List<ApplicationCommand> matches,
                                                   @NotNull BaseCommand command, @NotNull Refactor<CommandScope> refactor) {
                    if(matches.isEmpty()) throw new LApiIllegalStateException("No commands to delete.");

                    //Delete the command in the old scope
                    refactor.getOldValue().delete(info, matches, command);

                    //If the template is null, set the template from the online command.
                    if(command.getTemplate() == null) {
                        command.setTemplate(ApplicationCommandTemplate.of(matches.get(0)));
                    }

                    //Create a new command in the new scope
                    CommandUtils.createCommand(info, command, null, false);
                }
            });

    private final Functions<T> functions;

    private RefactorType(Functions<T> functions) {
        this.functions = functions;
    }

    List<ApplicationCommand> match(@NotNull MatchingInformation info, @NotNull BaseCommand command, @NotNull Refactor<T> refactor) {
        return functions.match(info, command, refactor);
    }

    void refactor(@NotNull MatchingInformation info, @NotNull List<ApplicationCommand> matches,
                  @NotNull BaseCommand command, @NotNull Refactor<T> refactor) {
        functions.refactor(info, matches, command, refactor);
    }

    private static interface Functions<T> {
        List<ApplicationCommand> match(@NotNull MatchingInformation info, @NotNull BaseCommand command, @NotNull Refactor<T> refactor);

        void refactor(@NotNull MatchingInformation info, @NotNull List<ApplicationCommand> matches,
                                    @NotNull BaseCommand command, @NotNull Refactor<T> refactor);
    }

}
