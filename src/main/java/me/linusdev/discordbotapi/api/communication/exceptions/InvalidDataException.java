package me.linusdev.discordbotapi.api.communication.exceptions;

import me.linusdev.data.Data;

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
    private final Data data;

    public InvalidDataException(Data data) {
        this.data = data;
        this.missingFields = new ArrayList<>();
    }

    public InvalidDataException(Data data, String message) {
        super(message);
        this.data = data;
        this.missingFields = new ArrayList<>();

    }

    public InvalidDataException(Data data, String message, Throwable cause, String... missingFields) {
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
    public Data getData() {
        return data;
    }
}
