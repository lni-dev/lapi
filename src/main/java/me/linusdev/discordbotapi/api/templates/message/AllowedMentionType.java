package me.linusdev.discordbotapi.api.templates.message;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mention-types
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mention-types" target="_top">Allowed Mention Types</a>
 * @see
 */
public enum AllowedMentionType implements SimpleDatable {

    /**
     * Controls role mentions
     */
    ROLE_MENTIONS("roles"),

    /**
     * Controls user mentions
     */
    USER_MENTIONS("users"),

    /**
     * Controls @everyone and @here mentions
     */
    EVERYONE_MENTIONS("everyone"),
    ;

    private final @NotNull String value;

    AllowedMentionType(@NotNull String value){
        this.value = value;
    }

    public @NotNull String getValue() {
        return value;
    }

    @Override
    @NotNull
    public Object simplify() {
        return value;
    }
}
