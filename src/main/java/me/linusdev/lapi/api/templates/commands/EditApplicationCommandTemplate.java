/*
 * Copyright  2022 Linus Andera
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

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.command.LocalizationDictionary;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.permission.Permissions;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.Nullable;

import static me.linusdev.lapi.api.objects.command.ApplicationCommand.*;

/**
 * Almost the same as {@link ApplicationCommandTemplate}, but this is a template for editing an
 * existing application command. That is why {@link #name} and {@link #description} can be {@code null}
 * and {@link ApplicationCommandTemplate#type} is missing. {@link #getData()} only adds not {@code null} fields
 */
public class EditApplicationCommandTemplate implements Template {

    private final @Nullable String name;
    private final @Nullable LocalizationDictionary nameLocalisations;
    private final @Nullable String description;
    private final @Nullable LocalizationDictionary descriptionLocalisations;
    private final @Nullable ApplicationCommandOption[] options;
    private final @Nullable Permissions defaultMemberPermissions;
    private final @Nullable Boolean dmPermissions;

    public EditApplicationCommandTemplate(@Nullable String name, @Nullable LocalizationDictionary nameLocalisations,
                                      @Nullable String description, @Nullable LocalizationDictionary descriptionLocalisations,
                                      @Nullable ApplicationCommandOption[] options, @Nullable Permissions defaultMemberPermissions, @Nullable Boolean dmPermissions) {
        this.name = name;
        this.nameLocalisations = nameLocalisations;
        this.description = description;
        this.descriptionLocalisations = descriptionLocalisations;
        this.options = options;
        this.defaultMemberPermissions = defaultMemberPermissions;
        this.dmPermissions = dmPermissions;
    }


    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(9);

        data.addIfNotNull(NAME_KEY, name);
        data.addIfNotNull(NAME_LOCALIZATIONS_KEY, nameLocalisations);
        data.addIfNotNull(DESCRIPTION_KEY, description);
        data.addIfNotNull(DESCRIPTION_LOCALIZATIONS_KEY, descriptionLocalisations);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.addIfNotNull(DEFAULT_MEMBER_PERMISSIONS_KEY, defaultMemberPermissions);
        data.addIfNotNull(DM_PERMISSIONS_KEY, dmPermissions);

        return data;
    }
}
