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

package me.linusdev.lapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.user.Recipient;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.channel.abstracts.DirectMessageChannelAbstract;
import me.linusdev.lapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * <a href="https://discord.com/developers/docs/resources/channel#channel-object-example-dm-channel">Example DM Channel</a>
 */
public class DirectMessageChannel extends Channel implements DirectMessageChannelAbstract {

    private @NotNull Recipient[] recipients;
    private @Nullable Snowflake lastMessageId;
    private @Nullable String lastPinTimestamp;

    public DirectMessageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                                @NotNull Recipient[] recipients, @Nullable Snowflake lastMessageId, @Nullable String lastPinTimestamp) {
        super(lApi, id, type);

        this.recipients = recipients;
        this.lastMessageId = lastMessageId;
        this.lastPinTimestamp = lastPinTimestamp;

    }

    public DirectMessageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);

        ArrayList<Data> recipients = (ArrayList<Data>) data.get(RECIPIENTS_KEY);

        if(recipients == null)
            throw new InvalidDataException(data, "field '" + RECIPIENTS_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(RECIPIENTS_KEY);

        this.recipients = new Recipient[recipients.size()];
        int i = 0;
        for(Data d : recipients) this.recipients[i++] = new Recipient(lApi, d);

        this.lastMessageId = Snowflake.fromString((String) data.get(LAST_MESSAGE_ID_KEY));
        this.lastPinTimestamp = (String) data.get(LAST_PIN_TIMESTAMP_KEY);
    }



    /**
     * Since this is a direct message channel, the guild id will always be {@code null}
     */
    @Override
    @Deprecated
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return null;
    }

    @Override
    public @NotNull Recipient[] getRecipients() {
        return recipients;
    }

    @Override
    public @Nullable Snowflake getLastMessageIdAsSnowflake() {
        return lastMessageId;
    }

    @Override
    public @Nullable String getLastPinTimestamp() {
        return lastPinTimestamp;
    }
}
