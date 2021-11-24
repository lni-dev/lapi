package me.linusdev.discordbotapi.api.templates.message.builder;

import me.linusdev.data.SimpleDatable;

public enum TimestampStyle implements SimpleDatable {

    /**
     * Example:	18:08
     */
    SHORT_TIME("t"),

    /**
     * Example:	18:08:29
     */
    LONG_TIME("T"),

    /**
     * Example: 24/11/2021
     */
    SHORT_DATE("d"),

    /**
     * Example: 25 April 2021
     */
    LONG_DATE("D"),

    /**
     * Example: 20 April 2021 16:20<br>
     * This is the default
     */
    SHORT_DATE_WITH_TIME("f"),

    /**
     * Example: Tuesday, 20 April 2021 16:20
     */
    LONG_DATE_WITH_TIME("F"),

    /**
     * Example: 2 months ago
     */
    RELATIVE_TIME("R"),

    ;

    private final String value;

    TimestampStyle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
