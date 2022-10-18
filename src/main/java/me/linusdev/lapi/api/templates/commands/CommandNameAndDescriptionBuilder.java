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
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.command.option.ApplicationCommandOption;
import me.linusdev.lapi.api.objects.nchannel.ChannelType;
import me.linusdev.lapi.api.objects.local.Locale;
import me.linusdev.lapi.api.other.localization.LocalizationDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class CommandNameAndDescriptionBuilder<B extends CommandNameAndDescriptionBuilder<B>> implements HasLApi {

    protected @Nullable String name = null;
    protected @Nullable LocalizationDictionary nameLocalizations = null;

    protected @Nullable String description = null;
    protected @Nullable LocalizationDictionary descriptionLocalizations = null;

    protected @Nullable ArrayList<ApplicationCommandOption> options = null;

    /**
     *
     * @param name name of the command
     * @return this
     */
    public B setName(@NotNull String name) {
        this.name = name;
        return (B) this;
    }

    /**
     * replace the {@link LocalizationDictionary} for the name with given one.
     * <br><br>
     * You are probably looking for {@link #setNameLocalization(Locale, String)}.
     * @param dictionary the new {@link LocalizationDictionary}
     * @see #setNameLocalization(Locale, String)
     * @return this
     */
    public B setNameLocalizations(@Nullable LocalizationDictionary dictionary) {
        this.nameLocalizations = dictionary;
        return (B) this;
    }

    /**
     * set name localization for given {@link Locale}. Set to {@code null} to remove the localization for given {@link Locale}
     * @param locale {@link Locale} to set localization for
     * @param localization {@link String} localization or {@code null}
     * @return this
     */
    public B setNameLocalization(@NotNull Locale locale, @Nullable String localization) {
        if(this.nameLocalizations == null) this.nameLocalizations = new LocalizationDictionary();

        nameLocalizations.setLocalization(locale, localization);
        return (B) this;
    }

    /**
     *
     * @param description description of the command
     * @return this
     */
    public B setDescription(@NotNull String description) {
        this.description = description;
        return (B) this;
    }

    /**
     * replace the {@link LocalizationDictionary} for the description with given one.
     * <br><br>
     * You are probably looking for {@link #setDescriptionLocalization(Locale, String)}.
     * @param dictionary the new {@link LocalizationDictionary}
     * @see #setDescriptionLocalization(Locale, String)
     * @return this
     */
    public B setDescriptionLocalizations(@Nullable LocalizationDictionary dictionary) {
        this.descriptionLocalizations = dictionary;
        return (B) this;
    }

    /**
     * set description localization for given {@link Locale}. Set to {@code null} to remove the localization for given {@link Locale}
     * @param locale {@link Locale} to set localization for
     * @param localization {@link String} localization or {@code null}
     * @return this
     */
    public B setDescriptionLocalization(@NotNull Locale locale, @Nullable String localization) {
        if(this.descriptionLocalizations == null) this.descriptionLocalizations = new LocalizationDictionary();

        descriptionLocalizations.setLocalization(locale, localization);
        return (B) this;
    }

    /**
     * replaces {@link #options} with given arraylist.
     * @param options {@link ArrayList} of {@link ApplicationCommandOption}
     * @return this
     */
    public B setOptions(@Nullable ArrayList<ApplicationCommandOption> options) {
        this.options = options;

        return (B) this;
    }

    /**
     * Adds given {@link ApplicationCommandOption}
     * @param option {@link ApplicationCommandOption} to add
     * @return this
     */
    public B addOption(@NotNull ApplicationCommandOption option) {
        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option);
        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newSubCommandOptionBuilder(HasLApi, String, String) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addSubCommandOption(@NotNull String name, @NotNull String description,
                                 @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster, boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newSubCommandOptionBuilder(this, name, description);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newSubCommandGroupOptionBuilder(HasLApi, String, String) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addSubCommandGroupOption(@NotNull String name, @NotNull String description,
                                 @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster, boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newSubCommandGroupOptionBuilder(this, name, description);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newStringOptionBuilder(HasLApi, String, String, boolean, Integer, Integer, Boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addStringOption(@NotNull String name, @NotNull String description, boolean required,
                             @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                             @Nullable Integer minLength, @Nullable Integer maxLength, @Nullable Boolean autocomplete,
                             boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newStringOptionBuilder(this, name, description, required, minLength, maxLength, autocomplete);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newStringOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addStringOption(@NotNull String name, @NotNull String description, boolean required,
                             @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                             boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newStringOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newIntegerOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addIntegerOption(@NotNull String name, @NotNull String description, boolean required,
                             @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                             boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newIntegerOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newIntegerOptionBuilder(HasLApi, String, String, boolean, Number, Number, Boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addIntegerOption(@NotNull String name, @NotNull String description, boolean required,
                              @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                              @Nullable Number minValue, @Nullable Number maxValue, @Nullable Boolean autocomplete,
                              boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newIntegerOptionBuilder(this, name, description, required, minValue, maxValue, autocomplete);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newBooleanOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addBooleanOption(@NotNull String name, @NotNull String description, boolean required,
                              @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                              boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newBooleanOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newUserOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addUserOption(@NotNull String name, @NotNull String description, boolean required,
                              @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                              boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newUserOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newChannelOptionBuilder(HasLApi, String, String, boolean, ChannelType...) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addChannelOption(@NotNull String name, @NotNull String description, boolean required,
                              @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                              boolean check, @NotNull ChannelType... channelTypes) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newChannelOptionBuilder(this, name, description, required, channelTypes);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newRoleOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addRoleOption(@NotNull String name, @NotNull String description, boolean required,
                           @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                           boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newRoleOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newMentionableOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addMentionableOption(@NotNull String name, @NotNull String description, boolean required,
                           @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                           boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newMentionableOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newNumberOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addNumberOption(@NotNull String name, @NotNull String description, boolean required,
                                  @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                                  boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newNumberOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newNumberOptionBuilder(HasLApi, String, String, boolean, Number, Number, Boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addNumberOption(@NotNull String name, @NotNull String description, boolean required,
                             @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                             @Nullable Number minValue, @Nullable Number maxValue, @Nullable Boolean autocomplete,
                             boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newNumberOptionBuilder(this, name, description, required, minValue, maxValue, autocomplete);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

    /**
     * see {@link ApplicationCommandOptionBuilder#newAttachmentOptionBuilder(HasLApi, String, String, boolean) here} for more information
     * @param adjuster {@link Consumer} to further adjust the {@link ApplicationCommandOptionBuilder}
     * @param check whether to if the {@link ApplicationCommandOption} will be valid
     * @return this
     */
    public B addAttachmentOption(@NotNull String name, @NotNull String description, boolean required,
                             @Nullable Consumer<ApplicationCommandOptionBuilder> adjuster,
                             boolean check) throws InvalidApplicationCommandOptionException {

        ApplicationCommandOptionBuilder option = ApplicationCommandOptionBuilder
                .newAttachmentOptionBuilder(this, name, description, required);

        if(adjuster != null) adjuster.accept(option);

        if(this.options == null) this.options = new ArrayList<>();
        this.options.add(option.build(check));

        return (B) this;
    }

}
