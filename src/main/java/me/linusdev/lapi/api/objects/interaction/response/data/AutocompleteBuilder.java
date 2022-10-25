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

package me.linusdev.lapi.api.objects.interaction.response.data;

import me.linusdev.lapi.api.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOptionChoice;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AutocompleteBuilder {
    private @NotNull List<ApplicationCommandOptionChoice> choices;

    public AutocompleteBuilder() {
        this.choices = new ArrayList<>();
    }

    /**
     * Sets choices to given list. replaces old {@link #choices}.
     * @param choices {@link ArrayList} of {@link ApplicationCommandOptionChoice}
     * @return this
     */
    public AutocompleteBuilder setChoices(@NotNull ArrayList<ApplicationCommandOptionChoice> choices) {
        this.choices = choices;
        return this;
    }

    /**
     * Add a choice.
     * @param choice {@link ApplicationCommandOptionChoice} to add
     * @return this
     */
    public AutocompleteBuilder addChoice(@NotNull ApplicationCommandOptionChoice choice) {
        this.choices.add(choice);
        return this;
    }

    /**
     * Add a choice.
     * @param name choice name
     * @param localizations choice name localizations
     * @param value {@link String}, Integer ({@link Integer}, {@link Long}, ...), Number ({@link Double} or {@link Float})
     *                           depending on the {@link ApplicationCommandOption#getType() option type}.
     * @return this
     */
    public AutocompleteBuilder addChoice(@NotNull String name, @Nullable LocalizationDictionary localizations, @NotNull Object value) {
        this.choices.add(new ApplicationCommandOptionChoice(name, null, localizations, value));
        return this;
    }

    /**
     * Add a choice.
     * @param name choice name
     * @param value {@link String}, Integer ({@link Integer}, {@link Long}, ...), Number ({@link Double} or {@link Float})
     *                            depending on the {@link ApplicationCommandOption#getType() option type}.
     * @param localizationAdjuster {@link Consumer} to set the name localizations. can be {@code null}.
     * @return this
     */
    public AutocompleteBuilder addChoice(@NotNull String name, @NotNull Object value, @Nullable Consumer<LocalizationDictionary> localizationAdjuster) {

        LocalizationDictionary localizations = null;
        if(localizationAdjuster != null){
            localizations = new LocalizationDictionary();
            localizationAdjuster.accept(localizations);
        }

        this.choices.add(new ApplicationCommandOptionChoice(name, null, localizations, value));
        return this;
    }

    /**
     * checks if this would build a valid {@link Autocomplete}.<br>
     * Does not check value!
     * @return {@code true}
     * @throws LApiIllegalStateException if it is invalid.
     */
    public boolean check() {
        if(choices.size() > Autocomplete.MAX_CHOICES)
            throw new LApiIllegalStateException("Too many choices. Max: " + Autocomplete.MAX_CHOICES);

        for(ApplicationCommandOptionChoice choice : choices) {
            if(choice.getName().length() > ApplicationCommandOptionChoice.NAME_MAX_CHARS) {
                throw new LApiIllegalStateException("name is too long. max: " + ApplicationCommandOptionChoice.NAME_MAX_CHARS);
            }
        }

        return true;
    }

    /**
     *
     * @param check whether to {@link #check()} if this would build a valid {@link Autocomplete}
     * @return built {@link Autocomplete}
     * @throws LApiIllegalStateException if check is true: see {@link #check()}
     */
    public @NotNull Autocomplete build(boolean check) {

        if(check) check();

        return new Autocomplete(choices);
    }

}
