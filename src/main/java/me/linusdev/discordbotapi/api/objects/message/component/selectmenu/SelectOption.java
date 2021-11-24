package me.linusdev.discordbotapi.api.objects.message.component.selectmenu;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentLimits;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-option-structure" target="_top">Select option</a>
 */
public class SelectOption implements Datable, HasLApi {

    public static final String LABEL_KEY = "label";
    public static final String VALUE_KEY = "value";
    public static final String DESCRIPTION_KEY = "description";
    public static final String EMOJI_KEY = "emoji";
    public static final String DEFAULT_KEY = "default";

    private final @NotNull String label;
    private final @NotNull String value;
    private final @Nullable String description;
    private final @Nullable Emoji emoji;
    private final @Nullable Boolean default_;

    private final @NotNull LApi lApi;

    /**
     *
     * @param label the user-facing name of the option, max {@value ComponentLimits#SO_LABEL_MAX_CHARS} characters
     * @param value the dev-define value of the option, max {@value ComponentLimits#SO_VALUE_MAX_CHARS} characters
     * @param description an additional description of the option, max {@value ComponentLimits#SO_DESCRIPTION_MAX_CHARS} characters
     * @param emoji partial {@link Emoji emoji} object: id, name, and animated
     * @param default_ will render this option as selected by default
     */
    public SelectOption(@NotNull LApi lApi, @NotNull String label, @NotNull String value, @Nullable String description, @Nullable Emoji emoji, @Nullable Boolean default_){
        this.lApi = lApi;
        this.label = label;
        this.value = value;
        this.description = description;
        this.emoji = emoji;
        this.default_ = default_;
    }

    /**
     *
     * @param data {@link Data} with required fields
     * @return {@link SelectOption}
     * @throws InvalidDataException if {@link #LABEL_KEY} or {@link #VALUE_KEY} are missing
     */
    public static @NotNull SelectOption fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        String label = (String) data.get(LABEL_KEY);
        String value = (String) data.get(VALUE_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        Data emoji = (Data) data.get(EMOJI_KEY);
        Boolean default_ = (Boolean) data.get(DEFAULT_KEY);

        if(label == null || value == null){
            InvalidDataException.throwException(data, null, SelectOption.class, new Object[]{label, value}, new String[]{LABEL_KEY, VALUE_KEY});
            return null; // this will never happen, because above method will throw an Exception
        }

        return new SelectOption(lApi, label, value, description,
                emoji == null ? null : EmojiObject.fromData(lApi, data), default_);
    }

    /**
     * the user-facing name of the option, max {@value ComponentLimits#SO_LABEL_MAX_CHARS} characters
     */
    public @NotNull String getLabel() {
        return label;
    }

    /**
     * the dev-define value of the option, max {@value ComponentLimits#SO_VALUE_MAX_CHARS} characters
     */
    public @NotNull String getValue() {
        return value;
    }

    /**
     * an additional description of the option, max {@value ComponentLimits#SO_DESCRIPTION_MAX_CHARS} characters
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * partial {@link Emoji emoji} object: id, name, and animated
     */
    public @Nullable Emoji getEmoji() {
        return emoji;
    }

    /**
     * will render this option as selected by default
     */
    public @Nullable Boolean getDefault() {
        return default_;
    }

    /**
     * @return false if {@link #getDefault()} is {@code null}, {@link #getDefault()} otherwise
     */
    public boolean isDefault(){
        return !(default_ == null) && default_;
    }

    /**
     *
     * @return {@link Data} for this {@link SelectOption}
     */
    @Override
    public Data getData() {
        Data data = new Data(0);

        data.add(LABEL_KEY, label);
        data.add(VALUE_KEY, value);
        if(description != null) data.add(DESCRIPTION_KEY, description);
        if(emoji != null) data.add(EMOJI_KEY, emoji);
        if(default_ != null) data.add(DEFAULT_KEY, default_);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
