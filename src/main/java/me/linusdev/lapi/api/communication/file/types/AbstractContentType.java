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

package me.linusdev.lapi.api.communication.file.types;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * represents a http content-type
 */
public interface AbstractContentType extends SimpleDatable {

    /**
     * the content-type as string for the http content-type header
     */
    @NotNull String getContentTypeAsString();

    @Override
    default Object simplify(){
        return getContentTypeAsString();
    }

    /**
     *
     * @param contentType1 contentType 1
     * @param contentType2 contentType 2
     * @return {@code true} if content type string is the same
     */
    static boolean equals(AbstractContentType contentType1, AbstractContentType contentType2) {
        return contentType1.getContentTypeAsString().equalsIgnoreCase(contentType2.getContentTypeAsString());
    }
}
