package me.linusdev.discordbotapi.api.communication.cdn.image;

import me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.retriever.query.AbstractLink;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;

/**
 * Every of these links always as the {@link me.linusdev.discordbotapi.api.communication.PlaceHolder#FILE_ENDING file-ending placeholder}
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
    public @NotNull String getLink() {
        return link;
    }
}
