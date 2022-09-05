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

package me.linusdev.lapi.api.objects.channel;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.Recipient;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.GroupDirectMessageChannelAbstract;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GroupDirectMessageChannel extends Channel<GroupDirectMessageChannel> implements GroupDirectMessageChannelAbstract {

    protected @NotNull Recipient[] recipients;
    protected @Nullable Snowflake lastMessageId;
    protected @Nullable ISO8601Timestamp lastPinTimestamp;
    protected @Nullable String iconHash;
    protected @NotNull Snowflake ownerId;
    protected @Nullable Snowflake applicationId;

    public GroupDirectMessageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                                     @NotNull Recipient[] recipients, @Nullable Snowflake lastMessageId, @Nullable ISO8601Timestamp lastPinTimestamp,
                                     @Nullable String iconHash, @NotNull Snowflake ownerId, @Nullable Snowflake applicationId) {
        super(lApi, id, type);

        this.recipients = recipients;
        this.lastMessageId = lastMessageId;
        this.lastPinTimestamp = lastPinTimestamp;
        this.iconHash = iconHash;
        this.ownerId = ownerId;
        this.applicationId = applicationId;

    }

    public GroupDirectMessageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull SOData data) throws InvalidDataException {
        super(lApi, id, type, data);

        List<Object> recipients = data.getList(RECIPIENTS_KEY);

        if(recipients == null)
            throw new InvalidDataException(data, "field '" + RECIPIENTS_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(RECIPIENTS_KEY);

        this.recipients = new Recipient[recipients.size()];
        int i = 0;
        for(Object d : recipients) this.recipients[i++] = new Recipient(lApi, (SOData) d);

        this.lastMessageId = Snowflake.fromString((String) data.get(LAST_MESSAGE_ID_KEY));
        this.lastPinTimestamp = ISO8601Timestamp.fromString((String) data.get(LAST_PIN_TIMESTAMP_KEY));
        this.iconHash = (String) data.get(ICON_KEY);
        this.ownerId = Snowflake.fromString((String) data.get(OWNER_ID_KEY));
        this.applicationId = Snowflake.fromString((String) data.get(APPLICATION_ID_KEY));

    }

    /**
     * Since this is a direct message channel, the guild id will always be {@code null}
     */
    @Deprecated
    @Override
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return null;
    }

    @Override
    public @NotNull Recipient[] getRecipients() {
        return recipients;
    }

    @Override
    public @Nullable String getIconHash() {
        return iconHash;
    }



    @Override
    public @NotNull Snowflake getOwnerIdAsSnowflake() {
        return ownerId;
    }

    @Override
    public @Nullable Snowflake getApplicationID() {
        return applicationId;
    }

    @Override
    public @Nullable Snowflake getLastMessageIdAsSnowflake() {
        return lastMessageId;
    }

    @Override
    public @Nullable ISO8601Timestamp getLastPinTimestamp() {
        return lastPinTimestamp;
    }

    @Override
    public @NotNull GroupDirectMessageChannel copy() {
        return new GroupDirectMessageChannel(lApi,
                Copyable.copy(id),
                type,
                Copyable.copyArrayFlat(recipients),
                Copyable.copy(lastMessageId),
                Copyable.copy(lastPinTimestamp),
                Copyable.copy(iconHash),
                Copyable.copy(ownerId),
                Copyable.copy(applicationId)
        );
    }

    @Override
    public void updateSelfByData(SOData data) throws InvalidDataException {
        super.updateSelfByData(data);

        List<Object> rs = data.getList(RECIPIENTS_KEY);
        if(rs != null) {
            this.recipients = new Recipient[rs.size()];
            int i = 0;
            for(Object d : rs) this.recipients[i++] = new Recipient(lApi,(SOData) d);
        }

        data.processIfContained(LAST_MESSAGE_ID_KEY, (String str) -> this.lastMessageId = Snowflake.fromString(str));
        data.processIfContained(LAST_PIN_TIMESTAMP_KEY, (String str) -> this.lastPinTimestamp = ISO8601Timestamp.fromString(str));
        data.processIfContained(ICON_KEY, (String str) -> this.iconHash = str);
        data.processIfContained(OWNER_ID_KEY, (String str) -> this.ownerId = Snowflake.fromString(str));
        data.processIfContained(APPLICATION_ID_KEY, (String str) -> this.applicationId = Snowflake.fromString(str));
    }
}
