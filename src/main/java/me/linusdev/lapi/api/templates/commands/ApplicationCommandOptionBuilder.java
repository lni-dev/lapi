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

import me.linusdev.lapi.api.communication.exceptions.InvalidApplicationCommandOptionException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOptionChoice;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOptionType;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class ApplicationCommandOptionBuilder extends CommandNameAndDescriptionBuilder<ApplicationCommandOptionBuilder> implements HasLApi {

    private final @NotNull LApi lApi;

    private @Nullable ApplicationCommandOptionType<?, ?, ?> type = null;
    private @Nullable Boolean required = null;
    private @Nullable ArrayList<ApplicationCommandOptionChoice> choices = null;

    private @Nullable ArrayList<ChannelType> channelTypes = null;
    private @Nullable Number minValue = null;
    private @Nullable Number maxValue = null;
    private @Nullable @Range(from = ApplicationCommandOption.MIN_LENGTH_MIN, to = ApplicationCommandOption.MIN_LENGTH_MAX) Integer minLength = null;
    private @Nullable @Range(from = ApplicationCommandOption.MAX_LENGTH_MIN, to = ApplicationCommandOption.MAX_LENGTH_MAX) Integer maxLength = null;
    private @Nullable Boolean autocomplete = null;

    public ApplicationCommandOptionBuilder(@NotNull LApi lApi) {
        this.lApi = lApi;
    }

    /**
     * creates a subcommand option, which can have nested options.
     * <br><br>
     * For more information about subcommands and subcommand groups read the
     * <a href="https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups">
     *     discord documentation</a>
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name subcommand name (see {@link ApplicationCommandOption rules})
     * @param description subcommand description (see {@link ApplicationCommandOption rules})
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newSubCommandOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name,
            @NotNull String description
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.SUB_COMMAND)
                .setName(name)
                .setDescription(description);
    }

    /**
     * creates a subcommand group option, which can have nested subcommands.
     * <br><br>
     * For more information about subcommands and subcommand groups read the
     * <a href="https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups">
     *     discord documentation</a>
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name subcommand group name (see {@link ApplicationCommandOption rules})
     * @param description subcommand group description (see {@link ApplicationCommandOption rules})
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newSubCommandGroupOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name,
            @NotNull String description
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.SUB_COMMAND_GROUP)
                .setName(name)
                .setDescription(description);
    }

    /**
     * Creates a new string option.<br>
     * supports {@link #addChoice(ApplicationCommandOptionChoice) choices} or autocomplete
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name  name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @param minLength the minimum allowed length (minimum of {@value ApplicationCommandOption#MIN_LENGTH_MIN}, maximum of {@value ApplicationCommandOption#MIN_LENGTH_MAX})
     * @param maxLength the maximum allowed length (minimum of {@value ApplicationCommandOption#MAX_LENGTH_MIN}, maximum of {@value ApplicationCommandOption#MAX_LENGTH_MAX})
     * @param autocomplete If autocomplete interactions are enabled
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newStringOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required,
            @Nullable Integer minLength, @Nullable Integer maxLength, @Nullable Boolean autocomplete
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.STRING)
                .setName(name)
                .setDescription(description)
                .setRequired(required)
                .setMinLength(minLength)
                .setMaxLength(maxLength)
                .setAutocomplete(autocomplete);
    }

    /**
     * Creates a new string option.<br>
     * supports {@link #addChoice(ApplicationCommandOptionChoice) choices} or autocomplete
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name  name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     * @see #newStringOptionBuilder(HasLApi, String, String, boolean, Integer, Integer, Boolean)
     */
    public static @NotNull ApplicationCommandOptionBuilder newStringOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return newStringOptionBuilder(lApi, name, description, required, null, null, null);
    }

    /**
     * creates a new integer ({@link Integer}, {@link Long}, ...) option.<br>
     * supports {@link #addChoice(ApplicationCommandOptionChoice) choices}  or autocomplete
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @param minValue the minimum value permitted
     * @param maxValue the maximum value permitted
     * @param autocomplete If autocomplete interactions are enabled
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newIntegerOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required,
            @Nullable Number minValue, @Nullable Number maxValue, @Nullable Boolean autocomplete
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.INTEGER)
                .setName(name)
                .setDescription(description)
                .setRequired(required)
                .setMinValue(minValue)
                .setMaxValue(maxValue)
                .setAutocomplete(autocomplete);
    }

    /**
     * creates a new integer ({@link Integer}, {@link Long}, ...) option<br>
     * supports {@link #addChoice(ApplicationCommandOptionChoice) choices}
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     * @see #newIntegerOptionBuilder(HasLApi, String, String, boolean, Number, Number, Boolean)
     */
    public static @NotNull ApplicationCommandOptionBuilder newIntegerOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return newIntegerOptionBuilder(lApi, name, description, required, null, null, null);
    }

    /**
     * creates a new boolean option.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newBooleanOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.BOOLEAN)
                .setName(name)
                .setDescription(description)
                .setRequired(required);
    }

    /**
     * creates a new user option.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newUserOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.USER)
                .setName(name)
                .setDescription(description)
                .setRequired(required);
    }

    /**
     * creates a new channel option.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @param channelTypes the channels shown will be restricted to these types
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newChannelOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required,
            @NotNull ChannelType... channelTypes
    ){
        ApplicationCommandOptionBuilder builder = new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.CHANNEL)
                .setName(name)
                .setDescription(description)
                .setRequired(required);

        if(channelTypes.length > 0)
            builder.setChannelTypes(channelTypes);

        return builder;
    }

    /**
     * creates a new role option.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newRoleOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.ROLE)
                .setName(name)
                .setDescription(description)
                .setRequired(required);
    }

    /**
     * creates a new mentionable option.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newMentionableOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.MENTIONABLE)
                .setName(name)
                .setDescription(description)
                .setRequired(required);
    }

    /**
     * creates a new number ({@link Double}, {@link Float}, ...) option<br>
     * supports {@link #addChoice(ApplicationCommandOptionChoice) choices} or autocomplete.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @param minValue the minimum value permitted
     * @param maxValue the maximum value permitted
     * @param autocomplete If autocomplete interactions are enabled
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newNumberOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required,
            @Nullable Number minValue, @Nullable Number maxValue, @Nullable Boolean autocomplete
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.NUMBER)
                .setName(name)
                .setDescription(description)
                .setRequired(required)
                .setMinValue(minValue)
                .setMaxValue(maxValue)
                .setAutocomplete(autocomplete);
    }

    /**
     * creates a new number ({@link Double}, {@link Float}, ...) option<br>
     * supports {@link #addChoice(ApplicationCommandOptionChoice) choices} or autocomplete.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     * @see #newNumberOptionBuilder(HasLApi, String, String, boolean, Number, Number, Boolean)
     */
    public static @NotNull ApplicationCommandOptionBuilder newNumberOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return newNumberOptionBuilder(lApi, name, description, required, null, null, null);
    }

    /**
     * creates a new attachment option.
     *
     * @param lApi {@link LApi} or {@link HasLApi}
     * @param name name (see {@link ApplicationCommandOption rules})
     * @param description description (see {@link ApplicationCommandOption rules})
     * @param required If the parameter is required or optional
     * @return new {@link ApplicationCommandOptionBuilder}
     */
    public static @NotNull ApplicationCommandOptionBuilder newAttachmentOptionBuilder(
            @NotNull HasLApi lApi, @NotNull String name, @NotNull String description, boolean required
    ){
        return new ApplicationCommandOptionBuilder(lApi.getLApi())
                .setType(ApplicationCommandOptionType.ATTACHMENT)
                .setName(name)
                .setDescription(description)
                .setRequired(required);
    }


    /**
     * Sets the type for this option
     * @param type {@link ApplicationCommandOptionType}
     * @return this
     * @see ApplicationCommandOptionType#SUB_COMMAND
     * @see ApplicationCommandOptionType#SUB_COMMAND_GROUP
     * @see ApplicationCommandOptionType#STRING
     * @see ApplicationCommandOptionType#INTEGER
     * @see ApplicationCommandOptionType#BOOLEAN
     * @see ApplicationCommandOptionType#USER
     * @see ApplicationCommandOptionType#CHANNEL
     * @see ApplicationCommandOptionType#ROLE
     * @see ApplicationCommandOptionType#MENTIONABLE
     * @see ApplicationCommandOptionType#NUMBER
     * @see ApplicationCommandOptionType#ATTACHMENT
     */
    public ApplicationCommandOptionBuilder setType(@NotNull ApplicationCommandOptionType<?, ?, ?> type) {
        this.type = type;
        return this;
    }

    /**
     * Whether this Option is required or optional. Default: {@code false}.
     * Set to {@code  null} to reset to default. <br>
     * Does not work on {@link ApplicationCommandOptionType#SUB_COMMAND} or {@link ApplicationCommandOptionType#SUB_COMMAND_GROUP}.
     * @param required {@code true} to make this command required, {@code false} or {@code null} to make this command optional.
     * @return this
     * @see #makeRequired()
     * @see #makeOptional()
     */
    public ApplicationCommandOptionBuilder setRequired(@Nullable Boolean required) {
        this.required = required;
        return this;
    }

    /**
     * Makes this option required.<br>
     * Does not work on {@link ApplicationCommandOptionType#SUB_COMMAND} or {@link ApplicationCommandOptionType#SUB_COMMAND_GROUP}.
     * @return this
     * @see #makeOptional()
     * @see #setRequired(Boolean)
     */
    public ApplicationCommandOptionBuilder makeRequired() {
        setRequired(true);
        return this;
    }

    /**
     * Makes this option optional<br>
     * Does not work on {@link ApplicationCommandOptionType#SUB_COMMAND} or {@link ApplicationCommandOptionType#SUB_COMMAND_GROUP}.
     * @return this
     * @see #makeRequired()
     * @see #setRequired(Boolean)
     */
    public ApplicationCommandOptionBuilder makeOptional() {
        setRequired(false);
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}
     * <br><br>
     * Sets choices to given list. replaces old {@link #choices}.
     * @param choices {@link ArrayList} of {@link ApplicationCommandOptionChoice}
     * @return this
     */
    public ApplicationCommandOptionBuilder setChoices(@Nullable ArrayList<ApplicationCommandOptionChoice> choices) {
        this.choices = choices;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}
     * <br><br>
     * Add a choice for this option.
     * @param choice {@link ApplicationCommandOptionChoice} to add
     * @return this
     */
    public ApplicationCommandOptionBuilder addChoice(@NotNull ApplicationCommandOptionChoice choice) {
        if(this.choices == null) this.choices = new ArrayList<>();
        this.choices.add(choice);
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}
     * <br><br>
     * Add a choice for this option.
     * @param name choice name
     * @param localizations choice name localizations
     * @param value {@link String}, Integer ({@link Integer}, {@link Long}, ...), Number ({@link Double} or {@link Float})
     *                           depending on the {@link #setType(ApplicationCommandOptionType) option type}.
     * @return this
     */
    public ApplicationCommandOptionBuilder addChoice(@NotNull String name, @Nullable LocalizationDictionary localizations, @NotNull Object value) {
        if(this.choices == null) this.choices = new ArrayList<>();
        this.choices.add(new ApplicationCommandOptionChoice(name, null, localizations, value));
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}
     * <br><br>
     * Add a choice for this option.
     * @param name choice name
     * @param value {@link String}, Integer ({@link Integer}, {@link Long}, ...), Number ({@link Double} or {@link Float})
     *                            depending on the {@link #setType(ApplicationCommandOptionType) option type}.
     * @param localizationAdjuster {@link Consumer} to set the name localizations. can be {@code null}.
     * @return this
     */
    public ApplicationCommandOptionBuilder addChoice(@NotNull String name, @NotNull Object value, @Nullable Consumer<LocalizationDictionary> localizationAdjuster) {
        if(this.choices == null) this.choices = new ArrayList<>();

        LocalizationDictionary localizations = null;
        if(localizationAdjuster != null){
            localizations = new LocalizationDictionary();
            localizationAdjuster.accept(localizations);
        }

        this.choices.add(new ApplicationCommandOptionChoice(name, null, localizations, value));
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#CHANNEL}. The channels shown will be restricted to these {@link ChannelType types}.
     * {@code null} for all {@link ChannelType channel types}.
     * <br><br>
     * Replaces current {@link #channelTypes}
     * @param channelTypes the {@link ChannelType channel types} that will be shown
     * @return this
     * @see #setChannelTypes(ChannelType...)
     * @see #addChannelTypes(ChannelType...)
     * @see #removeChannelTypes(ChannelType...)
     */
    public ApplicationCommandOptionBuilder setChannelTypes(@Nullable ArrayList<ChannelType> channelTypes) {
        this.channelTypes = channelTypes;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#CHANNEL}. The channels shown will be restricted to these {@link ChannelType types}.
     * set {@link #setChannelTypes(ArrayList)} to {@code null}, to show all {@link ChannelType channel types}.
     * <br><br>
     * Replaces current {@link #channelTypes}
     * @param channelTypes the {@link ChannelType channel types} that will be shown
     * @return this
     * @see #addChannelTypes(ChannelType...)
     * @see #removeChannelTypes(ChannelType...)
     */
    public ApplicationCommandOptionBuilder setChannelTypes(@NotNull ChannelType... channelTypes) {
        this.channelTypes = new ArrayList<>(List.of(channelTypes));
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#CHANNEL}. The channels shown will be restricted to these {@link ChannelType types}.
     * set {@link #setChannelTypes(ArrayList)} to {@code null}, to show all {@link ChannelType channel types}.
     * <br><br>
     * adds to the current {@link #channelTypes}
     * @param channelTypes the {@link ChannelType channel types} that should be added
     * @return this
     * @see #setChannelTypes(ChannelType...)
     * @see #removeChannelTypes(ChannelType...)
     */
    public ApplicationCommandOptionBuilder addChannelTypes(@NotNull ChannelType... channelTypes) {
        if(this.channelTypes == null) this.channelTypes = new ArrayList<>(Math.max(channelTypes.length, 10));
        for(ChannelType channelType : channelTypes) {
            this.channelTypes.add(channelType);
        }
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#CHANNEL}. The channels shown will be restricted to these {@link ChannelType types}.
     * set {@link #setChannelTypes(ArrayList)} to {@code null}, to show all {@link ChannelType channel types}.
     * <br><br>
     * removes from the current {@link #channelTypes}
     * @param channelTypes the {@link ChannelType channel types} that should be added
     * @return this
     * @see #setChannelTypes(ChannelType...)
     * @see #removeChannelTypes(ChannelType...)
     */
    public ApplicationCommandOptionBuilder removeChannelTypes(@NotNull ChannelType... channelTypes) {
        if(this.channelTypes == null) return this;
        for(ChannelType channelType : channelTypes) {
            this.channelTypes.remove(channelType);
        }
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#INTEGER} and {@link ApplicationCommandOptionType#NUMBER}
     * @param minValue the minimum value permitted
     * @return this
     */
    public ApplicationCommandOptionBuilder setMinValue(@Nullable Number minValue) {
        this.minValue = minValue;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#INTEGER} and {@link ApplicationCommandOptionType#NUMBER}
     * @param maxValue the maximum value permitted
     * @return this
     */
    public ApplicationCommandOptionBuilder setMaxValue(@Nullable Number maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}
     * @param minLength the minimum allowed length
     * @return this
     */
    public ApplicationCommandOptionBuilder setMinLength(
            @Range(from = ApplicationCommandOption.MIN_LENGTH_MIN, to = ApplicationCommandOption.MIN_LENGTH_MAX) @Nullable Integer minLength) {
        this.minLength = minLength;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}
     * @param maxLength the maximum allowed length
     * @return this
     */
    public ApplicationCommandOptionBuilder setMaxLength(
            @Range(from = ApplicationCommandOption.MAX_LENGTH_MIN, to = ApplicationCommandOption.MAX_LENGTH_MAX) @Nullable Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}
     * <br><br>
     * may not be set to true if choices are present
     * @param autocomplete if auto-completion is enabled
     * @return this
     */
    public ApplicationCommandOptionBuilder setAutocomplete(@Nullable Boolean autocomplete) {
        this.autocomplete = autocomplete;
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}. Enables autocomplete.
     * <br><br>
     * may not be enabled if choices are present
     * @return this
     * @see #disableAutocomplete()
     * @see #setAutocomplete(Boolean)
     */
    public ApplicationCommandOptionBuilder enableAutocomplete() {
        setAutocomplete(true);
        return this;
    }

    /**
     * Only for {@link ApplicationCommandOptionType#STRING}, {@link ApplicationCommandOptionType#INTEGER} and
     * {@link ApplicationCommandOptionType#NUMBER}. Enables autocomplete.
     * @return this
     * @see #enableAutocomplete()
     * @see #setAutocomplete(Boolean)
     */
    public ApplicationCommandOptionBuilder disableAutocomplete() {
        setAutocomplete(true);
        return this;
    }

    public ApplicationCommandOptionBuilder check() throws InvalidApplicationCommandOptionException {
        if(type == null) throw new InvalidApplicationCommandOptionException("type may not be null");
        if(name == null) throw new InvalidApplicationCommandOptionException("name may not be null or empty");
        if(description == null) throw new InvalidApplicationCommandOptionException("description may not be null or empty");

        if (name.length() < ApplicationCommandOption.NAME_MIN_CHARS || name.length() > ApplicationCommandOption.NAME_MAX_CHARS)
            throw new InvalidApplicationCommandOptionException("name too long or too short");

        if (description.length() < ApplicationCommandOption.DESCRIPTION_MIN_CHARS || description.length() > ApplicationCommandOption.DESCRIPTION_MAX_CHARS)
            throw new InvalidApplicationCommandOptionException("name too long or too short");

        if(choices != null && choices.size() > ApplicationCommandOption.MAX_CHOICES_AMOUNT) {
            throw new InvalidApplicationCommandOptionException("too many choices. max: " + ApplicationCommandOption.MAX_CHOICES_AMOUNT);
        }

        if(channelTypes != null && type != ApplicationCommandOptionType.CHANNEL) {
            throw new InvalidApplicationCommandOptionException("channel-types specified, but option type is not channel");
        }

        if(minValue != null || maxValue != null) {
            if(type !=ApplicationCommandOptionType.INTEGER && type !=ApplicationCommandOptionType.NUMBER) {
                throw new InvalidApplicationCommandOptionException("minValue or maxValue set, but option type is not integer or number");
            }
        }

        if(minLength != null || maxLength != null) {
            if(type != ApplicationCommandOptionType.STRING) {
                throw new InvalidApplicationCommandOptionException("minLength or maxLength set, but option type is not string");
            }

            if(minLength != null && (minLength < ApplicationCommandOption.MIN_LENGTH_MIN || minLength > ApplicationCommandOption.MIN_LENGTH_MAX));
            if(maxLength != null && (maxLength < ApplicationCommandOption.MAX_LENGTH_MIN || maxLength > ApplicationCommandOption.MAX_LENGTH_MAX));
        }

        if(autocomplete != null && autocomplete) {
            if(type != ApplicationCommandOptionType.STRING
                    && type != ApplicationCommandOptionType.INTEGER
                    && type != ApplicationCommandOptionType.NUMBER) {
                throw new InvalidApplicationCommandOptionException("autocomplete is enabled, but option type is not string, integer or number");
            }

            if(choices != null) {
                throw new InvalidApplicationCommandOptionException("autocomplete may not be set to true if choices are present.");
            }
        }

        if(options != null) {
            if(type != ApplicationCommandOptionType.SUB_COMMAND && type != ApplicationCommandOptionType.SUB_COMMAND_GROUP) {
                throw new InvalidApplicationCommandOptionException("only subcommand and subcommand group options can have nested options.");
            }

            ApplicationCommandBuilder.checkOptions(type, options);

        }

        return this;
    }

    public ApplicationCommandOption build(boolean check) throws InvalidApplicationCommandOptionException {

        if(check) check();

        return new ApplicationCommandOption(getLApi(), type, name, null, nameLocalizations, description,
                null, descriptionLocalizations, required,
                choices == null ? null : choices.toArray(new ApplicationCommandOptionChoice[0]),
                options == null ? null : options.toArray(new ApplicationCommandOption[0]),
                channelTypes == null ? null : channelTypes.toArray(new ChannelType[0]),
                minValue, maxValue, minLength, maxLength, autocomplete);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
