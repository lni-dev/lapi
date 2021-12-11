package me.linusdev.discordbotapi;


import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.file.types.FileType;
import me.linusdev.discordbotapi.api.config.ConfigBuilder;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.discordbotapi.api.objects.emoji.EmojiObject;
import me.linusdev.discordbotapi.api.objects.invite.Invite;
import me.linusdev.discordbotapi.api.objects.message.MessageImplementation;
import me.linusdev.discordbotapi.api.objects.message.Reaction;
import me.linusdev.discordbotapi.api.objects.message.component.Component;
import me.linusdev.discordbotapi.api.objects.message.component.ComponentType;
import me.linusdev.discordbotapi.api.objects.message.component.actionrow.ActionRow;
import me.linusdev.discordbotapi.api.objects.message.component.button.Button;
import me.linusdev.discordbotapi.api.objects.message.component.button.ButtonStyle;
import me.linusdev.discordbotapi.api.objects.message.embed.Embed;
import me.linusdev.discordbotapi.api.objects.message.embed.EmbedBuilder;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.api.templates.attachment.AttachmentTemplate;
import me.linusdev.discordbotapi.api.templates.message.MessageTemplate;
import me.linusdev.discordbotapi.api.templates.message.builder.MentionType;
import me.linusdev.discordbotapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.discordbotapi.api.templates.message.builder.TimestampStyle;
import me.linusdev.discordbotapi.helper.Helper;
import me.linusdev.discordbotapi.log.LogInstance;
import me.linusdev.discordbotapi.log.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("removal")
public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException, ExecutionException, URISyntaxException {

        Logger.start();
        LogInstance log = Logger.getLogger("main");

        final LApi api = new ConfigBuilder(Helper.getConfigPath()).buildLapi();

        User currentUser = api.getCurrentUserRetriever().queueAndWait();

        System.out.println(currentUser.getUsername());
        System.out.println(currentUser.isBot());
        System.out.println(currentUser.getId());

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

            for(MessageImplementation message : messages){
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

        api.getThreadMembersRetriever("912398268238037022").queue((threadMembers, error) -> {
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


        api.getActiveThreadsRetriever("912377387868639282").queue((threadResponseBody, error) -> {
            if(error != null){
                System.out.println("Error");
                System.out.println(((InvalidDataException) error.getThrowable()).getData().getJsonString());
                return;
            }
            System.out.println("getListActiveThreadsRetriever...");

            for(Thread thread : threadResponseBody.getThreads()){
                System.out.println(thread.getName());
            }

            for(ThreadMember member : threadResponseBody.getMembers()){
                System.out.println(member.getUserId());
            }

            System.out.println(threadResponseBody.hasMore());
        });

        api.getPublicArchivedThreadsRetriever("912377387868639282", null, null).queue((listThreadsResponseBody, error) -> {
            if(error != null){
                System.out.println("Error");
                return;
            }

            System.out.println("getListPublicArchivedThreadsRetriever...");

            for(Thread thread : listThreadsResponseBody.getThreads()){
                System.out.println(thread.getName());
            }

            for(ThreadMember member : listThreadsResponseBody.getMembers()){
                System.out.println(member.getUserId());
            }

            System.out.println(listThreadsResponseBody.hasMore());

        });

        api.getUserRetriever("378980330281107457").queue((user, error) -> {
            if(error != null){
                System.out.println("Error");
                error.getThrowable().printStackTrace();
                if(error.getThrowable() instanceof InvalidDataException){
                    System.out.println("Invalid Data:");
                    System.out.println(((InvalidDataException) error.getThrowable()).getData().getJsonString());
                }
                return;
            }

            System.out.println("Username: " + user.getUsername());
        });

        api.createMessage("912377387868639282", new MessageTemplate("look below", false,
                new Embed[]{new EmbedBuilder().setTitle("Ingore me ><").build()},
                null, null, new Component[]{new ActionRow(api, ComponentType.ACTION_ROW,
                        new Component[]{new Button(api, ComponentType.BUTTON,
                                ButtonStyle.Link, "Klick mich UwU",
                                null, null, "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                                null)}
                )}, null,
                new Attachment[]{
                        new AttachmentTemplate("image.png",
                                "fun image",
                                Paths.get("C:\\Users\\Linus\\Pictures\\Discord PP\\ezgif-3-909f89a603e6.png"),
                                FileType.PNG).setAttachmentId(0)
        }));/*.queue((message, error) -> {
            if(error != null){
                System.out.println("Error");
                InvalidDataException e = (InvalidDataException)error.getThrowable();
                System.out.println(e.getData().getJsonString());
                return;
            }

            System.out.println(message.getContent());
            for(Attachment attachment : message.getAttachments()){
                System.out.println(attachment.getId());
                System.out.println(attachment.getDescription());
            }

        });*/

        new MessageBuilder(api, null)
                .setTTS(false)
                .appendContent("Hi ")
                .appendUserMention("765495017552478208")
                .appendContent(", how are you doing?")
                .appendContent("<@247421526532554752>")
                .appendEmoji(new EmojiObject(api, Snowflake.fromString("776601688961712148"), "closecirclefill_red", null, null, null, null, false, null))
                .appendTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS, TimestampStyle.LONG_DATE_WITH_TIME)
                .getQueueable("912377387868639282")
                .queue();

        api.getChannelMessageRetriever("912377387868639282", "913107065285800026").queue(message -> {
            String content = message.getContent();
            Reaction reaction = message.getReactions()[1];
            System.out.println(reaction.getData().getJsonString());
            System.out.println(content);
        });

        Embed e = new EmbedBuilder()
                .setTitle("Hi")
                .setDescription(MentionType.USER.get(new PlaceHolder(PlaceHolder.USER_ID, LApi.CREATOR_ID))).build();

        api.createMessage("912377387868639282", e).queue();

        new MessageBuilder(api)
                .addEmbed(e)
                .allowAllUserMentions()
                .getQueueable("912377387868639282").queue(message -> System.out.println(message.getTimestamp()));

        //7534015381736783882
        //rainbowEnergy

        /*LApiHttpBody body = new LApiHttpBody(0, message.getData());
        LApiHttpRequest request = new LApiHttpRequest("https://httpbin.org/patch", Method.PATCH, body);
        HttpRequest r = request.getHttpRequest();
        HttpResponse<String> response = api.getClient().send(r, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());*/

    }
}
