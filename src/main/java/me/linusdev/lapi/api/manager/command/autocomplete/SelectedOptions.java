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

package me.linusdev.lapi.api.manager.command.autocomplete;

import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.objects.command.ApplicationCommandInteractionDataOption;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOptionType;
import me.linusdev.lapi.api.objects.interaction.InteractionData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SelectedOptions {


    private @Nullable ApplicationCommandInteractionDataOption subCommandGroup = null;
    private @Nullable ApplicationCommandInteractionDataOption subCommand = null;

    private @NotNull List<ApplicationCommandInteractionDataOption> options;

    public SelectedOptions(@Nullable InteractionData data) {
        options = new ArrayList<>();
        if(data == null) return;

        if(data.getOptions() != null && !data.getOptions().isEmpty()){
            ApplicationCommandInteractionDataOption option = data.getOptions().get(0);

            if(option.getType() == ApplicationCommandOptionType.SUB_COMMAND_GROUP) {
                subCommandGroup = option;
                readOptions(option);

            } else if(option.getType() == ApplicationCommandOptionType.SUB_COMMAND) {
                subCommand = option;
                readOptions(option);

            } else {
                options = data.getOptions();
            }
        }

    }

    private void readOptions(@NotNull ApplicationCommandInteractionDataOption option) {
        if (option.getOptions() != null && !option.getOptions().isEmpty()) {
            ApplicationCommandInteractionDataOption first = option.getOptions().get(0);
            if (first.getType() == ApplicationCommandOptionType.SUB_COMMAND) {
                subCommand = first;
                readOptions(first);

            } else {
                options = option.getOptions();

            }

        } else {
            options = new ArrayList<>();
        }
    }

    /**
     * Note: {@link ApplicationCommandOptionType#NUMBER NUMBER}
     * and {@link ApplicationCommandOptionType#INTEGER INTEGER} should always be cast to {@link Number}!
     * For more information see {@link ApplicationCommandInteractionDataOption#getValue() here}.
     * @param name name of the option to get the value from
     * @return value cast to {@link T} or {@code null} if no option with given name is included.
     * @param <T> the Type the value is of.
     */
    public @Nullable <T> T getValue(@NotNull String name) {
        try {
            for(ApplicationCommandInteractionDataOption option : options) {
                if(option.getName().equals(name)) return (T) option.getValue();
            }
        } catch (InvalidDataException e) {
            throw new LApiRuntimeException(e);
        }
        return null;
    }

    /**
     *
     * @return focused {@link ApplicationCommandInteractionDataOption} or {@code null} if none is focused, which should never happen.
     */
    public ApplicationCommandInteractionDataOption getFocused() {
        for(int i = options.size()-1; i >= 0; i--) {
            if(options.get(i).isFocused()) return options.get(i);
        }

        return null;
    }

    /**
     * Note: {@link ApplicationCommandOptionType#NUMBER NUMBER}.
     * and {@link ApplicationCommandOptionType#INTEGER INTEGER} should always be cast to {@link Number}!
     * For more information see {@link ApplicationCommandInteractionDataOption#getValue() here}.
     * @return value of focused option cast to {@link T}.
     * @param <T> class to cast the calue to.
     */
    public <T> T getFocusedValue() {
        try {
            return (T) getFocused().getValue();
        } catch (InvalidDataException e) {
            throw new LApiRuntimeException(e);
        }
    }

    /**
     *
     * @param name option name
     * @return {@link ApplicationCommandOption} with given name or {@code null} if no such option exists.
     */
    public ApplicationCommandInteractionDataOption getOption(@NotNull String name) {
        for(ApplicationCommandInteractionDataOption option : options) {
            if(option.getName().equals(name)) return option;
        }

        if(subCommandGroup != null && subCommandGroup.getName().equals(name)) return subCommandGroup;
        if(subCommand != null && subCommand.getName().equals(name)) return subCommandGroup;

        return null;
    }

    /**
     *
     * @return the sub command or {@code null} if no sub command is used
     */
    public ApplicationCommandInteractionDataOption getSubCommand() {
        return subCommand;
    }

    /**
     *
     * @return the sub command name or {@code null} if no sub command is used
     */
    public String getSubCommandName() {
        if(subCommand == null) return null;
        return subCommand.getName();
    }

    /**
     *
     * @return the sub command group or {@code null} if no sub command group is used
     */
    public ApplicationCommandInteractionDataOption getSubCommandGroup() {
        return subCommandGroup;
    }

    /**
     *
     * @return the sub command group name or {@code null} if no sub command group is used
     */
    public String getSubCommandGroupName() {
        if(subCommandGroup == null) return null;
        return subCommandGroup.getName();
    }
}
