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

package me.linusdev.lapi.api.communication.http.request;

import me.linusdev.lapi.api.exceptions.LApiException;

public class LApiHttpRequestException extends LApiException {
    public LApiHttpRequestException() {
    }

    public LApiHttpRequestException(String message) {
        super(message);
    }

    public LApiHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
