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

package me.linusdev.lapi.api.manager.guild.emoji;

import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmojisUpdate {

    private final List<EmojiObject> old;
    private final List<EmojiObject> updated;
    private final List<EmojiObject> added;
    private final List<EmojiObject> removed;

    public EmojisUpdate(List<EmojiObject> old, List<EmojiObject> updated, List<EmojiObject> added, List<EmojiObject> removed) {
        this.old = old;
        this.updated = updated;
        this.added = added;
        this.removed = removed;
    }

    /**
     * if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS}
     * and {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_EMOJI_ON_UPDATE_EVENT COPY_EMOJI_ON_UPDATE_EVENT}
     * is enabled, emojis will be copied into this {@link List} before they are updated.<br><br>
     *
     * If no {@link EmojiObject emojis} have been updated or
     * {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_EMOJI_ON_UPDATE_EVENT COPY_EMOJI_ON_UPDATE_EVENT} is
     * disabled this will return {@code null}
     */
    public @Nullable List<EmojiObject> getOld() {
        return old;
    }

    /**
     * if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS}
     * is enabled, updated emojis will be in this {@link List}<br><br>
     * If no {@link EmojiObject emojis} have been updated, this will return {@code null}
     */
    public @Nullable List<EmojiObject> getUpdated() {
        return updated;
    }

    /**
     * If no {@link EmojiObject emojis} have been added, this will return {@code null}
     */
    public @Nullable List<EmojiObject> getAdded() {
        return added;
    }

    /**
     * if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_EMOJIS CACHE_EMOJIS}
     * is enabled, updated emojis will be in this {@link List}<br><br>
     * If no {@link EmojiObject emojis} have been removed or
     * {@link me.linusdev.lapi.api.config.ConfigFlag#COPY_EMOJI_ON_UPDATE_EVENT COPY_EMOJI_ON_UPDATE_EVENT} is
     * disabled this will return {@code null}
     */
    public @Nullable List<EmojiObject> getRemoved() {
        return removed;
    }
}
