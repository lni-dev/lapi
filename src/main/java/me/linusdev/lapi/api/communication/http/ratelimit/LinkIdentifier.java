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

package me.linusdev.lapi.api.communication.http.ratelimit;

import me.linusdev.lapi.api.communication.retriever.query.AbstractLink;
import org.jetbrains.annotations.NotNull;

public class LinkIdentifier implements RateLimitId {

    private final @NotNull AbstractLink link;

    public LinkIdentifier(@NotNull AbstractLink link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkIdentifier that = (LinkIdentifier) o;

        return link.uniqueId() == that.link.uniqueId();
    }

    @Override
    public int hashCode() {
        return link.uniqueId();
    }

    @Override
    public @NotNull Type getType() {
        return Type.LINK_LEVEL_UNIQUE;
    }
}
