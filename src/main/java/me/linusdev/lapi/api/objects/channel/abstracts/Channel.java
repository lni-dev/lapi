/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.objects.channel.abstracts;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.channel.thread.GuildNewsThread;
import me.linusdev.lapi.api.objects.channel.thread.GuildPrivateThread;
import me.linusdev.lapi.api.objects.channel.thread.GuildPublicThread;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.channel.*;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#channels-resource" target="_top">Channels Resource</a>
 */
public abstract class Channel<T extends Channel<T>> implements CopyAndUpdatable<T>, SnowflakeAble {

    public final static String ID_KEY = "id";
    public final static String TYPE_KEY = "type";
    public final static String GUILD_ID_KEY = "guild_id";
    public final static String POSITION_KEY = "position";
    public final static String PERMISSION_OVERWRITES_KEY = "permission_overwrites";
    public final static String NAME_KEY = "name";
    public final static String TOPIC_KEY = "topic";
    public final static String NSFW_KEY = "nsfw";
    public final static String LAST_MESSAGE_ID_KEY = "last_message_id";
    public final static String BITRATE_KEY = "bitrate";
    public final static String USER_LIMIT_KEY = "user_limit";
    public final static String RATE_LIMIT_PER_USER_KEY = "rate_limit_per_user";
    public final static String RECIPIENTS_KEY = "recipients";
    public final static String ICON_KEY = "icon";
    public final static String OWNER_ID_KEY = "owner_id";
    public final static String APPLICATION_ID_KEY = "application_id";
    public final static String PARENT_ID_KEY = "parent_id";
    public final static String LAST_PIN_TIMESTAMP_KEY = "last_pin_timestamp";
    public final static String RTC_REGION_KEY = "rtc_region";
    public final static String VIDEO_QUALITY_MODE_KEY = "video_quality_mode";
    public final static String MESSAGE_COUNT_KEY = "message_count";
    public final static String MEMBER_COUNT_KEY = "member_count";
    public final static String THREAD_METADATA_KEY = "thread_metadata";

    //TODO
    public final static String MEMBER_KEY = "member";
    public final static String DEFAULT_AUTO_ARCHIVE_DURATION_KEY = "default_auto_archive_duration";
    public final static String PERMISSIONS_KEY = "permissions";

    protected final @NotNull LApi lApi;
    protected final @NotNull Snowflake id;
    protected final @NotNull ChannelType type;


    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable Channel<?> fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        ChannelType type = ChannelType.fromId(((Number) data.getOrDefaultBoth(TYPE_KEY, ChannelType.UNKNOWN.getId())).intValue());
        Snowflake id = Snowflake.fromString((String) data.getOrDefaultBoth(ID_KEY, null));

        switch (type){
            case UNKNOWN:
                break;
            case GUILD_TEXT: //0
                return new GuildTextChannel(lApi, id, type, data);
            case DM:
                return new DirectMessageChannel(lApi, id, type, data);
            case GUILD_VOICE:
                return new GuildVoiceChannel(lApi, id, type, data);
            case GROUP_DM:
                return new GroupDirectMessageChannel(lApi, id, type, data);
            case GUILD_CATEGORY:
                return new ChannelCategory(lApi, id, type, data);
            case GUILD_NEWS:
                return new GuildNewsChannel(lApi, id, type, data);
            case GUILD_STORE: //7
                return new GuildStoreChannel(lApi, id, type, data);
            case GUILD_NEWS_THREAD: //10
                return new GuildNewsThread(lApi, id, type, data);
            case GUILD_PUBLIC_THREAD:
                return new GuildPublicThread(lApi, id, type, data);
            case GUILD_PRIVATE_THREAD:
                return new GuildPrivateThread(lApi, id, type, data);
            case GUILD_STAGE_VOICE:
                return new GuildStageChannel(lApi, id, type, data);

        }

        return new UnknownChannel(lApi, id, type, data);
    }


    public Channel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type){
        this.lApi = lApi;
        this.id = id;
        this.type = type;
    }

    public Channel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data){
        this(lApi, id, type);
    }

    public @NotNull String getId() {
        return getIdAsSnowflake().asString();
    }

    public @NotNull Snowflake getIdAsSnowflake(){
        return id;
    }

    public @NotNull ChannelType getType() {
        return type;
    }

    public @NotNull LApi getLApi() {
        return lApi;
    }

    /**
     * This here and in {@link GuildChannel}, because it is very often required for abstraction.
     *
     * @return the guild id or {@code null} if this is not a guild channel
     */
    public @NotNull String getGuildId(){
        Snowflake snowflake = getGuildIdAsSnowflake();
        if(snowflake == null) return null;
        else return snowflake.asString();
    }

    /**
     *
     * @return the guild id snowflake or {@code null} if this is not a guild channel
     */
    public abstract @Nullable Snowflake getGuildIdAsSnowflake();

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        data.processIfContained(TYPE_KEY, (Number num) -> ChannelType.fromId(num.intValue()));
    }
}
