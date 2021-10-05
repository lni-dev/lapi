package me.linusdev.discordbotapi.api.objects;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.objects.user.abstracts.BasicUserInformation;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.objects.user.User.*;

/**
 * This class only holds information and might not be up to date at all!
 * for example{@link #discriminator} could have been changed by the user.
 *
 * That is why it is recommended, to retrieve a {@link User}
 */
public class Recipient implements BasicUserInformation, SnowflakeAble, Datable {
    private final @NotNull String username;
    private final @NotNull String discriminator;
    private final @NotNull Snowflake id;
    private final @NotNull Avatar avatar;

    public Recipient(@NotNull String username, @NotNull String discriminator, @NotNull Snowflake id, @NotNull Avatar avatar){
        this.username = username;
        this.discriminator = discriminator;
        this.id = id;
        this.avatar = avatar;
    }

    public Recipient(@NotNull Data data) throws InvalidDataException {
        this.username = (String) data.getOrDefault(USERNAME_KEY, null);
        this.discriminator = (String) data.getOrDefault(DISCRIMINATOR_KEY, null);
        this.id = Snowflake.fromString((String) data.get(USER_ID_KEY));
        this.avatar = new Avatar((String) data.get(AVATAR_KEY));

        if(this.username == null)
            throw new InvalidDataException("field '" + USERNAME_KEY + "' missing or null in Recipient with id:" + getId());
        else if(this.discriminator == null)
            throw new InvalidDataException("field '" + DISCRIMINATOR_KEY + "' missing or null in Recipient with id:" + getId());
        else if(this.id == null)
            throw new InvalidDataException("field '" + USER_ID_KEY + "' missing or null in Recipient with id:" + getId());
        //TODO: Avatar null check?

    }

    @Override
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public @NotNull String getDiscriminator() {
        return discriminator;
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    @Override
    public @NotNull Avatar getAvatar() {
        return avatar;
    }

    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(USERNAME_KEY, username);
        data.add(DISCRIMINATOR_KEY, discriminator);
        data.add(USER_ID_KEY, id);
        data.add(AVATAR_KEY, avatar);

        return data;
    }
}
