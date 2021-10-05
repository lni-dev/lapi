package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import org.jetbrains.annotations.Nullable;

public interface GuildTextChannelAbstract extends GuildChannel, TextChannel{

    /**
     * amount of seconds a user has to wait before sending another message (0-21600);
     * bots, as well as users with the permission manage_messages or manage_channel, are unaffected
     * //todo @link Permissions
     */
    int getRateLimitPerUser();

    /**
     * the channel topic (0-1024 characters)
     * {@code null} if no topic set
     */
    @Nullable
    String getTopic();
}
