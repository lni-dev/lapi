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

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOptionChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-autocomplete" target="_top">Autocomplete</a>
 */
public class Autocomplete implements Datable {

    public static final String CHOICES_KEY = "choices";

    private final @NotNull Collection<ApplicationCommandOptionChoice> choices;

    public Autocomplete(@NotNull Collection<ApplicationCommandOptionChoice> choices) {
        this.choices = choices;
    }

    public @NotNull Collection<ApplicationCommandOptionChoice> getChoices() {
        return choices;
    }

    @Override
    public Data getData() {
        Data data = new Data(1);
        data.add(CHOICES_KEY, choices);
        return data;
    }
}
