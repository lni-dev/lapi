package me.linusdev.discordbotapi.api.objects.message.component;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *   An Action Row is a non-interactive container component for other types of components.
 *   It has a {@link #getType() type}: {@link ComponentType#ACTION_ROW 1} and a sub-array of
 *   {@link Component components} of other types.
 * </p>
 * <ul>
 *     <li>
 *          You can have up to 5 Action Rows per message
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

    private @NotNull ComponentType type;
    private @NotNull Component[] components; //TODO: check if this is really @NotNull

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

    @Override
    public Data getData() {
        Data data = new Data(2);
        return data;
    }
}
