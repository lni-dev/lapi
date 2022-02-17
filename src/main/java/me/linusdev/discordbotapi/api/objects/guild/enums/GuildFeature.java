package me.linusdev.discordbotapi.api.objects.guild.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#guild-object-guild-features" target="_top">GuildImpl Features</a>
 */
public enum GuildFeature implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN("-1"),

    /**
     * guild has access to set an animated guild icon
     */
    ANIMATED_ICON("ANIMATED_ICON"),

    /**
     * guild has access to set a guild banner image
     */
    BANNER("BANNER"),

    /**
     * guild has access to use commerce features (i.e. create store channels)
     */
    COMMERCE("COMMERCE"),

    /**
     * guild can enable welcome screen, Membership Screening, stage channels and discovery, and receives community updates
     */
    COMMUNITY("COMMUNITY"),

    /**
     * guild is able to be discovered in the directory
     */
    DISCOVERABLE("DISCOVERABLE"),

    /**
     * guild is able to be featured in the directory
     */
    FEATURABLE("FEATURABLE"),

    /**
     * guild has access to set an invite splash background
     */
    INVITE_SPLASH("INVITE_SPLASH"),

    /**
     * guild has enabled Membership Screening
     */
    MEMBER_VERIFICATION_GATE_ENABLED("MEMBER_VERIFICATION_GATE_ENABLED"),

    /**
     * guild has enabled monetization
     */
    MONETIZATION_ENABLED("MONETIZATION_ENABLED"),

    /**
     * guild has increased custom sticker slots
     */
    MORE_STICKERS("MORE_STICKERS"),

    /**
     * guild has access to create news channels
     */
    NEWS("NEWS"),

    /**
     * guild is partnered
     */
    PARTNERED("PARTNERED"),

    /**
     * guild can be previewed before joining via Membership Screening or the directory
     */
    PREVIEW_ENABLED("PREVIEW_ENABLED"),

    /**
     * guild has access to create private threads
     */
    PRIVATE_THREADS("PRIVATE_THREADS"),

    /**
     * guild is able to set role icons
     */
    ROLE_ICONS("ROLE_ICONS"),

    /**
     * guild has access to the seven day archive time for threads
     */
    SEVEN_DAY_THREAD_ARCHIVE("SEVEN_DAY_THREAD_ARCHIVE"),

    /**
     * guild has access to the three day archive time for threads
     */
    THREE_DAY_THREAD_ARCHIVE("THREE_DAY_THREAD_ARCHIVE"),

    /**
     * guild has enabled ticketed events
     */
    TICKETED_EVENTS_ENABLED("TICKETED_EVENTS_ENABLED"),

    /**
     * guild has access to set a vanity URL
     */
    VANITY_URL("VANITY_URL"),

    /**
     * guild is verified
     */
    VERIFIED("VERIFIED"),

    /**
     * guild has access to set 384kbps bitrate in voice (previously VIP voice servers)
     */
    VIP_REGIONS("VIP_REGIONS"),

    /**
     * guild has enabled the welcome screen
     */
    WELCOME_SCREEN_ENABLED("WELCOME_SCREEN_ENABLED"),
    ;

    private final String value;

    GuildFeature(String value) {
        this.value = value;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable GuildFeature fromValue(@Nullable String value){
        if(value == null) return null;
        for(GuildFeature feature : GuildFeature.values()){
            if(feature.value.equals(value)) return feature;
        }

        return UNKNOWN;
    }

    /**
     *
     * @param values String array of GuildFeatures
     * @return {@link GuildFeature} array
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable GuildFeature[] fromValues(@Nullable String... values){
        if(values == null) return null;
        GuildFeature[] array = new GuildFeature[values.length];
        GuildFeature[] allFeatures = values();


        loop:
        for(int j = 0; j < values.length; j++){
            for(int i = 0; i < allFeatures.length; i++){
                if(allFeatures[i].value.equals(values[j])){
                    array[j] = allFeatures[i];

                    //move found feature to last, cause there are usually no duplicates in values.
                    //So we can find features, that would usually be at the end faster
                    GuildFeature temp = allFeatures[i];
                    allFeatures[i] = allFeatures[allFeatures.length-j-1];
                    allFeatures[allFeatures.length-j-1] = temp;
                    continue loop;
                }
            }
        }

        return array;
    }

    @Override
    public Object simplify() {
        return value;
    }
}
