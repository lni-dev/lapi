package me.linusdev.discordbotapi.api.objects.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-types" target="_top">Message Types</a>
 */
public enum MessageType {
    DEFAULT                                         (0, "default"),
    RECIPIENT_ADD                                   (1, "recipient add"),
    RECIPIENT_REMOVE                                (2, "recipient remove"),
    CALL                                            (3, "call"),
    CHANNEL_NAME_CHANGE                             (4, "channel name change"),
    CHANNEL_ICON_CHANGE                             (5, "channel icon change"),
    CHANNEL_PINNED_MESSAGE                          (6, "channel pinned message"),
    GUILD_MEMBER_JOIN                               (7, "guild member join"),
    USER_PREMIUM_GUILD_SUBSCRIPTION                 (8, "user premium guild subscription"),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_1          (9, "user premium guild subscription tier 1"),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_2          (10, "user premium guild subscription tier 2"),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_3          (11, "user premium guild subscription tier 3"),
    CHANNEL_FOLLOW_ADD                              (12, "channel follow add"),
    GUILD_DISCOVERY_DISQUALIFIED                    (14, "guild discovery disqualified"),
    GUILD_DISCOVERY_REQUALIFIED                     (15, "guild discovery requalified"),
    GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING    (16, "guild discovery grace period initial warning"),
    GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING      (17, "guild discovery grace period final warning"),
    THREAD_CREATED                                  (18, "thread created"),
    REPLY                                           (19, "reply"),
    CHAT_INPUT_COMMAND                              (20, "chat input command"),
    THREAD_STARTER_MESSAGE                          (21, "thread starter message"),
    GUILD_INVITE_REMINDER                           (22, "guild invite reminder"),
    CONTEXT_MENU_COMMAND                            (23, "context menu command"),
    ;

    private final int value;
    private final @NotNull String name;

    MessageType(int value, String name){
        this.value = value;
        this.name = name;
    }

    /**
     * the value of this {@link MessageType}
     */
    public int getValue() {
        return value;
    }

    /**
     * yes another useless {@link String}, that takes space on your drive
     * @return
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @param value
     * @return {@link MessageType} with given value or {@code null} if no such {@link MessageType} exists
     */
    public static @Nullable MessageType fromValue(int value){
        for(MessageType type : MessageType.values()){
            if(type.getValue() == value)
                return type;
        }
        return null;
    }

}
