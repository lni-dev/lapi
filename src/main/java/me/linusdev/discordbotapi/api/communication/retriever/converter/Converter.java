package me.linusdev.discordbotapi.api.communication.retriever.converter;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;


public interface Converter<C, R> {
    R apply(LApi lApi, C c) throws InvalidDataException;
}
