package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.discordbotapi.api.objects.enums.Permissions;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <a href="https://discord.com/developers/docs/topics/threads" target="_top"> discord documentation</a>
 */
public interface Thread extends TextChannel {

    /**
     * the name of the channel (1-100 characters)
     */
    @NotNull
    String getName();

    /**
     * for guild channels: id of the parent category for a channel
     * (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
     */
    @NotNull
    Snowflake getParentIdAsSnowflake();

    /**
     * for guild channels: id of the parent category for a channel
     * (each parent category can contain up to 50 channels), for threads: id of the text channel this thread was created
     */
    @NotNull default String getParentId(){
        return getParentIdAsSnowflake().asString();
    }

    /**
     * the id (as {@link Snowflake}) of the user that started the thread
     */
    @NotNull
    Snowflake getOwnerIdAsSnowflake();

    /**
     * the id of the user that started the thread
     */
    @NotNull default String getOwnerId(){
        return getParentIdAsSnowflake().asString();
    }

    /**
     *  stores an approximate count, but it stops counting at 50 (only used in our UI, so likely not valuable to bots)
     */
    int getMessageCount();

    /**
     *  stores an approximate count, but it stops counting at 50 (only used in our UI, so likely not valuable to bots)
     */
    int getMemberCount();

    /**
     * amount of seconds a user has to wait before sending another message (0-21600);
     * bots, as well as users with the permission manage_messages or manage_channel, are unaffected
     * //todo @link Permissions
     */
    int getRateLimitPerUser();

    /**
     * thread_metadata contains a few thread specific fields, archived, archive_timestamp, auto_archive_duration, locked. archive_timestamp is changed when creating, archiving, or unarchiving a thread, and when changing the auto_archive_duration field
     */
    @NotNull ThreadMetadata getThreadMetadata();

    /**
     * thread member object for the current user, if they have joined the thread, only included on certain API endpoints
     */
    @Nullable ThreadMember getMember();

    /**
     * default duration that the clients (not the API) will use for newly created threads,
     * in minutes, to automatically archive the thread after recent activity, can be set to: 60, 1440, 4320, 10080
     */
    @Nullable Integer getDefaultAutoArchiveDuration();

    /**
     * computed permissions for the invoking user in the channel, including overwrites,
     * only included when part of the resolved data received on a slash command interaction
     */
    @Nullable String getPermissionsAsString();

    /**
     * computed permissions for the invoking user in the channel, including overwrites,
     * only included when part of the resolved data received on a slash command interaction
     */
    @Nullable List<Permissions> getPermissions();

}
