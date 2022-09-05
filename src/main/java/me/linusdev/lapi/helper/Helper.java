/*
 * Copyright (c) 2021-2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.helper;

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

    public static Path getConfigPath() throws URISyntaxException {
        return getJarPath().getParent().resolve("config.json");
    }
}
