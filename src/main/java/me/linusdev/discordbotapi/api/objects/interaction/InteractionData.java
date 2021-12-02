package me.linusdev.discordbotapi.api.objects.interaction;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.command.ApplicationCommandInteractionDataOption;
import me.linusdev.discordbotapi.api.objects.command.ApplicationCommandType;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentType;
import me.linusdev.discordbotapi.api.objects.message.component.selectmenu.SelectOption;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure" target="_top">Interaction Data Structure</a>
 */
public class InteractionData implements Datable, HasLApi {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String RESOLVED_KEY = "resolved";
    public static final String OPTIONS_KEY = "options";
    public static final String CUSTOM_ID_KEY = "custom_id";
    public static final String COMPONENT_TYPE_KEY = "component_type";
    public static final String VALUES_KEY = "values";
    public static final String TARGET_ID_KEY = "target_id";

    private final @NotNull LApi lApi;

    private final @NotNull String id;
    private final @NotNull String name;
    private final @NotNull ApplicationCommandType type;
    private final @Nullable ResolvedData resolved;
    private final @Nullable ArrayList<ApplicationCommandInteractionDataOption> options;
    private final @Nullable String customId;
    private final @Nullable ComponentType componentType;
    private final @Nullable ArrayList<SelectOption> values;
    private final @Nullable Snowflake targetId;

    public InteractionData(@NotNull LApi lApi, @NotNull String id, @NotNull String name, @NotNull ApplicationCommandType type, @Nullable ResolvedData resolved, @Nullable ArrayList<ApplicationCommandInteractionDataOption> options, @Nullable String customId, @Nullable ComponentType componentType, @Nullable ArrayList<SelectOption> values, @Nullable Snowflake targetId) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.type = type;
        this.resolved = resolved;
        this.options = options;
        this.customId = customId;
        this.componentType = componentType;
        this.values = values;
        this.targetId = targetId;
    }

    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
