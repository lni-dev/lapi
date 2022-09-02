/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.communication;

public class PlaceHolder {

    public static final String GUILD_ID = "{guild.id}";
    public static final String CHANNEL_ID = "{channel.id}";
    public static final String ROLE_ID = "{role.id}";
    public static final String MESSAGE_ID = "{message.id}";
    public static final String APPLICATION_ID = "{application.id}";
    public static final String ACHIEVEMENT_ID = "{achievement.id}";
    public static final String STICKER_PACK_BANNER_ASSET_ID = "{stickerpackbannerasset.id}";
    public static final String STICKER_ID = "{sticker.id}";
    public static final String TEAM_ID = "{team.id}";
    public static final String OVERWRITE_ID = "{overwrite.id}";
    public static final String COMMAND_ID = "{command.id}";

    public static final String INTERACTION_ID = "{interaction.id}";
    public static final String INTERACTION_TOKEN = "{interaction.token}";

    public static final String USER_ID = "{user.id}";
    public static final String USER_DISCRIMINATOR = "{user.discriminator}";

    public static final String EMOJI = "{emoji}";
    public static final String EMOJI_NAME = "{emoji.name}";
    public static final String EMOJI_ID = "{emoji.id}";

    public static final String TIMESTAMP = "{timestamp}";
    public static final String TIMESTAMP_STYLE = "{timestamp.style}";

    public static final String FILE_ENDING = "<file-ending>";
    public static final String HASH = "<hash>";

    public static final String TOKEN = "<token>";
    public static final String LAPI_URL = "<LApi-url>";
    public static final String LAPI_VERSION = "<LApi-version>";

    public static final String DISCORD_API_VERSION_NUMBER = "<vn>";

    private final String name;
    private final String value;

    /**
     *
     * @param name {@link #GUILD_ID}, {@link #USER_ID}, {@link #CHANNEL_ID}, {@link #TOKEN}, {@link #LAPI_URL} or {@link #LAPI_VERSION}
     * @param value the value the placeholder should be replaced with
     */
    public PlaceHolder(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String place(String in){
        return in.replace(name, value);
    }

    public String getValue() {
        return value;
    }
}
