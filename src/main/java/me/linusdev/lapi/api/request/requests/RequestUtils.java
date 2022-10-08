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
import me.linusdev.lapi.api.interfaces.HasLApi;
import org.jetbrains.annotations.NotNull;

public class RequestUtils {

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
