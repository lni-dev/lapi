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

package me.linusdev.lapi.api.async.error;

import me.linusdev.lapi.api.async.exception.ErrorException;
import me.linusdev.lapi.api.communication.http.response.body.HttpErrorMessage;
import me.linusdev.lapi.log.LogInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Error {

    @Nullable Throwable getThrowable();

    default @NotNull Throwable asThrowable() {
        if(hasThrowable()) //noinspection ConstantConditions
            return getThrowable();
        return new ErrorException(this);
    }

    default boolean hasThrowable() {
        return getThrowable() != null;
    }

    @NotNull ErrorType getType();

    default @NotNull String getMessage() {
        if(!hasThrowable()) return "";
        return getThrowable().getMessage();
    }

    default void log(@NotNull LogInstance log) {
        log.error(getMessage());
        if(hasThrowable()) log.error(getThrowable());
    }

    static @NotNull Error of(HttpErrorMessage errorMessage) {
        return new Error() {
            @Override
            public @Nullable Throwable getThrowable() {
                return null;
            }

            @Override
            public @NotNull ErrorType getType() {
                return StandardErrorTypes.HTTP_ERROR_MESSAGE;
            }

            @Override
            public @NotNull String getMessage() {
                return "HttpErrorMessage from Discord: code: " + errorMessage.getCode()
                        + ", message: " + errorMessage.getMessage() +
                        (errorMessage.getErrors() != null ? ", errors:" + errorMessage.getErrors().toJsonString() : "");
            }
        };
    }

}
