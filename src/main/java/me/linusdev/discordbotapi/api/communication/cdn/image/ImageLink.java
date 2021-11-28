package me.linusdev.discordbotapi.api.communication.cdn.image;

import me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.retriever.query.AbstractLink;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;

/**
 * @see <a href="https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints" target="_top">CDN ENdpoints</a>
 */
public enum ImageLink implements AbstractLink {

    CUSTOM_EMOJI                    ("emojis/" + EMOJI_ID + "." + FILE_ENDING),

    GUILD_ICON                      ("icons/" + GUILD_ID + "/" + HASH + "." + FILE_ENDING),
    GUILD_SPLASH                    ("splashes/" + GUILD_ID + "/" + HASH + "." + FILE_ENDING),
    GUILD_DISCOVERY_SPLASH          ("discovery-splashes/" + GUILD_ID + "/" + HASH + "." + FILE_ENDING),
    GUILD_BANNER                    ("banners/" + GUILD_ID + "/" + HASH + "." + FILE_ENDING),

    GUILD_MEMBER_AVATAR             ("guilds/" + GUILD_ID + "/users/" + USER_ID + "/avatars/" + HASH + "." + FILE_ENDING),
    USER_BANNER                     ("banners/" + USER_ID + "/" + HASH + "." + FILE_ENDING),
    DEFAULT_USER_AVATAR             ("embed/avatars/" + USER_DISCRIMINATOR + "." + FILE_ENDING),
    USER_AVATAR                     ("avatars/" + USER_ID + "/" + HASH + "." + FILE_ENDING),

    APPLICATION_ICON                ("app-icons/" + APPLICATION_ID + "/" + HASH + "." + FILE_ENDING),
    APPLICATION_COVER               ("app-icons/" + APPLICATION_ID + "/" + HASH + "." + FILE_ENDING),
    APPLICATION_ASSET               ("app-assets/" + APPLICATION_ID + "/" + HASH + "." + FILE_ENDING),

    ACHIEVEMENT_ICON                ("app-assets/" + APPLICATION_ID + "/achievements/" + ACHIEVEMENT_ID + "/icons/" + HASH + "." + FILE_ENDING),

    STICKER_PACK_BANNER             ("app-assets/710982414301790216/store/" + STICKER_PACK_BANNER_ASSET_ID + "." + FILE_ENDING),
    STICKER                         ("stickers/" + STICKER_ID + "." + FILE_ENDING),

    TEAM_ICON                       ("team-icons/" + TEAM_ID + "/" + HASH + "." + FILE_ENDING),

    ROLE_ICON                       ("role-icons/" + ROLE_ID + "/" + HASH + "." + FILE_ENDING),
    ;

    private final String link;

    ImageLink(String link) {
        this.link = DiscordApiCommunicationHelper.O_DISCORD_CDN_LINK + link;
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
