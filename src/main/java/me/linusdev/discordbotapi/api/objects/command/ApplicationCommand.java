package me.linusdev.discordbotapi.api.objects.command;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object" target="_top">Application Command Object</a>
 */
public class ApplicationCommand implements Datable {

    public static final String ID_KEY = "id";
    public static final String TYPE_KEY = "type";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String OPTIONS_KEY = "options";
    public static final String DEFAULT_PERMISSIONS_KEY = "default_permission";
    public static final String VERSION_KEY = "version";


    @Override
    public Data getData() {
        return null;
    }
}
