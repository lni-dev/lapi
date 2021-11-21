package me.linusdev.discordbotapi.api.objects.invite;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.application.Application;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.discordbotapi.api.objects.toodo.Guild;
import me.linusdev.discordbotapi.api.objects.toodo.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/invite#invite-resource" target="_top">Invite Resource</a>
 */
public class Invite implements Datable, HasLApi {

    public static final String CODE_KEY = "code";
    public static final String GUILD_KEY = "guild";
    public static final String CHANNEL_KEY = "channel";
    public static final String INVITER_KEY = "inviter";
    public static final String TARGET_TYPE_KEY = "target_type";
    public static final String TARGET_USER_KEY = "target_user";
    public static final String TARGET_APPLICATION_KEY = "target_application";
    public static final String APPROXIMATE_PRESENCE_COUNT_KEY = "approximate_presence_count";
    public static final String APPROXIMATE_MEMBER_COUNT_KEY = "approximate_member_count";
    public static final String EXPIRES_AT_KEY = "expires_at";
    public static final String STAGE_INSTANCE_KEY = "stage_instance";
    public static final String GUILD_SCHEDULED_EVENT_KEY = "guild_scheduled_event";

    private final @NotNull LApi lApi;

    private final @NotNull String code;
    private final @Nullable Guild guild;
    private final @NotNull Channel channel;
    private final @Nullable User inviter;
    private final @Nullable TargetType targetType;
    private final @Nullable User targetUser;
    private final @Nullable Application targetApplication;
    private final @Nullable Integer approximatePresenceCount;
    private final @Nullable Integer approximateMemberCount;
    private final @Nullable ISO8601Timestamp expiresAt;
    private final @Nullable InviteStageInstance stageInstance;
    private final @Nullable GuildScheduledEvent guildScheduledEvent;

    //this is not in the object, but extends it instead
    private final @Nullable InviteMetadata inviteMetadata;

    public Invite(@NotNull LApi lApi, @NotNull String code, @Nullable Guild guild, @NotNull Channel channel, @Nullable User inviter, @Nullable TargetType targetType, @Nullable User targetUser, @Nullable Application targetApplication, @Nullable Integer approximatePresenceCount, @Nullable Integer approximateMemberCount, @Nullable ISO8601Timestamp expiresAt, @Nullable InviteStageInstance stageInstance, @Nullable GuildScheduledEvent guildScheduledEvent, @Nullable InviteMetadata inviteMetadata) {
        this.lApi = lApi;
        this.code = code;
        this.guild = guild;
        this.channel = channel;
        this.inviter = inviter;
        this.targetType = targetType;
        this.targetUser = targetUser;
        this.targetApplication = targetApplication;
        this.approximatePresenceCount = approximatePresenceCount;
        this.approximateMemberCount = approximateMemberCount;
        this.expiresAt = expiresAt;
        this.stageInstance = stageInstance;
        this.guildScheduledEvent = guildScheduledEvent;
        this.inviteMetadata = inviteMetadata;
    }

    //TODO

    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
