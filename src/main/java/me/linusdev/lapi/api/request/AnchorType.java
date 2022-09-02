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

package me.linusdev.lapi.api.request;

import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import org.jetbrains.annotations.NotNull;

public enum AnchorType {
    /**
     * Retrieve objects around the given object
     */
    AROUND(LinkQuery.AROUND_KEY),

    /**
     * Retrieve objects before the given object
     */
    BEFORE(LinkQuery.BEFORE_KEY),

    /**
     * Retrieve objects after the given object
     */
    AFTER(LinkQuery.AFTER_KEY),
    ;

    private final @NotNull String queryStringKey;

    AnchorType(@NotNull String queryStringKey) {
        this.queryStringKey = queryStringKey;
    }

    public @NotNull String getQueryStringKey() {
        return queryStringKey;
    }
}
