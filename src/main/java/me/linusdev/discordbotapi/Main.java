package me.linusdev.discordbotapi;


import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.discordbotapi.api.communication.queue.Error;
import me.linusdev.discordbotapi.api.communication.queue.Future;
import me.linusdev.discordbotapi.api.communication.retriever.ChannelRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.MessageRetriever;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.message.Message;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException, ExecutionException {

        Config conf = new Config(0, null);
        LApi api = new LApi(Private.TOKEN, conf);

        // Private Message
        // MessageRetriever msgRetriever = new MessageRetriever(api, "765540315905130516", "905348121675071508");

        // Guild Sticker Message
        //MessageRetriever msgRetriever = new MessageRetriever(api, "751169122775334942", "905396558969847879");

        // Guild Message with Reactions (also animated Emoji) https://ptb.discord.com/channels/317290087383826442/820084724693073921/854807543603003402
        //MessageRetriever msgRetriever = new MessageRetriever(api, "820084724693073921", "854807543603003402");

        //Guild message pinned
        //MessageRetriever msgRetriever = new MessageRetriever(api, "714942057260515398", "908716411927547924");

        /*api.getChannelRetriever("765540882387828746").queue(new BiConsumer<Channel, Error>() {
            @Override
            public void accept(Channel channel, Error error) {
                System.out.println("Channel Type:" + channel.getType());
            }
        })*/;

        api.getChannelMessagesRetriever("763440854332604428", "786539902111842337", 5, null)
                .queue((messages, error) -> {
                    System.out.println("Then...");
                    synchronized (messages){
                        try {
                            messages.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(error != null){
                        System.out.println("Error");
                        error.getThrowable().printStackTrace();
                        return;
                    }

                    for(Message msg : messages){
                        System.out.println(msg.getContent());
                    }
                });

        /*LApiHttpBody body = new LApiHttpBody(0, message.getData());
        LApiHttpRequest request = new LApiHttpRequest("https://httpbin.org/patch", Method.PATCH, body);
        HttpRequest r = request.getHttpRequest();
        HttpResponse<String> response = api.getClient().send(r, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());*/

    }
}
