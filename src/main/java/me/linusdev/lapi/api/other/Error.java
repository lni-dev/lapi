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

import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.response.body.ErrorMessage;
import me.linusdev.lapi.api.lapiandqueue.Future;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is an Error Object that contains a {@link Throwable}.<br>
 * This is used if an Exception cannot be thrown for whatever reason.<br>
 * For example in {@link Future}
 */
public class Error {
    private @Nullable final Throwable throwable;
    private final @Nullable ErrorMessage errorMessage;

    public Error(@Nullable Throwable t){
        this(t, null);
    }

    public Error(@Nullable ErrorMessage err){
        this(null, err);
    }

    public Error(@Nullable Throwable t, @Nullable ErrorMessage errorMessage){
        this.throwable = t;
        this.errorMessage = errorMessage;
    }

    /**
     *
     * @return the {@link Throwable}, most likely an {@link Exception}
     */
    public @NotNull Throwable getThrowable() {
        if(throwable == null && errorMessage == null) return new UnknownErrorException();
        if(throwable != null) return throwable;
        return new ErrorMessageException(errorMessage);
    }

    @Override
    public String toString() {
        return "" + throwable;
    }

    public static class UnknownErrorException extends LApiException {
        public UnknownErrorException() {
            super("Error, but no throwable or error message given");
        }
    }

    public static class ErrorMessageException extends LApiException {
        private final @NotNull ErrorMessage errorMessage;
        public ErrorMessageException(@NotNull ErrorMessage errorMessage) {
            super("ErrorMessage from Discord: code: " + errorMessage.getCode()
                    + ", message: " + errorMessage.getMessage() +
                    (errorMessage.getErrors() != null ? ", errors:" + errorMessage.getErrors().toJsonString() : ""));
            this.errorMessage = errorMessage;
        }

        public @NotNull ErrorMessage getErrorMessage() {
            return errorMessage;
        }
    }

}
