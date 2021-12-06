package me.linusdev.discordbotapi.api.communication.gateway.activity;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-assets" target="_top">Activity Assets</a>
 */
public class ActivityAssets implements Datable {

    public static final String LARGE_IMAGE_KEY = "large_image";
    public static final String LARGE_TEXT_KEY = "large_text";
    public static final String SMALL_IMAGE_KEY = "small_image";
    public static final String SMALL_TEXT_KEY = "small_text";

    private final @Nullable String largeImage;
    private final @Nullable String largeText;
    private final @Nullable String smallImage;
    private final @Nullable String smallText;

    /**
     *
     * @param largeImage the id for a large asset of the activity, usually a snowflake
     * @param largeText text displayed when hovering over the large image of the activity
     * @param smallImage the id for a small asset of the activity, usually a snowflake
     * @param smallText text displayed when hovering over the small image of the activity
     */
    public ActivityAssets(@Nullable String largeImage, @Nullable String largeText, @Nullable String smallImage, @Nullable String smallText) {
        this.largeImage = largeImage;
        this.largeText = largeText;
        this.smallImage = smallImage;
        this.smallText = smallText;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ActivityAssets}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ActivityAssets fromData(@Nullable Data data){
        if(data == null) return null;
        String largeImage = (String) data.get(LARGE_IMAGE_KEY);
        String largeText = (String) data.get(LARGE_TEXT_KEY);
        String smallImage = (String) data.get(SMALL_IMAGE_KEY);
        String smallText = (String) data.get(SMALL_TEXT_KEY);

        return new ActivityAssets(largeImage, largeText, smallImage, smallText);
    }

    /**
     * the id for a large asset of the activity, usually a snowflake
     */
    public @Nullable String getLargeImage() {
        return largeImage;
    }

    /**
     * text displayed when hovering over the large image of the activity
     */
    public @Nullable String getLargeText() {
        return largeText;
    }

    /**
     * the id for a small asset of the activity, usually a snowflake
     */
    public @Nullable String getSmallImage() {
        return smallImage;
    }

    /**
     * text displayed when hovering over the small image of the activity
     */
    public @Nullable String getSmallText() {
        return smallText;
    }

    @Override
    public Data getData() {
        Data data = new Data(4);

        data.addIfNotNull(LARGE_IMAGE_KEY, largeImage);
        data.addIfNotNull(LARGE_TEXT_KEY, largeText);
        data.addIfNotNull(SMALL_IMAGE_KEY, smallImage);
        data.addIfNotNull(SMALL_TEXT_KEY, smallText);

        return data;
    }
}
