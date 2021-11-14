package me.linusdev.discordbotapi.api.objects.message.component.actionrow;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentLimits;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * <p>
 *   An Action Row is a non-interactive container component for other types of components.
 *   It has a {@link #getType() type}: {@link ComponentType#ACTION_ROW 1} and a sub-array of
 *   {@link Component components} of other types.
 * </p>
 * <ul>
 *     <li>
 *          You can have up to {@value ComponentLimits#ACTION_ROW_MAX_CHILD_COMPONENTS} Action Rows per message
 *     </li>
 *     <li>
 *          An Action Row cannot contain another Action Row
 *     </li>
 * </ul>
 *
 *
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#action-rows" target="_top">
 *          Action Rows
 *     </a>
 */
public class ActionRow implements Datable, Component{

    private final @NotNull ComponentType type;
    private final @NotNull Component[] components; //TODO: check if this is really @NotNull

    /**
     *
     * @param type {@link ComponentType component type}
     * @param components a list of child components
     */
    public ActionRow(@NotNull ComponentType type, @NotNull Component[] components){
        this.type = type;
        this.components = components;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link ActionRow}
     * @throws InvalidDataException if {@link #TYPE_KEY} or {@link #COMPONENTS_KEY} are missing
     */
    public static @NotNull ActionRow fromData(@NotNull Data data) throws InvalidDataException {
        Number type = (Number) data.get(TYPE_KEY);
        ArrayList<Object> componentsData = (ArrayList<Object>) data.get(COMPONENTS_KEY);

        if(type == null || componentsData == null){
            InvalidDataException.throwException(data, null, ActionRow.class,
                    new Object[]{type, componentsData}, new String[]{TYPE_KEY, COMPONENTS_KEY});
            return null; //this will never happen, because above method will throw an Exception
        }

        Component[] components = new Component[componentsData.size()];
        int i = 0;
        for(Object o : componentsData){
            Data d = (Data) o;
            components[i++] = Component.fromData(d);
        }

        return new ActionRow(ComponentType.fromValue(type.intValue()), components);
    }

    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * a list of child components
     */
    public @NotNull Component[] getComponents() {
        return components;
    }

    /**
     *
     * @return {@link Data} representing this {@link ActionRow}
     */
    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(TYPE_KEY, type);
        data.add(COMPONENTS_KEY, components);

        return data;
    }
}
