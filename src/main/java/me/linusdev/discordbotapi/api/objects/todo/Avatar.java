package me.linusdev.discordbotapi.api.objects.todo;

import me.linusdev.data.SimpleDatable;

/**
 * This class holds information about an avatar of a user
 *
 * todo animated avatar, link to img, etc difference between icon and avatar? default avatar
 * todo https://discord.com/developers/docs/reference#image-formatting-cdn-endpoints
 */
public class Avatar implements SimpleDatable {
    private final String hash;

    public Avatar(String hash){
        this.hash = hash;
    }

    @Override
    public Object simplify() {
        return hash;
    }
}
