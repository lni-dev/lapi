/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.objects.other;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.communication.file.types.AbstractContentType;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.communication.file.types.ContentType;
import me.linusdev.lapi.api.exceptions.LApiIllegalStateException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * @see <a href="https://discord.com/developers/docs/reference#image-data">Discord Documentation</a>
 */
public class ImageData implements SimpleDatable {

    private final @NotNull AbstractContentType contentType;
    private final @NotNull String base64EncodedImageData;

    /**
     *
     * @param contentType {@link ContentType#IMAGE_JPEG}, {@link ContentType#IMAGE_PNG} or {@link ContentType#IMAGE_GIF}.
     * @param base64EncodedImageData base64 encoded image data.
     */
    public ImageData(@NotNull AbstractContentType contentType, @NotNull String base64EncodedImageData) {
        this.contentType = contentType;
        this.base64EncodedImageData = base64EncodedImageData;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ImageData of(@NotNull Path image) throws IOException {
        String fileName = image.getFileName().toString();
        int dotIndex = fileName.indexOf(".");

        if(dotIndex == -1)
            throw new LApiIllegalStateException("Given image path '" + image + "' has no file extension.");

        AbstractContentType contentType = AbstractFileType.of(fileName.substring(dotIndex + 1)).getContentType();

        if(contentType == null)
            throw new LApiIllegalStateException("Given image path '" + image + "' has an unknown file extension.");

        return of(contentType, image);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull ImageData of(@NotNull AbstractContentType contentType, @NotNull Path image) throws IOException {
        return new ImageData(contentType, Base64.getEncoder().encodeToString(Files.readAllBytes(image)));
    }

    public @NotNull AbstractContentType getContentType() {
        return contentType;
    }

    public @NotNull String getBase64EncodedImageData() {
        return base64EncodedImageData;
    }

    @Override
    public String simplify() {
        return "data:" + contentType.getContentTypeAsString() + ";base64," + base64EncodedImageData;
    }
}
