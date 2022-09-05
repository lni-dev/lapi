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

package me.linusdev.lapi.api.objects.snowflake;

import org.jetbrains.annotations.NotNull;

public interface SnowflakeAble {

    /**
     * the snowflake-id as {@link Snowflake}, it's recommended to use {@link #getId()} instead
     */
    @NotNull
    Snowflake getIdAsSnowflake();

    /**
     * the snowflake-id as string
     */
    @NotNull
    default String getId(){
        return getIdAsSnowflake().asString();
    }
}
