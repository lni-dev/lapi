package me.linusdev.discordbotapi.api.objects.message.component.button;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#button-object-button-styles" target="_top">Button Styles</a>
 */
public enum ButtonStyle implements SimpleDatable {

    UNKNOWN(0),

    /**
     * Name: Primary <br>
     * Color: blurple <br>
     * Required field: {@link Button#getCustomId() custom_id} <br>
     */
    PRIMARY(1),

    /**
     * Name: Secondary <br>
     * Color: grey <br>
     * Required field: {@link Button#getCustomId() custom_id} <br>
     */
    SECONDARY(2),

    /**
     * Name: Success <br>
     * Color: green <br>
     * Required field: {@link Button#getCustomId() custom_id} <br>
     */
    SUCCESS(3),

    /**
     * Name: Danger <br>
     * Color: red <br>
     * Required field: {@link Button#getCustomId() custom_id} <br>
     */
    DANGER(4),

    /**
     * Name: Link <br>
     * Color: grey, navigates to a URL <br>
     * Required field: {@link Button#getUrl() url} <br>
     */
    Link(5),
    ;

    private final int value;

    ButtonStyle(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return corresponding {@link ButtonStyle} or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ButtonStyle fromValue(int value){
        for(ButtonStyle style : ButtonStyle.values()){
            if(style.value == value) return style;
        }

        return UNKNOWN;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
