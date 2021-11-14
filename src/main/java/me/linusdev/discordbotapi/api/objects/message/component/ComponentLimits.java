package me.linusdev.discordbotapi.api.objects.message.component;

/**
 * Component Limits
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#component-object-component-structure" target="_top">
 *      Component Strcuture
 *     </a>
 */
public final class ComponentLimits {

    public static final int LABEL_MAX_CHARS = 80;
    public static final int CUSTOM_ID_MAX_CHARS = 100;
    public static final int SELECT_OPTIONS_MAX = 25;
    public static final int PLACEHOLDER_MAX_CHARS = 100;
    public static final int MIN_VALUE_FIELD_MIN = 0;
    public static final int MIN_VALUE_FIELD_MAX = 25;
    public static final int MAX_VALUE_FIELD_MAX = 25;
    public static final int ACTION_ROW_MAX_CHILD_COMPONENTS = 5;
}