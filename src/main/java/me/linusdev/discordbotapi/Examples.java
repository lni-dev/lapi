package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.LApiImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * Heading 1:<br>
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">Title</span><br>
 * Heading 2:<br>
 * <span style="margin-bottom:0;padding-bottom:0;font-size:15px;font-weight:'bold';">Title</span><br>
 */
public class Examples {

    public static void main(String... args) throws LApiException, IOException, ParseException, InterruptedException {

        Config config = ConfigBuilder.getDefault("TOKEN").build();
        LApi lApi = ConfigBuilder.getDefault("TOKEN").buildLapi();

        lApi.getEventTransmitter().addListener(new EventListener() {
            @Override
            public void onMessageCreate(@NotNull MessageCreateEvent event) {
                System.out.println("Message: " + event.getMessage().getContent());

                if(!event.getMessage().getAuthor().isBot()
                    && event.getMessage().getContent().equals("Hi")){
                    lApi.createMessage(event.getChannelId(), "Hi").queue();
                }
            }
        });


    }

}
