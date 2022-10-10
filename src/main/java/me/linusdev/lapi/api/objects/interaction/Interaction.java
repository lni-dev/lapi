/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.objects.interaction;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.message.Message;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object" target="_top">Interaction</a>
 */
public class Interaction implements Datable, HasLApi, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String TYPE_KEY = "type";
    public static final String DATA_KEY = "data";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String MEMBER_KEY = "member";
    public static final String USER_KEY = "user";
    public static final String TOKEN_KEY = "token";
    public static final String VERSION_KEY = "version";
    public static final String MESSAGE_KEY = "message";

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @NotNull Snowflake applicationId;
    private final @NotNull InteractionType type;
    private final @Nullable InteractionData data;
    private final @Nullable Snowflake guildId;
    private final @Nullable Snowflake channelId;
    private final @Nullable Member member;
    private final @Nullable User user;
    private final @NotNull String token;
    private final int version;
    private final @Nullable Message message;

    /**
     *
     * @param lApi {@link LApi}
     * @param id id of the interaction
     * @param applicationId id of the application this interaction is for
     * @param type the type of interaction
     * @param data the command data payload
     * @param guildId the guild it was sent from
     * @param channelId the channel it was sent from
     * @param member guild member data for the invoking user, including permissions
     * @param user user object for the invoking user, if invoked in a DM
     * @param token a continuation token for responding to the interaction
     * @param version read-only property, always 1
     * @param message for components, the message they were attached to
     */
    public Interaction(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull Snowflake applicationId, @NotNull InteractionType type, @Nullable InteractionData data, @Nullable Snowflake guildId, @Nullable Snowflake channelId, @Nullable Member member, @Nullable User user, @NotNull String token, int version, @Nullable Message message) {
        this.lApi = lApi;
        this.id = id;
        this.applicationId = applicationId;
        this.type = type;
        this.data = data;
        this.guildId = guildId;
        this.channelId = channelId;
        this.member = member;
        this.user = user;
        this.token = token;
        this.version = version;
        this.message = message;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData}
     * @return {@link Interaction}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #APPLICATION_ID_KEY}, {@link #TYPE_KEY}, {@link #TOKEN_KEY} or {@link #VERSION_KEY} are missing or {@code null}
     */
    @Contract("_, null -> null; _, !null -> !null;")
    public static @Nullable Interaction fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        String id = (String) data.get(ID_KEY);
        String applicationId = (String) data.get(APPLICATION_ID_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        SOData interactionData = (SOData) data.get(DATA_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        String channelId = (String) data.get(CHANNEL_ID_KEY);
        SOData member = (SOData) data.get(MEMBER_KEY);
        SOData user = (SOData) data.get(USER_KEY);
        String token = (String) data.get(TOKEN_KEY);
        Number version = (Number) data.get(VERSION_KEY);
        SOData message = (SOData) data.get(MESSAGE_KEY);

        if(id == null || applicationId == null || type == null || token == null || version == null){
            InvalidDataException.throwException(data, null, Interaction.class,
                    new Object[]{id, applicationId, type, token, version},
                    new String[]{ID_KEY, APPLICATION_ID_KEY, TYPE_KEY, TOKEN_KEY, VERSION_KEY});
        }

        //noinspection ConstantConditions
        return new Interaction(lApi, Snowflake.fromString(id), Snowflake.fromString(applicationId),
                InteractionType.fromValue(type.intValue()), InteractionData.fromData(lApi, interactionData),
                Snowflake.fromString(guildId), Snowflake.fromString(channelId), Member.fromData(lApi, member),
                User.fromData(lApi, user), token, version.intValue(), Message.fromData(lApi, message));
    }

    /**
     * id as {@link Snowflake} of the interaction
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * id as {@link Snowflake} of the application this interaction is for
     */
    public @NotNull Snowflake getApplicationIdAsSnowflake() {
        return applicationId;
    }

    /**
     * id as {@link String} of the application this interaction is for
     */
    public @NotNull String getApplicationId() {
        return applicationId.asString();
    }

    /**
     * the type of interaction
     */
    public @NotNull InteractionType getType() {
        return type;
    }

    /**
     * the command data payload
     */
    public @Nullable InteractionData getInteractionData(){
        return data;
    }

    /**
     * the id as {@link Snowflake} of the guild it was sent from
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * the id as {@link String} of the guild it was sent from
     */
    public @Nullable String getGuildId() {
        if(guildId == null) return null;
        return guildId.asString();
    }

    /**
     * the id as {@link Snowflake} of the channel it was sent from
     */
    public @Nullable Snowflake getChannelIdAsSnowflake() {
        return channelId;
    }

    /**
     * the id as {@link String} of the channel it was sent from
     */
    public @Nullable String getChannelId() {
        if(channelId == null) return null;
        return channelId.asString();
    }

    /**
     * guild member data for the invoking user, including permissions
     */
    public @Nullable Member getMember() {
        return member;
    }

    /**
     * user object for the invoking user, if invoked in a DM
     */
    public @Nullable User getUser() {
        return user;
    }

    /**
     * a continuation token for responding to the interaction
     */
    public @NotNull String getToken() {
        return token;
    }

    /**
     * read-only property, always 1
     */
    public int getVersion() {
        return version;
    }

    /**
     * for components, the message they were attached to
     */
    public @Nullable Message getMessage() {
        return message;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(11);

        data.add(ID_KEY, id);
        data.add(APPLICATION_ID_KEY, applicationId);
        data.add(TYPE_KEY, type);
        data.addIfNotNull(DATA_KEY, this.data);
        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.addIfNotNull(CHANNEL_ID_KEY, channelId);
        data.addIfNotNull(MEMBER_KEY, member);
        data.addIfNotNull(USER_KEY, user);
        data.add(TOKEN_KEY, token);
        data.add(VERSION_KEY, version);
        data.addIfNotNull(MESSAGE_KEY, message);


        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

}
