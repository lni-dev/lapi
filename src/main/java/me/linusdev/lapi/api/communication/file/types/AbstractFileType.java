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

package me.linusdev.lapi.api.communication.file.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbstractFileType {

    /**
     * The {@link AbstractContentType} for this file type, if available
     */
    @Nullable AbstractContentType getContentType();

    /**
     *
     * @return array of possible file-endings for this file-type without a dot ("."). Must contain at least one ending!
     */
    @NotNull String[] getFileEndings();

    default @NotNull String getFirstFileEnding() {
        return getFileEndings()[0];
    }

    /**
     *
     * @param fileEnding the file ending
     * @return matching {@link FileType} or a new {@link AbstractFileType} with given file ending.
     */
    @SuppressWarnings("UnnecessaryModifier")
    public static @NotNull AbstractFileType of(@NotNull String fileEnding) {
        for(FileType f : FileType.values()) {
            for(String ending : f.getFileEndings()) {
                if(ending.equalsIgnoreCase(fileEnding))
                    return f;
            }
        }

        return new AbstractFileType() {
            @Override
            public @Nullable AbstractContentType getContentType() {
                return null;
            }

            @Override
            public @NotNull String[] getFileEndings() {
                return new String[]{fileEnding};
            }
        };
    }
}
