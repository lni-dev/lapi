package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

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

    public static @Nullable WelcomeScreen fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        String description = (String) data.get(DESCRIPTION_KEY);
        ArrayList<WelcomeScreenChannelStructure> channels = data.getAndConvertArrayList(WELCOME_CHANNELS_KEY, (ExceptionConverter<Data, WelcomeScreenChannelStructure, InvalidDataException>) WelcomeScreenChannelStructure::fromData);


        return new WelcomeScreen(description, channels == null ? new WelcomeScreenChannelStructure[0] : channels.toArray(new WelcomeScreenChannelStructure[0]));
    }

    @Override
    public Data getData() {
        Data data = new Data(2);

        data.add(DESCRIPTION_KEY, description);
        data.add(WELCOME_CHANNELS_KEY, welcomeChannels);

        return data;
    }
}
