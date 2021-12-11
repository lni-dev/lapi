package me.linusdev.discordbotapi.api.communication.gateway.enums;

import me.linusdev.data.SimpleDatable;

/**
 * TODO add @links
 *
 * * Thread Members Update contains different data depending on which intents are used.<br>
 * ** Guild Scheduled Event User Add and Guild Scheduled Event User Remove are currently experimental and not officially supported.
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#gateway-intents" target="_top">Gateway Intents</a>
 */
public enum GatewayIntent implements SimpleDatable {

    /**
     * <ul>
     *     <li>
     *         GUILD_CREATE
     *     </li>
     *     <li>
     *         GUILD_UPDATE
     *     </li>
     *     <li>
     *         GUILD_DELETE
     *     </li>
     *     <li>
     *         GUILD_ROLE_CREATE
     *     </li>
     *     <li>
     *         GUILD_ROLE_UPDATE
     *     </li>
     *     <li>
     *         GUILD_ROLE_DELETE
     *     </li>
     *     <li>
     *         CHANNEL_CREATE
     *     </li>
     *     <li>
     *         CHANNEL_UPDATE
     *     </li>
     *     <li>
     *         CHANNEL_DELETE
     *     </li>
     *     <li>
     *         CHANNEL_PINS_UPDATE
     *     </li>
     *     <li>
     *         THREAD_CREATE
     *     </li>
     *     <li>
     *         THREAD_UPDATE
     *     </li>
     *     <li>
     *         THREAD_DELETE
     *     </li>
     *     <li>
     *         THREAD_LIST_SYNC
     *     </li>
     *     <li>
     *         THREAD_MEMBER_UPDATE
     *     </li>
     *     <li>
     *         THREAD_MEMBERS_UPDATE {@link GatewayIntent *}
     *     </li>
     *     <li>
     *         STAGE_INSTANCE_CREATE
     *     </li>
     *     <li>
     *         STAGE_INSTANCE_UPDATE
     *     </li>
     *     <li>
     *         STAGE_INSTANCE_DELETE
     *     </li>
     * </ul>
     */
    GUILDS (1 << 0),

    /**
     *   <ul>
     *       <li>
     *           GUILD_MEMBER_ADD
     *       </li>
     *       <li>
     *           GUILD_MEMBER_UPDATE
     *       </li>
     *       <li>
     *           GUILD_MEMBER_REMOVE
     *       </li>
     *       <li>
     *           THREAD_MEMBERS_UPDATE {@link GatewayIntent *}
     *       </li>
     *   </ul>
     */
    GUILD_MEMBERS(1 << 1),

    /**
     * <ul>
     *     <li>
     *          GUILD_BAN_ADD
     *     </li>
     *     <li>
     *          GUILD_BAN_REMOVE
     *     </li>
     * </ul>
     */
    GUILD_BANS(1 << 2),

    /**
     * <ul>
     *     <li>
     *         GUILD_EMOJIS_UPDATE
     *     </li>
     *     <li>
     *         GUILD_STICKERS_UPDATE
     *     </li>
     * </ul>
     */
    GUILD_EMOJIS_AND_STICKERS(1 << 3),

    /**
     * <ul>
     *     <li>
     *         GUILD_INTEGRATIONS_UPDATE
     *     </li>
     *     <li>
     *         INTEGRATION_CREATE
     *     </li>
     *     <li>
     *         INTEGRATION_UPDATE
     *     </li>
     *     <li>
     *         INTEGRATION_DELETE
     *     </li>
     * </ul>
     */
    GUILD_INTEGRATIONS (1 << 4),

    /**
     * <ul>
     *     <li>
     *         WEBHOOKS_UPDATE
     *     </li>
     * </ul>
     */
    GUILD_WEBHOOKS (1 << 5),

    /**
     * <ul>
     *     <li>
     *         INVITE_CREATE
     *     </li>
     *     <li>
     *         INVITE_DELETE
     *     </li>
     * </ul>
     */
    GUILD_INVITES (1 << 6),

    /**
     * <ul>
     *     <li>
     *         VOICE_STATE_UPDATE
     *     </li>
     * </ul>
     */
    GUILD_VOICE_STATES (1 << 7),

    /**
     * <ul>
     *     <li>
     *         PRESENCE_UPDATE
     *     </li>
     * </ul>
     */
    GUILD_PRESENCES (1 << 8),

