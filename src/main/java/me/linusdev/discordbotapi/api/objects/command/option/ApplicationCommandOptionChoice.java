package me.linusdev.discordbotapi.api.objects.command.option;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-choice-structure" target="_top">Application Command Option Choice Structure</a>
 */
public class ApplicationCommandOptionChoice implements Datable {

    public static final String NAME_KEY = "name";
    public static final String VALUE_KEY = "value";

    public static final int NAME_MAX_CHARS = 100;
    public static final int VALUE_MAX_CHARS = 100;

    private final @NotNull String name;
    private final @NotNull Object value;

    /**
     *
     * @param name 1-{@value #NAME_MAX_CHARS} character choice name
     * @param value value of the choice, up to {@value #VALUE_MAX_CHARS} characters if string
     */
    public ApplicationCommandOptionChoice(@NotNull String name, @NotNull Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data}
     * @return {@link ApplicationCommandOptionChoice}
     * @throws InvalidDataException if {@link #NAME_KEY} or {@link #VALUE_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable ApplicationCommandOptionChoice fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String name = (String) data.get(NAME_KEY);
        Object value = data.get(VALUE_KEY);

        if(name == null || value == null){
            InvalidDataException.throwException(data, null, ApplicationCommandOptionChoice.class,
                    new Object[]{name, value}, new String[]{NAME_KEY, VALUE_KEY});
        }

        //noinspection ConstantConditions
        return new ApplicationCommandOptionChoice(name, value);
    }

    /**
     *
     * @return 1-{@value #NAME_MAX_CHARS} character choice name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * value of the choice, up to {@value #VALUE_MAX_CHARS} characters if string
     * @return  Type of value depends on the {@link ApplicationCommandOptionType option type} that the choice belongs to.
     */
    public @NotNull Object getValue() {
        return value;
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(NAME_KEY, name);
        data.add(VALUE_KEY, value);

        return data;
    }
}
