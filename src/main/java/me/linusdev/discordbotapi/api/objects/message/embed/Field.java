package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-field-structure" target="_top" >Embed Field Structure</a>
 */
public class Field implements Datable {

    public static final String NAME_KEY = "name";
    public static final String VALUE_KEY = "value";
    public static final String INLINE_KEY = "inline";

    /**
     * @see #getName()
     */
    private final @NotNull String name;

    /**
     * @see #getValue()
     */
    private final @NotNull String value;

    /**
     * @see #isInline()
     */
    private final @Nullable Boolean inline;

    /**
     *
     * @param name name of the field
     * @param value value of the field
     * @param inline whether or not this field should display inline. Inline means, the fields can appear from left to right
     */
    public Field(@NotNull String name, @NotNull String value, @Nullable Boolean inline){
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    /**
     *
     * @param name name of the field
     * @param value value of the field
     */
    public Field(@NotNull String name, @NotNull String value){
        this(name, value, null);
    }

    /**
     * creates a {@link Field} from {@link Data}
     * @param data to create {@link Field}
     * @return {@link Field}
     * @throws InvalidDataException if {@link #NAME_KEY} or {@link #VALUE_KEY} are missing
     */
    public static @NotNull Field fromData(@NotNull Data data) throws InvalidDataException {
        final String name = (String) data.get(NAME_KEY);
        final String value = (String) data.get(VALUE_KEY);
        final Boolean inline = (Boolean) data.get(INLINE_KEY);

        if(name == null || value == null){
            InvalidDataException exception = new InvalidDataException(data, "one or more fields are missing. cannot create " + Field.class.getSimpleName());
            if(name == null) exception.addMissingFields(NAME_KEY);
            if(value == null) exception.addMissingFields(VALUE_KEY);
            throw exception;
        }

        return new Field(name, value, inline);
    }

    /**
     * name of the field
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * value of the field
     */
    public @NotNull String getValue() {
        return value;
    }

    /**
     * whether or not this field should display inline
     */
    public @Nullable Boolean isInline() {
        return inline;
    }

    /**
     * Creates a {@link Data} from this {@link Field}, useful to convert it to JSON
     */
    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(NAME_KEY, name);
        data.add(VALUE_KEY, value);
        if(inline != null) data.add(INLINE_KEY, inline);

        return data;
    }
}
