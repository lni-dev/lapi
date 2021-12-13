package me.linusdev.discordbotapi.api.communication;

import java.time.Duration;


/**
 * @see <a href="https://discord.com/developers/docs/resources/application" target="_top">Resources<a/>
 * @see <a href="https://discord.com/developers/docs/reference" target="_top">References<a/>
 */
public class DiscordApiCommunicationHelper {

    public static Duration DEFAULT_TIMEOUT_DURATION = Duration.ofSeconds(10L);



    //https://discord.com/developers/docs/resources/user
    public static final String DISCORD_COM = "https://discord.com";

    /**
     * <a href="https://discord.com/developers/docs/reference#api-versioning" target="_top">
     * API Versioning
     * <a/>
     */
    public static final String O_DISCORD_API_VERSION_LINK = "https://discord.com/api/v9/";

    /**
     * <a href="https://discord.com/developers/docs/reference#image-formatting-image-base-url" target="_top">
     *     Image Base Url
     * </a>
     */
    public static final String O_DISCORD_CDN_LINK = "https://cdn.discordapp.com/";

    /**
     * <a href="https://discord.com/developers/docs/reference#authentication" target="_top">Authentication<a/>
     */
    public static final String ATTRIBUTE_AUTHORIZATION_NAME = "Authorization";
    public static final String ATTRIBUTE_AUTHORIZATION_VALUE = "Bot " + PlaceHolder.TOKEN;

    /**
     * <a href="https://discord.com/developers/docs/reference#user-agent" target="_top">User Agent<a/>
     */
    public static final String ATTRIBUTE_USER_AGENT_NAME = "User-Agent";
    public static final String ATTRIBUTE_USER_AGENT_VALUE = "DiscordBot (" + PlaceHolder.LAPI_URL +", " + PlaceHolder.LAPI_VERSION + ")";

    /**
     * Time from 1. January 1970 to the first second of 2015
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Discord Epoch<a/>
     */
    public static final long DISCORD_EPOCH = 1420070400000L;

}
