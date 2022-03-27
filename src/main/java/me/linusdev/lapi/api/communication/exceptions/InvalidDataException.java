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

package me.linusdev.lapi.api.communication.exceptions;

import me.linusdev.data.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This exception is thrown if an Object is created from a {@link Data}, but the Data does not contain the necessary fields
 */
public class InvalidDataException extends LApiException{

    /**
     * @see #getMissingFields()
     */
    private final ArrayList<String> missingFields;

    /**
     * @see #getData()
     */
    private final @Nullable Data data;

    public InvalidDataException(@Nullable Data data) {
        this.data = data;
        this.missingFields = new ArrayList<>();
    }

    public InvalidDataException(@Nullable Data data, String message) {
        super(message);
        this.data = data;
        this.missingFields = new ArrayList<>();

    }

    public InvalidDataException(@Nullable Data data, String message, Throwable cause, String... missingFields) {
        super(message, cause);
        this.data = data;
        this.missingFields = new ArrayList<>();
        this.missingFields.addAll(Arrays.asList(missingFields));
    }

    /**
     * @param missingFields the fields missing in the {@link Data}
     * @return itself
     */
    public InvalidDataException addMissingFields(String... missingFields){
        this.missingFields.addAll(Arrays.asList(missingFields));
        return this;
    }

    /**
     * This may not contain all missing fields!
     *
     * @return {@link ArrayList} of keys as {@link String}, which are required but were missing in the {@link me.linusdev.data.Data}
     */
    public ArrayList<String> getMissingFields() {
        return missingFields;
    }

    /**
     * @return The {@link Data} where the fields are missing
     */
    public @Nullable Data getData() {
        return data;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage() + " Missing or null fields: ";

        for(String s : missingFields){
            msg += s + ", ";
        }

        msg += data != null ? "in data: \n" + data.getJsonString() + "\n": "";

        return msg;
    }

    /**
     *
     * @param data Invalid {@link Data}
     * @param message message to throw.
     * @param clazz class in which the {@link Data} was used in. this will be added to the message like this: ". Invalid Data while creating " + clazz.getSimpleName() + "."
     * @param checkIfNull array of Objects which may be null
     * @param keys array of keys corresponding to the Objects, will be added as {@link #addMissingFields(String...)} if Object with same index in checkIfNull array is null
     * @throws InvalidDataException always
     */
    public static void throwException(@NotNull Data data, @Nullable String message, @NotNull Class clazz, Object[] checkIfNull, @NotNull String[] keys) throws InvalidDataException {
        message = (message == null ? "" : message) + " Invalid Data while creating " + clazz.getSimpleName() + ".";
        InvalidDataException exception = new InvalidDataException(data, message);

        if(checkIfNull.length > keys.length) throw new IllegalArgumentException("checkIfNull array may not be bigger than keys array. One or more keys are missing!");

        int i = 0;
        for(Object o : checkIfNull){
            if(o == null) exception.addMissingFields(keys[i]);
            i++;
        }

        throw exception;
    }
}
