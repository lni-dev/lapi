/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.objects.command.option;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-choice-structure" target="_top">Application Command Option Choice Structure</a>
 */
public class ApplicationCommandOptionChoice implements Datable {

    public static final String NAME_KEY = "name";
    public static final String NAME_LOCALIZED_KEY = "name_localized";
    public static final String NAME_LOCALIZATIONS_KEY = "name_localizations";
    public static final String VALUE_KEY = "value";

    public static final int NAME_MIN_CHARS = 1;
    public static final int NAME_MAX_CHARS = 100;
    public static final int VALUE_MAX_CHARS = 100;

    private final @NotNull String name;
    private final @Nullable String nameLocalized;
    private final @Nullable LocalizationDictionary nameLocalizations;
    private final @NotNull Object value;

    /**
     * @param name              1-{@value #NAME_MAX_CHARS} character choice name
     * @param nameLocalized
     * @param nameLocalizations
     * @param value             value of the choice, up to {@value #VALUE_MAX_CHARS} characters if string
     */
    public ApplicationCommandOptionChoice(@NotNull String name, @Nullable String nameLocalized, @Nullable LocalizationDictionary nameLocalizations, @NotNull Object value) {
        this.name = name;
        this.nameLocalized = nameLocalized;
        this.nameLocalizations = nameLocalizations;
        this.value = value;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link ApplicationCommandOptionChoice}
     * @throws InvalidDataException if {@link #NAME_KEY} or {@link #VALUE_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ApplicationCommandOptionChoice fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String name = (String) data.get(NAME_KEY);
        String nameLocalized = (String) data.get(NAME_LOCALIZED_KEY);
        LocalizationDictionary nameLocalizations = data.getAndConvert(NAME_LOCALIZATIONS_KEY, LocalizationDictionary::fromData);
        Object value = data.get(VALUE_KEY);

        if(name == null || value == null){
            InvalidDataException.throwException(data, null, ApplicationCommandOptionChoice.class,
                    new Object[]{name, value}, new String[]{NAME_KEY, VALUE_KEY});
        }

        //noinspection ConstantConditions
        return new ApplicationCommandOptionChoice(name, nameLocalized, nameLocalizations, value);
    }

    /**
     *
     * @return 1-{@value #NAME_MAX_CHARS} character choice name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Only returned by some endpoints. Returns name localization relevant to the requester's locale
     */
    public @Nullable String getNameLocalized() {
        return nameLocalized;
    }

    /**
     * Name localization dictionary
     */
    public @Nullable LocalizationDictionary getNameLocalizations() {
        return nameLocalizations;
    }


    /**
     * value of the choice, up to {@value #VALUE_MAX_CHARS} characters if string
     * @return  Type of value depends on the {@link ApplicationCommandOptionType option type} that the choice belongs to.
     */
    public @NotNull Object getValue() {
        return value;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(NAME_KEY, name);
        data.addIfNotNull(NAME_LOCALIZED_KEY, nameLocalized);
        data.addIfNotNull(NAME_LOCALIZATIONS_KEY, nameLocalizations);
        data.add(VALUE_KEY, value);

        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationCommandOptionChoice that = (ApplicationCommandOptionChoice) o;

        if (!name.equals(that.name)) return false;
        if (!LocalizationDictionary.equalsContent(nameLocalizations, that.nameLocalizations)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
