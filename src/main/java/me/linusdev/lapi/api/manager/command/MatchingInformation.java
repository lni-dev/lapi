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

import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.log.LogInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MatchingInformation implements HasLApi {

    private final @NotNull LApi lApi;
    private final @NotNull LogInstance log;

    private final @NotNull List<BaseCommand> localCommands;

    private final @NotNull List<ApplicationCommand> globalCommandsOnDiscord;
    private final @NotNull Map<String, List<ApplicationCommand>> guildCommandsOnDiscord;

    private final @NotNull Map<String, BaseCommand> connectedCommands;

    public MatchingInformation(@NotNull LApi lApi, @NotNull LogInstance log, @NotNull List<BaseCommand> localCommands,
                               @NotNull List<ApplicationCommand> globalCommandsOnDiscord,
                               @NotNull Map<String, List<ApplicationCommand>> guildCommandsOnDiscord, @NotNull Map<String, BaseCommand> connectedCommands) {
        this.lApi = lApi;
        this.log = log;
        this.localCommands = localCommands;
        this.globalCommandsOnDiscord = globalCommandsOnDiscord;
        this.guildCommandsOnDiscord = guildCommandsOnDiscord;
        this.connectedCommands = connectedCommands;
    }

    public @NotNull List<BaseCommand> getLocalCommands() {
        return localCommands;
    }

    public @NotNull List<ApplicationCommand> getGlobalCommandsOnDiscord() {
        return globalCommandsOnDiscord;
    }

    public @NotNull Map<String, List<ApplicationCommand>> getGuildCommandsOnDiscord() {
        return guildCommandsOnDiscord;
    }

    public @NotNull Map<String, BaseCommand> getConnectedCommands() {
        return connectedCommands;
    }

    public @NotNull LogInstance getLog() {
        return log;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
