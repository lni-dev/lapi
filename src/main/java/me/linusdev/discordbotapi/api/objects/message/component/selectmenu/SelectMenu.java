package me.linusdev.discordbotapi.api.objects.message.component.selectmenu;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentType;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentLimits;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SelectMenu implements Component {

    private final @NotNull ComponentType type;
    private final @NotNull String customId;
    private final @NotNull SelectOption[] options;
    private final @Nullable String placeholder;
    private final @Nullable Integer minValues;
    private final @Nullable Integer maxValues;
    private final @Nullable Boolean disabled;

    /**
     *
     * @param type {@link ComponentType#SELECT_MENU 3} for a select menu
     * @param customId a developer-defined identifier for the button, max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters
     * @param options the choices in the select, max {@value ComponentLimits#SELECT_OPTIONS_MAX}
     * @param placeholder custom placeholder text if nothing is selected, max {@value ComponentLimits#PLACEHOLDER_MAX_CHARS} characters
     * @param minValues the minimum number of items that must be chosen; default 1, min {@value ComponentLimits#MIN_VALUE_FIELD_MIN}, max {@value ComponentLimits#MIN_VALUE_FIELD_MAX}
     * @param maxValues the maximum number of items that can be chosen; default 1, max {@value ComponentLimits#MAX_VALUE_FIELD_MAX}
     * @param disabled disable the select, default false
     */
    public SelectMenu(@NotNull ComponentType type, @NotNull String customId, @NotNull SelectOption[] options,
                      @Nullable String placeholder, @Nullable Integer minValues, @Nullable Integer maxValues, @Nullable Boolean disabled){
        this.type = type;
        this.customId = customId;
        this.options = options;
        this.placeholder = placeholder;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.disabled = disabled;
    }

    public static @NotNull SelectMenu fromData(@NotNull Data data){
        return null;
    }

    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * a developer-defined identifier for the button, max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters
     */
    public @NotNull String getCustomId() {
        return customId;
    }

    /**
     * the choices in the select, max {@value ComponentLimits#SELECT_OPTIONS_MAX}
     */
    public @NotNull SelectOption[] getOptions() {
        return options;
    }

    /**
     * custom placeholder text if nothing is selected, max {@value ComponentLimits#PLACEHOLDER_MAX_CHARS} characters
     */
    public @Nullable String getPlaceholder() {
        return placeholder;
    }

    /**
     * the minimum number of items that must be chosen; default 1, min {@value ComponentLimits#MIN_VALUE_FIELD_MIN}, max {@value ComponentLimits#MIN_VALUE_FIELD_MAX}
     */
    public @Nullable Integer getMinValues() {
        return minValues;
    }

    /**
     * the maximum number of items that can be chosen; default 1, max {@value ComponentLimits#MAX_VALUE_FIELD_MAX}
     */
    public @Nullable Integer getMaxValues() {
        return maxValues;
    }

    /**
     * disable the select, default false
     */
    public @Nullable Boolean getDisabled() {
        return disabled;
    }

    /**
     * disable the select, default false
     *
     * @return false if {@link #getDisabled()} is {@code null}, {@link #getDisabled()} otherwise
     */
    public boolean isDisabled(){
        return !(disabled == null) && disabled;
    }

    /**
     *
     * @return {@link Data} for this {@link SelectMenu}
     */
    @Override
    public Data getData() {
        Data data = new Data(3);

        data.add(TYPE_KEY, type);
        data.add(CUSTOM_ID_KEY, customId);
        data.add(OPTION_KEY, options);
        if(placeholder != null) data.add(PLACEHOLDER_KEY, placeholder);
        if(minValues != null) data.add(MIN_VALUES_KEY, minValues);
        if(maxValues != null) data.add(MAX_VALUES_KEY, maxValues);
        if(disabled != null) data.add(DISABLED_KEY, disabled);

        return data;
    }
}
