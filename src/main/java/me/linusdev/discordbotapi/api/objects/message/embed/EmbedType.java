package me.linusdev.discordbotapi.api.objects.message.embed;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * Embed types are "loosely defined" and, for the most part, are not used by our clients for rendering. Embed attributes power what is rendered. Embed types should be considered deprecated and might be removed in a future API version.
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-types" target="_top">Embed Types</a>
 */
public enum EmbedType implements SimpleDatable {
    /**
     * generic embed rendered from embed attributes
     */
    RICH("rich"),

    /**
     * image embed
     */
    IMAGE("image"),

    /**
     * video embed
     */
    VIDEO("video"),

    /**
     * animated gif image embed rendered as a video embed
     */
    GIFV("gifv"),

    /**
     * article embed
     */
    ARTICLE("article"),

    /**
     * link embed
     */
    LINK("link"),
    ;

    private final @NotNull String type;

    EmbedType(String type){
        this.type = type;
    }

    /**
     *
     * @param type String of Type
     * @return {@link EmbedType} corresponding to given type or {@link #RICH} if no {@link EmbedType} matched
     */
    public static EmbedType fromTypeString(String type){
        for(EmbedType embedType : values()){
            if(embedType.type.equalsIgnoreCase(type)){
                return embedType;
            }
        }

        return RICH;
    }

    @Override
    public String toString() {
        return type;
    }

    /**
     * String used in Json from Discord
     */
    public String asTypeString(){
        return type;
    }

    @Override
    public Object simplify() {
        return asTypeString();
    }
}
