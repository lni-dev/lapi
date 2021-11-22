package me.linusdev.discordbotapi;


import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.discordbotapi.api.objects.invite.Invite;
import me.linusdev.discordbotapi.api.objects.message.Message;
import me.linusdev.discordbotapi.api.objects.message.Reaction;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException, ExecutionException, URISyntaxException {

        Logger.start();
        LogInstance log = Logger.getLogger("main");

        Config config = new Config(0, null);
        LApi api = new LApi(Private.TOKEN, config);

        //Retrieve the Message with id=912377554105688074 inside the channel with id=912377387868639282
        Queueable<Message> msgRetriever
                = api.getChannelMessageRetriever(
                        "912377387868639282",
                        "912377554105688074");
        msgRetriever.queue((message, error) -> {
            //This will be executed once the message has been retrieved
            if(error != null){
                System.out.println("Error!");
                return;
            }

            System.out.println(message.getContent());
        });


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
                if (e != null){
                    e.getThrowable().printStackTrace();
                    return;
                }
                System.out.println(Arrays.toString(list.stream().map(User::getUsername).toArray()));
            });
        });

        log.log("getChannelInvites");
        api.getChannelInvitesRetriever("387972238256898048").queue((invites, error) -> {
            System.out.println("retrieved invites....");
            if (error != null) {
                System.out.println("error");
                log.error(error.getThrowable());
                //error.getThrowable().printStackTrace();
                return;
            }
            for (Invite invite : invites) {
                System.out.println(invite.getCode());
            }
        });

        api.getPinnedMessagesRetriever("912377387868639282").queue(((messages, error) -> {
            if(error != null){
                System.out.println(error);
                return;
            }

            for(Message message : messages){
                System.out.println("Pinned Message: " + message.getContent());
            }

        }));

        api.getThreadMemberRetriever("912398268238037022", "247421526532554752").queue((member, error) -> {
            if(error != null){
                System.out.println("Error");
                if(error.getThrowable() instanceof InvalidDataException){
                    InvalidDataException e = (InvalidDataException) error.getThrowable();
                    System.out.println(e.getMissingFields());
                    System.out.println(e.getData().getJsonString().toString());
                }
                return;
            }

            System.out.println(member.getJoinTimestamp());
            System.out.println(member.getUserId());
        });

        api.listThreadMembersRetriever("912398268238037022").queue((threadMembers, error) -> {
            if(error != null){
                System.out.println("Error");
                return;
            }

            System.out.println("list thread member: ");

            for(ThreadMember member : threadMembers){
                System.out.println(member.getUserId());
                System.out.println(member.getJoinTimestamp());
            }
        });

        /*LApiHttpBody body = new LApiHttpBody(0, message.getData());
        LApiHttpRequest request = new LApiHttpRequest("https://httpbin.org/patch", Method.PATCH, body);
        HttpRequest r = request.getHttpRequest();
        HttpResponse<String> response = api.getClient().send(r, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());*/

    }
}
