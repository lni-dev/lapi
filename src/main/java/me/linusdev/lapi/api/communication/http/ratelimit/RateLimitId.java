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

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.interfaces.Unique;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface RateLimitId extends Unique {

    static final LogInstance log = Logger.getLogger("StringIdentifier");

    /**
     * Identifier for links without any {@link PlaceHolder}.
     * @param query {@link Query}
     * @return {@link LinkIdentifier}
     */
    private static @NotNull RateLimitId newLinkOnlyIdentifier(@NotNull Query query) {
        return new LinkIdentifier(query.getLink());
    }

    /**
     * Identifier for links with {@link PlaceHolder}s.
     * @param query {@link Query}
     * @return {@link StringIdentifier}
     */
    private static @NotNull RateLimitId newCompleteLinkIdentifier(@NotNull Query query) {
        @NotNull PlaceHolder[] placeHolders = query.getPlaceHolders();

        int hash = query.getMethod().ordinal() +  query.getLink().uniqueId() * 10;
        StringBuilder id = new StringBuilder(hash);

        for (PlaceHolder placeHolder : placeHolders) {
            if (!placeHolder.getKey().isImportantForIdentifier()) {
                continue;
            }
            hash = 31 * hash + placeHolder.getValue().hashCode();
            id.append("_").append(placeHolder.getValue());
        }

        log.debugAlign("Created complete link id for query " + query.asString() + ":\nid=" + id + "\nhash=" + hash);

        return new StringIdentifier(id.toString(), hash);
    }

    /**
     * Identifier for links with a top level resource id
     * @param query {@link Query}
     * @return {@link TopLevelIdentifier}
     */
    private static @NotNull RateLimitId newTopLevelIdentifier(@NotNull Query query) {
        assert query.getLink().containsTopLevelResource();

        for(PlaceHolder p : query.getPlaceHolders()) {
            if(p.getKey().isTopLevelResource()) {
                log.debugAlign("Created top level id for query " + query.asString() + ":\nname=" + p.getKey() + "\nresource-id=" + p.getValue());
                return new TopLevelIdentifier(p.getValue(), p.getKey());
            }
        }

        throw new LApiIllegalStateException("Missing top level resource!");
    }

    /**
     * A {@link RateLimitId} for a shared resource id.
     * @param query {@link Query}
     * @return the correct {@link RateLimitId} for given {@link Query}.
     */
    static @NotNull RateLimitId newSharedResourceIdentifier(@NotNull Query query) {
        return newCompleteLinkIdentifier(query);
    }

    /**
     *
     * @param query {@link Query}
     * @return the correct {@link RateLimitId} for given {@link Query}.
     */
    static @NotNull RateLimitId newIdentifier(@NotNull Query query) {
        if(query.getLink().containsTopLevelResource())
            return newTopLevelIdentifier(query);

        if(!query.getLink().containsPlaceholders())
            return newLinkOnlyIdentifier(query);

        return newCompleteLinkIdentifier(query);
    }

}
