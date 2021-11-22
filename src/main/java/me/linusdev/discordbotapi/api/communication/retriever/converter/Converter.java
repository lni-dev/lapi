package me.linusdev.discordbotapi.api.communication.retriever.converter;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;


/**
 *
 * This interface is used to convert from {@link C} to {@link R}.
 * The converting process can throw an {@link InvalidDataException}, because
 * {@link C the convertible} is usually a {@link me.linusdev.data.Data Data}.
 *
 * @param <C> the convertible-type
 * @param <R> the result-type to convert to
 */
public interface Converter<C, R> {
    R convert(LApi lApi, C c) throws InvalidDataException;
}
