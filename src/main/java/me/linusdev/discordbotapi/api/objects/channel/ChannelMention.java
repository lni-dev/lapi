package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

/**
 * This might be used in a {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message} to store information about
 * a mentioned Channel.
 * <br><br>
 * Note that this Object will not be changed after creation, even if the channel name changes or the channel is deleted
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-mention-object" target="_top">Channel Mention Object</a>
 */
public class ChannelMention implements Datable {

    public final static String ID_KEY = "id";
    public final static String GUILD_ID_KEY = "guild_id";
    public final static String TYPE_KEY = "type";
    public final static String NAME_KEY = "name";

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake guildId;
    private final @NotNull ChannelType type;
    private final @NotNull String name;

    public ChannelMention(@NotNull Snowflake id, @NotNull Snowflake guildId, @NotNull ChannelType type, @NotNull String name){
        this.id = id;
        this.guildId = guildId;
        this.type = type;
        this.name = name;
    }

    /**
     * Creates a {@link ChannelMention} from {@link Data}
     * Required fields are {@link #ID_KEY}, {@link #GUILD_ID_KEY}, {@link #TYPE_KEY} and {@link #NAME_KEY}
     *
     * @param data {@link Data} to create this {@link ChannelMention}
     * @throws InvalidDataException if a required field is missing
     */
    public ChannelMention(@NotNull Data data) throws InvalidDataException {
        Snowflake id = Snowflake.fromString((String) data.get(ID_KEY));
        Snowflake guildId = Snowflake.fromString((String) data.get(GUILD_ID_KEY));
        int type = ((Number) data.get(TYPE_KEY, -1)).intValue();
        String name = (String) data.get(NAME_KEY);

        if(id == null || guildId == null || type == -1 || name == null) {
            InvalidDataException exception = new InvalidDataException(data, "Cannot create ChannelMention from Data, cause one or more fields are Missing");

            if(id == null) exception.addMissingFields(ID_KEY);
            if(guildId == null) exception.addMissingFields(GUILD_ID_KEY);
            if(type == -1) exception.addMissingFields(TYPE_KEY);
            if(name == null) exception.addMissingFields(NAME_KEY);

            throw exception;
        }

        this.id = id;
        this.guildId = guildId;
        this.type = ChannelType.fromId(type);
        this.name = name;
    }

    /**
     * id of the channel as {@link Snowflake}
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * id of the channel as {@link String}
     */
    public String getId() {
        return id.asString();
    }

    /**
     * id as {@link Snowflake} of the guild containing the channel
     */
    public @NotNull Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * id as {@link String} of the guild containing the channel
     */
    public String getGuildId() {
        return guildId.asString();
    }

    /**
     * the type of channel
     * @see ChannelType
     */
    public @NotNull ChannelType getType() {
        return type;
    }

    /**
     * the name of the channel
     */
    public @NotNull String getName(){
        return name;
    }

    @Override
    public Data getData() {
        Data data = new Data(4);

        data.add(ID_KEY, id);
        data.add(GUILD_ID_KEY, guildId);
        data.add(TYPE_KEY, type);
        data.add(NAME_KEY, name);

        return data;
    }
}
