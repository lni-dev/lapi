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

package me.linusdev.lapi.api.communication.cdn.image;

import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.communication.retriever.query.AbstractLink;
import me.linusdev.lapi.api.communication.retriever.query.Query;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.file.types.FileType;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints" target="_top">CDN Endpoints</a>
 */
public interface CDNImage extends HasLApi {

    /**
     * The link to download this image. Might still have some {@link PlaceHolder placeholders} in it
     */
    AbstractLink getLink();

    /**
     *
     * @param desiredSize the size you want the image to be. see {@link ImageQuery#ImageQuery(LApi, AbstractLink, int, AbstractFileType, PlaceHolder...)} for restrictions
     * @return {@link Query} used to Retrieve the image
     */
    Query getQuery(int desiredSize);

    /**
     *
     * @param lApi {@link LApi}
     * @param link The link to download this image. Might still have some {@link PlaceHolder placeholders} in it
     * @param placeHolders The placeholders to place in the link
     * @return new {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage of(final @NotNull LApi lApi, final @NotNull AbstractLink link,
                                final @NotNull AbstractFileType fileType,
                                final @NotNull PlaceHolder... placeHolders){
        return new CDNImage() {
            @Override
            public @NotNull LApi getLApi() {
                return lApi;
            }

            @Override
            public AbstractLink getLink() {
                return link;
            }

            @Override
            public Query getQuery(int desiredSize) {
                return new ImageQuery(lApi, getLink(), desiredSize, fileType, placeHolders);
            }
        };
    }

    /**
     * <p>If the hash begins with "a_", it is available in GIF format. (example: "a_1269e74af4df7417b13759eae50c83dc")</p>
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG}, {@link FileType#WEBP} or {@link FileType#GIF}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, -> new", pure = true)
    static @NotNull CDNImage ofCustomEmoji(final @NotNull LApi lApi, final @NotNull String emojiId, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.CUSTOM_EMOJI, fileType, new PlaceHolder(PlaceHolder.EMOJI_ID, emojiId));
    }

    /**
     * <p>If the hash begins with "a_", it is available in GIF format. (example: "a_1269e74af4df7417b13759eae50c83dc")</p>
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG}, {@link FileType#WEBP} or {@link FileType#GIF}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofGuildIcon(final @NotNull LApi lApi, final @NotNull String guildId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.GUILD_ICON, fileType,
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofGuildSplash(final @NotNull LApi lApi, final @NotNull String guildId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.GUILD_SPLASH, fileType,
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofGuildDiscoverySplash(final @NotNull LApi lApi, final @NotNull String guildId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.GUILD_DISCOVERY_SPLASH, fileType,
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofGuildBanner(final @NotNull LApi lApi, final @NotNull String guildId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.GUILD_BANNER, fileType,
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     * <p>If the hash begins with "a_", it is available in GIF format. (example: "a_1269e74af4df7417b13759eae50c83dc")</p>
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG}, {@link FileType#WEBP} or {@link FileType#GIF}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofUserBanner(final @NotNull LApi lApi, final @NotNull String userId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.USER_BANNER, fileType,
                new PlaceHolder(PlaceHolder.USER_ID, userId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofDefaultUserAvatar(final @NotNull LApi lApi, final @NotNull String UserDiscriminator, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        final int discriminator = Integer.parseInt(UserDiscriminator) % 5;

        return of(lApi, ImageLink.DEFAULT_USER_AVATAR, fileType,
                new PlaceHolder(PlaceHolder.USER_DISCRIMINATOR, String.valueOf(discriminator)),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     * <p>If the hash begins with "a_", it is available in GIF format. (example: "a_1269e74af4df7417b13759eae50c83dc")</p>
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG}, {@link FileType#WEBP} or {@link FileType#GIF}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofUserAvatar(final @NotNull LApi lApi, final @NotNull String userId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.USER_AVATAR, fileType,
                new PlaceHolder(PlaceHolder.USER_ID, userId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     * <p>If the hash begins with "a_", it is available in GIF format. (example: "a_1269e74af4df7417b13759eae50c83dc")</p>
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG}, {@link FileType#WEBP} or {@link FileType#GIF}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofGuildMemberAvatar(final @NotNull LApi lApi, final @NotNull String guildId, final @NotNull String userId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.GUILD_MEMBER_AVATAR, fileType,
                new PlaceHolder(PlaceHolder.GUILD_ID, guildId),
                new PlaceHolder(PlaceHolder.USER_ID, userId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofApplicationIcon(final @NotNull LApi lApi, final @NotNull String applicationId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.ACHIEVEMENT_ICON, fileType,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofApplicationCover(final @NotNull LApi lApi, final @NotNull String applicationId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.APPLICATION_COVER, fileType,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofApplicationAsset(final @NotNull LApi lApi, final @NotNull String applicationId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.APPLICATION_ASSET, fileType,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofAchievementIcon(final @NotNull LApi lApi, final @NotNull String applicationId, final @NotNull String achievementId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.ACHIEVEMENT_ICON, fileType,
                new PlaceHolder(PlaceHolder.APPLICATION_ID, applicationId),
                new PlaceHolder(PlaceHolder.ACHIEVEMENT_ID, achievementId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, -> new", pure = true)
    static @NotNull CDNImage ofStickerPackBanner(final @NotNull LApi lApi, final @NotNull String stickerPackBannerAssetId, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.STICKER_PACK_BANNER, fileType,
                new PlaceHolder(PlaceHolder.STICKER_PACK_BANNER_ASSET_ID, stickerPackBannerAssetId));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofTeamIcon(final @NotNull LApi lApi, final @NotNull String teamId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.TEAM_ICON, fileType,
                new PlaceHolder(PlaceHolder.TEAM_ID, teamId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

    /**
     *
     * fileType must be {@link FileType#PNG PNG} if {@link Sticker#getFormatType() format_type} is
     * {@link me.linusdev.lapi.api.objects.sticker.StickerFormatType#PNG PNG} or
     * {@link me.linusdev.lapi.api.objects.sticker.StickerFormatType#APNG APNG}.<br>
     * fileType must be {@link FileType#LOTTIE LOTTIE} if {@link Sticker#getFormatType() format_type} is
     * {@link me.linusdev.lapi.api.objects.sticker.StickerFormatType#LOTTIE LOTTIE}.<br>
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG} or {@link FileType#LOTTIE}.
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, -> new", pure = true)
    static @NotNull CDNImage ofSticker(final @NotNull LApi lApi, final @NotNull String stickerId, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.STICKER, fileType,
                new PlaceHolder(PlaceHolder.STICKER_ID, stickerId));
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param fileType {@link FileType#PNG}, {@link FileType#JPEG} or {@link FileType#WEBP}
     * @return {@link CDNImage}
     */
    @Contract(value = "_, _, _, _, -> new", pure = true)
    static @NotNull CDNImage ofRoleIcon(final @NotNull LApi lApi, final @NotNull String roleId, final @NotNull String hash, final @NotNull AbstractFileType fileType){
        return of(lApi, ImageLink.ROLE_ICON, fileType,
                new PlaceHolder(PlaceHolder.ROLE_ID, roleId),
                new PlaceHolder(PlaceHolder.HASH, hash));
    }

}
