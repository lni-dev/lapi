package me.linusdev.discordbotapi.api.objects.integration;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#integration-account-object-integration-account-structure" target="_top"></a>
 */
public class IntegrationAccount implements Datable {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";


    private final @NotNull String id;
    private final @NotNull String name;

    /**
     *
     * @param id id of the account
     * @param name name of the account
     */
    public IntegrationAccount(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link IntegrationAccount}
     * @throws InvalidDataException if {@link #ID_KEY} or {@link #NAME_KEY} are missing or {@code null}
     */
    @Contract("!null -> !null")
    @SuppressWarnings("ConstantConditions")
    public static @Nullable IntegrationAccount fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);

        if(id == null || name == null){
            InvalidDataException.throwException(data, null, IntegrationAccount.class,
                    new Object[]{id, name},
                    new String[]{ID_KEY, NAME_KEY});
        }

        return new IntegrationAccount(id, name);
    }

    /**
     * id of the account
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * name of the account
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return {@link Data}
     */
    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);

        return data;
    }
}
