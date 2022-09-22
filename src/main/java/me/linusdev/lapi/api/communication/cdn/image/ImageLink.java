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

import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.lapi.api.communication.http.request.Method;
import me.linusdev.lapi.api.communication.retriever.query.AbstractLink;
import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.other.placeholder.Name.*;

/**
 * Every of these links always as the {@link Name#FILE_ENDING file-ending placeholder}
 *
 * @see <a href="https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints" target="_top">CDN ENdpoints</a>
 */
public enum ImageLink implements AbstractLink {

    CUSTOM_EMOJI                    ("emojis/" + EMOJI_ID),

    GUILD_ICON                      ("icons/" + GUILD_ID + "/" + HASH),
    GUILD_SPLASH                    ("splashes/" + GUILD_ID + "/" + HASH),
    GUILD_DISCOVERY_SPLASH          ("discovery-splashes/" + GUILD_ID + "/" + HASH),
    GUILD_BANNER                    ("banners/" + GUILD_ID + "/" + HASH),

    GUILD_MEMBER_AVATAR             ("guilds/" + GUILD_ID + "/users/" + USER_ID + "/avatars/" + HASH),
    USER_BANNER                     ("banners/" + USER_ID + "/" + HASH),
    DEFAULT_USER_AVATAR             ("embed/avatars/" + USER_DISCRIMINATOR),
    USER_AVATAR                     ("avatars/" + USER_ID + "/" + HASH),

    APPLICATION_ICON                ("app-icons/" + APPLICATION_ID + "/" + HASH),
    APPLICATION_COVER               ("app-icons/" + APPLICATION_ID + "/" + HASH),
    APPLICATION_ASSET               ("app-assets/" + APPLICATION_ID + "/" + HASH),

    ACHIEVEMENT_ICON                ("app-assets/" + APPLICATION_ID + "/achievements/" + ACHIEVEMENT_ID + "/icons/" + HASH),

    STICKER_PACK_BANNER             ("app-assets/710982414301790216/store/" + STICKER_PACK_BANNER_ASSET_ID),
    STICKER                         ("stickers/" + STICKER_ID),

    TEAM_ICON                       ("team-icons/" + TEAM_ID + "/" + HASH),

    ROLE_ICON                       ("role-icons/" + ROLE_ID + "/" + HASH),
    ;

    private final String link;

    ImageLink(String link) {
        this.link = DiscordApiCommunicationHelper.O_DISCORD_CDN_LINK + link  + "." + FILE_ENDING;
    }

    @Override
    public @NotNull Method getMethod() {
        return Method.GET;
    }

    @Override
    public @NotNull String getLink(@NotNull ApiVersion apiVersion) {
        //The discord api version is not present in cdn links, so we do not need to replace it
        return link;
    }

    @Override
    public @NotNull String construct(@NotNull ApiVersion apiVersion, @NotNull PlaceHolder... placeHolders) {
        //TODO: implement
        return null;
    }

    @Override
    public boolean isBoundToGlobalRateLimit() {
        return true;
    }
}
