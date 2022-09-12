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

import java.util.List;

public class Refactor<T> {

    private final @NotNull BaseCommand command;

    private final @NotNull RefactorType<T> type;
    private final @NotNull T oldValue;

    /**
     * <p>
     *     Signals the {@link CommandManager}, that this command requires refactoring,
     * </p>
     *
     * @param command the {@link BaseCommand} this {@link Refactor} is for.
     * @param type the {@link RefactorType}. What should be refactored.
     * @param oldValue the old value before the refactor. The new value should be returned by given {@link BaseCommand}
     */
    public Refactor(@NotNull BaseCommand command, @NotNull RefactorType<T> type, @NotNull T oldValue) {
        if(command == null || type == null || oldValue == null) throw new LApiIllegalStateException("type and oldValue may not be null.");
        this.command = command;
        this.type = type;
        this.oldValue = oldValue;
    }

    @ApiStatus.Internal
    List<ApplicationCommand> match(@NotNull MatchingInformation info) {
        return type.match(info, command, this);
    }

    @ApiStatus.Internal
    void refactor(@NotNull MatchingInformation info, @NotNull List<ApplicationCommand> matches) {
        type.refactor(info, matches, command, this);
    }

    public @NotNull RefactorType<T> getType() {
        return type;
    }

    public @NotNull T getOldValue() {
        return oldValue;
    }

    @Override
    public String toString() {
        return "Refactor " + type + ". Old value: " + oldValue;
    }
}
