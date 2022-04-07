/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.file.types.FileType;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.command.RequestGuildMembersCommand;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberAddEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberRemoveEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.sticker.GuildStickersUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.communication.gateway.events.voice.state.VoiceStateUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.presence.StatusType;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.enums.MessageFlag;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.interaction.response.InteractionResponseBuilder;
import me.linusdev.lapi.api.objects.message.abstracts.Message;
import me.linusdev.lapi.api.objects.message.component.Component;
import me.linusdev.lapi.api.objects.message.component.ComponentType;
import me.linusdev.lapi.api.objects.message.component.actionrow.ActionRow;
import me.linusdev.lapi.api.objects.message.component.button.Button;
import me.linusdev.lapi.api.objects.message.component.button.ButtonStyle;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.message.embed.EmbedBuilder;
import me.linusdev.lapi.api.objects.message.embed.InvalidEmbedException;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.templates.attachment.AttachmentTemplate;
import me.linusdev.lapi.api.templates.message.MessageTemplate;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.lapi.helper.Helper;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Test implements EventListener{


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start(true, false);

        long cMillis = System.currentTimeMillis();

        while(System.currentTimeMillis() - cMillis < 1){}

        LApi lApi = new ConfigBuilder(Helper.getConfigPath())
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .enable(ConfigFlag.CACHE_ROLES)
                .enable(ConfigFlag.CACHE_GUILDS)
                .enable(ConfigFlag.COPY_GUILD_ON_UPDATE_EVENT)
                .enable(ConfigFlag.COPY_ROLE_ON_UPDATE_EVENT)
                .enable(ConfigFlag.CACHE_EMOJIS)
                .enable(ConfigFlag.COPY_EMOJI_ON_UPDATE_EVENT)
                .enable(ConfigFlag.CACHE_VOICE_STATES)
                .enable(ConfigFlag.COPY_VOICE_STATE_ON_UPDATE_EVENT)
                .disable(ConfigFlag.CACHE_VOICE_REGIONS)
                .enable(ConfigFlag.CACHE_MEMBERS)
                .enable(ConfigFlag.COPY_MEMBER_ON_UPDATE_EVENT)
                .adjustGatewayConfig(gatewayConfigBuilder -> {
                    gatewayConfigBuilder
                            .setApiVersion(ApiVersion.V9)
                            .setCompression(GatewayCompression.NONE)
                            .setEncoding(GatewayEncoding.JSON)
                            .setOs("Windows 10")
                            .addIntent(GatewayIntent.ALL)
                            .adjustStartupPresence(presence -> {
                                presence.setStatus(StatusType.ONLINE);
                            })
                            .setUnexpectedEventHandler(new GatewayWebSocket.UnexpectedEventHandler() {
                                @Override
                                public void handleError(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull Throwable error) {

                                }

                                @Override
                                public void onFatal(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull String information, @Nullable Throwable cause) {
                                    System.out.println("Fatal");
                                }
                            });
                }).buildLapi();

        lApi.getEventTransmitter().addListener(new Test());



    }


    @Override
    public void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload) {
        System.out.println("onUnknownEvent");
    }

    @Override
    public void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        System.out.println("onReady");
    }

    @Override
    public void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {
        System.out.println("onGuildsReady");

        for(CachedGuildImpl guild : event.getGuildPool()){
            System.out.println(guild);
            RequestGuildMembersCommand cmd = RequestGuildMembersCommand.createQueryGuildMembersCommand(lApi, guild.getId(),
                    "Lin", true, "fjdsijfdsjfojdsofjdslk");
            cmd.send();
        }

    }

    @Override
    public void onLApiReady(@NotNull LApi lApi, @NotNull LApiReadyEvent event) {
        System.out.println("onLApiReady");
    }

    @Override
    public void onGuildCreate(@NotNull LApi lApi, @NotNull GuildCreateEvent event) {
        System.out.println("GuildImpl create: " + event.getCachedGuild().getName());
    }

    @Override
    public void onGuildDelete(@NotNull LApi lApi, @NotNull GuildDeleteEvent event) {
        System.out.println("onGuildDelete");
    }

    @Override
    public void onGuildUpdate(@NotNull LApi lApi, @NotNull GuildUpdateEvent event) {
        System.out.println("onGuildUpdate");
        System.out.println(((Data)event.getPayload().getPayloadData()).getJsonString());
    }

    @Override
    public void onGuildJoined(@NotNull LApi lApi, @NotNull GuildJoinedEvent event) {
        System.out.println("onGuildJoined");
    }

    @Override
    public void onGuildLeft(@NotNull LApi lApi, @NotNull GuildLeftEvent event) {
        System.out.println("onGuildLeft");
    }

    @Override
    public void onGuildUnavailable(@NotNull LApi lApi, @NotNull GuildUnavailableEvent event) {
        System.out.println("onGuildUnavailable");
    }

    @Override
    public void onGuildAvailable(@NotNull LApi lApi, @NotNull GuildAvailableEvent event) {
        System.out.println("onGuildAvailable");
    }

    @Override
    public void onGuildEmojisUpdate(@NotNull LApi lApi, @NotNull GuildEmojisUpdateEvent event) {
        System.out.println("onGuildEmojisUpdate");

        ListUpdate<EmojiObject> update = event.getUpdate();

        System.out.println("old: " + (update.getOld()));
        System.out.println("updated: " +update.getUpdated());
        System.out.println("added: " +update.getAdded());
        System.out.println("removed: " +update.getRemoved());

    }

    @Override
    public void onGuildStickersUpdate(@NotNull LApi lApi, @NotNull GuildStickersUpdateEvent event) {
        System.out.println("onGuildStickersUpdate");
    }

    @Override
    public void onGuildMemberAdd(@NotNull LApi lApi, @NotNull GuildMemberAddEvent event) {
        System.out.println("onGuildMemberAdd: " + event.getMember().getUser().getUsername());
    }

    @Override
    public void onGuildMemberUpdate(@NotNull LApi lApi, @NotNull GuildMemberUpdateEvent event) {
        System.out.println("onGuildMemberUpdate");
    }

    @Override
    public void onGuildMemberRemove(@NotNull LApi lApi, @NotNull GuildMemberRemoveEvent event) {
        System.out.println("onGuildMemberRemove");
    }

    @Override
    public void onGuildMembersChunk(@NotNull LApi lApi, @NotNull GuildMembersChunkEvent event) {
        ArrayList<Member> members = event.getChunkData().getMembers();

        System.out.println("onGuildMembersChunk: " + members.size() + " members, nonce: " + event.getChunkData().getNonce());

        for(Member member : members) {
            System.out.println(member.getUser().getId() + ": " + member.getUser().getUsername());
        }
    }

    @Override
    public void onGuildRoleCreate(@NotNull LApi lApi, @NotNull GuildRoleCreateEvent event) {
        System.out.println("onGuildRoleCreate: " + event.getRole().getName());
    }

    @Override
    public void onGuildRoleUpdate(@NotNull LApi lApi, @NotNull GuildRoleUpdateEvent event) {
        System.out.println("onGuildRoleUpdate: old: " + event.getOldRole().getName() + ", new: " + event.getRole().getName());
    }

    @Override
    public void onGuildRoleDelete(@NotNull LApi lApi, @NotNull GuildRoleDeleteEvent event) {
        System.out.println("onGuildRoleDelete: " + event.getRole().getName());
    }

    @Override
    public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        System.out.println("onMessageCreate");

        Message msg = event.getMessage();
        String content = msg.getContent();
        String channelId = msg.getChannelId();
        User author = msg.getAuthor();

        if(!author.isBot()){
            if(content.toLowerCase().startsWith("hi")) {

                new MessageBuilder(event.getLApi(), "Hi " + author.getUsername())
                        .setReplyTo(msg, true)
                        .getQueueable(channelId)
                        .queue();
            }else if(content.toLowerCase().startsWith("hey")) {
                System.out.println("hey");
                try {
                    lApi.createMessage(event.getChannelId(), new MessageTemplate("look below", false,
                            new Embed[]{new EmbedBuilder().setTitle("Ingore me ><").build()},
                            null, null, new Component[]{new ActionRow(lApi, ComponentType.ACTION_ROW,
                            new Component[]{new Button(lApi, ComponentType.BUTTON,
                                    ButtonStyle.PRIMARY, "Klick mich UwU",
                                    null, "me.linusdev.btn_1", null,
                                    null)}
                    )}, null,
                            /*new Attachment[]{
                                    new AttachmentTemplate("image.png",
                                            "fun image",
                                            Paths.get("C:\\Users\\Linus\\Pictures\\Discord PP\\E0fcU7MVIAIGoil.jpg"),
                                            FileType.JPEG).setAttachmentId(0)
                            }*/null, null)).queue();
                } catch (InvalidEmbedException e) {
                    e.printStackTrace();
                }
            } else if(content.equalsIgnoreCase("info")) {
                MessageBuilder builder = new MessageBuilder(lApi);
                EmbedBuilder embed = new EmbedBuilder();

                embed.setTitle("Info");

                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                StringBuilder ts = new StringBuilder();
                ts.append("");
                for(Thread t : threadSet) {
                    ts.append(t.getName());
                    ts.append(", ");
                }
                embed.setDescription("Threads: " + ts + "\n");

                try {
                    builder.addEmbed(embed.build(false));
                    builder.getQueueable(event.getChannelId()).queue();
                } catch (InvalidEmbedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onNonGuildMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        System.out.println("onNonGuildMessageCreate");
    }

    @Override
    public void onGuildMessageCreate(@NotNull LApi lApi, @NotNull GuildMessageCreateEvent event) {
        System.out.println("onGuildMessageCreate");
    }

    @Override
    public void onVoiceStateUpdate(@NotNull LApi lApi, @NotNull VoiceStateUpdateEvent event) {
        System.out.println("onVoiceStateUpdate");
        System.out.println("updated: " + event.getVoiceState().getData().getJsonString());
        if(!event.isNewVoiceState()){
            System.out.println("old: " + event.getOldVoiceState().getData().getJsonString());
        }

    }

    @Override
    public void onLApiError(@NotNull LApi lApi, @NotNull LApiErrorEvent event) {
        System.out.println("onLApiError in event " + event.getInEvent() + ", error code: " + event.getError().getCode());
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        System.out.println("onInteractionCreate");
        System.out.println("customID: " + event.getCustomId());

        if(event.getCustomId().equals("me.linusdev.btn_1")){
            System.out.println("button pressed");

            InteractionResponseBuilder builder = new InteractionResponseBuilder(event.getLApi(), event.getInteraction());

            builder.channelMessageWithSource(messageBuilder -> {
                messageBuilder.appendContent("good job!")
                        .setFlag(MessageFlag.EPHEMERAL);
            }, true);

            builder.getQueueable().queue();
        }

    }
}
