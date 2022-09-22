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
import org.jetbrains.annotations.NotNull;

public enum LinkPart implements Concatable {
    PREFIX("https://discord.com/api/v") {
        @Override
        public void concat(@NotNull StringBuilder sb, @NotNull Object... value) {
            sb.append(getString());
            if(value.length > 0) sb.append(value[0]);
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

    ME("@me"),
    BOT("bot"),

    CALLBACK("callback"),
    BULK_DELETE("bulk-delete"),
    CROSSPOST("crosspost"),
    TYPING("typing"),
    ACTIVE("active"),
    ARCHIVED("archived"),
    PUBLIC("public"),
    PRIVATE("private"),

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
}
