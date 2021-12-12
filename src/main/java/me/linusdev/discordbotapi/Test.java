package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.exceptions.LimitException;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.emoji.StandardEmoji;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.discordbotapi.helper.Helper;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();

        new ConfigBuilder(Helper.getConfigPath()).adjustGatewayConfig(gatewayConfigBuilder -> {
            gatewayConfigBuilder.addIntent(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILDS);
        }).writeToFile(Helper.getConfigPath(), true);

        System.exit(0);

        Config c = new ConfigBuilder(Helper.getConfigPath()).build();

        final LApi lApi = new LApi(c);

        EventTransmitter transmitter = new EventTransmitter(lApi);

        transmitter.addListener(new EventListener() {
            @Override
            public void onMessageCreate(@NotNull MessageCreateEvent event) {
                if(event.getMessage().getContent().equalsIgnoreCase("hi")){

                    try {
                        new MessageBuilder(event.getLApi()).setReplyTo(event.getMessage(), true)
                                .appendContent("Hi ").appendEmoji(StandardEmoji.values()[new Random().nextInt(StandardEmoji.values().length)])
                                .getQueueable(event.getChannelId()).queue((messageImplementation, error) -> {
                                    if(error != null){
                                        error.getThrowable().printStackTrace();
                                    }
                                });
                    } catch (LimitException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        //gateway.start();

    }


}
