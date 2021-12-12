package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.ApiVersion;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.discordbotapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.discordbotapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.config.ConfigFlag;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.message.abstracts.Message;
import me.linusdev.discordbotapi.api.objects.message.embed.Author;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.discordbotapi.helper.Helper;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();

        LApi lApi = new ConfigBuilder(Helper.getConfigPath())
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .disable(ConfigFlag.LOAD_VOICE_REGIONS_ON_STARTUP)
                .adjustGatewayConfig(gatewayConfigBuilder -> {
                    gatewayConfigBuilder
                            .setApiVersion(ApiVersion.V9)
                            .setCompression(GatewayCompression.NONE)
                            .setEncoding(GatewayEncoding.JSON)
                            .setOs("Windows 10")
                            .addIntent(GatewayIntent.GUILD_MESSAGES);
                }).buildLapi();

        lApi.getEventTransmitter().addSpecifiedListener(new EventListener() {
            @Override
            public void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event) {
                Message msg = event.getMessage();
                String content = msg.getContent();
                String channelId = msg.getChannelId();
                User author = msg.getAuthor();

                if(!author.isBot() && content.toLowerCase().startsWith("hi")) {

                    new MessageBuilder(event.getLApi(), "Hi " + author.getUsername())
                            .setReplyTo(msg, true)
                            .getQueueable(channelId)
                            .queue();
                }
            }
        }, EventIdentifier.GUILD_MESSAGE_CREATE);

    }



}
