package me.linusdev.discordbotapi;


import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.objects.invite.Invite;
import me.linusdev.discordbotapi.api.objects.message.Reaction;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException, ExecutionException, URISyntaxException {

        Logger.start();
        LogInstance log = Logger.getLogger("main");

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
        })*/
        ;

        log.log("getChannelMessageRetriever");
        api.getChannelMessageRetriever("820084724693073921", "854807543603003402").queue((message, error) -> {
            if (error != null) error.getThrowable().printStackTrace();
            Reaction[] reactions = message.getReactions();

            Reaction reaction = reactions[0];
            api.getReactionsRetriever(message.getChannelId(), message.getId(), reaction.getEmoji(), null, 100).queue((list, e) -> {
                if (e != null) e.getThrowable().printStackTrace();
                for (User user : list) {
                    System.out.println(user.getUsername());
                }
            });
        });

        log.log("getChannelInvites");
        api.getChannelInvites("387972238256898048").queue((invites, error) -> {
            System.out.println("retrieved invites....");
            if (error != null) {
                error.getThrowable().printStackTrace();
                return;
            }
            for (Invite invite : invites) {
                System.out.println(invite.getCode());
            }
        });

        /*LApiHttpBody body = new LApiHttpBody(0, message.getData());
        LApiHttpRequest request = new LApiHttpRequest("https://httpbin.org/patch", Method.PATCH, body);
        HttpRequest r = request.getHttpRequest();
        HttpResponse<String> response = api.getClient().send(r, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());*/

    }
}
