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

package me.linusdev.lapi.api.objects.sticker;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     Represents a pack of standard stickers.
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/sticker#sticker-pack-object" target="_top">Sticker Pack Object</a>
 * @see Sticker
 */
public class StickerPack implements Datable {

    public static final String ID_KEY = "id";
    public static final String STICKERS_KEY = "stickers";
    public static final String NAME_KEY = "name";
    public static final String SKU_ID_KEY = "sku_id";
    public static final String COVER_STICKER_ID_KEY = "cover_sticker_id";
    public static final String DESCRIPTION_KEY = "description";
    public static final String BANNER_ASSET_ID_KEY = "banner_asset_id";

    private final @NotNull Snowflake id;
    private final @NotNull Sticker[] stickers;
    private final @NotNull String name;
    private final @NotNull Snowflake skuId;
    private final @Nullable Snowflake coverStickerId;
    private final @NotNull String description;
    private final @NotNull Snowflake bannerAssetId;

    /**
     *
     * @param id id of the sticker pack
     * @param stickers the stickers in the pack
     * @param name name of the sticker pack
     * @param skuId id of the pack's SKU
     * @param coverStickerId id of a sticker in the pack which is shown as the pack's icon
     * @param description description of the sticker pack
     * @param bannerAssetId id of the sticker pack's banner image
     */
    public StickerPack(@NotNull Snowflake id, @NotNull Sticker[] stickers, @NotNull String name, @NotNull Snowflake skuId, @Nullable Snowflake coverStickerId, @NotNull String description, @NotNull Snowflake bannerAssetId) {
        this.id = id;
        this.stickers = stickers;
        this.name = name;
        this.skuId = skuId;
        this.coverStickerId = coverStickerId;
        this.description = description;
        this.bannerAssetId = bannerAssetId;
    }

    /**
     * id as {@link Snowflake} of the sticker pack
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * id as {@link String} of the sticker pack
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * the stickers in the pack
     */
    public Sticker[] getStickers() {
        return stickers;
    }

    /**
     * name of the sticker pack
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * id as {@link Snowflake} of the pack's SKU
     */
    public @NotNull Snowflake getSkuIdAsSnowflake() {
        return skuId;
    }

    /**
     * id as {@link String} of the pack's SKU
     */
    public @NotNull String getSkuId() {
        return skuId.asString();
    }

    /**
     * id as {@link Snowflake} of a sticker in the pack which is shown as the pack's icon
     */
    public @Nullable Snowflake getCoverStickerIdAsSnowflake() {
        return coverStickerId;
    }

    /**
     * id as {@link String} of a sticker in the pack which is shown as the pack's icon
     */
    public @Nullable String getCoverStickerId() {
        if(coverStickerId == null) return null;
        return coverStickerId.asString();
    }

    /**
     * description of the sticker pack
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * id as {@link Snowflake} of the sticker pack's banner image
     */
    public @NotNull Snowflake getBannerAssetIdAsSnowflake() {
        return bannerAssetId;
    }

    /**
     * id as {@link String} of the sticker pack's banner image
     */
    public @NotNull String getBannerAssetId() {
        return bannerAssetId.asString();
    }

    /**
     *
     * @return {@link Data} for this {@link StickerPack}
     */
    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(7);

        data.add(ID_KEY, id);
        data.add(STICKERS_KEY, stickers);
        data.add(NAME_KEY, name);
        data.add(SKU_ID_KEY, skuId);
        if(coverStickerId != null) data.add(COVER_STICKER_ID_KEY, coverStickerId);
        data.add(DESCRIPTION_KEY, description);
        data.add(BANNER_ASSET_ID_KEY, bannerAssetId);

        return data;
    }
}
