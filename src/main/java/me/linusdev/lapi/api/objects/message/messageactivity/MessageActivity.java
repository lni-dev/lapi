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

package me.linusdev.lapi.api.objects.message.messageactivity;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/channel#message-object-message-activity-structure" target="_top">Message Activity Structure</a>
 */
public class MessageActivity implements Datable {

    public static final String TYPE_KEY = "type";
    public static final String PARTY_ID_KEY = "party_id";

    /**
     * @see #getType()
     */
    private @NotNull final MessageActivityType type;

    /**
     * @see #getPartyId()
     */
    private @Nullable final String partyId;

    /**
     *
     * @param type 	type of message activity. see {@link MessageActivityType}
     * @param partyId party_id from a Rich Presence event
     */
    public MessageActivity(@NotNull MessageActivityType type, @Nullable String partyId){
        this.type = type;
        this.partyId = partyId;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link MessageActivity}
     * @throws InvalidDataException if {@link #TYPE_KEY} field is missing
     */
    @Contract("null -> null; !null -> new")
    public static @Nullable MessageActivity fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        Number typeNumber = (Number) data.get(TYPE_KEY);
        String partyId = (String) data.get(PARTY_ID_KEY);

        if(typeNumber == null)
            throw new InvalidDataException(data, TYPE_KEY + " field may not be missing or null in " + MessageActivity.class.getSimpleName()).addMissingFields(TYPE_KEY);

        return new MessageActivity(MessageActivityType.fromValue(typeNumber.intValue()), partyId);
    }

    /**
     * type of message activity
     * @see MessageActivityType
     */
    public @NotNull MessageActivityType getType() {
        return type;
    }

    /**
     * party_id from a <a href="https://discord.com/developers/docs/rich-presence/how-to#updating-presence-update-presence-payload-fields" target="_top">Rich Presence event</a>
     */
    public @Nullable String getPartyId() {
        return partyId;
    }

    /**
     * Generates a {@link SOData} from this {@link MessageActivity}
     */
    @Override
    public @NotNull SOData getData() {
        SOData data =SOData.newOrderedDataWithKnownSize(2);

        data.add(TYPE_KEY, type);
        if(partyId != null) data.add(PARTY_ID_KEY, partyId);

        return data;
    }
}
