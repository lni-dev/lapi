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

package me.linusdev.lapi.api.communication.gateway.websocket;

import me.linusdev.data.Datable;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.exceptions.LApiException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommand;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommandType;
import me.linusdev.lapi.api.communication.gateway.enums.*;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelPinsUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiError;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.ban.GuildBanEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.integration.GuildIntegrationsUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberAddEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberRemoveEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMemberChunkEventData;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventUserAddRemoveData;
import me.linusdev.lapi.api.communication.gateway.events.guild.scheduledevent.GuildScheduledEventUserEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.sticker.GuildStickersUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.integration.IntegrationUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.invite.InviteCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.invite.InviteDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageDeleteBulkEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.reaction.*;
import me.linusdev.lapi.api.communication.gateway.events.presence.PresenceUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.resumed.ResumedEvent;
import me.linusdev.lapi.api.communication.gateway.events.stage.StageInstanceEvent;
import me.linusdev.lapi.api.communication.gateway.events.thread.*;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.lapi.api.communication.gateway.events.typing.TypingStartEvent;
import me.linusdev.lapi.api.communication.gateway.events.typing.TypingStartEventFields;
import me.linusdev.lapi.api.communication.gateway.events.user.UserUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.VoiceServerUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.voice.VoiceStateUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.webhooks.WebhooksUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.identify.ConnectionProperties;
import me.linusdev.lapi.api.communication.gateway.identify.Identify;
import me.linusdev.lapi.api.communication.gateway.other.GatewayPayload;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.queue.DispatchEventQueue;
import me.linusdev.lapi.api.communication.gateway.queue.ReceivedPayload;
import me.linusdev.lapi.api.communication.gateway.queue.processor.DispatchEventProcessorFactory;
import me.linusdev.lapi.api.communication.gateway.resume.Resume;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.communication.http.request.LApiHttpHeader;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.guild.member.MemberManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.guild.scheduledevent.GuildScheduledEventManager;
import me.linusdev.lapi.api.manager.guild.thread.ThreadManager;
import me.linusdev.lapi.api.manager.guild.thread.ThreadMemberUpdate;
import me.linusdev.lapi.api.manager.guild.thread.ThreadUpdate;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStateManager;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.manager.presence.PresenceManager;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.guild.GuildImpl;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.guild.scheduledevent.GuildScheduledEvent;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import me.linusdev.lapi.api.objects.integration.Integration;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.message.AnyMessage;
import me.linusdev.lapi.api.objects.message.concrete.CreateEventMessage;
import me.linusdev.lapi.api.objects.message.concrete.UpdateEventMessage;
import me.linusdev.lapi.api.objects.presence.PresenceUpdate;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.stage.StageInstance;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.thread.LApiThreadFactory;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.net.SocketException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#gateways" target="_top">Gateways</a>
 */
public class GatewayWebSocket implements WebSocket.Listener, HasLApi, Datable {


    public static final String QUERY_STRING_API_VERSION_KEY = "v";
    public static final String QUERY_STRING_ENCODING_KEY = "encoding";
    public static final String QUERY_STRING_COMPRESS_KEY = "compress";

    public static final String HEARTBEAT_INTERVAL_KEY = "heartbeat_interval";

    //Some payload fields
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String ROLE_KEY = "role";
    public static final String ROLE_ID_KEY = "role_id";
    public static final String EMOJIS_KEY = "emojis";
    public static final String STICKERS_KEY = "stickers";
    public static final String USER_KEY = "user";
    public static final String CHANNEL_ID_KEY = "channel_id";
    public static final String LAST_PIN_TIMESTAMP = "last_pin_timestamp";
    public static final String MESSAGE_ID_KEY = "message_id";
    public static final String EMOJI_KEY = "emoji";

    public static final ExceptionConverter<String, GatewayPayloadAbstract, Exception> STANDARD_JSON_TO_PAYLOAD_CONVERTER = convertible -> {
        StringReader reader = new StringReader(convertible);
        SOData data = new JsonParser().parseReader(reader);
        return GatewayPayload.fromData(data);
    };

    public static final UnexpectedEventHandler STANDARD_UNEXPECTED_EVENT_HANDLER = new UnexpectedEventHandler() {
        @Override
        public void handleError(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull Throwable error) {
            error.printStackTrace();
        }

        @Override
        public boolean handleUnexpectedClose(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull GatewayCloseStatusCode closeStatusCode, String reason) {
            new LApiException("Discord closed its output. Status-code: " + closeStatusCode + ", reason: " + reason + ". Gateway will try to reconnect").printStackTrace();
            return true;
        }

        @Override
        public void onFatal(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull String information, @Nullable Throwable cause) {
            new LApiException("Fatal Error in the GatewayWebSocket! Gateway is now closed! " + information).printStackTrace();
        }
    };

    //Generate Data keys
    public static final String SESSION_ID_KEY = "sessionId";
    public static final String CAN_RESUME_KEY = "can_resume";
    public static final String DISPATCH_EVENT_QUEUE_KEY = "dispatch_event_queue";
    public static final String HEARTBEATS_SENT_KEY = "heartbeats_sent";
    public static final String HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY = "heartbeat_acks_received";
    public static final String DATA_GENERATED_TIME_MILLIS_KEY = "generated_at";


    private final @NotNull LApiImpl lApi;

    private final EventTransmitter transmitter;

    private final @NotNull String token;
    private final @NotNull ConnectionProperties properties;

    private final @NotNull ApiVersion apiVersion;
    private final @NotNull GatewayEncoding encoding;
    private final @NotNull GatewayCompression compression;
    private final int largeThreshold;
    private final boolean usesSharding;
    private final int shardId;
    private final int numShards;
    private final @NotNull SelfUserPresenceUpdater selfPresence;
    private final @NotNull GatewayIntent[] intents;

    private WebSocket webSocket = null;

    private DispatchEventQueue dispatchEventQueue;
    private long heartbeatInterval;
    private String sessionId;

    private final AtomicLong heartbeatsSent;
    private final AtomicLong heartbeatAcknowledgementsReceived;

    private final AtomicBoolean canResume;

    private String currentText = null;
    private ArrayList<ByteBuffer> currentBytes = null;

    private final ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatFuture;

    //Converter and handler
    private final @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter;
    private final ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> bytesToPayloadConverter;

    private UnexpectedEventHandler unexpectedEventHandler = null;

    /**
     * if this is above 1, we have tried to connect 2 or more times in a row. Something is probably wrong then
     */
    private final AtomicInteger pendingConnects;

    private final LogInstance logger;

    /**
     * Queue for commands. Is currently periodically checked, when a Heartbeat ack is received. (in {@link #handleReceivedPayload(GatewayPayloadAbstract)})
     */
    private final ConcurrentLinkedQueue<GatewayCommand> commandQueue;
    private final AtomicBoolean queueWorking = new AtomicBoolean(false);
    
    public GatewayWebSocket(@NotNull LApiImpl lApi, @NotNull EventTransmitter transmitter, @NotNull Config config){
        this(lApi, transmitter, config.getToken(),
                config.getGatewayConfig().getApiVersion(),
                config.getGatewayConfig().getEncoding(),
                config.getGatewayConfig().getCompression(),
                config.getGatewayConfig().getOs(),
                config.getGatewayConfig().getLargeThreshold(),
                config.getGatewayConfig().getShardId(),
                config.getGatewayConfig().getNumShards(),
                config.getGatewayConfig().getStartupPresence(),
                config.getGatewayConfig().getIntents(),
                config.getGatewayConfig().getJsonToPayloadConverter(),
                config.getGatewayConfig().getEtfToPayloadConverter(),
                config.getGatewayConfig().getUnexpectedEventHandler(),
                config.getGatewayConfig().getDispatchEventQueueSize(),
                config.getGatewayConfig().getDispatchEventProcessorFactory()
        );
    }

    /**
     * You should not use this, as the @Nullable annotations are possibly wrong. That is because the checking of Nullability was
     * moved to {@link me.linusdev.lapi.api.config.ConfigBuilder ConfigBuilder} and
     * {@link me.linusdev.lapi.api.config.GatewayConfigBuilder GatewayConfigBuilder}.<br>
     * Use {@link GatewayWebSocket#GatewayWebSocket(LApiImpl, EventTransmitter, Config)} instead.
     */
    @ApiStatus.Internal
    private GatewayWebSocket(@NotNull LApiImpl lApi, @NotNull EventTransmitter transmitter, @NotNull String token, @Nullable ApiVersion apiVersion,
                             @Nullable GatewayEncoding encoding, @Nullable GatewayCompression compression,
                             @NotNull String os, @NotNull Integer largeThreshold, @Nullable Integer shardId,
                             @Nullable Integer numShards, @NotNull SelfUserPresenceUpdater selfPresence, @NotNull GatewayIntent[] intents,
                             @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter,
                             ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> bytesToPayloadConverter,
                             @NotNull UnexpectedEventHandler unexpectedEventHandler, int dispatchEventQueueSize,
                             @NotNull DispatchEventProcessorFactory dispatchEventProcessorFactory) {
        this.lApi = lApi;
        this.unexpectedEventHandler = unexpectedEventHandler;
        this.transmitter = transmitter;
        this.token = token;
        this.properties = new ConnectionProperties(os, LApi.LAPI_NAME, LApi.LAPI_NAME);
        this.largeThreshold = largeThreshold;

        if (shardId != null && numShards != null) {
            this.usesSharding = true;
            this.shardId = shardId;
            this.numShards = numShards;

        } else {
            this.usesSharding = false;
            this.shardId = 0;
            this.numShards = 0;

        }

        this.selfPresence = selfPresence.setGateway(this);
        this.intents = intents;


        if (apiVersion == null) apiVersion = ApiVersion.V9;
        if (encoding == null) encoding = GatewayEncoding.JSON;
        if (compression == null) compression = GatewayCompression.NONE;

        this.apiVersion = apiVersion;
        this.encoding = encoding;
        this.compression = compression;

        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(new LApiThreadFactory(lApi, true, "Gateway Heartbeat Thread"));

        this.logger = Logger.getLogger(GatewayWebSocket.class.getSimpleName(), Logger.Type.DEBUG);

        this.canResume = new AtomicBoolean(false);

        this.dispatchEventQueue = new DispatchEventQueue(dispatchEventQueueSize);
        this.dispatchEventQueue.setProcessor(
                dispatchEventProcessorFactory.newInstance(lApi, dispatchEventQueue, this));

        this.heartbeatsSent = new AtomicLong(0);
        this.heartbeatAcknowledgementsReceived = new AtomicLong(0);
        this.pendingConnects = new AtomicInteger(0);

        this.jsonToPayloadConverter = jsonToPayloadConverter;
        this.bytesToPayloadConverter = bytesToPayloadConverter;

        this.commandQueue = new ConcurrentLinkedQueue<>();
    }

