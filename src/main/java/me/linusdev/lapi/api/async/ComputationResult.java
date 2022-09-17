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

package me.linusdev.lapi.api.async;

import me.linusdev.lapi.api.async.error.Error;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComputationResult<R, S> {

    private final @Nullable R result;
    private final @NotNull S secondary;
    private final @Nullable Error error;

    public ComputationResult(@Nullable R result, @NotNull S secondary, @Nullable Error error) {
        this.result = result;
        this.secondary = secondary;
        this.error = error;
    }

    public boolean hasResult() {
        return result != null;
    }

    public R getResult() {
        return result;
    }

    public boolean hasError() {
        return error != null;
    }

    public Error getError() {
        return error;
    }

    public @NotNull S getSecondary() {
        return secondary;
    }
}
