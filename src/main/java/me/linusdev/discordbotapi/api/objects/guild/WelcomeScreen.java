package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#welcome-screen-object" target="_top">https://discord.com/developers/docs/resources/guild#welcome-screen-object</a>
 */
public class WelcomeScreen implements Datable {

    public static final String DESCRIPTION_KEY = "description";
    public static final String WELCOME_CHANNELS_KEY = "welcome_channels";

    private final @Nullable String description;
    private final @NotNull WelcomeScreenChannelStructure[] welcomeChannels;

    /**
     *
     * @param description the server description shown in the welcome screen
     * @param welcomeChannels the channels shown in the welcome screen, up to 5
     */
    public WelcomeScreen(@Nullable String description, @NotNull WelcomeScreenChannelStructure[] welcomeChannels) {
        this.description = description;
        this.welcomeChannels = welcomeChannels;
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(DESCRIPTION_KEY, description);
        data.add(WELCOME_CHANNELS_KEY, welcomeChannels);

        return data;
    }
}
