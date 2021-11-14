package me.linusdev.discordbotapi.api.objects.message.component;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;

/**
 * This does not actually exist in Discord! But will be created if Discord adds a new type, not yet supported by this api
 */
public class UnknownComponent implements Component{

    private final @NotNull ComponentType type;
    private final int typeInt;
    private final @NotNull Data data;

    /**
     *
     * @param data data of this Component
     * @param type {@link ComponentType#UNKNOWN}
     * @param typeInt type as int
     */
    public UnknownComponent(@NotNull Data data, @NotNull ComponentType type, int typeInt){
        this.data = data;
        this.type = type;
        this.typeInt = typeInt;
    }

    /**
     *
     * @param data {@link Data} for this Component
     * @return {@link UnknownComponent}
     * @throws InvalidDataException if {@link #TYPE_KEY} is null or missing
     */
    public static @NotNull UnknownComponent fromData(@NotNull Data data) throws InvalidDataException {
        Number type = (Number) data.get(TYPE_KEY);

        if(type == null){
            InvalidDataException.throwException(data, null, UnknownComponent.class, new Object[]{type}, new String[]{TYPE_KEY});
            return null; // this will never happen, because above method will throw an exception
        }

        return new UnknownComponent(data, ComponentType.fromValue(type.intValue()), type.intValue());
    }

    /**
     * will always be {@link ComponentType#UNKNOWN}
     */
    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * int value of this Components type
     */
    public int getTypeInt() {
        return typeInt;
    }

    /**
     * Data of this component, to read all fields
     */
    @Override
    public @NotNull Data getData() {
        return data;
    }
}
