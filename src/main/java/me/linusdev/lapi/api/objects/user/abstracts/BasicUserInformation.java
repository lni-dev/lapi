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

package me.linusdev.lapi.api.objects.user.abstracts;

import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BasicUserInformation extends SnowflakeAble, HasLApi {

    /**
     * the username (in front of the '#')
     */
    @NotNull
    String getUsername();

    /**
     * the Discriminator (the numbers after the '#')
     */
    @NotNull
    String getDiscriminator();

    /**
     * snowflake id of the user
     * It's easier to use {@link #getId()}
     */
    @NotNull
    Snowflake getIdAsSnowflake();

    /**
     * the id of the user as string
     */
    default @NotNull String getId(){
        return getIdAsSnowflake().asString();
    }

    /**
     * the avatar of the user as hash
     */
    @Nullable
    String getAvatarHash();

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofUserAvatar(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the avatar
     */
    default CDNImageRetriever getAvatar(int desiredSize, @NotNull AbstractFileType fileType){
        if(getAvatarHash() == null) throw new IllegalArgumentException("This user object has no avatar hash");
        return new CDNImageRetriever(CDNImage.ofUserAvatar(getLApi(), getId(), getAvatarHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofUserAvatar(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the avatar
     */
    default CDNImageRetriever getAvatar(@NotNull AbstractFileType fileType){
        if(getAvatarHash() == null) throw new IllegalArgumentException("This user object has no avatar hash");
        return new CDNImageRetriever(CDNImage.ofUserAvatar(getLApi(), getId(), getAvatarHash(), fileType));
    }

}
