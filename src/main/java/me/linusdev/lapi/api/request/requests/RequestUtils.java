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

package me.linusdev.lapi.api.request.requests;

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.interfaces.HasLApi;
import org.jetbrains.annotations.NotNull;

/**
 * Util class for some {@link me.linusdev.lapi.api.request.RequestFactory requests}.
 * Also contains some json keys.
 */
public class RequestUtils {

    public static final String MESSAGES = "messages";

    /**
     * @see Link#BULK_DELETE_MESSAGES
     */
    public static final int BULK_DELETE_MESSAGES_MAX_MESSAGE_COUNT = 100;

    /**
     * @see Link#BULK_DELETE_MESSAGES
     */
    public static final int BULK_DELETE_MESSAGES_MIN_MESSAGE_COUNT = 2;

    /**
     * 2 weeks.
     * @see Link#BULK_DELETE_MESSAGES
     */
    public static final long BULK_DELETE_MESSAGES_MAX_OLDNESS_MILLIS = 14L* 24L* 60L * 60L * 1000L;

    /**
     *
     * @param lApi {@link HasLApi}
     * @return current bot's application id as {@link String}
     * @throws LApiIllegalStateException if the application id is not cached.
     */
    public static @NotNull String getApplicationIdFromCache(@NotNull HasLApi lApi) {
        if(lApi.getLApi().getCache() == null)
            throw new LApiIllegalStateException("Application id is not cached, because config flag BASIC_CACHE is not enabled.");

        else if(lApi.getLApi().getCache().getCurrentApplicationId() == null)
            throw new LApiIllegalStateException("Application id is not cached, please wait for CACHE_READY or LAPI_READY. see LApi.waitUntilLApiReadyEvent().");

        return lApi.getLApi().getCache().getCurrentApplicationId();
    }

}
