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

package me.linusdev.lapi.api.objects.interaction.response.data;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.message.component.Component;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-modal" target="_top">Modal</a>
 */
public class Modal implements Datable, Template {

    public static final String CUSTOM_ID_KEY = "custom_id";
    public static final String TITLE_KEY = "title";
    public static final String COMPONENTS_KEY = "components";

    private final @NotNull String customId;
    private final @NotNull String title;
    private final @NotNull Collection<Component> components;

    /**
     *
     * @param customId a developer-defined identifier for the component, max 100 characters
     * @param title the title of the popup modal, max 45 characters
     * @param components between 1 and 5 (inclusive) components that make up the modal
     */
    public Modal(@NotNull String customId, @NotNull String title, @NotNull Collection<Component> components) {
        this.customId = customId;
        this.title = title;
        this.components = components;
    }

    /**
     * a developer-defined identifier for the component, max 100 characters
     */
    public @NotNull String getCustomId() {
        return customId;
    }

    /**
     * the title of the popup modal, max 45 characters
     */
    public @NotNull String getTitle() {
        return title;
    }

    /**
     * between 1 and 5 (inclusive) components that make up the modal
     */
    public @NotNull Collection<Component> getComponents() {
        return components;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);
        data.add(CUSTOM_ID_KEY, customId);
        data.add(TITLE_KEY, title);
        data.add(COMPONENTS_KEY, components);
        return data;
    }
}
