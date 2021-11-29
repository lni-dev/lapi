package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.user.Recipient;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GroupDirectMessageChannelAbstract;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GroupDirectMessageChannel extends Channel implements GroupDirectMessageChannelAbstract {

    private @NotNull Recipient[] recipients;
    private @Nullable Snowflake lastMessageId;
    private @Nullable String lastPinTimestamp;
    private @Nullable String iconHash;
    private @NotNull Snowflake ownerId;
    private @Nullable Snowflake applicationId;

    public GroupDirectMessageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type,
                                     @NotNull Recipient[] recipients, @Nullable Snowflake lastMessageId, @Nullable String lastPinTimestamp,
                                     @Nullable String iconHash, @NotNull Snowflake ownerId, @Nullable Snowflake applicationId) {
        super(lApi, id, type);

        this.recipients = recipients;
        this.lastMessageId = lastMessageId;
        this.lastPinTimestamp = lastPinTimestamp;
        this.iconHash = iconHash;
        this.ownerId = ownerId;
        this.applicationId = applicationId;

    }

    public GroupDirectMessageChannel(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull ChannelType type, @NotNull Data data) throws InvalidDataException {
        super(lApi, id, type, data);

        ArrayList<Data> recipients = (ArrayList<Data>) data.get(RECIPIENTS_KEY);

        if(recipients == null)
            throw new InvalidDataException(data, "field '" + RECIPIENTS_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId()).addMissingFields(RECIPIENTS_KEY);

        this.recipients = new Recipient[recipients.size()];
        int i = 0;
        for(Data d : recipients) this.recipients[i++] = new Recipient(lApi, d);

        this.lastMessageId = Snowflake.fromString((String) data.get(LAST_MESSAGE_ID_KEY));
        this.lastPinTimestamp = (String) data.get(LAST_PIN_TIMESTAMP_KEY);
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
    public @Nullable String getLastPinTimestamp() {
        return lastPinTimestamp;
    }
}
