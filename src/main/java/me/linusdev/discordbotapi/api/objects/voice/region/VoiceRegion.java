package me.linusdev.discordbotapi.api.objects.voice.region;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.updatable.Updatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/resources/voice#voice-region-object" target="_top">Voice Region Object</a>
 */
public class VoiceRegion implements Datable, Updatable {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String CUSTOM_KEY = "custom";
    public static final String DEPRECATED_KEY = "deprecated";
    public static final String OPTIMAL_KEY = "optimal";

    private @NotNull final String id;
    private String name;
    private boolean custom;
    private boolean deprecated;
    private boolean optimal;

    public VoiceRegion(@NotNull String id, String name, boolean custom, boolean deprecated, boolean optimal){
        this.id = id;
        this.name = name;
        this.custom = custom;
        this.deprecated = deprecated;
        this.optimal = optimal;
    }

    /**
     * Idk why one would use this, but its here anyways
     *
     * @return Data representing this Voice Region
     */
    @Override
    public Data getData() {
        Data data = new Data(5);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(CUSTOM_KEY, custom);
        data.add(DEPRECATED_KEY, deprecated);
        data.add(OPTIMAL_KEY, optimal);

        return data;
    }

    @Override
    public void updateSelfByData(Data data) {
        name = (String) data.get(NAME_KEY, name);
        custom = (boolean) data.get(CUSTOM_KEY, custom);
        deprecated = (boolean) data.get(DEPRECATED_KEY, deprecated);
        optimal = (boolean) data.get(OPTIMAL_KEY, optimal);
    }

    /**
     *
     * @param id
     * @return true if the given id matches {@link VoiceRegion#id}, false otherwise
     */
    public boolean equalsId(String id){
        return this.id.equals(id);
    }

    /**
     * unique ID for the region
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * name of the region
     */
    public String getName() {
        return name;
    }

    /**
     * whether this is a custom voice region (used for events/etc)
     */
    public boolean isCustom() {
        return custom;
    }

    /**
     * whether this is a deprecated voice region (avoid switching to these)
     */
    public boolean isDeprecated() {
        return deprecated;
    }

    /**
     * true for a single server that is closest to the current user's client
     */
    public boolean isOptimal() {
        return optimal;
    }

    /**
     *
     * If this returns {@code true} {@link #isCustom()}, {@link #isDeprecated()} and {@link #isOptimal()} must not be used
     * and {@link #getName()} will always return {@code null}
     *
     * @return {@code true} if this voice region was not retrieved from Discord!
     */
    public boolean isUnknown(){
        return name == null;
    }

    public static VoiceRegion fromData(Data data){
        return new VoiceRegion((String) data.get(ID_KEY), (String) data.get(NAME_KEY), (boolean) data.get(CUSTOM_KEY, false),
                (boolean) data.get(DEPRECATED_KEY, false), (boolean) data.get(OPTIMAL_KEY, false));
    }
}
