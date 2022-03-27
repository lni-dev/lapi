/*
 * Copyright  2022 Linus Andera
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

import me.linusdev.lapi.api.lapiandqueue.Future;
import org.jetbrains.annotations.Nullable;

/**
 * This is an Error Object that contains a {@link Throwable}.<br>
 * This is used if an Exception cannot be thrown for whatever reason.<br>
 * For example in {@link Future}
 */
public class Error {
    private @Nullable final Throwable throwable;

    public Error(@Nullable Throwable t){
        this.throwable = t;
    }

    /**
     *
     * @return the {@link Throwable}, most likely an {@link Exception}
     */
    public @Nullable Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "" + throwable;
    }
}
