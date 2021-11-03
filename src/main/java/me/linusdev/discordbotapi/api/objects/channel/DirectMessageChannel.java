package me.linusdev.discordbotapi.api.objects.channel;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.Recipient;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.DirectMessageChannelAbstract;
import me.linusdev.discordbotapi.api.objects.enums.ChannelType;
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
            throw new InvalidDataException("field '" + RECIPIENTS_KEY + "' missing or null in " + this.getClass().getSimpleName() + " with id:" + getId());

        this.recipients = new Recipient[recipients.size()];
        int i = 0;
        for(Data d : recipients) this.recipients[i++] = new Recipient(d);

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
