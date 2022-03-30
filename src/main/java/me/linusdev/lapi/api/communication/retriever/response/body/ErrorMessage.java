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

package me.linusdev.lapi.api.communication.retriever.response.body;

import me.linusdev.data.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ErrorMessage {

    public static final String CODE_KEY = "code";
    public static final String ERRORS_KEY = "errors";
    public static final String MESSAGE_KEY = "message";

    private final @NotNull Long code;
    private final @Nullable Data errors;
    private final @Nullable String message;

    public ErrorMessage(@NotNull Long code, @Nullable Data errors, @Nullable String message) {
        this.code = code;
        this.errors = errors;
        this.message = message;
    }

    public static @Nullable ErrorMessage fromData(@Nullable Data data) {
        if(data == null) return null;

        Long code = (Long) data.get(CODE_KEY);

        if(code == null) return null;

        Data errors = (Data) data.get(ERRORS_KEY);
        String message = (String) data.get(MESSAGE_KEY);

        return new ErrorMessage(code, errors, message);
    }

    public @NotNull Long getCode() {
        return code;
    }

    public @Nullable Data getErrors() {
        return errors;
    }

    public @Nullable String getMessage() {
        return message;
    }
}
