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

package me.linusdev.lapi.api.templates.commands;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.command.ApplicationCommandType;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.linusdev.lapi.api.objects.command.ApplicationCommand.*;

/**
 * {@link Template} to create an {@link ApplicationCommand}.<br>
 * It is recommended to use {@link ApplicationCommandBuilder} instead
 * @see RequestFactory#createGlobalApplicationCommand(String, ApplicationCommandTemplate)
 * @see ApplicationCommandBuilder
 */
public class ApplicationCommandTemplate implements Template, Datable {

    private final @NotNull String name;
    private final @Nullable LocalizationDictionary nameLocalisations;
    private final @NotNull String description;
    private final @Nullable LocalizationDictionary descriptionLocalisations;
    private final @Nullable ApplicationCommandOption[] options;
    private final @Nullable Permissions defaultMemberPermissions;
    private final @Nullable Boolean dmPermissions;
    private final @Nullable ApplicationCommandType type;

    public ApplicationCommandTemplate(@NotNull String name, @Nullable LocalizationDictionary nameLocalisations,
                                      @NotNull String description, @Nullable LocalizationDictionary descriptionLocalisations,
                                      @Nullable ApplicationCommandOption[] options, @Nullable Permissions defaultMemberPermissions, @Nullable Boolean dmPermissions,
                                      @Nullable ApplicationCommandType type) {
        this.name = name;
        this.nameLocalisations = nameLocalisations;
        this.description = description;
        this.descriptionLocalisations = descriptionLocalisations;
        this.options = options;
        this.defaultMemberPermissions = defaultMemberPermissions;
        this.dmPermissions = dmPermissions;
        this.type = type;
    }


    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(9);

        data.add(NAME_KEY, name);
        data.addIfNotNull(NAME_LOCALIZATIONS_KEY, nameLocalisations);
        data.add(DESCRIPTION_KEY, description);
        data.addIfNotNull(DESCRIPTION_LOCALIZATIONS_KEY, descriptionLocalisations);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.addIfNotNull(DEFAULT_MEMBER_PERMISSIONS_KEY, defaultMemberPermissions);
        data.addIfNotNull(DM_PERMISSIONS_KEY, dmPermissions);
        data.addIfNotNull(TYPE_KEY, type);

        return data;
    }
}
