package me.linusdev.discordbotapi.api.objects.message.component;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.message.component.actionrow.ActionRow;
import me.linusdev.discordbotapi.api.objects.message.component.button.Button;
import me.linusdev.discordbotapi.api.objects.message.component.selectmenu.SelectMenu;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     Message components—we'll call them "components" moving forward—are a framework for adding interactive elements
 *     to the messages your app or bot sends. They're accessible, customizable, and easy to use.
 * </p>
 * <p>
 *     There are several different types of components; this documentation will outline the basics of this new framework and each example.
 * </p>
 * <br>
 * <h2 style="margin-bottom:0;padding-bottom:0">
 *     <a href="https://discord.com/developers/docs/interactions/message-components#what-is-a-component" target="_top">
 *         What is a Component
 *     </a>
 * </h2>
 * <p>
 *     Components are a new field on the {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message message object}, so you can use them whether you're sending messages or responding to a slash command or other interaction
 *     The top-level components field is an array of {@link ActionRow Action Row} components.
 * </p>
 * <br>
 * <h2 style="margin-bottom:0;padding-bottom:0">
 *     <a href="https://discord.com/developers/docs/interactions/message-components#custom-id" target="_top">
 *         Custom ID
 *     </a>
 * </h2>
 * <p>
 *     Components, aside from {@link ActionRow Action Rows}, must have a custom_id field ({@link Button#getCustomId(), {@link SelectMenu#getCustomId()}}).
 *     This field is defined by the developer (yes, YOU) when sending the component payload,
 *     and is returned in the {@link me.linusdev.discordbotapi.api.objects.toodo.Interaction interaction} payload sent when a user interacts with the component.
 *     For example, if you set custom_id: click_me on a button,
 *     you'll receive an {@link me.linusdev.discordbotapi.api.objects.toodo.Interaction interaction} containing custom_id: click_me when a user clicks that button.
 * </p>
 * <p>
 *     custom_id must be unique per component; multiple buttons on the same message must not share the same custom_id.
 *     This field is a string of max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters, and can be used flexibly to maintain state or pass through other important data.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#component-object" target="_top">Component Object</a>
 * @see ActionRow
 * @see Button
 * @see SelectMenu
 */
public interface Component extends Datable {

    public static final String TYPE_KEY = "type";
    public static final String CUSTOM_ID_KEY = "custom_id";
    public static final String DISABLED_KEY = "disabled";
    public static final String STYLE_KEY = "style";
    public static final String LABEL_KEY = "label";
    public static final String EMOJI_KEY = "emoji";
    public static final String URL_KEY = "url";
    public static final String OPTION_KEY = "option";
    public static final String PLACEHOLDER_KEY = "placeholder";
    public static final String MIN_VALUES_KEY = "min_values";
    public static final String MAX_VALUES_KEY = "max_values";
    public static final String COMPONENTS_KEY = "components";

    /**
     *
     * @param data {@link Data} to create Component
     * @return {@link ActionRow}, {@link Button}, {@link SelectMenu} or {@link UnknownComponent}. Depending on {@link #getType()} stored in given data
     * @throws InvalidDataException if {@link #TYPE_KEY} is missing or null
     */
    public static @NotNull Component fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        Number type = (Number) data.get(TYPE_KEY);

        if(type == null){
            InvalidDataException.throwException(data, null, Component.class, new Object[]{type}, new String[]{TYPE_KEY});
            return null; //this will never happen, because above method will throw an Exception
        }

        int typeInt = type.intValue();

        if(typeInt == ComponentType.ACTION_ROW.getValue()){
            return ActionRow.fromData(lApi, data);

        }else if(typeInt == ComponentType.BUTTON.getValue()){
            return Button.fromData(lApi, data);

        }else if(typeInt == ComponentType.SELECT_MENU.getValue()){
            return SelectMenu.fromData(lApi, data);
        }

        return UnknownComponent.fromData(data);
    }

    /**
     * {@link ComponentType component type}
     */
    @NotNull ComponentType getType();

}
