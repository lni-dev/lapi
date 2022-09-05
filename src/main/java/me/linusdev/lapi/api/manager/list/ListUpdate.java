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

package me.linusdev.lapi.api.manager.list;

import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListUpdate<T>{

    private final List<T> old;
    private final List<T> updated;
    private final List<T> added;
    private final List<T> removed;

    public ListUpdate(List<T> old, List<T> updated, List<T> added, List<T> removed) {
        this.old = old;
        this.updated = updated;
        this.added = added;
        this.removed = removed;
    }

    /**
     * Cache Config and Copy config for {@link T} must be enabled. <br>
     * May be {@code null} if empty.
     */
    public @Nullable List<T> getOld() {
        return old;
    }

    /**
     * Cache Config for {@link T} must be enabled. <br>
     * May be {@code null} if empty.
     */
    public @Nullable List<T> getUpdated() {
        return updated;
    }

    /**
     * Cache Config for {@link T} must be enabled. <br>
     * May be {@code null} if empty.
     */
    public @Nullable List<T> getAdded() {
        return added;
    }

    /**
     * Cache Config and Copy config for {@link T} must be enabled. <br>
     * May be {@code null} if empty.
     */
    public @Nullable List<T> getRemoved() {
        return removed;
    }
}
