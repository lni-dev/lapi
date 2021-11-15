package me.linusdev.discordbotapi.api.objects.user;

import me.linusdev.data.Data;
import org.jetbrains.annotations.NotNull;

/**
 * TODO has an additional partial member field! https://discord.com/developers/docs/resources/channel#message-object-message-structure
 * used in {@link me.linusdev.discordbotapi.api.objects.message.abstracts.Message}
 *
 */
public class UserMention extends User{

    public static @NotNull UserMention fromData(@NotNull Data data){
        return new UserMention();
    }

}
