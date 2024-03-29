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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StringIdentifier implements RateLimitId {



    private final String id;
    private final int hash;
    private final @NotNull Type type;

    public StringIdentifier(String id, int hash, @NotNull Type type) {
        this.id = id;
        this.hash = hash;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringIdentifier that = (StringIdentifier) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public @NotNull Type getType() {
        return type;
    }
}
