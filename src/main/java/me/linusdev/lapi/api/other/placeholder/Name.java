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

/**
 *
 */
public enum Name implements Concatable{
    GUILD_ID("{guild.id}", true),
    CHANNEL_ID("{channel.id}", true),

    WEBHOOK_ID("{webhook.id}", true),
    WEBHOOK_TOKEN("{webhook.token}"),

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
    AUTO_MODERATION_RULE_ID("{auto_moderation_rule.id}"),
    INTEGRATION_ID("{integration.id}"),

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

        @Override
        public boolean isImportantForIdentifier() {
            return false;
        }
    },
    HASH("<hash>"),

    TOKEN("<token>"),

    LAPI_URL("<LApi-url>"),
    LAPI_VERSION("<LApi-version>"),

    DISCORD_API_VERSION_NUMBER("<vn>"),
    ;

    public static final int amount = values().length;

    private final @NotNull String placeholder;
    private final boolean topLevelResource;

    Name(@NotNull String placeholder) {
        this.placeholder = placeholder;
        this.topLevelResource = false;
    }

    Name(@NotNull String placeholder, boolean topLevelResource) {
        this.placeholder = placeholder;
        this.topLevelResource = topLevelResource;
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

    public boolean isImportantForIdentifier() {
        return true;
    }

    /**
     * Rate Limits often account for top-level resources withing the path
     * @return Whether this ia a top level resource id
     * @see <a href="https://discord.com/developers/docs/topics/rate-limits#rate-limits">Discord Documentation</a>
     */
    public boolean isTopLevelResource() {
        return topLevelResource;
    }

    /**
     * Unique for this class and for all {@link Concatable}.
     * @return int unique code
     * @see Concatable#code()
     */
    @Override
    public int code() {
        return ordinal();
    }
}
