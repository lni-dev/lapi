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
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.GuildMessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.communication.gateway.presence.StatusType;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
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
import java.util.concurrent.ExecutionException;

public class Test implements EventListener{


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start(true, false);

        LApi lApi = new ConfigBuilder(Helper.getConfigPath())
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .enable(ConfigFlag.CACHE_ROLES)
                .enable(ConfigFlag.CACHE_GUILDS)
                .enable(ConfigFlag.COPY_GUILD_ON_UPDATE_EVENT)
                .enable(ConfigFlag.COPY_ROLE_ON_UPDATE_EVENT)
                .enable(ConfigFlag.CACHE_EMOJIS)
                .enable(ConfigFlag.COPY_EMOJI_ON_UPDATE_EVENT)
                .disable(ConfigFlag.CACHE_VOICE_REGIONS)
                .adjustGatewayConfig(gatewayConfigBuilder -> {
                    gatewayConfigBuilder
                            .setApiVersion(ApiVersion.V9)
                            .setCompression(GatewayCompression.NONE)
                            .setEncoding(GatewayEncoding.JSON)
                            .setOs("Windows 10")
                            .addIntent(GatewayIntent.ALL)
                            .adjustStartupPresence(presence -> {
                                presence.setStatus(StatusType.ONLINE);
                            });
                }).buildLapi();

        lApi.getEventTransmitter().addListener(new Test());



    }


    @Override
    public void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload) {
        System.out.println("onUnknownEvent");
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("onReady");
    }

    @Override
    public void onGuildsReady(@NotNull GuildsReadyEvent event) {
        System.out.println("onGuildsReady");

        for(CachedGuildImpl guild : event.getGuildPool()){
            System.out.println(guild);
        }
    }

    @Override
    public void onLApiReady(@NotNull LApiReadyEvent event) {
        System.out.println("onLApiReady");
    }

    @Override
    public void onGuildCreate(@NotNull GuildCreateEvent event) {
        System.out.println("GuildImpl create: " + event.getCachedGuild().getName());
    }

    @Override
    public void onGuildDelete(@NotNull GuildDeleteEvent event) {
        System.out.println("onGuildDelete");
    }

    @Override
    public void onGuildUpdate(@NotNull GuildUpdateEvent event) {
        System.out.println("onGuildUpdate");
        System.out.println(((Data)event.getPayload().getPayloadData()).getJsonString());
    }

    @Override
    public void onGuildJoined(@NotNull GuildJoinedEvent event) {
        System.out.println("onGuildJoined");
    }

    @Override
    public void onGuildLeft(@NotNull GuildLeftEvent event) {
        System.out.println("onGuildLeft");
    }

    @Override
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        System.out.println("onGuildUnavailable");
    }

    @Override
    public void onGuildAvailable(@NotNull GuildAvailableEvent event) {
        System.out.println("onGuildAvailable");
    }

    @Override
    public void onGuildEmojisUpdate(@NotNull GuildEmojisUpdateEvent event) {
        System.out.println("onGuildEmojisUpdate");

        ListUpdate<EmojiObject> update = event.getUpdate();

        System.out.println("old: " + (update.getOld()));
        System.out.println("updated: " +update.getUpdated());
        System.out.println("added: " +update.getAdded());
        System.out.println("removed: " +update.getRemoved());

    }

    @Override
    public void onGuildRoleCreate(@NotNull GuildRoleCreateEvent event) {
        System.out.println("onGuildRoleCreate: " + event.getRole().getName());
    }

    @Override
    public void onGuildRoleUpdate(@NotNull GuildRoleUpdateEvent event) {
        System.out.println("onGuildRoleUpdate: old: " + event.getOldRole().getName() + ", new: " + event.getRole().getName());
    }

    @Override
    public void onGuildRoleDelete(@NotNull GuildRoleDeleteEvent event) {
        System.out.println("onGuildRoleDelete: " + event.getRole().getName());
    }

    @Override
    public void onMessageCreate(@NotNull MessageCreateEvent event) {
        System.out.println("onMessageCreate");
    }

    @Override
    public void onNonGuildMessageCreate(@NotNull MessageCreateEvent event) {
        System.out.println("onNonGuildMessageCreate");
        Message msg = event.getMessage();
        String content = msg.getContent();
        String channelId = msg.getChannelId();
        User author = msg.getAuthor();

        System.out.println(content);

        if(!author.isBot()){
            if(content.toLowerCase().startsWith("hi")) {

                new MessageBuilder(event.getLApi(), "Hi " + author.getUsername())
                        .setReplyTo(msg, true)
                        .getQueueable(channelId)
                        .queue();
            }else if(content.toLowerCase().startsWith("hey")) {
                System.out.println("hey");
                LApi lApi = event.getLApi();
                try {
                    lApi.createMessage(event.getChannelId(), new MessageTemplate("look below", false,
                            new Embed[]{new EmbedBuilder().setTitle("Ingore me ><").build()},
                            null, null, new Component[]{new ActionRow(lApi, ComponentType.ACTION_ROW,
                            new Component[]{new Button(lApi, ComponentType.BUTTON,
                                    ButtonStyle.PRIMARY, "Klick mich UwU",
                                    null, "me.linusdev.btn_1", null,
                                    null)}
                    )}, null,
                            new Attachment[]{
                                    new AttachmentTemplate("image.png",
                                            "fun image",
                                            Paths.get("C:\\Users\\Linus\\Pictures\\Discord PP\\ezgif-3-909f89a603e6.png"),
                                            FileType.PNG).setAttachmentId(0)
                            })).queue();
                } catch (InvalidEmbedException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    @Override
    public void onGuildMessageCreate(@NotNull GuildMessageCreateEvent event) {
        System.out.println("onGuildMessageCreate");
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
                LApi lApi = event.getLApi();
                try {
                    lApi.createMessage(event.getChannelId(), new MessageTemplate("look below", false,
                            new Embed[]{new EmbedBuilder().setTitle("Ingore me ><").build()},
                            null, null, new Component[]{new ActionRow(lApi, ComponentType.ACTION_ROW,
                            new Component[]{new Button(lApi, ComponentType.BUTTON,
                                    ButtonStyle.PRIMARY, "Klick mich UwU",
                                    null, "me.linusdev.btn_1", null,
                                    null)}
                    )}, null,
                            new Attachment[]{
                                    new AttachmentTemplate("image.png",
                                            "fun image",
                                            Paths.get("C:\\Users\\Linus\\Pictures\\Discord PP\\E0fcU7MVIAIGoil.jpg"),
                                            FileType.JPEG).setAttachmentId(0)
                            })).queue();
                } catch (InvalidEmbedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLApiError(@NotNull LApiErrorEvent event) {
        System.out.println("onLApiError in event " + event.getInEvent() + ", error code: " + event.getError().getCode());
    }

    @Override
    public void onInteractionCreate(@NotNull InteractionCreateEvent event) {
        System.out.println("onInteractionCreate");
        System.out.println("customID: " + event.getCustomId());

        if(event.getCustomId().equals("me.linusdev.btn_1")){
            System.out.println("button pressed");
            event.getLApi().createMessage(event.getInteraction().getChannelId(), "good job!").queue();
        }

    }
}
