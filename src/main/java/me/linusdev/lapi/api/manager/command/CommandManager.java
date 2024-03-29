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
import me.linusdev.lapi.api.async.Nothing;
import me.linusdev.lapi.api.interfaces.HasLApi;
import org.jetbrains.annotations.NotNull;

public interface CommandManager extends HasLApi {

    //TODO: add functions to get BaseCommands by id or iterate over them


    @NotNull Future<BaseCommand, CommandManager> getCommandByClass(@NotNull Class<? extends BaseCommand> clazz);

    /**
     * <p>
     * If the {@link BaseCommand} is already enabled for any of the guilds in the list, the method will return without
     * doing anything.
     * </p>
     *
     * @param clazz   {@link Class} of your {@link BaseCommand}
     * @param guildId the ids of all guild to enable this command for
     * @return
     */
    @NotNull Future<Nothing, CommandManager> enabledCommandForGuild(@NotNull Class<? extends BaseCommand> clazz, @NotNull String guildId);
}
