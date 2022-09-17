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

public interface Condition {

    /**
     * Check whether the condition is met. Once a condition is met, this method must return {@code true} until
     * the corresponding {@link Task} has completed execution.
     * @return {@code true}  if the condition is met. {@code false} otherwise.
     */
    boolean check();

    /**
     * Waits the current thread until the condition is met.
     * @throws InterruptedException if interrupted while waiting.
     */
    void await() throws InterruptedException;

}
