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
import me.linusdev.lapi.api.communication.retriever.query.LinkPart;
import me.linusdev.lapi.api.other.placeholder.Concatable;
import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.communication.retriever.query.LinkPart.*;
import static me.linusdev.lapi.api.other.placeholder.Name.*;

/**
 * Every of these links always as the {@link Name#FILE_ENDING file-ending placeholder}
 *
 * @see <a href="https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints" target="_top">CDN ENdpoints</a>
 */
public enum ImageLink implements AbstractLink {

    CUSTOM_EMOJI                    (EMOJIS, EMOJI_ID),

    GUILD_ICON                      (ICONS, GUILD_ID, HASH),
    GUILD_SPLASH                    (SPLASHES, GUILD_ID, HASH),
    GUILD_DISCOVERY_SPLASH          (DISCOVERY_SPLASHES, GUILD_ID, HASH),
    GUILD_BANNER                    (BANNERS, GUILD_ID, HASH),
    GUILD_SCHEDULED_EVENT_COVER     (GUILD_EVENTS, SCHEDULED_EVENT_ID, HASH),

    GUILD_MEMBER_AVATAR             (GUILDS, GUILD_ID, USERS, USER_ID, AVATARS, HASH),
    GUILD_MEMBER_BANNER             (GUILDS, GUILD_ID, USERS, USER_ID, BANNERS, HASH),
    USER_BANNER                     (BANNERS, USER_ID, HASH),
    DEFAULT_USER_AVATAR             (DEFAULT_AVATARS, USER_DISCRIMINATOR),
    USER_AVATAR                     (AVATARS, USER_ID, HASH),

    APPLICATION_ICON                (APP_ICONS, APPLICATION_ID, HASH),
    APPLICATION_COVER               (APP_ICONS, APPLICATION_ID, HASH),
    APPLICATION_ASSET               (APP_ASSETS, APPLICATION_ID, HASH),

    ACHIEVEMENT_ICON                (APP_ASSETS, APPLICATION_ID, ACHIEVEMENTS, ACHIEVEMENT_ID, ICONS, HASH),

    STICKER_PACK_BANNER             (STICKER_PACK_BANNERS, STICKER_PACK_BANNER_ASSET_ID),
    STICKER                         (STICKERS, STICKER_ID),

    TEAM_ICON                       (TEAM_ICONS, TEAM_ID, HASH),

    ROLE_ICON                       (ROLE_ICONS, ROLE_ID, HASH),
    ;

    private final @NotNull Concatable[] concatables;

    ImageLink(Concatable... concatables) {
        this.concatables = concatables;
    }

    @Override
    public @NotNull Method getMethod() {
        return Method.GET;
    }

    @Override
    public @NotNull String construct(@NotNull ApiVersion apiVersion, @NotNull PlaceHolder... placeHolders) {
        StringBuilder sb = new StringBuilder();

        //The discord api version is not present in cdn links, so we do not need to replace it
        LinkPart.CDN_PREFIX.concat(sb);

        int i = 0;
        boolean first = true;
        for(Concatable concatable : concatables) {
            concatable.connect(sb);
            if(concatable.isKey()) {
                concatable.concat(sb, placeHolders[i++].getValue());
                //assert that the value was used for the correct placeHolder
                assert concatable == placeHolders[i-1].getKey();
            } else {
                concatable.concat(sb);
            }
        }

        FILE_ENDING.concat(sb, placeHolders[++i].getValue());
        assert FILE_ENDING == placeHolders[i].getKey();

        return sb.toString();
    }

    @Override
    public boolean isBoundToGlobalRateLimit() {
        return true;
    }
}
