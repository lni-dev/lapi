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

public interface ResultAndErrorConsumer<R, S> extends ResultConsumer<R, S> {
    @Override
    default void onError(@NotNull Error error, @NotNull Task<R, S> task, @NotNull S secondary) {
        consume(null, secondary, error);
    }

    @Override
    default void consume(@NotNull R result, @NotNull S secondary) {
        consume(result, secondary, null);
    }


    /**
     * One of result or error are always {@code null}. <br>
     * If result is not {@code null}, error will be {@code null}.<br>
     * If error is not {@code null}, result will be {@code null}.
     *
     * @param result {@link R} result
     * @param secondary {@link S} secondary result
     * @param error {@link Error}
     */
    void consume(R result, @NotNull S secondary, @Nullable Error error);
}
