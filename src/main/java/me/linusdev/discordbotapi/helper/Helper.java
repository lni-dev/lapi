package me.linusdev.discordbotapi.helper;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Helper {

    public static String DEBUG_PATH = "C:/Users/Linus/Desktop/DiscordBotApi/bot.jar";

    /**
     *
     * @return Path to the currently executed .jar file
     */
    @NotNull
    public static Path getJarPath() throws URISyntaxException {
        Path path = Paths.get(Helper.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!path.toString().endsWith(".jar")) {
            //we are in IntelliJJ run
            path = Paths.get(DEBUG_PATH);
        }
        return path;
    }
}
