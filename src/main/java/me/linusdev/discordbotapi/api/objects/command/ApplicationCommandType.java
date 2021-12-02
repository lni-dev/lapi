package me.linusdev.discordbotapi.api.objects.command;

import me.linusdev.data.SimpleDatable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-types" target="_top">Application Command Types</a>
 */
public enum ApplicationCommandType implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(0),

    /**
     * Slash commands; a text-based command that shows up when a user types /
     */
    CHAT_INPUT(1),

    /**
     * A UI-based command that shows up when you right click or tap on a user
     */
    USER(2),

    /**
     * A UI-based command that shows up when you right click or tap on a message
     */
    MESSAGE(3),

    ;


    private final int value;

    ApplicationCommandType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
