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

package me.linusdev.lapi.api.objects.presence;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.activity.Activity;
import me.linusdev.lapi.api.communication.gateway.presence.StatusType;
import me.linusdev.lapi.api.interfaces.CopyAndUpdatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * received from the {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket Gateway} to
 * indicate a presence update of a specific user.
 * <br><br>
 * <p>
 *     The user object within this event can be partial, the only field which must be sent is the id field,
 *     everything else is optional. Along with this limitation, no fields are required, and the types of the fields
 *     are not validated. Your client should expect any combination of fields and types within this event.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#presence-update" target="_top">Presence Update</a>
 */
public class PresenceUpdate implements Datable, CopyAndUpdatable<PresenceUpdate> {

    public static final String USER_KEY = "user";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String STATUS_KEY = "status";
    public static final String ACTIVITIES_KEY = "activities";
    public static final String CLIENT_STATUS_KEY = "client_status";

    private final @Nullable PartialUser user;
    private final @Nullable Snowflake guildId;
    private @Nullable StatusType status;
    private @Nullable ArrayList<Activity> activities;
    private @Nullable ClientStatus clientStatus;

    /**
     *
     * @param user the user presence is being updated for
     * @param guildId id of the guild
     * @param status either "idle", "dnd", "online", or "offline"
     * @param activities user's current activities
     * @param clientStatus user's platform-dependent status
     */
    public PresenceUpdate(@Nullable PartialUser user, @Nullable Snowflake guildId, @Nullable StatusType status, @Nullable ArrayList<Activity> activities, @Nullable ClientStatus clientStatus) {
        this.user = user;
        this.guildId = guildId;
        this.status = status;
        this.activities = activities;
        this.clientStatus = clientStatus;
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable PresenceUpdate fromData(@Nullable Data data) throws InvalidDataException {
        if(data == null) return null;

        Data userData = (Data) data.get(USER_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        String status = (String) data.get(STATUS_KEY);
        ArrayList<Activity> activities = data.getAndConvertArrayList(ACTIVITIES_KEY,
                (ExceptionConverter<Data, Activity, InvalidDataException>) Activity::fromData);
        Data clientStatusData = (Data) data.get(CLIENT_STATUS_KEY);

        return new PresenceUpdate(
                userData == null ? null : new PartialUser(userData),
                Snowflake.fromString(guildId),
                StatusType.fromValue(status),
                activities,
                ClientStatus.fromData(clientStatusData)
        );
    }

    /**
     * the user presence is being updated for
     */
    public @Nullable PartialUser getUser() {
        return user;
    }

    /**
     * id of the guild
     */
    public @Nullable Snowflake getGuildId() {
        return guildId;
    }

    /**
     * either "idle", "dnd", "online", or "offline"
     */
    public @Nullable StatusType getStatus() {
        return status;
    }

    /**
     * user's current activities
     */
    public @Nullable ArrayList<Activity> getActivities() {
        return activities;
    }

    /**
     * user's platform-dependent status
     */
    public @Nullable ClientStatus getClientStatus() {
        return clientStatus;
    }

    @Override
    public Data getData() {
        Data data = new Data(5);

        data.addIfNotNull(USER_KEY, user);
        data.addIfNotNull(GUILD_ID_KEY, guildId);
        data.addIfNotNull(STATUS_KEY, status);
        data.addIfNotNull(ACTIVITIES_KEY, activities);
        data.addIfNotNull(CLIENT_STATUS_KEY, clientStatus);

        return data;
    }

    @Override
    public @NotNull PresenceUpdate copy() {
        return new PresenceUpdate(user,
                Copyable.copy(guildId),
                status,
                activities == null ? null : (ArrayList<Activity>) activities.clone(),
                Copyable.copy(clientStatus));
    }

    @Override
    public void updateSelfByData(Data data) throws InvalidDataException {
        //user is not updated
        //guildId is not updated
        data.processIfContained(STATUS_KEY, (String str) -> status = StatusType.fromValue(str));

        ArrayList<Activity> activities = data.getAndConvertArrayList(ACTIVITIES_KEY,
                (ExceptionConverter<Data, Activity, InvalidDataException>) Activity::fromData);

        if(activities != null) this.activities = activities;

        data.processIfContained(CLIENT_STATUS_KEY, (Data d) -> this.clientStatus = ClientStatus.fromData(d));
    }

}
