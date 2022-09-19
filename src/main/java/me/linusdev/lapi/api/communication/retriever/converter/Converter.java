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

package me.linusdev.lapi.api.communication.retriever.converter;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;


/**
 *
 * This interface is used to convert from {@link C} to {@link R}.
 * The converting process can throw an {@link InvalidDataException}, because
 * {@link C the convertible} is usually a {@link SOData}.
 *
 * @param <C> the convertible-type
 * @param <R> the result-type to convert to
 */
public interface Converter<C, R> {
    R convert(LApi lApi, C c) throws InvalidDataException;
}
