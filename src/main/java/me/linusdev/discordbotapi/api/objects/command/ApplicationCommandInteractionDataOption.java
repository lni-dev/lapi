package me.linusdev.discordbotapi.api.objects.command;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.command.option.type.ApplicationCommandOptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-interaction-data-option-structure" target="_top">Application Command Interaction Data Option Structure</a>
 */
public class ApplicationCommandInteractionDataOption implements Datable {

    public final static String NAME_KEY = "name";
    public final static String TYPE_KEY = "type";
    public final static String VALUE_KEY = "value";
    public final static String OPTIONS_KEY = "options";
    public final static String FOCUSED_KEY = "focused";

    private final @NotNull LApi lApi;

    private final @NotNull String name;
    private final @NotNull ApplicationCommandOptionType type;
    private final @Nullable Object value;
    private final @Nullable ArrayList<ApplicationCommandInteractionDataOption> options;
    private final @Nullable Boolean focused;

    /**
     *
     * @param lApi
     * @param name the name of the parameter
     * @param type value of {@link ApplicationCommandOptionType application command option type}
     * @param value the value of the pair
     * @param options present if this option is a group or subcommand
     * @param focused true if this option is the currently focused option for autocomplete
     */
    public ApplicationCommandInteractionDataOption(@NotNull LApi lApi, @NotNull String name, @NotNull ApplicationCommandOptionType type, @Nullable Object value, @Nullable ArrayList<ApplicationCommandInteractionDataOption> options, @Nullable Boolean focused) {
        this.lApi = lApi;
        this.name = name;
        this.type = type;
        this.value = value;
        this.options = options;
        this.focused = focused;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data}
     * @return {@link ApplicationCommandInteractionDataOption}
     * @throws InvalidDataException if {@link #NAME_KEY} or {@link #TYPE_KEY} are missing or {@code null}
     */
    public static @Nullable ApplicationCommandInteractionDataOption fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String name = (String) data.get(NAME_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        Object value = data.get(VALUE_KEY);
        ArrayList<ApplicationCommandInteractionDataOption> options = data.getAndConvertArrayList(OPTIONS_KEY, (ExceptionConverter<Data, ApplicationCommandInteractionDataOption, InvalidDataException>)
                convertible -> ApplicationCommandInteractionDataOption.fromData(lApi, convertible));
        Boolean focused = (Boolean) data.get(FOCUSED_KEY);

        if(name == null || type == null) {
            InvalidDataException.throwException(data, null, ApplicationCommandInteractionDataOption.class,
                    new Object[]{name, type},
                    new String[]{NAME_KEY, TYPE_KEY});
        }

        //noinspection ConstantConditions
        return new ApplicationCommandInteractionDataOption(lApi, name, ApplicationCommandOptionType.fromValue(type.intValue()), value, options, focused);
    }

    /**
     * the name of the parameter
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * value of application command option type. <br>
     * Gives information about what {@link #getValue()} will return
     */
    public @NotNull ApplicationCommandOptionType getType() {
        return type;
    }

    /**
     * the value of the pair how {@link me.linusdev.data.parser.JsonParser JsonParser} parsed it
     */
    public @Nullable Object getValueRaw() {
        return value;
    }

    /**
     * Converts the value to the Type it should be. See {@link #getType()} and {@link ApplicationCommandOptionType#values} for more information.<br>
     * <br>
     * For Example if {@link #getType()} is:
     * <ul>
     *     <li>
     *         {@link ApplicationCommandOptionType#INTEGER INTEGER}: a {@link Long} will be returned
     *     </li>
     *     <li>
     *         {@link ApplicationCommandOptionType#BOOLEAN BOOLEAN}: a {@link Boolean} will be returned
     *     </li>
     *     <li>
     *         {@link ApplicationCommandOptionType#USER USER}: a {@link me.linusdev.discordbotapi.api.objects.user.User User} will be returned
     *     </li>
     *     <li>
     *         {@link ApplicationCommandOptionType#CHANNEL CHANNEL}: a {@link me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel Channel} will be returned
     *     </li>
     *     <li>
     *         {@link ApplicationCommandOptionType#ROLE ROLE}: a {@link me.linusdev.discordbotapi.api.objects.role.Role Role} will be returned
     *     </li>
     *     <li>
     *         {@link ApplicationCommandOptionType#STRING STRING}: a {@link String} will be returned
     *     </li>
     *     <li>
     *         {@link ApplicationCommandOptionType#NUMBER NUMBER}: a {@link Double} will be returned
     *     </li>
     *
     * </ul>
     *
     *
     * @throws InvalidDataException if an exception while converting occured
     */
    public Object getValue() throws InvalidDataException {
        return type.convertValue(lApi, getValueRaw());
    }

    public @Nullable ArrayList<ApplicationCommandInteractionDataOption> getOptions() {
        return options;
    }

    /**
     * true if this option is the currently focused option for autocomplete
     */
    public @Nullable Boolean getFocused() {
        return focused;
    }

    /**
     * true if this option is the currently focused option for autocomplete
     * @return {@code true} if and only if {@link #getFocused()} is {@code true}
     */
    public boolean isFocused(){
        return !(focused == null) && focused;
    }

    @Override
    public Data getData() {
        Data data = new Data(0);

        data.add(NAME_KEY, name);
        data.add(TYPE_KEY, type);
        data.addIfNotNull(VALUE_KEY, value);
        data.addIfNotNull(OPTIONS_KEY, options);
        data.addIfNotNull(FOCUSED_KEY, focused);

        return data;
    }
}
