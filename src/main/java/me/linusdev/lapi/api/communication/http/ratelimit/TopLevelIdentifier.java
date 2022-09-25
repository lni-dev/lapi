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

import me.linusdev.lapi.api.other.placeholder.Name;
import org.jetbrains.annotations.NotNull;

public class TopLevelIdentifier implements RateLimitId {

    private final @NotNull String topLevelId;
    private final @NotNull Name topLevelResource;

    public TopLevelIdentifier(@NotNull String topLevelId, @NotNull Name topLevelResource) {
        this.topLevelId = topLevelId;
        this.topLevelResource = topLevelResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopLevelIdentifier that = (TopLevelIdentifier) o;
        return topLevelResource == that.topLevelResource && topLevelId.equals(that.topLevelId);
    }

    @Override
    public int hashCode() {
        return topLevelId.hashCode() + topLevelResource.code();
    }
}
