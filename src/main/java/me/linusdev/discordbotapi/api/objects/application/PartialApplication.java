package me.linusdev.discordbotapi.api.objects.application;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is only used in the {@link me.linusdev.discordbotapi.api.communication.gateway.events.ReadyEvent ReadyEvent}
 *
 * @see me.linusdev.discordbotapi.api.communication.gateway.events.ReadyEvent ReadyEvent
 * @see Application
 */
public class PartialApplication implements ApplicationAbstract {

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @Nullable Integer flags;
    private final @Nullable ApplicationFlag[] flagsArray;

    /**
     *
     * @param lApi {@link LApi}
     * @param id the id of the app
     * @param flags the application's public flags
     */
    public PartialApplication(@NotNull LApi lApi, @NotNull Snowflake id, @Nullable Integer flags) {
        this.lApi = lApi;
        this.id = id;
        this.flags = flags;
        this.flagsArray = flags == null ? null : ApplicationFlag.getFlagsFromInteger(flags);
    }

    /**
     *
     * @param data with required fields
     * @return {@link Application} or {@code null} if data was {@code null}
     * @throws InvalidDataException if (id == null) == true
     */
    public static @Nullable PartialApplication fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if (data == null) return null;

        String id = (String) data.get(Application.ID_KEY);
        Number flags = (Number) data.get(Application.FLAGS_KEY);

        if(id == null){
            InvalidDataException.throwException(data, null, Application.class, new Object[]{id}, new String[]{Application.ID_KEY});
        }

        //noinspection ConstantConditions
        return new PartialApplication(lApi, Snowflake.fromString(id), flags == null ? null : flags.intValue());
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the application's public flags
     * @see #getFlagsArray()
     */
    public @Nullable Integer getFlags() {
        return flags;
    }

    /**
     * the application's public flags
     * @see ApplicationFlag
     */
    public ApplicationFlag[] getFlagsArray() {
        return flagsArray;
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(Application.ID_KEY, id);
        data.addIfNotNull(Application.FLAGS_KEY, flags);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
