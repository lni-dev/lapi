package me.linusdev.discordbotapi.api.objects.message.messageactivity;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
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
     * @param data {@link Data} with required fields
     * @return {@link MessageActivity}
     * @throws InvalidDataException if {@link #TYPE_KEY} field is missing
     */
    public static @NotNull MessageActivity fromData(@NotNull Data data) throws InvalidDataException {
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
     * Generates a {@link Data} from this {@link MessageActivity}
     */
    @Override
    public @NotNull Data getData() {
        Data data = new Data(1);

        data.add(TYPE_KEY, type);
        if(partyId != null) data.add(PARTY_ID_KEY, partyId);

        return data;
    }
}
