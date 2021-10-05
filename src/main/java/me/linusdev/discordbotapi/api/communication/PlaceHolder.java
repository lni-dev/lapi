package me.linusdev.discordbotapi.api.communication;

public class PlaceHolder {

    public static final String GUILD_ID = "<guild-id>";
    public static final String USER_ID = "<user-id>";
    public static final String CHANNEL_ID = "{channel.id}";
    public static final String TOKEN = "<token>";
    public static final String LAPI_URL = "<LApi-url>";
    public static final String LAPI_VERSION = "<LApi-version>";

    private final String name;
    private final String value;

    /**
     *
     * @param name {@link #GUILD_ID}, {@link #USER_ID}, {@link #CHANNEL_ID}, {@link #TOKEN}, {@link #LAPI_URL} or {@link +LAPI_VERSION}
     * @param value
     */
    public PlaceHolder(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String place(String in){
        return in.replace(name, value);
    }

}
