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

package me.linusdev.lapi.api.exceptions;

/**
 * Exceptions like this are thrown, if an Exception is thrown in some corner cases, but
 * catching it every time would be a hassle
 */
public class LApiUncheckedException extends RuntimeException {
    public LApiUncheckedException() {
    }

    public LApiUncheckedException(String message) {
        super(message);
    }

    public LApiUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LApiUncheckedException(Throwable cause) {
        super(cause);
    }

    public LApiUncheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
