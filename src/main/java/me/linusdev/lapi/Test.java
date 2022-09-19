/*
 * Copyright (c) 2021-2022 Linus Andera
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

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.cache.CacheReadyEvent;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.command.RequestGuildMembersCommand;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelPinsUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.ban.GuildBanEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.integration.GuildIntegrationsUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberAddEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberRemoveEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventUserEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.sticker.GuildStickersUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.invite.InviteCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.invite.InviteDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.*;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.MessageReactionEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.MessageReactionRemoveAllEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.MessageReactionRemoveEmojiEvent;
import me.linusdev.lapi.api.communication.gateway.events.presence.PresenceUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.GuildsReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.LApiReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.resumed.ResumedEvent;
import me.linusdev.lapi.api.communication.gateway.events.stage.StageInstanceEvent;
import me.linusdev.lapi.api.communication.gateway.events.thread.*;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.communication.gateway.events.typing.TypingStartEvent;
import me.linusdev.lapi.api.communication.gateway.events.user.UserUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.VoiceServerUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.VoiceStateUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.webhooks.WebhooksUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.presence.StatusType;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayCompression;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayEncoding;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.manager.command.event.CommandManagerInitializedEvent;
import me.linusdev.lapi.api.manager.command.event.CommandManagerReadyEvent;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManagerReadyEvent;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.emoji.StandardEmoji;
import me.linusdev.lapi.api.objects.enums.MessageFlag;
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
import me.linusdev.lapi.api.templates.message.MessageTemplate;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.lapi.helper.Helper;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
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
                .enable(ConfigFlag.CACHE_CHANNELS)
                .enable(ConfigFlag.COPY_CHANNEL_ON_UPDATE_EVENT)
                .enable(ConfigFlag.CACHE_THREADS)
                .disable(ConfigFlag.DO_NOT_REMOVE_ARCHIVED_THREADS)
                .enable(ConfigFlag.COPY_THREAD_ON_UPDATE_EVENT)
                .enable(ConfigFlag.CACHE_PRESENCES)
                .enable(ConfigFlag.COPY_PRESENCE_ON_UPDATE_EVENT)
                .enable(ConfigFlag.COMMAND_MANAGER)
                .adjustGatewayConfig(gatewayConfigBuilder -> {
                    gatewayConfigBuilder
                            .setApiVersion(ApiVersion.V10)
                            .setCompression(GatewayCompression.NONE)
                            .setEncoding(GatewayEncoding.JSON)
                            .setOs("Windows 10")
                            .addIntent(GatewayIntent.ALL)
                            .removeIntent(GatewayIntent.GUILD_PRESENCES)
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
                }).buildLApi();

        lApi.getEventTransmitter().addListener(new Test());

        System.out.println("waitUntilLApiReadyEvent");
        long tiem = System.currentTimeMillis();
        lApi.waitUntilLApiReadyEvent();
        System.out.println("yay took: " + (System.currentTimeMillis() - tiem) + " millis");

    }


    @Override
    public void onUncaughtException(Throwable uncaught) {
        EventListener.super.onUncaughtException(uncaught);
    }

    @Override
    public void onUnknownEvent(@NotNull LApi lApi, @Nullable GatewayEvent type, @Nullable GatewayPayloadAbstract payload) {
        System.out.println("onUnknownEvent");
    }

    @Override
    public void onReady(@NotNull LApi lApi, @NotNull ReadyEvent event) {
        System.out.println("onReady");

        String applicationId = event.getApplication().getId();

        /*lApi.getRequestFactory().createGlobalApplicationCommand(applicationId,
                new ApplicationCommandTemplate("hi", null, "says hi to you",
                        null,
                        null, null, null, null))
                .queue(((applicationCommand, error) -> {
                    if(error != null){
                        System.out.println(error);
                        return;
                    }

                    System.out.println("application command id: " + applicationCommand.getId());
                    System.out.println("application command name: " + applicationCommand.getName());
                }));*/

    }

    @Override
    public void onGuildsReady(@NotNull LApi lApi, @NotNull GuildsReadyEvent event) {
        System.out.println("onGuildsReady");
    }

    @Override
    public void onLApiReady(@NotNull LApi lApi, @NotNull LApiReadyEvent event) {
        System.out.println("onLApiReady");
    }

    @Override
    public void onVoiceRegionManagerReady(@NotNull LApi lApi, @NotNull VoiceRegionManagerReadyEvent event) {
        System.out.println("onLApiReady");
    }

    @Override
    public void onCacheReady(@NotNull LApi lApi, @NotNull CacheReadyEvent event) {
        System.out.println("onCacheReady");
    }

    @Override
    public void onCommandManagerInitialized(@NotNull LApi lApi, @NotNull CommandManagerInitializedEvent event) {
        System.out.println("onCommandManagerInitialized");
    }

    @Override
    public void onCommandManagerReady(@NotNull LApi lApi, @NotNull CommandManagerReadyEvent event) {
        System.out.println("onCommandManagerReady");
    }

    @Override
    public void onResumed(@NotNull LApi lApi, @NotNull ResumedEvent event) {
        System.out.println("onResumed");
    }

    @Override
    public void onChannelCreate(@NotNull LApi lApi, @NotNull ChannelCreateEvent event) {
        System.out.println("onChannelCreate: " + event.getChannel());
    }

    @Override
    public void onChannelUpdate(@NotNull LApi lApi, @NotNull ChannelUpdateEvent event) {
        System.out.println("onChannelUpdate: " + event.getChannel());
    }

    @Override
    public void onChannelDelete(@NotNull LApi lApi, @NotNull ChannelDeleteEvent event) {
        System.out.println("onChannelDelete: " + event.getChannel());
    }

    @Override
    public void onThreadCreate(@NotNull LApi lApi, @NotNull ThreadCreateEvent event) {
        System.out.println("onThreadCreate: " + event.getThread());
    }

    @Override
    public void onThreadUpdate(@NotNull LApi lApi, @NotNull ThreadUpdateEvent event) {
        System.out.println("onThreadUpdate");
    }

    @Override
    public void onThreadDelete(@NotNull LApi lApi, @NotNull ThreadDeleteEvent event) {
        System.out.println("onThreadDelete");
    }

    @Override
    public void onThreadListSync(@NotNull LApi lApi, @NotNull ThreadListSyncEvent event) {
        System.out.println("onThreadListSync");
    }

    @Override
    public void onThreadMemberUpdate(@NotNull LApi lApi, @NotNull ThreadMemberUpdateEvent event) {
        System.out.println("onThreadMemberUpdate");
    }

    @Override
    public void onThreadMembersUpdate(@NotNull LApi lApi, @NotNull ThreadMembersUpdateEvent event) {
        System.out.println("onThreadMembersUpdate");
    }

    @Override
    public void onChannelPinsUpdate(@NotNull LApi lApi, @NotNull ChannelPinsUpdateEvent event) {
        System.out.println("onChannelPinsUpdate: " + event.getChannelId());
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
        System.out.println(((SOData)event.getPayload().getPayloadData()).toJsonString());
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
    public void onGuildBanAdd(@NotNull LApi lApi, @NotNull GuildBanEvent event) {
        EventListener.super.onGuildBanAdd(lApi, event);
    }

    @Override
    public void onGuildBanRemove(@NotNull LApi lApi, @NotNull GuildBanEvent event) {
        EventListener.super.onGuildBanRemove(lApi, event);
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
    public void onGuildIntegrationsUpdate(@NotNull LApi lApi, @NotNull GuildIntegrationsUpdateEvent event) {
        EventListener.super.onGuildIntegrationsUpdate(lApi, event);
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
    public void onGuildScheduledEventCreate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        System.out.println("onGuildScheduledEventCreate: " + event.getGuildScheduledEvent().getData().toJsonString());
    }

    @Override
    public void onGuildScheduledEventUpdate(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        System.out.println("onGuildScheduledEventUpdate: " + event.getGuildScheduledEvent().getData().toJsonString());
    }

    @Override
    public void onGuildScheduledEventDelete(@NotNull LApi lApi, @NotNull GuildScheduledEventEvent event) {
        System.out.println("onGuildScheduledEventDelete: " + event.getGuildScheduledEvent().getData().toJsonString());
    }

    @Override
    public void onGuildScheduledEventUserAdd(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {
        System.out.println("onGuildScheduledEventUserAdd");
    }

    @Override
    public void onGuildScheduledEventUserRemove(@NotNull LApi lApi, @NotNull GuildScheduledEventUserEvent event) {
        System.out.println("onGuildScheduledEventUserRemove");
    }

    @Override
    public void onIntegrationUpdate(@NotNull LApi lApi, @NotNull IntegrationUpdateEvent event) {
        System.out.println("onIntegrationUpdate");
    }

    @Override
    public void onIntegrationDelete(@NotNull LApi lApi, @NotNull IntegrationDeleteEvent event) {
        System.out.println("onIntegrationDelete");
    }

    @Override
    public void onInviteCreate(@NotNull LApi lApi, @NotNull InviteCreateEvent event) {
        System.out.println("onInviteCreate: " + event.getCode());
    }

    @Override
    public void onInviteDelete(@NotNull LApi lApi, @NotNull InviteDeleteEvent event) {
        System.out.println("onInviteDelete: " + event.getCode());
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
                    lApi.getRequestFactory().createMessage(event.getChannelId(), new MessageTemplate("look below", false,
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
    public void onMessageUpdate(@NotNull LApi lApi, @NotNull MessageUpdateEvent event) {
        System.out.println("onMessageUpdate: " + event.getPayload().toJsonString());
    }

    @Override
    public void onMessageDelete(@NotNull LApi lApi, @NotNull MessageDeleteEvent event) {
        System.out.println("onMessageDelete: " + event.getMessageId());
    }

    @Override
    public void onMessageDeleteBulk(@NotNull LApi lApi, @NotNull MessageDeleteBulkEvent event) {
        System.out.println("onMessageDeleteBulk: channel: " + event.getChannelId() + "messages: " + event.getMessageIds());
    }

    @Override
    public void onMessageReactionAdd(@NotNull LApi lApi, @NotNull MessageReactionEvent event) {
        System.out.println("onMessageReactionAdd: " + event.getUserId() + ", " + event.getEmoji());
    }

    @Override
    public void onMessageReactionRemove(@NotNull LApi lApi, @NotNull MessageReactionEvent event) {
        System.out.println("onMessageReactionRemove: " + event.getUserId() + ", " + event.getEmoji());
    }

    @Override
    public void onMessageReactionRemoveAll(@NotNull LApi lApi, @NotNull MessageReactionRemoveAllEvent event) {
        System.out.println("onMessageReactionRemoveAll: " + event.getMessageId());
    }

    @Override
    public void onMessageReactionRemoveEmoji(@NotNull LApi lApi, @NotNull MessageReactionRemoveEmojiEvent event) {
        System.out.println("onMessageReactionRemoveEmoji: " + event.getMessageId() + ", " + event.getEmoji());
    }

    @Override
    public void onPresenceUpdate(@NotNull LApi lApi, @NotNull PresenceUpdateEvent event) {
        System.out.print("onPresenceUpdate: ");
        System.out.println("" + (event.getOldPresence() == null ? "" : event.getOldPresence().getStatus()) + " -> " + event.getPresence().getStatus());

    }

    @Override
    public void onStageInstanceCreate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        System.out.println("onStageInstanceCreate: " + event.getStageInstance().getData().toJsonString());
    }

    @Override
    public void onStageInstanceDelete(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        System.out.println("onStageInstanceDelete: " + event.getStageInstance().getData().toJsonString());
    }

    @Override
    public void onStageInstanceUpdate(@NotNull LApi lApi, @NotNull StageInstanceEvent event) {
        System.out.println("onStageInstanceUpdate: "  + event.getStageInstance().getData().toJsonString());
    }

    @Override
    public void onTypingStart(@NotNull LApi lApi, @NotNull TypingStartEvent event) {
        System.out.println("onTypingStart");

        if(event.getGuildId() == null) {
            MessageBuilder msg = new MessageBuilder(lApi, "Hi ");
            msg.appendUserMention(event.getUserId());
            msg.getQueueable(event.getChannelId()).queue();
        }
    }

    @Override
    public void onUserUpdate(@NotNull LApi lApi, @NotNull UserUpdateEvent event) {
        System.out.println("onUserUpdate: " + event.getUser().getData().toJsonString());
    }

    @Override
    public void onVoiceStateUpdate(@NotNull LApi lApi, @NotNull VoiceStateUpdateEvent event) {
        System.out.println("onVoiceStateUpdate");
        System.out.println("updated: " + event.getVoiceState().getData().toJsonString());
        if(!event.isNewVoiceState()){
            System.out.println("old: " + event.getOldVoiceState().getData().toJsonString());
        }

    }

    @Override
    public void onVoiceServerUpdate(@NotNull LApi lApi, @NotNull VoiceServerUpdateEvent event) {
        System.out.println("onVoiceServerUpdate: token=" + event.getToken() + ", endpoint=" + event.getEndpoint());
    }

    @Override
    public void onWebhooksUpdate(@NotNull LApi lApi, @NotNull WebhooksUpdateEvent event) {
        System.out.println("onWebhooksUpdate: guildId=" + event.getGuildId() + ", channelId=" + event.getChannelId());
    }

    @Override
    public void onLApiError(@NotNull LApi lApi, @NotNull LApiErrorEvent event) {
        System.out.println("onLApiError in event " + event.getInEvent() + ", error code: " + event.getError().getCode());
    }

    @Override
    public void onInteractionCreate(@NotNull LApi lApi, @NotNull InteractionCreateEvent event) {
        System.out.println("onInteractionCreate");
        System.out.println("customID: " + event.getCustomId());

        if(event.hasCustomId() && event.getCustomId().equals("me.linusdev.btn_1")){
            System.out.println("button pressed");

            InteractionResponseBuilder builder = new InteractionResponseBuilder(event.getLApi(), event.getInteraction());

            builder.channelMessageWithSource(messageBuilder -> {
                messageBuilder.appendContent("https://tenor.com/view/milim-milim-nava-that-time-i-got-reincarnated-as-a-slime-cute-tensura-gif-17182466")
                        .setFlag(MessageFlag.EPHEMERAL);
            }, true);

            builder.getQueueable().queue();

        } else if(event.hasCommandId() && event.getCommandId().equals("1015622878298919026")){
            InteractionResponseBuilder builder = new InteractionResponseBuilder(event.getLApi(), event.getInteraction());

            builder.channelMessageWithSource(messageBuilder -> {
                messageBuilder.appendContent("Hi ").appendEmoji(StandardEmoji.getRandom());
            }, true);

            builder.getQueueable().queue();
        }

    }
}
