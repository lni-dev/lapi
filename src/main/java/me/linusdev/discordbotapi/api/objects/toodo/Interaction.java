package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.interaction.InteractionType;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object" target="_top">Interaction</a>
 */
public class Interaction implements Datable, HasLApi, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String APPLICATION_KEY = "application_id";
    public static final String TYPE_KEY = "type";
    public static final String DATA_KEY = "data";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String MEMBER_KEY = "member";
    public static final String USER_KEY = "user";
    public static final String TOKEN_KEY = "token";
    public static final String VERSION_KEY = "version";
    public static final String MESSAGE_KEY = "message";

    private @NotNull LApi lApi;

    private @NotNull Snowflake id;
    private @NotNull Snowflake applicationId;
    private @NotNull InteractionType type;

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }

    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

}
