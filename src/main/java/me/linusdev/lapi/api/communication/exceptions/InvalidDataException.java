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

package me.linusdev.lapi.api.communication.exceptions;

import me.linusdev.data.AbstractData;
import me.linusdev.data.functions.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This exception is thrown if an Object is created from a {@link AbstractData}, but the Data does not contain the necessary fields
 */
public class InvalidDataException extends LApiException {

    public static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static final ExceptionSupplier<String, AbstractData<String, Object>, InvalidDataException> SUPPLIER =
            (data, key) -> {
                //skip the frame of this method
                //get the first frame of lapi
                //The others are of the json parser: data
                @Nullable StackWalker.StackFrame frame = STACK_WALKER.walk(s -> s.skip(1).dropWhile(f -> !f.getClassName().startsWith("me.linusdev.lapi")).findFirst().orElse(null));
                return new InvalidDataException(data, frame, key, null);
            };

    /**
     * @see #getData()
     */
    private final @Nullable AbstractData<String, Object> data;

    public InvalidDataException(@Nullable AbstractData<String, Object> data, @Nullable StackWalker.StackFrame frame, @Nullable String causeKey, @Nullable Throwable cause) {
        super(
                frame != null
                        ? String.format("Invalid data in %s (%s:%s). key %s is null.",
                                frame.getDeclaringClass().getSimpleName(), frame.getClassName(), frame.getLineNumber(), causeKey)
                        : causeKey != null ? String.format("Invalid data. Key %s is null.", causeKey) : "Invalid data.",
                cause
        );
        this.data = data;
    }

    public InvalidDataException(@Nullable AbstractData<String, Object> data, @Nullable String causeKey, @Nullable Throwable cause) {
        this(data, STACK_WALKER.walk(s -> s.skip(1).findFirst().orElse(null)), causeKey, cause);
    }

    public InvalidDataException(@Nullable AbstractData<String, Object> data, String message) {
        super(message);
        this.data = data;
    }

    @Deprecated
    public InvalidDataException(@Nullable AbstractData<String, Object> data, String message, Throwable cause, String... missingFields) {
        super(message, cause);
        this.data = data;
    }

    /**
     * @param missingFields the fields missing in the {@link AbstractData}
     * @return itself
     */
    @Deprecated
    public InvalidDataException addMissingFields(String... missingFields){
        return this;
    }

    /**
     * @return The invalid {@link AbstractData}.
     */
    public @Nullable AbstractData<String, Object> getData() {
        return data;
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder();
        if(super.getMessage() != null) msg.append(super.getMessage());
        if(data != null) msg.append("Data:\n").append(data.toJsonString()).append("\n");
        return msg.toString();
    }

    /**
     *
     * @param data Invalid {@link AbstractData}
     * @param message message to throw.
     * @param clazz class in which the {@link AbstractData} was used in. this will be added to the message like this: ". Invalid Data while creating " + clazz.getSimpleName() + "."
     * @param checkIfNull array of Objects which may be null
     * @param keys array of keys corresponding to the Objects, will be added as {@link #addMissingFields(String...)} if Object with same index in checkIfNull array is null
     * @throws InvalidDataException always
     */
    @Deprecated
    public static void throwException(@NotNull AbstractData<String, Object> data, @Nullable String message, @NotNull Class<?> clazz, Object[] checkIfNull, @NotNull String[] keys) throws InvalidDataException {
        String causeKey = null;
        int i = 0;
        for(Object o : checkIfNull){
            if(o == null) causeKey = keys[i];
            i++;
        }

        //will not be null, because this is not the main method
        @NotNull StackWalker.StackFrame frame = STACK_WALKER.walk(s -> s.skip(1).findFirst().orElse(null));
        throw new InvalidDataException(data, frame, causeKey, null);
    }
}
