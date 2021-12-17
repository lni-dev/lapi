package me.linusdev.discordbotapi.api.objects.guild;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.updatable.Updatable;
import me.linusdev.discordbotapi.api.manager.*;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.guild.enums.*;
import me.linusdev.discordbotapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.discordbotapi.api.objects.stage.StageInstance;
import me.linusdev.discordbotapi.api.objects.sticker.Sticker;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.discordbotapi.api.objects.role.Role;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdatableGuild extends Guild implements UpdatableGuildAbstract, Datable, HasLApi, SnowflakeAble, Updatable {

    protected @Nullable Boolean unavailable;
    protected boolean awaitingEvent;
    protected boolean removed;

    //Create Guild
    protected @Nullable ISO8601Timestamp joinedAt;
    protected @Nullable Boolean large;
    protected @Nullable Integer memberCount;
    protected @Nullable VoiceStatesManager voiceStatesManager;
    protected @Nullable MembersManager membersManager;
    protected @Nullable ChannelsManager channelsManager;
    protected @Nullable ThreadsManager threadsManager;
    protected @Nullable PresencesManager presencesManager;
    protected @Nullable StageInstance[] stageInstances;
    protected @Nullable GuildScheduledEvent[] guildScheduledEvents;


    public UpdatableGuild(@NotNull LApi lApi, @NotNull Snowflake id, @Nullable Boolean unavailable, boolean awaitingEvent){
        super(lApi,
                id,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        this.unavailable = unavailable;
        this.awaitingEvent = awaitingEvent;
        this.removed = unavailable == null;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable UpdatableGuild fromData(@NotNull LApi lApi, @Nullable Data data){
        if(data == null) return null;
        String id = (String) data.get(ID_KEY);
        Boolean unavailable = (Boolean) data.getOrDefaultIfNull(UNAVAILABLE_KEY, false);


        UpdatableGuild guild = new UpdatableGuild(lApi, Snowflake.fromString(id), unavailable, false);
        guild.updateSelfByData(data);

        return guild;
    }

    public static @NotNull UpdatableGuild fromUnavailableGuild(@NotNull LApi lApi, @NotNull UnavailableGuild guild){
        return new UpdatableGuild(lApi, guild.getIdAsSnowflake(), guild.getUnavailable(), true);
    }


    @Override
    public @Nullable String getName() {
        return super.getName();
    }

    @Override
    public boolean isUnavailable() {
        return (!(unavailable == null)) && unavailable;
    }

    public boolean isAwaitingEvent() {
        return awaitingEvent;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public Data getData() {
        //TODO
        return null;
    }

    @Override
    public void updateSelfByData(@Nullable Data data) {
        if(data == null) return;
        this.awaitingEvent = false;
        data.processIfContained(NAME_KEY, (String name) -> this.name = name);
        this.unavailable = (Boolean) data.getOrDefault(UNAVAILABLE_KEY, false);
        //data.processIfContained(UNAVAILABLE_KEY, (Boolean unavailable) -> this.unavailable = unavailable);
    }
}
