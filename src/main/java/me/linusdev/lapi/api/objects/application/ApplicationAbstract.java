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

package me.linusdev.lapi.api.objects.application;

import me.linusdev.data.Datable;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;

public interface ApplicationAbstract extends HasLApi, Datable {

    /**
     * 	the id as {@link Snowflake} of the app
     */
    @NotNull Snowflake getIdAsSnowflake();

    /**
     * 	the id as {@link String} of the app
     */
    @NotNull default String getId() {
        return getIdAsSnowflake().asString();
    }

}