    public void start() {
        try {
            LApiHttpHeader authenticationHeader = lApi.getAuthorizationHeader();
            LApiHttpHeader userAgentHeader = lApi.getUserAgentHeader();

            HttpClient client = lApi.getClient();

            WebSocket.Builder builder = client.newWebSocketBuilder()
                    .header(authenticationHeader.getName(), authenticationHeader.getValue())
                    .header(userAgentHeader.getName(), userAgentHeader.getValue());

            // If we have no internet connection, LApi will automatically delay this request
            // until we have internet connection again
            lApi.getRequestFactory().getGatewayBot().queue((getGatewayResponse, response, error) -> {
                try {
                    if (error != null) {
                        error.log(logger);
                        if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error.asThrowable());
                        return;
                    }

                    //TODO check session start limit

                    URI uri = new URI(getGatewayResponse.getUrl()
                            + "?" + QUERY_STRING_API_VERSION_KEY + "=" + apiVersion.getVersionNumber()
                            + "&" + QUERY_STRING_ENCODING_KEY + "=" + encoding.getValue()
                            + (compression.getValue() != null ? "&" + QUERY_STRING_COMPRESS_KEY + "=" + compression.getValue() : ""));

                    logger.debug("Gateway connecting to " + uri);

                    final GatewayWebSocket _this = this;
                    pendingConnects.incrementAndGet();
                    this.webSocket = null;
                    builder.buildAsync(uri, this).whenComplete((webSocket, throwable) -> {

                        if(throwable != null){
                            logger.error("Could not build web socket! We will try again");
                            logger.error(throwable);
                            // if this happens, we should have an internet connection, because getGatewayBot worked...
                            // so let's try again
                            if(pendingConnects.get() < 4){
                                start();
                                return;
                            }

                            // we already tried 3+ times
                            if(unexpectedEventHandler != null) unexpectedEventHandler.onFatal(lApi, _this, "The web socket could not be build several times in a row", throwable);
                            return;
                        }

                        this.webSocket = webSocket;
                        logger.debug("build async finished");
                    });

                } catch (Exception e) {
                    logger.error(e);
                    if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, e);
                }
            });


        } catch (Exception error) {
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }

    }


    /**
     * Handle {@link GatewayOpcode#DISPATCH Dispatch} events.<br>
     *
     * These are not {@link GatewayOpcode#DISPATCH Dispatch} events and not handled here:
     * <ul>
     *     <li>{@link GatewayEvent#HELLO}</li>
     *     <li>{@link GatewayEvent#INVALID_SESSION}</li>
     *     <li>{@link GatewayEvent#RECONNECT}</li>
     * </ul>
     *
     * Note: {@link GatewayEvent#GUILD_CREATE} must be handled as first event for every guild,
     * otherwise an {@link IllegalStateException} will be thrown.
     *
     * @param payload {@link GatewayPayloadAbstract}
     */
    @ApiStatus.Internal
    public void handleReceivedEvent(@NotNull GatewayPayloadAbstract payload) {
        try {
            @Nullable GatewayEvent type = payload.getType();
            @Nullable SOData innerPayload = (SOData) payload.getPayloadData();

            if (type == null) {
                transmitter.onUnknownEvent(lApi, null, payload);
                return;
            }

            //handle Events, that do not require a innerPayload Data here:
            if (type == GatewayEvent.RESUMED) {
                //related gateway logic is done in handleReceivedPayload
                transmitter.onResumed(lApi, new ResumedEvent(lApi, payload));
                return;
            }

            //Handle events, that do require a innerPayload Data here:
            @Nullable GuildManager guildManager = lApi.getGuildManager();
            if (innerPayload == null) throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");
            @NotNull SOData data = innerPayload;

            switch (type) {
                case READY:
                    //related gateway logic is done in handleReceivedPayload
                    transmitter.onReady(lApi,  ReadyEvent.fromData(lApi, payload, data));
                    break;

                case CHANNEL_CREATE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel
                            //TODO: cache non guild channels
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    null, Channel.channelFromData(lApi, data)));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), Channel.channelFromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), Channel.channelFromData(lApi, data)));
                            break;
                        }

                        ListManager<Channel> channelManager = guild.getChannelManager();

                        if(channelManager == null) {
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), Channel.channelFromData(lApi, data)));
                            break;
                        }

                        Channel channel = channelManager.onAdd(data);
                        transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                Snowflake.fromString(guildId), channel));
                    }
                    break;

                case CHANNEL_UPDATE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel
                            //TODO: cache non guild channels
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    null, new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        ListManager<Channel> channelManager = guild.getChannelManager();

                        if(channelManager == null) {
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }


                        Update<Channel, Channel> update = channelManager.onUpdate(data);

                        if(update == null) {
                            //ChannelManager did not contain this channel -> error
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_CHANNEL, null)));
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), update));
                    }
                    break;

                case CHANNEL_DELETE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel
                            //TODO: cache non guild channels
                            transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                    null, null, Channel.channelFromData(lApi, data)));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                    Snowflake.fromString(guildId), null, Channel.channelFromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                    Snowflake.fromString(guildId), null, Channel.channelFromData(lApi, data)));
                            break;
                        }

                        ListManager<Channel> channelManager = guild.getChannelManager();

                        if(channelManager == null) {
                            transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                    Snowflake.fromString(guildId), null, Channel.channelFromData(lApi, data)));
                            break;
                        }

                        Channel received = Channel.channelFromData(lApi, data);
                        Channel deleted = channelManager.onDelete(received.getId());

                        if(deleted == null) {
                            //ChannelManager did not contain this channel -> error
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_CHANNEL, null)));
                            transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                    Snowflake.fromString(guildId), null, Channel.channelFromData(lApi, data)));
                            break;
                        }

                        transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                Snowflake.fromString(guildId), deleted, received));
                    }
                    break;

                case CHANNEL_PINS_UPDATE:
                    {
                        @Nullable String guildId = (String) data.get(Channel.GUILD_ID_KEY);
                        @NotNull String channelId = (String) data.getAndRequireNotNull(CHANNEL_ID_KEY, InvalidDataException.SUPPLIER);
                        @Nullable ISO8601Timestamp lastPinTimestamp = data.getAndConvert(LAST_PIN_TIMESTAMP, ISO8601Timestamp::fromString);

                        ChannelPinsUpdateEvent event = new ChannelPinsUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), Snowflake.fromString(channelId), lastPinTimestamp);

                        transmitter.onChannelPinsUpdate(lApi, event);

                        //TODO: It would maybe be a good Idea, to have an option to keep the channel pins up-to-date
                        //That would mean, if this event is triggered, the pins for given channel should be retrieved
                        //AND if a message is deleted in a channel which has pins, it needs to be checked if the deleted
                        //message was a pinned message
                    }

                    break;

                case THREAD_CREATE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel. This should never, but let's keep it here just in case
                            transmitter.onThreadCreate(lApi, new ThreadCreateEvent(lApi, payload, null,
                                    new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onThreadCreate(lApi, new ThreadCreateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onThreadCreate(lApi, new ThreadCreateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        ThreadManager threadManager = guild.getThreadManager();
                        if(threadManager == null) {
                            transmitter.onThreadCreate(lApi, new ThreadCreateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new Update<>(null, Channel.channelFromData(lApi, data))));
                            break;
                        }

                        Update<Channel, Channel> update = threadManager.onCreate(data);
                        transmitter.onThreadCreate(lApi, new ThreadCreateEvent(lApi, payload, Snowflake.fromString(guildId),
                                update));
                    }
                    break;

                case THREAD_UPDATE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel. This should never, but let's keep it here just in case
                            transmitter.onThreadUpdate(lApi, new ThreadUpdateEvent(lApi, payload, null,
                                    new ThreadUpdate(null, Channel.channelFromData(lApi, data), false, false)));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onThreadUpdate(lApi, new ThreadUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new ThreadUpdate(null, Channel.channelFromData(lApi, data), false, false)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onThreadUpdate(lApi, new ThreadUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new ThreadUpdate(null, Channel.channelFromData(lApi, data), false, false)));
                            break;
                        }

                        ThreadManager threadManager = guild.getThreadManager();
                        if(threadManager == null) {
                            transmitter.onThreadUpdate(lApi, new ThreadUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new ThreadUpdate(null, Channel.channelFromData(lApi, data), false, false)));
                            break;
                        }

                        ThreadUpdate update = threadManager.onUpdate(data);
                        transmitter.onThreadUpdate(lApi, new ThreadUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                update));
                    }
                    break;

                case THREAD_DELETE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel. This should never, but let's keep it here just in case
                            transmitter.onThreadDelete(lApi, new ThreadDeleteEvent(lApi, payload, null,
                                    Channel.channelFromData(lApi, data)));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onThreadDelete(lApi, new ThreadDeleteEvent(lApi, payload, Snowflake.fromString(guildId),
                                    Channel.channelFromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onThreadDelete(lApi, new ThreadDeleteEvent(lApi, payload, Snowflake.fromString(guildId),
                                    Channel.channelFromData(lApi, data)));
                            break;
                        }

                        ThreadManager threadManager = guild.getThreadManager();
                        if(threadManager == null) {
                            transmitter.onThreadDelete(lApi, new ThreadDeleteEvent(lApi, payload, Snowflake.fromString(guildId),
                                    Channel.channelFromData(lApi, data)));
                            break;
                        }

                        Channel deleted = threadManager.onDelete(data);
                        transmitter.onThreadDelete(lApi, new ThreadDeleteEvent(lApi, payload, Snowflake.fromString(guildId),
                                deleted == null ? Channel.channelFromData(lApi, data) : deleted));
                    }
                    break;

                case THREAD_LIST_SYNC:
                    {
                        ThreadListSyncData threadListSyncData = ThreadListSyncData.fromData(lApi, data);

                        if(guildManager == null) {
                            transmitter.onThreadListSync(lApi, new ThreadListSyncEvent(lApi, payload, threadListSyncData.getGuildIdAsSnowflake(),
                                    threadListSyncData, null));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(threadListSyncData.getGuildId());
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onThreadListSync(lApi, new ThreadListSyncEvent(lApi, payload, threadListSyncData.getGuildIdAsSnowflake(),
                                    threadListSyncData, null));
                            break;
                        }

                        ThreadManager threadManager = guild.getThreadManager();
                        if(threadManager == null) {
                            transmitter.onThreadListSync(lApi, new ThreadListSyncEvent(lApi, payload, threadListSyncData.getGuildIdAsSnowflake(),
                                    threadListSyncData, null));
                            break;
                        }

                        ListUpdate<Channel> update = threadManager.onThreadListSync(threadListSyncData);
                        transmitter.onThreadListSync(lApi, new ThreadListSyncEvent(lApi, payload, threadListSyncData.getGuildIdAsSnowflake(),
                                threadListSyncData, update));
                    }
                    break;

                case THREAD_MEMBER_UPDATE:
                    {
                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel. This should never, but let's keep it here just in case
                            transmitter.onThreadMemberUpdate(lApi, new ThreadMemberUpdateEvent(lApi, payload, null,
                                    new ThreadMemberUpdate(null, null, ThreadMember.fromData(data))));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onThreadMemberUpdate(lApi, new ThreadMemberUpdateEvent(lApi, payload, null,
                                    new ThreadMemberUpdate(null, null, ThreadMember.fromData(data))));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onThreadMemberUpdate(lApi, new ThreadMemberUpdateEvent(lApi, payload, null,
                                    new ThreadMemberUpdate(null, null, ThreadMember.fromData(data))));
                            break;
                        }

                        ThreadManager threadManager = guild.getThreadManager();
                        if(threadManager == null) {
                            transmitter.onThreadMemberUpdate(lApi, new ThreadMemberUpdateEvent(lApi, payload, null,
                                    new ThreadMemberUpdate(null, null, ThreadMember.fromData(data))));
                            break;
                        }

                        ThreadMemberUpdate update = threadManager.onThreadMemberUpdate(data);
                        transmitter.onThreadMemberUpdate(lApi, new ThreadMemberUpdateEvent(lApi, payload, null,
                                update));
                    }
                    break;

                case THREAD_MEMBERS_UPDATE:
                    {
                        ThreadMembersUpdateData threadMembersUpdateData = ThreadMembersUpdateData.fromData(lApi, data);
                        transmitter.onThreadMembersUpdate(lApi, new ThreadMembersUpdateEvent(lApi, payload, threadMembersUpdateData.getGuildIdAsSnowflake(),
                                threadMembersUpdateData));

                        if(guildManager == null) break;


                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(threadMembersUpdateData.getGuildId());
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onThreadMembersUpdate(lApi, new ThreadMembersUpdateEvent(lApi, payload, threadMembersUpdateData.getGuildIdAsSnowflake(),
                                    threadMembersUpdateData));
                            break;
                        }

                        ThreadManager threadManager = guild.getThreadManager();
                        if(threadManager == null) break;
                        threadManager.onThreadMembersUpdate(threadMembersUpdateData);
                    }

                    break;

                case GUILD_CREATE:
                    {
                        if (guildManager == null) {
                            //we can't do this event properly if CACHE_GUILDS is disabled.
                            GuildImpl guild = GuildImpl.fromData(lApi, data);
                            transmitter.onGuildCreate(lApi, new GuildCreateEvent(lApi, payload, guild));
                            break;
                        }

                        OnGuildCreateReturn ret = guildManager.onGuildCreate(payload);
                        transmitter.onGuildCreate(lApi, new GuildCreateEvent(lApi, payload, ret.guild));

                        if (ret.isNew) {
                            transmitter.onGuildJoined(lApi, new GuildJoinedEvent(lApi, ret.guild));
                        } else if (ret.becameAvailable) {
                            transmitter.onGuildAvailable(lApi, new GuildAvailableEvent(lApi, ret.guild));
                        }
                    }
                    break;

                case GUILD_UPDATE:
                    {
                        if (guildManager == null) {
                            //we can't do this event properly if CACHE_GUILDS is disabled.
                            GuildImpl guild = GuildImpl.fromData(lApi, data);
                            transmitter.onGuildUpdate(lApi, new GuildUpdateEvent(lApi, payload, guild));
                            break;
                        }

                        Update<CachedGuildImpl, Guild> update = guildManager.onGuildUpdate(payload);
                        transmitter.onGuildUpdate(lApi, new GuildUpdateEvent(lApi, payload, update));
                    }
                    break;

                case GUILD_DELETE:
                {
                        if (guildManager == null) {
                            String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                            transmitter.onGuildDelete(lApi, new GuildDeleteEvent(lApi, payload, guildId));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.onGuildDelete(payload);
                        transmitter.onGuildDelete(lApi, new GuildDeleteEvent(lApi, payload, guild));

                        if (guild.isRemoved()) {
                            //If this is set by the GuildManager, it means, the current user left the guild
                            transmitter.onGuildLeft(lApi, new GuildLeftEvent(lApi, guild));
                        } else {
                            //The GuildImpl became unavailable
                            transmitter.onGuildUnavailable(lApi, new GuildUnavailableEvent(lApi, guild));
                        }
                    }
                    break;

                case GUILD_BAN_ADD:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        SOData user = (SOData) data.getAndRequireNotNull(USER_KEY, InvalidDataException.SUPPLIER);

                        GuildBanEvent event = new GuildBanEvent(lApi, payload, Snowflake.fromString(guildId),
                                User.fromData(lApi, user));

                        transmitter.onGuildBanAdd(lApi, event);
                    }
                    break;

                case GUILD_BAN_REMOVE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        SOData user = (SOData) data.getAndRequireNotNull(USER_KEY, InvalidDataException.SUPPLIER);

                        GuildBanEvent event = new GuildBanEvent(lApi, payload, Snowflake.fromString(guildId),
                                User.fromData(lApi, user));

                        transmitter.onGuildBanRemove(lApi, event);
                    }
                    break;

                case GUILD_EMOJIS_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        ArrayList<SOData> emojisData = data.getListAndConvert(EMOJIS_KEY, convertible -> (SOData) convertible);

                        if(emojisData == null) throw InvalidDataException.SUPPLIER.supply(data, EMOJIS_KEY);

                        if (guildManager == null) {
                            transmitter.onGuildEmojisUpdate(lApi,
                                    new GuildEmojisUpdateEvent(lApi, payload, null, emojisData, null, null));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);

                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildEmojisUpdate(lApi,
                                    new GuildEmojisUpdateEvent(lApi, payload, null, emojisData, null, null));
                            break;
                        }

                        ListManager<EmojiObject> emojiManager = guild.getEmojiManager();

                        if (emojiManager == null) {
                            transmitter.onGuildEmojisUpdate(lApi,
                                    new GuildEmojisUpdateEvent(lApi, payload, null, emojisData, null, null));
                            break;
                        }

                        ListUpdate<EmojiObject> update = emojiManager.onUpdate(emojisData);
                        transmitter.onGuildEmojisUpdate(lApi,
                                new GuildEmojisUpdateEvent(lApi, payload, null, emojisData, update, emojiManager));
                    }
                    break;

                case GUILD_STICKERS_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        ArrayList<SOData> stickersData = data.getListAndConvert(STICKERS_KEY, convertible -> (SOData) convertible);

                        if(stickersData == null) throw InvalidDataException.SUPPLIER.supply(data, STICKERS_KEY);

                        if (guildManager == null) {
                            transmitter.onGuildStickersUpdate(lApi,
                                    new GuildStickersUpdateEvent(lApi, payload, null, stickersData, null, null));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);

                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildStickersUpdate(lApi,
                                    new GuildStickersUpdateEvent(lApi, payload, null, stickersData, null, null));
                            break;
                        }

                        ListManager<Sticker> stickerManager = guild.getStickerManager();

                        if (stickerManager == null) {
                            transmitter.onGuildStickersUpdate(lApi,
                                    new GuildStickersUpdateEvent(lApi, payload, null, stickersData, null, null));
                            break;
                        }

                        ListUpdate<Sticker> update = stickerManager.onUpdate(stickersData);
                        transmitter.onGuildStickersUpdate(lApi,
                                new GuildStickersUpdateEvent(lApi, payload, null, stickersData, update, stickerManager));
                    }
                    break;

                case GUILD_INTEGRATIONS_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        transmitter.onGuildIntegrationsUpdate(lApi,
                                new GuildIntegrationsUpdateEvent(lApi, payload, Snowflake.fromString(guildId)));
                    }
                    break;

                case GUILD_MEMBER_ADD:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if(guildManager == null) {
                            transmitter.onGuildMemberAdd(lApi,
                                    new GuildMemberAddEvent(lApi, payload, Snowflake.fromString(guildId),
                                            Member.fromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildMemberAdd(lApi,
                                    new GuildMemberAddEvent(lApi, payload, Snowflake.fromString(guildId),
                                            Member.fromData(lApi, data)));
                            break;
                        }

                        MemberManager memberManager = guild.getMemberManager();
                        if (memberManager == null) {
                            transmitter.onGuildMemberAdd(lApi, new GuildMemberAddEvent(lApi,
                                    payload, Snowflake.fromString(guildId), Member.fromData(lApi, data)));
                            break;
                        }

                        Member member = memberManager.onMemberAdd(data);
                        transmitter.onGuildMemberAdd(lApi, new GuildMemberAddEvent(lApi,
                                payload, Snowflake.fromString(guildId), member));
                    }
                    break;

                case GUILD_MEMBER_REMOVE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        SOData userData = (SOData) data.getAndRequireNotNull(Member.USER_KEY, InvalidDataException.SUPPLIER);
                        String userId = (String) userData.getAndRequireNotNull(User.ID_KEY, InvalidDataException.SUPPLIER);

                        if(guildManager == null) {
                            transmitter.onGuildMemberRemove(lApi, new GuildMemberRemoveEvent(lApi,
                                    payload, Snowflake.fromString(guildId), null, User.fromData(lApi, userData)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildMemberRemove(lApi, new GuildMemberRemoveEvent(lApi,
                                    payload, Snowflake.fromString(guildId), null, User.fromData(lApi, userData)));
                            break;
                        }

                        MemberManager memberManager = guild.getMemberManager();
                        if (memberManager == null) {
                            transmitter.onGuildMemberRemove(lApi, new GuildMemberRemoveEvent(lApi,
                                    payload, Snowflake.fromString(guildId), null, User.fromData(lApi, userData)));
                            break;
                        }

                        Member member = memberManager.onMemberRemove(userId);
                        transmitter.onGuildMemberRemove(lApi, new GuildMemberRemoveEvent(lApi,
                                payload, Snowflake.fromString(guildId), member, User.fromData(lApi, userData)));
                    }
                    break;

                case GUILD_MEMBER_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        SOData userData = (SOData) data.getAndRequireNotNull(Member.USER_KEY, InvalidDataException.SUPPLIER);
                        String userId = (String) userData.getAndRequireNotNull(User.ID_KEY, InvalidDataException.SUPPLIER);

                        if(guildManager == null) {
                            transmitter.onGuildMemberUpdate(lApi, new GuildMemberUpdateEvent(
                                    lApi, payload, Snowflake.fromString(guildId), data
                            ));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildMemberUpdate(lApi, new GuildMemberUpdateEvent(
                                    lApi, payload, Snowflake.fromString(guildId), data
                            ));
                            break;
                        }

                        MemberManager memberManager = guild.getMemberManager();
                        if (memberManager == null) {
                            transmitter.onGuildMemberUpdate(lApi, new GuildMemberUpdateEvent(
                                    lApi, payload, Snowflake.fromString(guildId), data
                            ));
                            break;
                        }

                        Update<Member, Member> update = memberManager.onMemberUpdate(userId, data);
                        if (update == null) {
                            //MemberManager didn't contain this role...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_MEMBER, null)));
                            transmitter.onGuildMemberUpdate(lApi, new GuildMemberUpdateEvent(
                                    lApi, payload, Snowflake.fromString(guildId), data
                            ));
                            break;
                        }
                        transmitter.onGuildMemberUpdate(lApi, new GuildMemberUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), update));
                    }
                    break;

                case GUILD_MEMBERS_CHUNK:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        transmitter.onGuildMembersChunk(lApi, new GuildMembersChunkEvent(lApi,
                                payload, Snowflake.fromString(guildId),
                                GuildMemberChunkEventData.fromData(lApi, data)));
                    }
                    break;

                case GUILD_ROLE_CREATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        SOData roleData = (SOData) data.getAndRequireNotNull(ROLE_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onGuildRoleCreate(lApi, new GuildRoleCreateEvent(lApi, payload, Snowflake.fromString(guildId), Role.fromData(lApi, roleData)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild != null) {
                            RoleManager roleManager = guild.getRoleManager();

                            Role role = Role.fromData(lApi, roleData);
                            if (roleManager != null) roleManager.addRole(role);

                            transmitter.onGuildRoleCreate(lApi, new GuildRoleCreateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                        } else {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildRoleCreate(lApi, new GuildRoleCreateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    Role.fromData(lApi, roleData)));
                            break;
                        }

                    }
                    break;

                case GUILD_ROLE_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        SOData roleData = (SOData) data.getAndRequireNotNull(ROLE_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            Update<Role, Role> role = new Update<>(null, Role.fromData(lApi, roleData));
                            transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new Update<>(null, Role.fromData(lApi, roleData))));
                            break;
                        }

                        RoleManager roleManager = guild.getRoleManager();
                        if (roleManager == null) {
                            //RoleManagerImpl may be null, if CACHE_ROLES is disabled.
                            Update<Role, Role> role = new Update<>(null, Role.fromData(lApi, roleData));
                            transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                            break;
                        }

                        Update<Role, Role> role = roleManager.updateRole(roleData);
                        if (role == null) {
                            //RoleManagerImpl didn't contain this role...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_ROLE, null)));
                            transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                    new Update<>(null, Role.fromData(lApi, roleData))));
                            break;
                        }

                        transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                    }

                    break;

                case GUILD_ROLE_DELETE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        String roleId = (String) data.getAndRequireNotNull(ROLE_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, roleId));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, roleId));
                            break;
                        }

                        RoleManager roleManager = guild.getRoleManager();
                        if (roleManager == null) {
                            //RoleManagerImpl may be null, if CACHE_ROLES is disabled.
                            transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, roleId));
                            break;
                        }

                        Role role = roleManager.removeRole(roleId);
                        if (role == null) {
                            //RoleManagerImpl didn't contain this role...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_ROLE, null)));
                            transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, roleId));
                            break;
                        }
                        transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, role));

                    }
                    break;

                case GUILD_SCHEDULED_EVENT_CREATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onGuildScheduledEventCreate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.CREATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildScheduledEventCreate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.CREATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        GuildScheduledEventManager guildScheduledEventManager = guild.getScheduledEventManager();
                        if (guildScheduledEventManager == null) {
                            //Cache disabled
                            transmitter.onGuildScheduledEventCreate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.CREATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        GuildScheduledEvent scheduledEvent = guildScheduledEventManager.onAdd(data);
                        transmitter.onGuildScheduledEventCreate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                GuildScheduledEventEvent.Type.CREATE, scheduledEvent));
                    }
                    break;

                case GUILD_SCHEDULED_EVENT_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onGuildScheduledEventUpdate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.UPDATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildScheduledEventUpdate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.UPDATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        GuildScheduledEventManager guildScheduledEventManager = guild.getScheduledEventManager();
                        if (guildScheduledEventManager == null) {
                            //Cache disabled
                            transmitter.onGuildScheduledEventUpdate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.UPDATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        Update<GuildScheduledEvent, GuildScheduledEvent> update = guildScheduledEventManager.onUpdate(data);
                        if (update == null) {
                            //RoleManagerImpl didn't contain this role...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD_SCHEDULED_EVENT, null)));
                            transmitter.onGuildScheduledEventUpdate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.UPDATE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }
                        transmitter.onGuildScheduledEventUpdate(lApi, new GuildScheduledEventEvent(lApi, payload,
                                GuildScheduledEventEvent.Type.UPDATE, update));
                    }
                    break;

                case GUILD_SCHEDULED_EVENT_DELETE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onGuildScheduledEventDelete(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.DELETE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildScheduledEventDelete(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.DELETE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        GuildScheduledEventManager guildScheduledEventManager = guild.getScheduledEventManager();
                        if (guildScheduledEventManager == null) {
                            //Cache disabled
                            transmitter.onGuildScheduledEventDelete(lApi, new GuildScheduledEventEvent(lApi, payload,
                                    GuildScheduledEventEvent.Type.DELETE, GuildScheduledEvent.fromData(lApi, data)));
                            break;
                        }

                        GuildScheduledEvent removed = guildScheduledEventManager.onDelete(data);
                        if (removed == null) {
                            //RoleManagerImpl didn't contain this role...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD_SCHEDULED_EVENT, null)));
                        }

                        //For the event, the received version is used instead of the cached one
                        transmitter.onGuildScheduledEventDelete(lApi, new GuildScheduledEventEvent(lApi, payload,
                                GuildScheduledEventEvent.Type.DELETE, GuildScheduledEvent.fromData(lApi, data)));
                    }
                    break;

                case GUILD_SCHEDULED_EVENT_USER_ADD:
                    {
                        GuildScheduledEventUserAddRemoveData userData = GuildScheduledEventUserAddRemoveData.fromData(data);

                        if (guildManager == null) {
                            transmitter.onGuildScheduledEventUserAdd(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                    userData,true, null));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(userData.getGuildId());
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildScheduledEventUserAdd(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                    userData,true, null));
                            break;
                        }

                        GuildScheduledEventManager guildScheduledEventManager = guild.getScheduledEventManager();
                        if (guildScheduledEventManager == null) {
                            //Cache disabled
                            transmitter.onGuildScheduledEventUserAdd(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                    userData,true, null));
                            break;
                        }

                        GuildScheduledEvent scheduledEvent = guildScheduledEventManager.onUserAdd(userData);
                        if (scheduledEvent == null) {
                            //guildScheduledEventManager didn't contain this scheduled event...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD_SCHEDULED_EVENT, null)));
                        }

                        transmitter.onGuildScheduledEventUserAdd(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                userData,true, scheduledEvent));
                    }
                    break;

                case GUILD_SCHEDULED_EVENT_USER_REMOVE:
                    {
                        GuildScheduledEventUserAddRemoveData userData = GuildScheduledEventUserAddRemoveData.fromData(data);

                        if (guildManager == null) {
                            transmitter.onGuildScheduledEventUserRemove(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                    userData,false, null));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(userData.getGuildId());
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onGuildScheduledEventUserRemove(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                    userData,false, null));
                            break;
                        }

                        GuildScheduledEventManager guildScheduledEventManager = guild.getScheduledEventManager();
                        if (guildScheduledEventManager == null) {
                            //Cache disabled
                            transmitter.onGuildScheduledEventUserRemove(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                    userData,false, null));
                            break;
                        }

                        GuildScheduledEvent scheduledEvent = guildScheduledEventManager.onUserRemove(userData);
                        if (scheduledEvent == null) {
                            //guildScheduledEventManager didn't contain this scheduled event...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD_SCHEDULED_EVENT, null)));
                        }

                        transmitter.onGuildScheduledEventUserRemove(lApi, new GuildScheduledEventUserEvent(lApi, payload,
                                userData,false, scheduledEvent));
                    }
                    break;

                case INTEGRATION_CREATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        Integration integration = Integration.fromData(lApi, data);

                        transmitter.onIntegrationCreate(lApi,
                                new IntegrationCreateEvent(lApi, payload, Snowflake.fromString(guildId), integration));
                    }
                    break;

                case INTEGRATION_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        Integration integration = Integration.fromData(lApi, data);

                        transmitter.onIntegrationUpdate(lApi,
                                new IntegrationUpdateEvent(lApi, payload, Snowflake.fromString(guildId), integration));
                    }
                    break;

                case INTEGRATION_DELETE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        Snowflake integrationId = data.getAndRequireNotNullAndConvert(Integration.ID_KEY, Snowflake::fromString, InvalidDataException.SUPPLIER);
                        @Nullable Snowflake applicationId = data.getAndConvert(IntegrationDeleteEvent.APPLICATION_ID_KEY, Snowflake::fromString);

                        transmitter.onIntegrationDelete(lApi,
                                new IntegrationDeleteEvent(lApi, payload, Snowflake.fromString(guildId), integrationId, applicationId));
                    }
                    break;

                case INTERACTION_CREATE:
                    {
                        Interaction interaction = Interaction.fromData(lApi, data);
                        InteractionCreateEvent event = new InteractionCreateEvent(lApi, payload, interaction.getGuildIdAsSnowflake(), interaction);
                        transmitter.onInteractionCreate(lApi, event);
                    }
                    break;

                case INVITE_CREATE:
                    {
                        String guildId = (String) data.get(GUILD_ID_KEY);
                        InviteCreateEvent event = InviteCreateEvent.fromData(lApi, payload, Snowflake.fromString(guildId), data);
                        transmitter.onInviteCreate(lApi, event);
                    }
                    break;

                case INVITE_DELETE:
                    {
                        String guildId = (String) data.get(GUILD_ID_KEY);
                        InviteDeleteEvent event = InviteDeleteEvent.fromData(lApi, payload, Snowflake.fromString(guildId), data);
                        transmitter.onInviteDelete(lApi, event);
                    }
                    break;

                case MESSAGE_CREATE:
                    {
                        CreateEventMessage msg = AnyMessage.createEventMessageFromData(lApi, data);
                        transmitter.onMessageCreate(lApi, new MessageCreateEvent(lApi, payload, msg));
                    }
                    break;

                case MESSAGE_UPDATE:
                    {
                        UpdateEventMessage msg = AnyMessage.updateEventMessageFromData(lApi, data);
                        transmitter.onMessageUpdate(lApi, new MessageUpdateEvent(lApi, payload, msg));
                    }
                    break;

                case MESSAGE_DELETE:
                    {
                        String guildId = (String) data.get(GUILD_ID_KEY);
                        String channelId = (String) data.getAndRequireNotNull(AnyMessage.CHANNEL_ID_KEY, InvalidDataException.SUPPLIER);
                        String messageId = (String) data.getAndRequireNotNull(AnyMessage.ID_KEY, InvalidDataException.SUPPLIER);

                        MessageDeleteEvent event = new MessageDeleteEvent(lApi, payload, Snowflake.fromString(guildId),
                                Snowflake.fromString(messageId), Snowflake.fromString(channelId));

                        transmitter.onMessageDelete(lApi, event);
                    }
                    break;

                case MESSAGE_DELETE_BULK:
                    {
                        String guildId = (String) data.get(GUILD_ID_KEY);
                        String channelId = (String) data.getAndRequireNotNull(CHANNEL_ID_KEY, InvalidDataException.SUPPLIER);
                        List<String> messageIds = data.getListAndConvert(MessageDeleteBulkEvent.IDS_KEY, convertible -> (String) convertible);

                        if(messageIds == null)
                            throw InvalidDataException.SUPPLIER.supply(data, MessageDeleteBulkEvent.IDS_KEY);

                        MessageDeleteBulkEvent event = new MessageDeleteBulkEvent(lApi, payload, Snowflake.fromString(guildId),
                                messageIds, Snowflake.fromString(channelId));
                        transmitter.onMessageDeleteBulk(lApi, event);
                    }
                    break;

                case MESSAGE_REACTION_ADD:
                    {
                        MessageReactionEventFields fields = MessageReactionEventFields.fromData(lApi, data);

                        transmitter.onMessageReactionAdd(lApi,
                                new MessageReactionEvent(lApi, payload, MessageReactionEventType.ADD, fields));
                    }

                    break;

                case MESSAGE_REACTION_REMOVE:
                    {
                        MessageReactionEventFields fields = MessageReactionEventFields.fromData(lApi, data);

                        transmitter.onMessageReactionRemove(lApi,
                                new MessageReactionEvent(lApi, payload, MessageReactionEventType.REMOVE, fields));
                    }
                    break;

                case MESSAGE_REACTION_REMOVE_ALL:
                    {
                        String guildId = (String) data.get(GUILD_ID_KEY);
                        Snowflake channelId = data.getAndRequireNotNullAndConvert(CHANNEL_ID_KEY, Snowflake::fromString, InvalidDataException.SUPPLIER);
                        Snowflake messageId = data.getAndRequireNotNullAndConvert(MESSAGE_ID_KEY, Snowflake::fromString, InvalidDataException.SUPPLIER);

                        transmitter.onMessageReactionRemoveAll(lApi,
                                new MessageReactionRemoveAllEvent(lApi, payload,
                                        Snowflake.fromString(guildId), channelId, messageId));
                    }
                    break;

                case MESSAGE_REACTION_REMOVE_EMOJI:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        Snowflake channelId = data.getAndRequireNotNullAndConvert(CHANNEL_ID_KEY, Snowflake::fromString, InvalidDataException.SUPPLIER);
                        Snowflake messageId = data.getAndRequireNotNullAndConvert(MESSAGE_ID_KEY, Snowflake::fromString, InvalidDataException.SUPPLIER);
                        EmojiObject emoji = data.getAndRequireNotNullAndConvertWithException(EMOJI_KEY, (SOData c)  -> EmojiObject.fromData(lApi, c), InvalidDataException.SUPPLIER);

                        transmitter.onMessageReactionRemoveEmoji(lApi,
                                new MessageReactionRemoveEmojiEvent(lApi, payload,
                                        Snowflake.fromString(guildId), channelId, messageId , emoji));
                    }
                    break;

                case PRESENCE_UPDATE:
                    {
                        String guildId = (String) data.get(GUILD_ID_KEY);


                        if (guildId == null){
                            transmitter.onPresenceUpdate(lApi, new PresenceUpdateEvent(lApi,
                                    payload,
                                    new Update<>(null, PresenceUpdate.fromData(data))));
                            break;
                        }

                        if (guildManager == null) {
                            transmitter.onPresenceUpdate(lApi, new PresenceUpdateEvent(lApi,
                                    payload,
                                    new Update<>(null, PresenceUpdate.fromData(data))));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);

                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onPresenceUpdate(lApi, new PresenceUpdateEvent(lApi,
                                    payload,
                                    new Update<>(null, PresenceUpdate.fromData(data))));
                            break;
                        }

                        PresenceManager presenceManager = guild.getPresenceManager();

                        if (presenceManager == null) {
                            transmitter.onPresenceUpdate(lApi, new PresenceUpdateEvent(lApi,
                                    payload,
                                    new Update<>(null, PresenceUpdate.fromData(data))));
                            break;
                        }

                        Update<PresenceUpdate, PresenceUpdate> update = presenceManager.onUpdate(data);
                        transmitter.onPresenceUpdate(lApi, new PresenceUpdateEvent(lApi,
                                payload,
                                update));
                    }
                    break;

                case STAGE_INSTANCE_CREATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onStageInstanceCreate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.CREATE));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onStageInstanceCreate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.CREATE));
                            break;
                        }

                        ListManager<StageInstance> stageInstanceManager = guild.getStageInstanceManager();
                        if (stageInstanceManager == null) {
                            //RoleManagerImpl may be null, if CACHE_ROLES is disabled.
                            transmitter.onStageInstanceCreate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.CREATE));
                            break;
                        }

                        StageInstance instance = stageInstanceManager.onAdd(data);
                        transmitter.onStageInstanceCreate(lApi, new StageInstanceEvent(lApi, payload,
                                new Update<>(null, instance),
                                StageInstanceEvent.Type.CREATE));
                    }
                    break;

                case STAGE_INSTANCE_DELETE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onStageInstanceDelete(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.DELETE));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onStageInstanceDelete(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.DELETE));
                            break;
                        }

                        ListManager<StageInstance> stageInstanceManager = guild.getStageInstanceManager();
                        if (stageInstanceManager == null) {
                            //RoleManagerImpl may be null, if CACHE_ROLES is disabled.
                            transmitter.onStageInstanceDelete(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.DELETE));
                            break;
                        }

                        StageInstance stageInstance = StageInstance.fromData(data);
                        StageInstance deleted = stageInstanceManager.onDelete(stageInstance.getId());
                        if (deleted == null) {
                            //StageInstanceManager didn't contain this stage instance...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_STAGE_INSTANCE, null)));
                            transmitter.onStageInstanceDelete(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.DELETE));
                            break;
                        }
                        transmitter.onStageInstanceDelete(lApi, new StageInstanceEvent(lApi, payload,
                                new Update<>(deleted, StageInstance.fromData(data)),
                                StageInstanceEvent.Type.DELETE));
                    }
                    break;

                case STAGE_INSTANCE_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);

                        if (guildManager == null) {
                            transmitter.onStageInstanceUpdate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.UPDATE));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onStageInstanceUpdate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.UPDATE));
                            break;
                        }

                        ListManager<StageInstance> stageInstanceManager = guild.getStageInstanceManager();
                        if (stageInstanceManager == null) {
                            //RoleManagerImpl may be null, if CACHE_ROLES is disabled.
                            transmitter.onStageInstanceUpdate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.UPDATE));
                            break;
                        }

                        Update<StageInstance, StageInstance> update = stageInstanceManager.onUpdate(data);
                        if (update == null) {
                            //StageInstanceManager didn't contain this stage instance...
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_STAGE_INSTANCE, null)));
                            transmitter.onStageInstanceUpdate(lApi, new StageInstanceEvent(lApi, payload,
                                    new Update<>(null, StageInstance.fromData(data)),
                                    StageInstanceEvent.Type.UPDATE));
                            break;
                        }
                        transmitter.onStageInstanceUpdate(lApi, new StageInstanceEvent(lApi, payload,
                                update,
                                StageInstanceEvent.Type.UPDATE));
                    }
                    break;

                case TYPING_START:
                    {
                        TypingStartEventFields fields = TypingStartEventFields.fromData(lApi, data);
                        transmitter.onTypingStart(lApi, new TypingStartEvent(lApi, payload, fields));
                    }
                    break;

                case USER_UPDATE:
                    {
                        User user = User.fromData(lApi, data);
                        UserUpdateEvent event = new UserUpdateEvent(lApi, payload, user);
                        transmitter.onUserUpdate(lApi, event);
                    }
                    break;

                case VOICE_STATE_UPDATE:
                    {
                        String guildId = (String) data.get(VoiceState.GUILD_ID_KEY);

                        if (guildManager == null || guildId == null) {
                            //TODO: Maybe add a manager for VoiceStates, which are not part of a guild. A Bot cant receive these tho.
                            transmitter.onVoiceStateUpdate(lApi,
                                    new VoiceStateUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                            new Update<>(null, VoiceState.fromData(lApi, data))));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if (guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            transmitter.onVoiceStateUpdate(lApi,
                                    new VoiceStateUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                            new Update<>(null, VoiceState.fromData(lApi, data))));
                            break;
                        }

                        VoiceStateManager voiceStateManager = guild.getVoiceStatesManager();
                        if (voiceStateManager == null) {
                            transmitter.onVoiceStateUpdate(lApi,
                                    new VoiceStateUpdateEvent(lApi, payload, Snowflake.fromString(guildId),
                                            new Update<>(null, VoiceState.fromData(lApi, data))));
                            break;
                        }

                        Update<VoiceState, VoiceState> update = voiceStateManager.update(data);
                        transmitter.onVoiceStateUpdate(lApi, new VoiceStateUpdateEvent(lApi, payload, Snowflake.fromString(guildId), update));
                    }
                    break;

                case VOICE_SERVER_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        String token = (String) data.getAndRequireNotNull(VoiceServerUpdateEvent.TOKEN_KEY, InvalidDataException.SUPPLIER);
                        String endpoint = (String) data.getAndRequireNotNull(VoiceServerUpdateEvent.ENDPOINT_KEY, InvalidDataException.SUPPLIER);

                        VoiceServerUpdateEvent event = new VoiceServerUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), token, endpoint);

                        transmitter.onVoiceServerUpdate(lApi, event);
                    }
                    break;

                case WEBHOOKS_UPDATE:
                    {
                        String guildId = (String) data.getAndRequireNotNull(GUILD_ID_KEY, InvalidDataException.SUPPLIER);
                        Snowflake channelId = data.getAndRequireNotNullAndConvert(CHANNEL_ID_KEY, Snowflake::fromString, InvalidDataException.SUPPLIER);

                        WebhooksUpdateEvent event = new WebhooksUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), channelId);
                        transmitter.onWebhooksUpdate(lApi, event);
                    }
                    break;

                default:
                    transmitter.onUnknownEvent(lApi, type, payload);
                    break;

            }
        } catch (Throwable error) {
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }
    }

    /**
     * handles any received payloads and starts heart-beating, sets sessionId, etc.
     * <br> Events are then transmitted to {@link #handleReceivedEvent(GatewayPayloadAbstract)}
     */
    protected void handleReceivedPayload(@NotNull GatewayPayloadAbstract payload) throws Throwable {
        Long seq = payload.getSequence();
        GatewayOpcode opcode = payload.getOpcode();

        if(Logger.DEBUG_LOG) logger.debug(String.format("Received Payload. Opcode: %s, Sequence: %d, Event: %s", opcode, seq, payload.getType()));
        if(Logger.DEBUG_LOG) logger.debugData("received payload: " + payload.toJsonString(), "payloads");

        if (opcode == GatewayOpcode.DISPATCH) {
            if (payload.getType() == GatewayEvent.READY) {
                if(Logger.DEBUG_LOG) logger.debug("Received " + payload.getType() + " event");
                //ready event. we need to save the session id
                if(payload.getPayloadData() == null)
                    throw new InvalidDataException(null, "READY event data was null!");
                ReadyEvent event = ReadyEvent.fromData(lApi, payload, (SOData) payload.getPayloadData());

                this.sessionId = event.getSessionId();
                this.canResume.set(true);
                this.pendingConnects.set(0);

                if(lApi.getGuildManager() != null) lApi.getGuildManager().onReady(event);
                workOnQueueIfPossible();

            } else if (payload.getType() == GatewayEvent.RESUMED) {
                //resume successful...
                if(Logger.DEBUG_LOG) logger.debug("successfully resumed");
                workOnQueueIfPossible();
            }

            if(payload.getType() == null){
                logger.warningAlign("Received a payload with opcode "
                        + GatewayOpcode.DISPATCH + " but without a type... payload:\n" + payload.toJsonString());
            }

            dispatchEventQueue.push(new ReceivedPayload(payload));
            //handleReceivedEvent(payload);

        } else if (opcode == GatewayOpcode.HEARTBEAT) {
            //Discord requested us to send a Heartbeat
            sendHeartbeat();

        } else if (opcode == GatewayOpcode.RECONNECT) {
            //Discord wants us to reconnect and resume
            resume();

        } else if (opcode == GatewayOpcode.INVALID_SESSION) {
            //Session invalidated.
            Boolean canResume = (Boolean) payload.getPayloadData();

            unexpectedEventHandler.onInvalidSession(lApi, this, canResume != null && canResume);

            if(canResume == null || !canResume){
                reconnect(true);
            }else {
                resume();
            }

        } else if (opcode == GatewayOpcode.HELLO) {
            //sent after connecting
            //the payload data is a json with contains the heartbeat_interval
            Object data = payload.getPayloadData();

            if(data == null) {
                unexpectedEventHandler.onFatal(lApi, this,"payload in " + GatewayOpcode.HELLO + " was null", null);
                return;
            }

            Number heartbeatInterval = (Number) ((SOData) data).get(HEARTBEAT_INTERVAL_KEY);
            if (heartbeatInterval == null) {
                unexpectedEventHandler.onFatal(lApi, this,"No " + HEARTBEAT_INTERVAL_KEY + " received", null);
                disconnect("No " + HEARTBEAT_INTERVAL_KEY + " received");
                return;
            }

            this.heartbeatInterval = heartbeatInterval.longValue();

            this.heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(
                    this::sendHeartbeat, this.heartbeatInterval, this.heartbeatInterval, TimeUnit.MILLISECONDS);



            //check if this is an old session. if so, we should resume
            if(canResume.get()){
                Resume resume = new Resume(token, sessionId, dispatchEventQueue.getLastSequence());

                GatewayPayload resumePayload = GatewayPayload.newResume(resume);
                sendPayload(resumePayload);

            }else{
                Identify identify = new Identify(token, properties, compression == GatewayCompression.PAYLOAD_COMPRESSION,
                        largeThreshold, usesSharding ? shardId : null, usesSharding ? numShards : null, selfPresence.getPresenceUpdate(),
                        GatewayIntent.toInt(intents));

                GatewayPayload identifyPayload = GatewayPayload.newIdentify(identify);
                sendPayload(identifyPayload);
            }

        } else if (opcode == GatewayOpcode.HEARTBEAT_ACK) {
            //our heartbeat was acknowledged
            heartbeatAcknowledgementsReceived.incrementAndGet();
            if(Logger.DEBUG_LOG) logger.debug(String.format("Heartbeat ack received. Heartbeats sent: %d, acks received: %d"
                    , heartbeatsSent.get(), heartbeatAcknowledgementsReceived.get()));

            //Periodically check queue:
            workOnQueueIfPossible();

        } else {
            //This should never be sent to us... something might have gone wrong
            logger.warning("Received an " + opcode + " payload. Did something go wrong?");
        }
    }

    /**
     * will close the current WebSocket and start a new Session. Meaning:
     * This will reset {@link #sessionId}, {@link #heartbeatsSent}, {@link #heartbeatAcknowledgementsReceived},
     * {@link #heartbeatInterval}, {@link #dispatchEventQueue}
     *
     * @param sendClose whether to send a close, if the web socket is still open
     */
    public void reconnect(boolean sendClose){
        logger.debug("reconnecting...");
        heartbeatFuture.cancel(true);

        if(!webSocket.isOutputClosed() && sendClose){
            disconnect(null).whenComplete((webSocket, throwable) -> webSocket.abort());
            //disconnect will call resetState(), no need to call it here
            start();
            return;
        }else{
            webSocket.abort();
        }

        resetState();
        start();
    }

    /**
     * Will create a new WebSocket and resume the Session
     */
    public void resume() {
        heartbeatFuture.cancel(true);

        if(webSocket.isInputClosed() && webSocket.isOutputClosed()){
            //input and output are both closed
            start();
        }else if(webSocket.isInputClosed() && !webSocket.isOutputClosed()){
            //Discord has already closed it's output
            webSocket.abort();
            start();
        }else{
            webSocket.abort();
            start();
        }

    }

    /**
     * The Gateway will stop sending heartbeats or receiving any events. But a close will NOT be sent.<br>
     * This is useful, if you want to call {@link #resume(SOData)} later
     */
    public void abort(){
        heartbeatFuture.cancel(true);
        webSocket.abort();
    }

    /**
     *
     * This can be used to resume another {@link GatewayWebSocket}.<br>
     * This is useful, if you want to quickly restart your os, but keeping the same Gateway connection afterwards.<br>
     * Note that this has to be done quickly, because discord will close the connection after not receiving any heartbeats
     * after a while
     *
     * @param data {@link #getData()}
     * @throws InvalidDataException if the given data was invalid
     */
    @SuppressWarnings("ConstantConditions")
    public void resume(SOData data) throws InvalidDataException {
        if(webSocket != null) throw new UnsupportedOperationException("resume(Data) is exclusive to start()");

        @NotNull String sessionId = (String) data.getAndRequireNotNull(SESSION_ID_KEY, InvalidDataException.SUPPLIER);
        @NotNull Boolean canResume = (Boolean) data.getAndRequireNotNull(CAN_RESUME_KEY, InvalidDataException.SUPPLIER);
        @NotNull SOData dispatchEventQueueData = (SOData) data.getAndRequireNotNull(DISPATCH_EVENT_QUEUE_KEY, InvalidDataException.SUPPLIER);
        @NotNull Number heartbeatsSent = (Number) data.getAndRequireNotNull(HEARTBEATS_SENT_KEY, InvalidDataException.SUPPLIER);
        @NotNull  Number heartbeatsAcksReceived = (Number) data.getAndRequireNotNull(HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY, InvalidDataException.SUPPLIER);
        @NotNull Number genMillis = (Number) data.getAndRequireNotNull(DATA_GENERATED_TIME_MILLIS_KEY, InvalidDataException.SUPPLIER);

        long timePasted = System.currentTimeMillis() - genMillis.longValue();

        logger.debug("going to resume with a " + timePasted / 1000 + " seconds old GatewayResumeData");

        this.sessionId = sessionId;
        this.canResume.set(canResume);
        this.dispatchEventQueue = DispatchEventQueue.fromData(dispatchEventQueueData);
        this.heartbeatsSent.set(heartbeatsSent.longValue());
        this.heartbeatAcknowledgementsReceived.set(heartbeatsAcksReceived.longValue());

        start();
    }

    /**
     * Sends {@link GatewayCloseStatusCode#SEND_CLOSE close status code} to discord.
     * Your session will be invalidated and your bot will appear offline
     *
     * @param reason string, can be {@code null}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<WebSocket> disconnect(@Nullable String reason) {
        if (webSocket == null || webSocket.isOutputClosed())
            throw new UnsupportedOperationException("cannot disconnect, because you are not connected in the first place");

        logger.debug("sending close with reason: " + reason);
        CompletableFuture<WebSocket> future = webSocket.sendClose(GatewayCloseStatusCode.SEND_CLOSE.getCode(), reason == null ? "" : reason);

        final GatewayWebSocket _this = this;
        future = future.whenComplete((webSocket, error) -> {
            webSocket.abort();
            if (error != null) {
                logger.error(error);
                if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, _this, error);
            }
        });

        resetState();
        return future;
    }

    /**
     * Resets this {@link GatewayWebSocket}, so it could be {@link #start() started} again
     */
    protected void resetState(){
        heartbeatFuture.cancel(true);
        webSocket = null;
        sessionId = null;
        canResume.set(false);

        dispatchEventQueue.reset();

        heartbeatsSent.set(0);
        heartbeatAcknowledgementsReceived.set(0);
    }

    /**
     * sends a Heartbeat to discord
     */
    protected void sendHeartbeat() {
        long sequence = dispatchEventQueue.getLastSequence();

        GatewayPayload payload = GatewayPayload.newHeartbeat(sequence);
        logger.debug("sending heartbeat: sequence=" + sequence);
        sendPayload(payload).whenComplete((webSocket, throwable) -> {
            if(throwable == null) heartbeatsSent.incrementAndGet();
        });
    }

    /**
     * Sends given payload to Discord
     *
     * @param payload payload to send
     */
    protected CompletableFuture<WebSocket> sendPayload(GatewayPayloadAbstract payload) {
        if (webSocket == null || webSocket.isOutputClosed())
            throw new UnsupportedOperationException("cannot send a payload, because you are not connected");

        if(Logger.DEBUG_LOG) logger.debug(String.format("Sending Payload. Opcode: %s", payload.getOpcode()));
        if(Logger.DEBUG_LOG) logger.debugData("Sending payload data: " + payload.toJsonString(), "payloads");
        CompletableFuture<WebSocket> future = webSocket.sendText(payload.toJsonString(), true);

        final GatewayWebSocket _this = this;
        return future.whenComplete((webSocket, error) -> {
            if (error != null) {
                logger.error(error);
                if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, _this, error);
            }
        });
    }

    public synchronized void queueCommand(@NotNull GatewayCommand command) {
        this.commandQueue.add(command);
        workOnQueueIfPossible();
    }

    /**
     * Triggered periodically, when a Heartbeat ACK is received.<br>
     * Also triggered, when a {@link GatewayEvent#READY} or {@link GatewayEvent#RESUMED} event is received
     */
    public synchronized void workOnQueueIfPossible(){
        if(!queueWorking.get()) {
            if(webSocket != null && !webSocket.isOutputClosed())
                sendNextInCommandQueue();
        }
    }

     public synchronized void sendNextInCommandQueue(){
        GatewayCommand cmd = commandQueue.poll();
        if(cmd != null) {
            queueWorking.set(true);
            sendCommand(cmd.getType(), cmd.getObject());
        }else{
            queueWorking.set(false);
        }
    }

    /**
     * Sends given command to Discord
     * @param command the command to send
     * @param data the data of the command, most like to be a {@link SOData}
     */
    @ApiStatus.Internal
    public void sendCommand(GatewayCommandType command, Object data){

        GatewayPayload payload = new GatewayPayload(command.getOpcode(), data, null, null, null);

        if(command == GatewayCommandType.IDENTIFY || command == GatewayCommandType.RESUME || command == GatewayCommandType.HEARTBEAT){
            logger.warning("Sending a " + command + " command using method sendCommand(). This should never be done! " +
                    "The GatewayWebSocket will send these commands automatically when required.");
        }

        sendPayload(payload).thenApply(webSocket -> {
            sendNextInCommandQueue();
            return null;
        });
    }


    @Override
    public synchronized void onOpen(WebSocket webSocket) {
        logger.debug("onOpen");
        this.webSocket = webSocket;
        WebSocket.Listener.super.onOpen(webSocket);
    }

    /**
     *
     * transmits the raw input to {@link #handleReceivedPayload(GatewayPayloadAbstract)}
     */
    @Override
    public synchronized CompletionStage<?> onText(WebSocket webSocket, CharSequence text, boolean last) {
        try {

            if(webSocket != this.webSocket) return null;

            if (!last) {
                if (currentText == null) {
                    currentText = text.toString();
                    return WebSocket.Listener.super.onText(webSocket, text, false);
                }
                currentText += text;
                return WebSocket.Listener.super.onText(webSocket, text, false);
            }

            if (currentText == null) currentText = text.toString();
            else currentText += text.toString();

            GatewayPayloadAbstract payload = jsonToPayloadConverter.convert(currentText);
            handleReceivedPayload(payload);

            currentText = null;
        } catch (Throwable error) {
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }

        return WebSocket.Listener.super.onText(webSocket, text, false);
    }

    /**
     *
     * transmits the raw input to {@link #handleReceivedPayload(GatewayPayloadAbstract)}
     */
    @Override
    public synchronized CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer bytes, boolean last) {
        try {

            if(webSocket != this.webSocket) return null;

            if (!last) {
                if (currentBytes == null) {
                    currentBytes = new ArrayList<>();
                    currentBytes.add(bytes);
                    return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
                }

                currentBytes.add(bytes);
                return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
            }

            if (currentBytes == null) currentBytes = new ArrayList<>(1);
            currentBytes.add(bytes);

            try {
                GatewayPayloadAbstract payload = bytesToPayloadConverter.convert(currentBytes);
                handleReceivedPayload(payload);
            } catch (Exception e) {
                logger.error(e);
                if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, e);
            }

            currentBytes.clear();
        } catch (Throwable error) {
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }
        return WebSocket.Listener.super.onBinary(webSocket, bytes, last);
    }

    @Override
    public synchronized CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        if(webSocket != this.webSocket) return null;
        logger.warning("Received a ping... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public synchronized CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        if(webSocket != this.webSocket) return null;
        logger.warning("Received a pong... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public synchronized CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        try {
            if (webSocket != this.webSocket) return null;
            heartbeatFuture.cancel(true);

            GatewayCloseStatusCode closeCode = GatewayCloseStatusCode.fromInt(statusCode);

            logger.debug("Discord closed its output. Status-code: " + closeCode + ", reason: " + reason);

            if(pendingConnects.get() > 3){
                logger.warning("we have already tried to connect 3 times in a row without success. We wont try again");
                if(unexpectedEventHandler != null) unexpectedEventHandler.onFatal(lApi, this, "Gateway tried to connect 3 times in a row without success", null);
                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }

            if(unexpectedEventHandler != null){
                if(unexpectedEventHandler.handleUnexpectedClose(lApi, this, closeCode, reason)){
                    reconnect(true);
                }else{
                    logger.error("Gateway Closed! Discord closed its output. Status-code: " + closeCode + "(" + statusCode + "), reason: " + reason);
                }
            }else{
                logger.warning("No errorHandler set! will do standard action.");
                if (closeCode == GatewayCloseStatusCode.SESSION_TIMED_OUT ||
                        closeCode == GatewayCloseStatusCode.RATE_LIMITED ||
                        closeCode == GatewayCloseStatusCode.UNKNOWN_ERROR ||
                        closeCode == GatewayCloseStatusCode.INVALID_SEQUENCE) {
                    reconnect(true);
                }else{
                    logger.error("Gateway Closed! Discord closed its output. Status-code: " + closeCode + "(" + statusCode + "), reason: " + reason);
                }
            }

        }catch (Throwable error){
            logger.error(error);
            if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error);
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        if (webSocket != this.webSocket) return;

        if(error instanceof SocketException){
            if(error.getMessage().equals("Connection reset")){
                logger.debug("SocketException: Connection reset.");
                //we probably lost internet connection
                if(pendingConnects.get() < 4){
                    logger.debug("Trying to resume");
                    resume();
                    //reconnect(false);
                    return;
                }
                // we already tried 3+ times
                if(unexpectedEventHandler != null) unexpectedEventHandler.onFatal(lApi, this,
                        "The web socket had a SocketException with message=\"Connection reset\"" +
                                " and we have already tried connection at least 3 times in a row.", null);
                return;

            }
        }

        logger.error(error);
        if (unexpectedEventHandler != null) unexpectedEventHandler.onFatal(lApi, this, "Unknown error", error);
        WebSocket.Listener.super.onError(webSocket, error);
    }

    public void setUnexpectedEventHandler(@Nullable GatewayWebSocket.UnexpectedEventHandler unexpectedEventHandler) {
        this.unexpectedEventHandler = unexpectedEventHandler;
    }

    /**
     *
     * @return the {@link SelfUserPresenceUpdater} bound to this Gateway
     */
    public @NotNull SelfUserPresenceUpdater getSelfUserPresenceUpdater() {
        return selfPresence;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(SESSION_ID_KEY, sessionId);
        data.add(CAN_RESUME_KEY, canResume.get());
        data.add(DISPATCH_EVENT_QUEUE_KEY, dispatchEventQueue);
        data.add(HEARTBEATS_SENT_KEY, heartbeatsSent.get());
        data.add(HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY, heartbeatAcknowledgementsReceived.get());
        data.add(DATA_GENERATED_TIME_MILLIS_KEY, System.currentTimeMillis());

        return data;
    }


    /**
     * This will handle unexpected events occurring in the gateway. The most important method is
     * {@link #onFatal(LApi, GatewayWebSocket, String, Throwable) onFatal(...)}, you should definitely notify yourself
     * if this happens because your bot might remain offline.
     */
    public interface UnexpectedEventHandler {

        /**
         * Most of the time, the Gateway will continue working, but you should at least notify yourself somehow (like printing, etc.).
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket} in which this error occurred
         * @param error the error
         */
        void handleError(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull Throwable error);

        /**
         * You can just return {@code true} if you wish.
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket} connection was closed
         * @param closeStatusCode {@link GatewayCloseStatusCode}
         * @param reason the reason Discord send us
         * @return whether the {@link GatewayWebSocket} should try to connect again
         */
        default boolean handleUnexpectedClose(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket,
                                          @NotNull GatewayCloseStatusCode closeStatusCode, String reason){
            return closeStatusCode == GatewayCloseStatusCode.SESSION_TIMED_OUT  ||
                   closeStatusCode == GatewayCloseStatusCode.RATE_LIMITED       ||
                   closeStatusCode == GatewayCloseStatusCode.UNKNOWN_ERROR      ||
                   closeStatusCode == GatewayCloseStatusCode.INVALID_SEQUENCE;
        }

        /**
         * Either we already tried to connect 3 times in a row, without success or something unexpected happened.<br>
         * Anyway, the {@link GatewayWebSocket} will <b>not</b> automatically reconnect
         *
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket}
         * @param information some information from LApi
         * @param cause the {@link Throwable} which caused this or {@code null}
         */
        void onFatal(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, @NotNull String information, @Nullable Throwable cause);

        /**
         * The {@link GatewayWebSocket} received an {@link GatewayOpcode#INVALID_SESSION} from discord.
         * The {@link GatewayWebSocket} will automatically resume or reconnect after this
         *
         * @param lApi {@link LApi}
         * @param gatewayWebSocket the {@link GatewayWebSocket}
         * @param canResume {@code true} means the {@link GatewayWebSocket} will try to {@link #resume() resume}.
         * {@code false} means the {@link GatewayWebSocket} will try to {@link #reconnect(boolean) reconnect}
         */
        @SuppressWarnings("unused")
        default void onInvalidSession(@NotNull LApi lApi, @NotNull GatewayWebSocket gatewayWebSocket, boolean canResume){}

    }

    /**
     * Return value for {@link GuildManager#onGuildCreate(GatewayPayloadAbstract)}
     */
    public static class OnGuildCreateReturn{

        public final CachedGuildImpl guild;
        public final boolean isNew;
        public final boolean becameAvailable;

        /**
         *
         * @param guild the {@link CachedGuildImpl} object
         * @param isNew true if the current user just joined this guild
         * @param becameAvailable true if this guild was unavailable and is available again
         */
        public OnGuildCreateReturn(CachedGuildImpl guild, boolean isNew, boolean becameAvailable){
            this.guild = guild;
            this.isNew = isNew;
            this.becameAvailable = becameAvailable;
        }

    }
}
