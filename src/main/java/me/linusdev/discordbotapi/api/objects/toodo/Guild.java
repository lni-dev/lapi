package me.linusdev.discordbotapi.api.objects.toodo;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.guild.enums.DefaultMessageNotificationLevel;
import me.linusdev.discordbotapi.api.objects.guild.enums.ExplicitContentFilterLevel;
import me.linusdev.discordbotapi.api.objects.guild.enums.MFALevel;
import me.linusdev.discordbotapi.api.objects.guild.enums.VerificationLevel;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Guild implements Datable, HasLApi, SnowflakeAble {

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @NotNull String name;
    private final @Nullable Icon icon;
    private final @Nullable Icon iconHash;
    private final @Nullable Splash splash;
    private final @Nullable Splash discoverySplash;
    private final @Nullable Boolean owner;
    private final @NotNull Snowflake ownerId;
    private final @Nullable String permissions; //TODO PermissionsList
    private final @Nullable @Deprecated VoiceRegion region;
    private final @Nullable Snowflake afkChannelId;
    private final int afkTimeout;
    private final @Nullable Boolean widgetEnabled;
    private final @Nullable Snowflake widgetChannelId;
    private final @NotNull VerificationLevel verificationLevel;
    private final @NotNull DefaultMessageNotificationLevel defaultMessageNotifications;
    private final @NotNull ExplicitContentFilterLevel explicitContentFilter;
    private final @NotNull Role[] roles;
    private final @NotNull EmojiObject[] emojis;
    private final @Nullable String[] features; //see GuildFeature
    private final @NotNull MFALevel mfaLevel;
    private final @Nullable Snowflake applicationId;
    private final @NotNull Snowflake systemChannelId;



    public static @Nullable Guild fromData(@NotNull LApi lApi, @Nullable Data data){
        if(data == null) return null;

        return new Guild();
    }

    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return null;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public @NotNull LApi getLApi() {
        return null;
    }


}
