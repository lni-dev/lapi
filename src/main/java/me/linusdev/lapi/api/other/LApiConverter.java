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

package me.linusdev.lapi.api.other;

import me.linusdev.lapi.api.lapi.LApi;

/**
 *
 * @param <C> the convertible type
 * @param <R> the result type
 * @param <E> {@link Throwable} that might be thrown while converting
 */
public interface LApiConverter<C, R, E extends Throwable> {

    /**
     *
     * @param lApi {@link LApi} required or conversation
     * @param convertible the convertible
     * @return the result
     * @throws E exception might be thrown in conversion
     */
    R convert(LApi lApi, C convertible) throws E;
}
