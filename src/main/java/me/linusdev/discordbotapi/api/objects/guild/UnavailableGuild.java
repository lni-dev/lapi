package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnavailableGuild implements Datable {

    private final @Nullable Boolean unavailable;
    private final @NotNull Snowflake id;


    public UnavailableGuild(@Nullable Boolean unavailable, @NotNull Snowflake id) {
        this.unavailable = unavailable;
        this.id = id;
    }

    @Contract("!null -> !null; null -> null")
    public static @Nullable UnavailableGuild fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        String id = (String) data.get(Guild.ID_KEY);
        Boolean unavailable = (Boolean) data.get(Guild.UNAVAILABLE_KEY);

        if(id == null) {
            InvalidDataException.throwException(data, null, UnavailableGuild.class, new Object[]{null}, new String[]{Guild.ID_KEY});
        }

        //noinspection ConstantConditions
        return new UnavailableGuild(unavailable, Snowflake.fromString(id));
    }

    public @Nullable Boolean getUnavailable() {
        return unavailable;
    }

    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    public @NotNull String getId() {
        return id.asString();
    }

    @Override
    public Data getData() {
        Data data = new Data(2);
        data.add(Guild.ID_KEY, id);
        data.add(Guild.UNAVAILABLE_KEY, unavailable);
        return data;
    }
}
