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

package me.linusdev.lapi.api.async.conditioned;

import me.linusdev.lapi.api.async.Task;
import org.jetbrains.annotations.NotNull;


/**
 * A {@link ConditionedTask} is a {@link Task} that will only be executed after a certain {@link #getCondition() condition}
 * hast been met.
 *
 * @param <R> result
 * @param <S> secondary result
 */
public interface ConditionedTask<R, S> extends Task<R, S> {

    /**
     * @return {@link Condition} which must be met to start execution of this {@link Task}.
     */
    @NotNull Condition getCondition();

}
