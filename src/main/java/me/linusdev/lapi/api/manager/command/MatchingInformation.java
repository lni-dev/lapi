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
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.log.LogInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MatchingInformation implements HasLApi {

    private final @NotNull LApi lApi;
    private final @NotNull LogInstance log;

    private final @NotNull List<BaseCommand> localCommands;

    private final @Nullable List<ApplicationCommand> globalCommandsOnDiscord;
    private final @Nullable List<ApplicationCommand> guildCommandsOnDiscord;

    private final @NotNull Map<String, BaseCommand> commandLinks;

    private final @NotNull ConcurrentLinkedDeque<Future<?, ?>> responses;

    public MatchingInformation(@NotNull LApi lApi, @NotNull LogInstance log, @NotNull List<BaseCommand> localCommands,
                               @Nullable List<ApplicationCommand> globalCommandsOnDiscord,
                               @Nullable List<ApplicationCommand> guildCommandsOnDiscord, @NotNull Map<String, BaseCommand> commandLinks, @NotNull ConcurrentLinkedDeque<Future<?, ?>> responses) {
        this.lApi = lApi;
        this.log = log;
        this.localCommands = localCommands;
        this.globalCommandsOnDiscord = globalCommandsOnDiscord;
        this.guildCommandsOnDiscord = guildCommandsOnDiscord;
        this.commandLinks = commandLinks;
        this.responses = responses;
    }

    public @NotNull List<BaseCommand> getLocalCommands() {
        return localCommands;
    }

    public @Nullable List<ApplicationCommand> getGlobalCommandsOnDiscord() {
        return globalCommandsOnDiscord;
    }

    public @Nullable List<ApplicationCommand> getGuildCommandsOnDiscord() {
        return guildCommandsOnDiscord;
    }

    public @NotNull Map<String, BaseCommand> getCommandLinks() {
        return commandLinks;
    }

    public @NotNull LogInstance getLog() {
        return log;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
