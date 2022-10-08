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

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.lapi.api.other.placeholder.Concatable;
import me.linusdev.lapi.api.other.placeholder.Name;
import org.jetbrains.annotations.NotNull;

public enum LinkPart implements Concatable {
    CDN_PREFIX("https://cdn.discordapp.com/"),
    HTTP_PREFIX("https://discord.com/api/v") {
        @Override
        public void concat(@NotNull StringBuilder sb, @NotNull Object... value) {
            sb.append(getString());
            sb.append(value[0]);
        }
    },

    APPLICATIONS("applications"),
    COMMANDS("commands"),
    GUILDS("guilds"),
    PERMISSIONS("permissions"),
    INTERACTIONS("interactions"),
    AUDIT_LOGS("audit-logs"),
    CHANNEL("channels"),
    MESSAGES("messages"),
    REACTIONS("reactions"),
    INVITES("invites"),
    FOLLOWERS("followers"),
    PINS("pins"),
    RECIPIENTS("recipients"),
    THREADS("threads"),
    THREAD_MEMBERS("thread-members"),
    USERS("users"),
    CONNECTIONS("connections"),
    GATEWAY("gateway"),
    VOICE("voice"),
    REGIONS("regions"),
    GUILD_EVENTS("guild-events"),
    CHANNELS("channels"),
    AUTO_MODERATION("auto-moderation"),
    RULES("rules"),
    MEMBERS("members"),
    ROLES("roles"),
    BANS("bans"),
    INTEGRATIONS("integrations"),
    VOICE_STATES("voice-states"),
    SCHEDULED_EVENTS("scheduled-events"),
    TEMPLATES("templates"),
    STAGE_INSTANCES("stage-instances"),
    STICKER_PACKS("sticker-packs"),

    OAUTH2("oauth2"),

    ME("@me"),
    BOT("bot"),

    PREVIEW("preview"),
    NICK("nick"),
    MFA("mfa"),
    PRUNE("prune"),
    WIDGET("widget"),
    WIDGET_JSON("widget.json"),
    VANITY_URL("vanity-url"),
    WIDGET_PNG("widget.png"),
    WELCOME_SCREEN("welcome-screen"),
    MEMBER("member"),

    CALLBACK("callback"),
    BULK_DELETE("bulk-delete"),
    CROSSPOST("crosspost"),
    TYPING("typing"),
    ACTIVE("active"),
    ARCHIVED("archived"),
    PUBLIC("public"),
    PRIVATE("private"),
    SEARCH("search"),

    //CDN

    EMOJIS("emojis"),
    ICONS("icons"),
    SPLASHES("splashes"),
    DISCOVERY_SPLASHES("discovery-splashes"),
    BANNERS("banners"),
    AVATARS("avatars"),
    DEFAULT_AVATARS("embed/avatars"),
    APP_ICONS("app-icons"),
    APP_ASSETS("app-assets"),
    STICKER_PACK_BANNERS("app-assets/710982414301790216/store/"),
    STICKERS("stickers"),
    TEAM_ICONS("team-icons"),
    ROLE_ICONS("role-icons"),
    ACHIEVEMENTS("achievements"),

    ;

    private final @NotNull String value;

    LinkPart(@NotNull String value) {
        this.value = value;
    }

    @Override
    public @NotNull String getString() {
        return value;
    }

    @Override
    public void concat(@NotNull StringBuilder sb, @NotNull Object... value) {
        sb.append(this.value);
    }

    @Override
    public void connect(@NotNull StringBuilder sb) {
        sb.append('/');
    }

    @Override
    public int code() {
        return Name.amount + (ordinal() % (100-Name.amount));
    }
}