    /**
     * <ul>
     *     <li>
     *          MESSAGE_CREATE
     *     </li>
     *     <li>
     *          MESSAGE_UPDATE
     *     </li>
     *     <li>
     *          MESSAGE_DELETE
     *     </li>
     *     <li>
     *          MESSAGE_DELETE_BULK
     *     </li>
     * </ul>
     */
    GUILD_MESSAGES (1 << 9),

    /**
     * <ul>
     *     <li>
     *         MESSAGE_REACTION_ADD
     *     </li>
     *     <li>
     *         MESSAGE_REACTION_REMOVE
     *     </li>
     *     <li>
     *         MESSAGE_REACTION_REMOVE_ALL
     *     </li>
     *     <li>
     *         MESSAGE_REACTION_REMOVE_EMOJI
     *     </li>
     * </ul>
     */
    GUILD_MESSAGE_REACTIONS (1 << 10),

    /**
     * <ul>
     *     <li>
     *         TYPING_START
     *     </li>
     * </ul>
     */
    GUILD_MESSAGE_TYPING (1 << 11),

    /**
     * <ul>
     *     <li>
     *         MESSAGE_CREATE
     *     </li>
     *     <li>
     *         MESSAGE_UPDATE
     *     </li>
     *     <li>
     *         MESSAGE_DELETE
     *     </li>
     *     <li>
     *         CHANNEL_PINS_UPDATE
     *     </li>
     * </ul>
     */
    DIRECT_MESSAGES (1 << 12),

    /**
     *
     * <ul>
     *     <li>
     *         MESSAGE_REACTION_ADD
     *     </li>
     *     <li>
     *         MESSAGE_REACTION_REMOVE
     *     </li>
     *     <li>
     *         MESSAGE_REACTION_REMOVE_ALL
     *     </li>
     *     <li>
     *         MESSAGE_REACTION_REMOVE_EMOJI
     *     </li>
     * </ul>
     */
    DIRECT_MESSAGE_REACTIONS (1 << 13),

    /**
     * <ul>
     *     <li>
     *         TYPING_START
     *     </li>
     * </ul>
     */
    DIRECT_MESSAGE_TYPING (1 << 14),

    /**
     * <ul>
     *     <li>
     *          GUILD_SCHEDULED_EVENT_CREATE
     *     </li>
     *     <li>
     *          GUILD_SCHEDULED_EVENT_UPDATE
     *     </li>
     *     <li>
     *          GUILD_SCHEDULED_EVENT_UPDATE
     *     </li>
     *     <li>
     *          GUILD_SCHEDULED_EVENT_USER_ADD {@link GatewayIntent **}
     *     </li>
     *     <li>
     *          GUILD_SCHEDULED_EVENT_USER_REMOVE {@link GatewayIntent **}
     *     </li>
     * </ul>
     */
    GUILD_SCHEDULED_EVENTS (1 << 16),
    ;

    private final int value;

    GatewayIntent(int value) {
        this.value = value;
    }

    /**
     *
     * @param value string
     * @return {@link GatewayIntent} matching (ignores case) given value or {@code null} if none matches
     */
    public static GatewayIntent fromName(String value){
        for(GatewayIntent intent : GatewayIntent.values()){
            if(intent.toString().equalsIgnoreCase(value)) return intent;
        }

        return null;
    }

    /**
     *
     * @param flags int with set bits
     * @return {@link GatewayIntent} array of intents set in given int
     */
    public static GatewayIntent[] fromInt(int flags){
        GatewayIntent[] intents = new GatewayIntent[Integer.bitCount(flags)];

        int i = 0;
        for(GatewayIntent intent : GatewayIntent.values()){
            if(intent.isSet(flags)) intents[i++] = intent;
        }

        return intents;
    }

    /**
     *
     * @param intents {@link GatewayIntent Intents} to set
     * @return int with all intent flags corresponding to the given array set
     */
    public static int toInt(GatewayIntent[] intents){
        int flags = 0;

        for(GatewayIntent intent : intents)
            flags = flags | intent.value;

        return flags;
    }

    /**
     *
     * @param flags with set bits
     * @return {@code true} if this GatewayIntent is set in given flags
     */
    public boolean isSet(int flags){
        return (flags & value) != 0;
    }

    @Override
    public Object simplify() {
        return toString();
    }
}
