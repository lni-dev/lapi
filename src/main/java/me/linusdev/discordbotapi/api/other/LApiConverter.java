package me.linusdev.discordbotapi.api.other;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;

/**
 *
 * @param <C> the convertible type
 * @param <R> the result type
 * @param <E> {@link Throwable} that might be thrown while converting
 */
public interface LApiConverter<C, R, E extends Throwable> {

    /**
     *
     * @param lApi {@link LApi} required or conversation
     * @param convertible the convertible
     * @return the result
     * @throws E exception might be thrown in conversion
     */
    R convert(LApi lApi, C convertible) throws E;
}
