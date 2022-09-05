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

package me.linusdev.lapi.api.objects.command.option;

import me.linusdev.data.Datable;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 *
 * <h1 style="margin-bottom:0;">Option Name and Description Rules</h1>
 * <ul>
 *     <li>
 *         name length: {@value ApplicationCommandOption#NAME_MIN_CHARS} - {@value ApplicationCommandOption#NAME_MAX_CHARS} character
 *     </li>
 *     <li>
 *         description length: {@value ApplicationCommandOption#DESCRIPTION_MIN_CHARS} - {@value ApplicationCommandOption#DESCRIPTION_MAX_CHARS} character
 *     </li>
 * </ul>
 *
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure" target="_top">Application Command Option Structure</a>
 */
public class ApplicationCommandOption implements Datable, HasLApi {

    public static final String TYPE_KEY = "type";
    public static final String NAME_KEY = "name";
    public static final String NAME_LOCALIZED_KEY = "name_localized";
    public static final String NAME_LOCALIZATIONS_KEY = "name_localizations";
    public static final String DESCRIPTION_KEY = "description";
    public static final String DESCRIPTION_LOCALIZED_KEY = "description_localized";
    public static final String DESCRIPTION_LOCALIZATIONS_KEY = "description_localizations";
    public static final String REQUIRED_KEY = "required";
    public static final String CHOICES_KEY = "choices";
    public static final String OPTIONS_KEY = "options";
    public static final String CHANNEL_TYPES_KEY = "channel_types";
    public static final String MIN_VALUE_KEY = "min_value";
    public static final String MAX_VALUE_KEY = "max_value";
    public static final String MIN_LENGTH_KEY = "min_length";
    public static final String MAX_LENGTH_KEY = "max_length";
    public static final String AUTOCOMPLETE_KEY = "autocomplete";


    public static final int NAME_MIN_CHARS = 1;
    public static final int NAME_MAX_CHARS = 32;
    public static final int DESCRIPTION_MIN_CHARS = 1;
    public static final int DESCRIPTION_MAX_CHARS = 100;
    public static final int MAX_CHOICES_AMOUNT = 25;

    public static final int MIN_LENGTH_MIN = 0;
    public static final int MIN_LENGTH_MAX = 6000;

    public static final int MAX_LENGTH_MIN = 1;
    public static final int MAX_LENGTH_MAX = 6000;


    private final @NotNull LApi lApi;

    private final @NotNull ApplicationCommandOptionType type;
    private final @NotNull String name;
    private final @Nullable String nameLocalized;
    private final @Nullable LocalizationDictionary nameLocalizations;
    private final @NotNull String description;
    private final @Nullable String descriptionLocalized;
    private final @Nullable LocalizationDictionary descriptionLocalizations;
    private final @Nullable Boolean required;
    private final @Nullable ApplicationCommandOptionChoice[] choices;
    private final @Nullable ApplicationCommandOption[] options;
    private final @Nullable ChannelType[] channelTypes;
    private final @Nullable Number minValue;
    private final @Nullable Number maxValue;
    private final @Nullable Integer minLength;
    private final @Nullable Integer maxLength;
    private final @Nullable Boolean autocomplete;

    /**
     * @param lApi                     {@link LApi}
     * @param type                     the type of option
     * @param name                     1-{@value #NAME_MAX_CHARS} character name. see {@link ApplicationCommand restrictions}
     * @param nameLocalized
     * @param nameLocalizations        Localization dictionary for name field. Values follow the same restrictions as name
     * @param description              1-{@value #DESCRIPTION_MAX_CHARS} character description
     * @param descriptionLocalized
     * @param descriptionLocalizations Localization dictionary for the description field. Values follow the same restrictions as description
     * @param required                 if the parameter is required or optional--default false
     * @param choices                  choices for {@link ApplicationCommandOptionType#STRING STRING}, {@link ApplicationCommandOptionType#INTEGER INTEGER}, and {@link ApplicationCommandOptionType#NUMBER NUMBER} types for the user to pick from, max {@value #MAX_CHOICES_AMOUNT}
     * @param options                  if the option is a subcommand or subcommand group type, these nested options will be the parameters
     * @param channelTypes             if the option is a channel type, the channels shown will be restricted to these types
     * @param minValue                 if the option is an {@link ApplicationCommandOptionType#INTEGER INTEGER} or {@link ApplicationCommandOptionType#NUMBER NUMBER} type, the minimum value permitted
     * @param maxValue                 if the option is an {@link ApplicationCommandOptionType#INTEGER INTEGER} or {@link ApplicationCommandOptionType#NUMBER NUMBER} type, the maximum value permitted
     * @param minLength
     * @param maxLength
     * @param autocomplete             enable autocomplete interactions for this option. may not be set to true if choices are present.
     */
    public ApplicationCommandOption(@NotNull LApi lApi, @NotNull ApplicationCommandOptionType type, @NotNull String name, @Nullable String nameLocalized, @Nullable LocalizationDictionary nameLocalizations, @NotNull String description, @Nullable String descriptionLocalized, @Nullable LocalizationDictionary descriptionLocalizations, @Nullable Boolean required, @Nullable ApplicationCommandOptionChoice[] choices, @Nullable ApplicationCommandOption[] options, @Nullable ChannelType[] channelTypes, @Nullable Number minValue, @Nullable Number maxValue, @Nullable Integer minLength, @Nullable Integer maxLength, @Nullable Boolean autocomplete) {
        this.lApi = lApi;
        this.type = type;
        this.name = name;
        this.nameLocalized = nameLocalized;
        this.nameLocalizations = nameLocalizations;
        this.description = description;
        this.descriptionLocalized = descriptionLocalized;
        this.descriptionLocalizations = descriptionLocalizations;
        this.required = required;
        this.choices = choices;
        this.options = options;
        this.channelTypes = channelTypes;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.autocomplete = autocomplete;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ApplicationCommandOption fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        Number type = (Number) data.get(TYPE_KEY);
        String name = (String) data.get(NAME_KEY);
        String nameLocalized = (String) data.get(NAME_LOCALIZED_KEY);
        LocalizationDictionary nameLocalizations = data.getAndConvert(NAME_LOCALIZATIONS_KEY, LocalizationDictionary::fromData);
        String description = (String) data.get(DESCRIPTION_KEY);
        String descriptionLocalized = (String) data.get(DESCRIPTION_LOCALIZED_KEY);
        LocalizationDictionary descriptionLocalizations = data.getAndConvert(DESCRIPTION_LOCALIZATIONS_KEY, LocalizationDictionary::fromData);
        Boolean required = (Boolean) data.get(REQUIRED_KEY);

        ArrayList<ApplicationCommandOptionChoice> choices = data.getListAndConvertWithException(CHOICES_KEY, (ExceptionConverter<SOData, ApplicationCommandOptionChoice, InvalidDataException>)
                convertible -> ApplicationCommandOptionChoice.fromData(lApi, convertible));

        ArrayList<ApplicationCommandOption> options = data.getListAndConvertWithException(OPTIONS_KEY, (ExceptionConverter<SOData, ApplicationCommandOption, InvalidDataException>)
                convertible -> ApplicationCommandOption.fromData(lApi, convertible));

        ArrayList<ChannelType> channelTypes = data.getListAndConvertWithException(CHANNEL_TYPES_KEY, (ExceptionConverter<Number, ChannelType, InvalidDataException>)
                convertible -> ChannelType.fromId(convertible.intValue()));

        Number minValue = (Number) data.get(MIN_VALUE_KEY);
        Number maxValue = (Number) data.get(MAX_VALUE_KEY);
        Number minLength = (Number) data.get(MIN_LENGTH_KEY);
        Number maxLength = (Number) data.get(MAX_LENGTH_KEY);
        Boolean autocomplete = (Boolean) data.get(AUTOCOMPLETE_KEY);

        if(type == null || name == null || description == null){
            InvalidDataException.throwException(data, null, ApplicationCommandOption.class,
                    new Object[]{type, name, description},
                    new String[]{TYPE_KEY, NAME_KEY, DESCRIPTION_KEY});

        }

        //noinspection ConstantConditions
        return new ApplicationCommandOption(lApi, ApplicationCommandOptionType.fromValue(type.intValue()), name, nameLocalized, nameLocalizations, description,
                descriptionLocalized, descriptionLocalizations, required,
                choices == null ? null : choices.toArray(new ApplicationCommandOptionChoice[0]),
                options == null ? null : options.toArray(new ApplicationCommandOption[0]),
                channelTypes == null ? null : channelTypes.toArray(new ChannelType[0]),
                minValue, maxValue,
                minLength == null ? null : minLength.intValue(),
                maxLength == null ? null : maxLength.intValue(),
                autocomplete);
    }

    /**
     * the type of option
     */
    public @NotNull ApplicationCommandOptionType getType() {
        return type;
    }

    /**
     * 1-{@value #NAME_MAX_CHARS} character name
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
     * 1-{@value #DESCRIPTION_MAX_CHARS} character description
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Only returned by some endpoints. Returns description localization relevant to the requester's locale
     */
    public @Nullable String getDescriptionLocalized() {
        return descriptionLocalized;
    }

    /**
     * Description localization dictionary
     */
    public @Nullable LocalizationDictionary getDescriptionLocalizations() {
        return descriptionLocalizations;
    }

    /**
     * if the parameter is required or optional--default false
     */
    public @Nullable Boolean getRequired() {
        return required;
    }

    /**
     * choices for STRING, INTEGER, and NUMBER types for the user to pick from, max {@value #MAX_CHOICES_AMOUNT}
     */
    public @Nullable ApplicationCommandOptionChoice[] getChoices() {
        return choices;
    }

    /**
     * if the option is a subcommand or subcommand group type, these nested options will be the parameters
     */
    public @Nullable ApplicationCommandOption[] getOptions() {
        return options;
    }

    /**
     * if the option is a channel type, the channels shown will be restricted to these types
     */
    public @Nullable ChannelType[] getChannelTypes() {
        return channelTypes;
    }

    /**
     * if the option is an {@link ApplicationCommandOptionType#INTEGER INTEGER} or {@link ApplicationCommandOptionType#NUMBER NUMBER} type, the minimum value permitted
     */
    public @Nullable Number getMinValue() {
        return minValue;
    }

    /**
     * if the option is an {@link ApplicationCommandOptionType#INTEGER INTEGER} or {@link ApplicationCommandOptionType#NUMBER NUMBER} type, the maximum value permitted
     */
    public @Nullable Number getMaxValue() {
        return maxValue;
    }

    /**
     * enable autocomplete interactions for this option
     */
    public @Nullable Boolean getAutocomplete() {
        return autocomplete;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(10);

        data.add(TYPE_KEY, type);
        data.add(NAME_KEY, name);
        data.addIfNotNull(NAME_LOCALIZED_KEY, nameLocalized);
        data.addIfNotNull(NAME_LOCALIZATIONS_KEY, nameLocalizations);
        data.add(DESCRIPTION_KEY, description);
        data.addIfNotNull(DESCRIPTION_LOCALIZED_KEY, descriptionLocalized);
        data.addIfNotNull(DESCRIPTION_LOCALIZATIONS_KEY, descriptionLocalizations);
        data.addIfNotNull(REQUIRED_KEY, required);
        data.addIfNotNull(CHOICES_KEY, choices);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.addIfNotNull(CHANNEL_TYPES_KEY, channelTypes);
        data.addIfNotNull(MIN_VALUE_KEY, minValue);
        data.addIfNotNull(MAX_VALUE_KEY, maxValue);
        data.addIfNotNull(AUTOCOMPLETE_KEY, autocomplete);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
