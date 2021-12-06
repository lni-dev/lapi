package me.linusdev.discordbotapi.api.communication.gateway.identify;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#identify-identify-connection-properties" target="_top">Identify Connection Properties</a>
 */
public class ConnectionProperties implements Datable {

    public static final String OS_KEY = "$os";
    public static final String BROWSER_KEY = "$browser";
    public static final String DEVICE_KEY = "$device";

    private final @NotNull String os;
    private final @NotNull String browser;
    private final @NotNull String device;

    /**
     *
     * @param os your operating system
     * @param browser your library name
     * @param device your library name
     */
    public ConnectionProperties(@NotNull String os, @NotNull String browser, @NotNull String device) {
        this.os = os;
        this.browser = browser;
        this.device = device;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ConnectionProperties}
     * @throws InvalidDataException if {@link #OS_KEY}, {@link #BROWSER_KEY} or {@link #DEVICE_KEY} are missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ConnectionProperties fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        String os = (String) data.get(OS_KEY);
        String browser = (String) data.get(BROWSER_KEY);
        String device = (String) data.get(DEVICE_KEY);

        if(os == null || browser == null || device == null){
            InvalidDataException.throwException(data, null, ConnectionProperties.class,
                    new Object[]{os, null, device},
                    new String[]{OS_KEY, BROWSER_KEY, DEVICE_KEY});
        }

        //noinspection ConstantConditions
        return new ConnectionProperties(os, browser, device);
    }

    /**
     * your operating system
     */
    public @NotNull String getOs() {
        return os;
    }

    /**
     * your library name
     */
    public @NotNull String getBrowser() {
        return browser;
    }

    /**
     * your library name
     */
    public @NotNull String getDevice() {
        return device;
    }

    @Override
    public Data getData() {
        Data data = new Data(3);

        data.add(OS_KEY, os);
        data.add(BROWSER_KEY, browser);
        data.add(DEVICE_KEY, device);

        return data;
    }
}
