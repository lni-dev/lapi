package me.linusdev.discordbotapi.api.objects.user.connection;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/user#connection-object" target="_top">Connection Object</a>
 */
public class Connection implements Datable {

    //TODO

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String REVOKED_KEY = "revoked";
    public static final String INTEGRATIONS_KEY = "integrations";
    public static final String VERIFIED_KEY = "verified";
    public static final String FRIEND_SYNC_KEY = "friend_sync";
    public static final String SHOW_ACTIVITY_KEY = "show_activity";
    public static final String VISIBILITY_KEY = "visibility";



    @Override
    public Data getData() {
        Data data = new Data(9);
        return data;
    }
}
