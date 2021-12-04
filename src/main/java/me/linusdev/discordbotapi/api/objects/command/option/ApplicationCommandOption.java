package me.linusdev.discordbotapi.api.objects.command.option;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.command.ApplicationCommand;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import me.linusdev.discordbotapi.api.objects.enums.SimpleChannelType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure" target="_top">Application Command Option Structure</a>
 */
public class ApplicationCommandOption implements Datable, HasLApi {

    public static final String TYPE_KEY = "type";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String REQUIRED_KEY = "required";
    public static final String CHOICES_KEY = "choices";
    public static final String OPTIONS_KEY = "options";
    public static final String CHANNEL_TYPES_KEY = "channel_types";
    public static final String MIN_VALUE_KEY = "min_value";
    public static final String MAX_VALUE_KEY = "max_value";
    public static final String AUTOCOMPLETE_KEY = "autocomplete";

    public static final int NAME_MAX_CHARS = 32;
    public static final int DESCRIPTION_MAX_CHARS = 100;
    public static final int MAX_CHOICES_AMOUNT = 25;


    private final @NotNull LApi lApi;

    private final @NotNull ApplicationCommandOptionType type;
    private final @NotNull String name;
    private final @NotNull String description;
    private final @Nullable Boolean required;
    private final @Nullable ApplicationCommandOptionChoice[] choices;
    private final @Nullable ApplicationCommandOption[] options;
    private final @Nullable ChannelType[] channelTypes;
    private final @Nullable Number minValue;
    private final @Nullable Number maxValue;
    private final @Nullable Boolean autocomplete;

    /**
     *
     * @param lApi {@link LApi}
     * @param type the type of option
     * @param name 	1-{@value #NAME_MAX_CHARS} character name. see {@link ApplicationCommand restrictions}
     * @param description 1-{@value #DESCRIPTION_MAX_CHARS} character description
     * @param required if the parameter is required or optional--default false
     * @param choices choices for {@link ApplicationCommandOptionType#STRING STRING}, {@link ApplicationCommandOptionType#INTEGER INTEGER}, and {@link ApplicationCommandOptionType#NUMBER NUMBER} types for the user to pick from, max {@value #MAX_CHOICES_AMOUNT}
     * @param options if the option is a subcommand or subcommand group type, these nested options will be the parameters
     * @param channelTypes if the option is a channel type, the channels shown will be restricted to these types
     * @param minValue if the option is an {@link ApplicationCommandOptionType#INTEGER INTEGER} or {@link ApplicationCommandOptionType#NUMBER NUMBER} type, the minimum value permitted
     * @param maxValue if the option is an {@link ApplicationCommandOptionType#INTEGER INTEGER} or {@link ApplicationCommandOptionType#NUMBER NUMBER} type, the maximum value permitted
     * @param autocomplete enable autocomplete interactions for this option. may not be set to true if choices are present.
     */
    public ApplicationCommandOption(@NotNull LApi lApi, @NotNull ApplicationCommandOptionType type, @NotNull String name, @NotNull String description, @Nullable Boolean required, @Nullable ApplicationCommandOptionChoice[] choices, @Nullable ApplicationCommandOption[] options, @Nullable ChannelType[] channelTypes, @Nullable Number minValue, @Nullable Number maxValue, @Nullable Boolean autocomplete) {
        this.lApi = lApi;
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.choices = choices;
        this.options = options;
        this.channelTypes = channelTypes;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.autocomplete = autocomplete;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ApplicationCommandOption fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        Number type = (Number) data.get(TYPE_KEY);
        String name = (String) data.get(NAME_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        Boolean required = (Boolean) data.get(REQUIRED_KEY);

        ArrayList<ApplicationCommandOptionChoice> choices = data.getAndConvertArrayList(CHOICES_KEY, (ExceptionConverter<Data, ApplicationCommandOptionChoice, InvalidDataException>)
                convertible -> ApplicationCommandOptionChoice.fromData(lApi, convertible));

        ArrayList<ApplicationCommandOption> options = data.getAndConvertArrayList(OPTIONS_KEY, (ExceptionConverter<Data, ApplicationCommandOption, InvalidDataException>)
                convertible -> ApplicationCommandOption.fromData(lApi, convertible));

        ArrayList<ChannelType> channelTypes = data.getAndConvertArrayList(CHANNEL_TYPES_KEY, (ExceptionConverter<Number, ChannelType, InvalidDataException>)
                convertible -> ChannelType.fromId(convertible.intValue()));

        Number minValue = (Number) data.get(MIN_VALUE_KEY);
        Number maxValue = (Number) data.get(MAX_VALUE_KEY);
        Boolean autocomplete = (Boolean) data.get(AUTOCOMPLETE_KEY);

        if(type == null || name == null || description == null){
            InvalidDataException.throwException(data, null, ApplicationCommandOption.class,
                    new Object[]{type, name, description},
                    new String[]{TYPE_KEY, NAME_KEY, DESCRIPTION_KEY});

        }

        //noinspection ConstantConditions
        return new ApplicationCommandOption(lApi, ApplicationCommandOptionType.fromValue(type.intValue()), name, description,
                required,
                choices == null ? null : choices.toArray(new ApplicationCommandOptionChoice[0]),
                options == null ? null : options.toArray(new ApplicationCommandOption[0]),
                channelTypes == null ? null : channelTypes.toArray(new ChannelType[0]),
                minValue, maxValue, autocomplete);
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
     * 1-{@value #DESCRIPTION_MAX_CHARS} character description
     */
    public @NotNull String getDescription() {
        return description;
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
    public Data getData() {
        Data data = new Data(0);

        data.add(TYPE_KEY, type);
        data.add(NAME_KEY, name);
        data.add(DESCRIPTION_KEY, description);
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
