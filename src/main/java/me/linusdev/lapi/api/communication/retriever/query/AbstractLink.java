/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.cdn.image.ImageLink;
import me.linusdev.lapi.api.communication.http.request.Method;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import org.jetbrains.annotations.NotNull;

/**
 * @see Link
 * @see ImageLink
 */
public interface AbstractLink {

    /**
     *
     * @return the {@link Method} for this Link
     */
    @NotNull Method getMethod();

    /**
     * Construct the {@link AbstractLink} with given {@link ApiVersion} and {@link PlaceHolder placeholders}. The required
     * Placeholders depend on the specific {@link AbstractLink}.
     * @param apiVersion {@link ApiVersion} to use when constructing the link.
     * @param placeHolders {@link PlaceHolder} to fill ids, hashes, ...
     * @return {@link String} built link (url)
     */
    public @NotNull String construct(@NotNull ApiVersion apiVersion, @NotNull PlaceHolder... placeHolders);

    /**
     *
     * @return Whether this endpoint is bound to the global rate limits for the bot.
     */
    boolean isBoundToGlobalRateLimit();
}
