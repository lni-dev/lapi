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
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.command.ApplicationCommandType;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandBuilder;
import me.linusdev.lapi.api.templates.commands.ApplicationCommandTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
@Command
public abstract class BaseCommand implements HasLApi {

    protected final @NotNull LApi lApi;
    private final @NotNull AtomicBoolean connected = new AtomicBoolean(false);

    private @Nullable ApplicationCommandTemplate template;
    private @Nullable ApplicationCommand connectedCommand;

    public BaseCommand(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    @ApiStatus.OverrideOnly
    public @Nullable String getId() {
        return null;
    }

    @ApiStatus.OverrideOnly
    public @Nullable String getName() {

        if(getTemplate() == null) {
            if(isConnected() && connectedCommand != null)
                return connectedCommand.getName();

            return null;
        }

        return getTemplate().getName();
    }

    @ApiStatus.OverrideOnly
    public @Nullable ApplicationCommandType getType() {
        if(getTemplate() == null) {
            if(isConnected() && connectedCommand != null)
                return connectedCommand.getType();

            return null;
        }
        ApplicationCommandType type = getTemplate().getType();
        if(type == null) {
            //while creating a command the type is actually optional.
            //The default type is 1 (CHAT_INPUT)
            type = ApplicationCommandType.CHAT_INPUT;
        }
        return type;
    }

    @ApiStatus.OverrideOnly
    public abstract @NotNull CommandScope getScope();

    @ApiStatus.Internal
    public final @Nullable ApplicationCommandTemplate getTemplate() {
        if(template == null) return create();
        return template;
    }

    @ApiStatus.OverrideOnly
    protected abstract @Nullable ApplicationCommandTemplate create();

    @ApiStatus.OverrideOnly
    public abstract void onInteract(InteractionCreateEvent event);

    /**
     * Whether this command represents a discord command
     * @return {@code true} if this command represents a discord command
     */
    public final boolean isConnected() {
        return connected.get();
    }

    @ApiStatus.Internal
    public final void setConnected(@NotNull ApplicationCommand connectedCommand) {
        this.connected.set(true);
        this.connectedCommand = connectedCommand;
    }

    @Override
    public final @NotNull LApi getLApi() {
        return lApi;
    }
}
