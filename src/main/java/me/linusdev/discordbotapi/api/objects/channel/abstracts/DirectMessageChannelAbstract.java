package me.linusdev.discordbotapi.api.objects.channel.abstracts;

import me.linusdev.discordbotapi.api.objects.Recipient;
import org.jetbrains.annotations.NotNull;

public interface DirectMessageChannelAbstract extends TextChannel{

    /**
     * the recipients of the DM
     */
    @NotNull
    Recipient[] getRecipients();
}
