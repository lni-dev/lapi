/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.other.placeholder;

import org.jetbrains.annotations.NotNull;

public enum Name implements Concatable{
    GUILD_ID("{guild.id}"),
    CHANNEL_ID("{channel.id}"),
    ROLE_ID("{role.id}"),
    MESSAGE_ID("{message.id}"),
    APPLICATION_ID("{application.id}"),
    ACHIEVEMENT_ID("{achievement.id}"),
    STICKER_PACK_BANNER_ASSET_ID("{stickerpackbannerasset.id}"),
    STICKER_ID("{sticker.id}"),
    TEAM_ID("{team.id}"),
    OVERWRITE_ID("{overwrite.id}"),
    COMMAND_ID("{command.id}"),
    SCHEDULED_EVENT_ID("{guild_scheduled_event.id}"),

    INTERACTION_ID("{interaction.id}"),
    INTERACTION_TOKEN("{interaction.token}"),

    USER_ID("{user.id}"),
    USER_DISCRIMINATOR("{user.discriminator}"),

    EMOJI("{emoji}"),
    EMOJI_NAME("{emoji.name}"),
    EMOJI_ID("{emoji.id}"),

    TIMESTAMP("{timestamp}"),
    TIMESTAMP_STYLE("{timestamp.style}"),

    FILE_ENDING("<file-ending>") {
        @Override
        public void concat(@NotNull StringBuilder sb, @NotNull Object... value) {
            sb.append(value[0]);
        }

        @Override
        public void connect(@NotNull StringBuilder sb) {
            sb.append('.');
        }
    },
    HASH("<hash>"),

    TOKEN("<token>"),

    LAPI_URL("<LApi-url>"),
    LAPI_VERSION("<LApi-version>"),

    DISCORD_API_VERSION_NUMBER("<vn>"),
    ;

    private final @NotNull String placeholder;

    Name(@NotNull String placeholder) {
        this.placeholder = placeholder;
    }

    public @NotNull PlaceHolder withValue(@NotNull String value) {
        return new PlaceHolder(this, value);
    }

    public @NotNull String getPlaceholder() {
        return placeholder;
    }

    @Override
    public String toString() {
        return getPlaceholder();
    }

    @Override
    public @NotNull String getString() {
        return placeholder;
    }

    @Override
    public void concat(@NotNull StringBuilder sb, @NotNull Object... value) {
        sb.append(value[0]);
    }

    @Override
    public void connect(@NotNull StringBuilder sb) {
        sb.append('/');
    }

    @Override
    public boolean isKey() {
        return true;
    }
}
