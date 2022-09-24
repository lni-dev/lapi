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

import me.linusdev.lapi.api.communication.http.request.Method;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.interfaces.Unique;
import me.linusdev.lapi.api.other.placeholder.Concatable;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;

public interface RateLimitId extends Unique {

    static final LogInstance log = Logger.getLogger("StringIdentifier");

    static @NotNull RateLimitId newLinkIdentifier(@NotNull Query query) {
        return new LinkIdentifier(query.getLink());
    }

    static @NotNull RateLimitId newSharedResourceIdentifier(@NotNull Query query) {
        @NotNull Method method = query.getMethod();
        @NotNull Concatable[] concatables = query.getLink().getConcatables();
        @NotNull PlaceHolder[] placeHolders = query.getPlaceHolders();

        String idString;


        int hash = method.ordinal();
        int factor = 10;
        for (Concatable concatable : concatables) {
            if (concatable.isKey()) continue;
            hash += concatable.code() * factor;
            factor *= 100;
        }

        boolean genId = false;
        for (PlaceHolder placeHolder : placeHolders) {
            if (!placeHolder.getKey().isImportantForIdentifier()) {
                genId = true;
                continue;
            }
            hash = 31 * hash + placeHolder.getValue().hashCode();
        }

        if (genId) {
            StringBuilder id = new StringBuilder();
            int i = 0;
            for (Concatable concatable : concatables) {
                concatable.connect(id);
                if (concatable.isKey()) {
                    concatable.concat(id, placeHolders[i].getKey().isImportantForIdentifier() ? placeHolders[i].getValue() : "");
                    i++;
                } else {
                    concatable.concat(id);
                }
            }
            log.debugAlign("Created shared resource id for query " + query.asString() + ":\nid=" + id + "\nhash=" + hash);

            return new StringIdentifier(id.toString(), hash);
        } else {
            String id = query.asString();
            log.debugAlign("Created shared resource id for query " + id + ":\nid=" + id + "\nhash=" + hash);
            return new StringIdentifier(id, hash);
        }
    }

}
