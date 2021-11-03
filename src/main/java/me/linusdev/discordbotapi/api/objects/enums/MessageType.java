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
    CALL                                            (3, ""),
    CHANNEL_NAME_CHANGE                             (4, ""),
    CHANNEL_ICON_CHANGE                             (5, ""),
    CHANNEL_PINNED_MESSAGE                          (6, ""),
    GUILD_MEMBER_JOIN                               (7, ""),
    USER_PREMIUM_GUILD_SUBSCRIPTION                 (8, ""),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_1          (9, ""),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_2          (10, ""),
    USER_PREMIUM_GUILD_SUBSCRIPTION_TIER_3          (11, ""),
    CHANNEL_FOLLOW_ADD                              (12, ""),
    GUILD_DISCOVERY_DISQUALIFIED                    (14, ""),
    GUILD_DISCOVERY_REQUALIFIED                     (15, ""),
    GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING    (16, ""),
    GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING      (17, ""),
    THREAD_CREATED                                  (18, ""),
    REPLY                                           (19, ""),
    CHAT_INPUT_COMMAND                              (20, ""),
    THREAD_STARTER_MESSAGE                          (21, ""),
    GUILD_INVITE_REMINDER                           (22, ""),
    CONTEXT_MENU_COMMAND                            (23, ""),
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
