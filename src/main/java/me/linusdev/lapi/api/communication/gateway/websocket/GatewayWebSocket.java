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

package me.linusdev.lapi.api.communication.gateway.websocket;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommand;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommandType;
import me.linusdev.lapi.api.communication.gateway.enums.*;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.channel.ChannelUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiError;
import me.linusdev.lapi.api.communication.gateway.events.error.LApiErrorEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.*;
import me.linusdev.lapi.api.communication.gateway.events.guild.emoji.GuildEmojisUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberAddEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberRemoveEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.GuildMemberUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMemberChunkEventData;
import me.linusdev.lapi.api.communication.gateway.events.guild.member.chunk.GuildMembersChunkEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.role.GuildRoleUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.guild.sticker.GuildStickersUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.interaction.InteractionCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.messagecreate.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.ready.ReadyEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventTransmitter;
import me.linusdev.lapi.api.communication.gateway.events.voice.state.VoiceStateUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.identify.ConnectionProperties;
import me.linusdev.lapi.api.communication.gateway.identify.Identify;
import me.linusdev.lapi.api.communication.gateway.other.GatewayPayload;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.resume.Resume;
import me.linusdev.lapi.api.communication.gateway.update.Update;
import me.linusdev.lapi.api.communication.lapihttprequest.LApiHttpHeader;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.guild.member.MemberManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStateManager;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.manager.list.ListUpdate;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.guild.CachedGuildImpl;
import me.linusdev.lapi.api.objects.guild.Guild;
import me.linusdev.lapi.api.objects.guild.GuildImpl;
import me.linusdev.lapi.api.objects.guild.member.Member;
import me.linusdev.lapi.api.objects.guild.voice.VoiceState;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.message.abstracts.Message;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import me.linusdev.lapi.api.objects.user.User;
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

    public static final ExceptionConverter<String, GatewayPayloadAbstract, Exception> STANDARD_JSON_TO_PAYLOAD_CONVERTER = convertible -> {
        StringReader reader = new StringReader(convertible);
        Data data = new JsonParser().readDataFromReader(reader);
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
    public static final String LAST_RECEIVED_SEQUENCE_KEY = "sequence";
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
    /**
     * The last sequence received from Discord. -1 if we haven't received one yet
     */
    private final AtomicLong lastReceivedSequence;
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
                config.getGatewayConfig().getUnexpectedEventHandler()
        );
    }

    /**
     * You should not use this, as the @Nullable annotations are possibly wrong. That is because the checking of Nullability was
     * moved to {@link me.linusdev.lapi.api.config.ConfigBuilder ConfigBuilder} and
     * {@link me.linusdev.lapi.api.config.GatewayConfigBuilder GatewayConfigBuilder}.<br>
     * Use {@link GatewayWebSocket#GatewayWebSocket(LApi, EventTransmitter, Config)} instead.
     */
    @ApiStatus.Internal
    private GatewayWebSocket(@NotNull LApiImpl lApi, @NotNull EventTransmitter transmitter, @NotNull String token, @Nullable ApiVersion apiVersion,
                             @Nullable GatewayEncoding encoding, @Nullable GatewayCompression compression,
                             @NotNull String os, @NotNull Integer largeThreshold, @Nullable Integer shardId,
                             @Nullable Integer numShards, @NotNull SelfUserPresenceUpdater selfPresence, @NotNull GatewayIntent[] intents,
                             @NotNull ExceptionConverter<String, GatewayPayloadAbstract, ? extends Throwable> jsonToPayloadConverter,
                             ExceptionConverter<ArrayList<ByteBuffer>, GatewayPayloadAbstract, ? extends Throwable> bytesToPayloadConverter,
                             @NotNull UnexpectedEventHandler unexpectedEventHandler) {
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

        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

        this.logger = Logger.getLogger(GatewayWebSocket.class.getSimpleName(), Logger.Type.DEBUG);

        this.canResume = new AtomicBoolean(false);

        this.lastReceivedSequence = new AtomicLong(-1);

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
            lApi.getGatewayBot().queue((getGatewayResponse, error) -> {
                try {
                    if (error != null) {
                        logger.error(error.getThrowable());
                        if (unexpectedEventHandler != null) unexpectedEventHandler.handleError(lApi, this, error.getThrowable());
                        return;
                    }

                    //TODO check session start limit

                    URI uri = new URI(getGatewayResponse.getUrl()
                            + "?" + QUERY_STRING_API_VERSION_KEY + "=" + apiVersion.getVersionNumber()
                            + "&" + QUERY_STRING_ENCODING_KEY + "=" + encoding.getValue()
                            + (compression.getValue() != null ? "&" + QUERY_STRING_COMPRESS_KEY + "=" + compression.getValue() : ""));

                    logger.debug("Gateway connecting to " + uri.toString());

                    final GatewayWebSocket _this = this;
                    pendingConnects.incrementAndGet();
                    builder.buildAsync(uri, this).whenComplete((webSocket, throwable) -> {

                        if(throwable != null){
                            logger.error("Could not build web socket! We will try again");
                            logger.error(throwable);
                            // if this happens, we should have a internet connection, because getGatewayBot worked...
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


    @SuppressWarnings("DuplicateBranchesInSwitch")
    protected void handleReceivedEvent(@Nullable GatewayEvent type, @Nullable Data innerPayload, @NotNull GatewayPayloadAbstract payload) throws InvalidDataException {
        try {
            if (innerPayload == null) {
                //handle events without an inner payload. for example RESUMED
                return;
            }

            if (type == null) {
                transmitter.onUnknownEvent(lApi, null, payload);
                return;
            }

            @Nullable GuildManager guildManager = lApi.getGuildManager();

            switch (type) {
                case HELLO:
                    break;

                case READY:
                    //see handleReceivedPayload
                    break;

                case RESUMED:
                    break;

                case RECONNECT:
                    break;

                case INVALID_SESSION:
                    break;

                case CHANNEL_CREATE:
                    {
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel
                            //TODO: cache non guild channels
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    null, Channel.fromData(lApi, data)));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), Channel.fromData(lApi, data)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            break;
                        }

                        ListManager<Channel<?>> channelManager = guild.getChannelManager();

                        if(channelManager == null) {
                            transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), Channel.fromData(lApi, data)));
                            break;
                        }

                        Channel<?> channel = Channel.fromData(lApi, data);
                        channelManager.add(channel);
                        transmitter.onChannelCreate(lApi, new ChannelCreateEvent(lApi, payload,
                                Snowflake.fromString(guildId), channel));
                    }
                    break;

                case CHANNEL_UPDATE:
                    {
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                        if(guildId == null) {
                            //This may be a non guild channel
                            //TODO: cache non guild channels
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    null, new Update<>(null, Channel.fromData(lApi, data))));
                            break;
                        }

                        if(guildManager == null) {
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), new Update<>(null, Channel.fromData(lApi, data))));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                            break;
                        }

                        ListManager<Channel<?>> channelManager = guild.getChannelManager();

                        if(channelManager == null) {
                            transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                    Snowflake.fromString(guildId), new Update<>(null, Channel.fromData(lApi, data))));
                            break;
                        }


                        Update<Channel<?>, Channel<?>> update = channelManager.onUpdate(data);

                        if(update == null) {
                            //ChannelManager did not contain this channel -> error
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_CHANNEL, null)));
                            break;
                        }

                        transmitter.onChannelUpdate(lApi, new ChannelUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), update));
                    }
                    break;

                case CHANNEL_DELETE:
                {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(Channel.GUILD_ID_KEY);

                    if(guildId == null) {
                        //This may be a non guild channel
                        //TODO: cache non guild channels
                        transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                null, null, Channel.fromData(lApi, data)));
                        break;
                    }

                    if(guildManager == null) {
                        transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                Snowflake.fromString(guildId), null, Channel.fromData(lApi, data)));
                        break;
                    }

                    CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                    if(guild == null) {
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                        break;
                    }

                    ListManager<Channel<?>> channelManager = guild.getChannelManager();

                    if(channelManager == null) {
                        transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                                Snowflake.fromString(guildId), null, Channel.fromData(lApi, data)));
                        break;
                    }

                    Channel<?> received = Channel.fromData(lApi, data);
                    Channel<?> deleted = channelManager.onDelete(received.getId());

                    if(deleted == null) {
                        //ChannelManager did not contain this channel -> error
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_CHANNEL, null)));
                        break;
                    }

                    transmitter.onChannelDelete(lApi, new ChannelDeleteEvent(lApi, payload,
                            Snowflake.fromString(guildId), deleted, received));
                }
                    break;

                case CHANNEL_PINS_UPDATE:
                    break;

                case THREAD_CREATE:
                    break;

                case THREAD_UPDATE:
                    break;

                case THREAD_DELETE:
                    break;

                case THREAD_LIST_SYNC:
                    break;

                case THREAD_MEMBER_UPDATE:
                    break;

                case THREAD_MEMBERS_UPDATE:
                    {
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        //TODO: remove
                        System.out.println(data.getJsonString());
                    }

                    break;

                case GUILD_CREATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

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

                case GUILD_UPDATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

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

                case GUILD_DELETE: {

                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    if (guildManager == null) {
                        String guildId = (String) data.get(GUILD_ID_KEY);

                        if (guildId == null) throw new InvalidDataException(data, "", null, GUILD_ID_KEY);

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
                    break;

                case GUILD_BAN_REMOVE:
                    break;

                case GUILD_EMOJIS_UPDATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(GUILD_ID_KEY);
                    ArrayList<Data> emojisData = (ArrayList<Data>) data.get(EMOJIS_KEY);


                    if (guildId == null || emojisData == null)
                        throw new InvalidDataException(data, "", null, GUILD_ID_KEY, EMOJIS_KEY);

                    if (guildManager == null) {
                        transmitter.onGuildEmojisUpdate(lApi,
                                new GuildEmojisUpdateEvent(lApi, payload, null, emojisData, null, null));
                        break;
                    }

                    CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);

                    if (guild == null) {
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
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

                case GUILD_STICKERS_UPDATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(GUILD_ID_KEY);
                    ArrayList<Data> stickersData = (ArrayList<Data>) data.get(STICKERS_KEY);


                    if (guildId == null || stickersData == null)
                        throw new InvalidDataException(data, "", null, GUILD_ID_KEY, STICKERS_KEY);

                    if (guildManager == null) {
                        transmitter.onGuildStickersUpdate(lApi,
                                new GuildStickersUpdateEvent(lApi, payload, null, stickersData, null, null));
                        break;
                    }

                    CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);

                    if (guild == null) {
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
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
                    break;

                case GUILD_MEMBER_ADD:
                    {
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        String guildId = (String) data.get(GUILD_ID_KEY);
                        Data userData = (Data) data.get(Member.USER_KEY);
                        if(userData == null || guildId == null)
                            throw new InvalidDataException(data, "guildId or user missing", null, GUILD_ID_KEY, Member.USER_KEY);

                        String userId = (String) userData.get(User.ID_KEY);
                        if(userId == null)
                            throw new InvalidDataException(userData, "userId missing", null, User.ID_KEY);

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
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        String guildId = (String) data.get(GUILD_ID_KEY);
                        Data userData = (Data) data.get(Member.USER_KEY);
                        if(userData == null || guildId == null)
                            throw new InvalidDataException(data, "guildId or user missing", null, GUILD_ID_KEY, Member.USER_KEY);

                        String userId = (String) userData.get(User.ID_KEY);
                        if(userId == null)
                            throw new InvalidDataException(userData, "userId missing", null, User.ID_KEY);

                        if(guildManager == null) {
                            transmitter.onGuildMemberRemove(lApi, new GuildMemberRemoveEvent(lApi,
                                    payload, Snowflake.fromString(guildId), null, User.fromData(lApi, userData)));
                            break;
                        }

                        CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                        if(guild == null) {
                            transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                    new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
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
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        String guildId = (String) data.get(GUILD_ID_KEY);
                        Data userData = (Data) data.get(Member.USER_KEY);
                        if(userData == null || guildId == null)
                            throw new InvalidDataException(data, "guildId or user missing", null, GUILD_ID_KEY, Member.USER_KEY);

                        String userId = (String) userData.get(User.ID_KEY);
                        if(userId == null)
                            throw new InvalidDataException(userData, "userId missing", null, User.ID_KEY);

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
                            break;
                        }
                        transmitter.onGuildMemberUpdate(lApi, new GuildMemberUpdateEvent(lApi, payload,
                                Snowflake.fromString(guildId), update));
                    }
                    break;

                case GUILD_MEMBERS_CHUNK:
                    {
                        Data data = (Data) payload.getPayloadData();
                        if (data == null)
                            throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                        String guildId = (String) data.get(GUILD_ID_KEY);
                        if(guildId == null)
                            throw new InvalidDataException(data, "guildId missing", null, GUILD_ID_KEY);

                        transmitter.onGuildMembersChunk(lApi, new GuildMembersChunkEvent(lApi,
                                payload, Snowflake.fromString(guildId),
                                GuildMemberChunkEventData.fromData(lApi, data)));
                    }
                    break;

                case GUILD_ROLE_CREATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(GUILD_ID_KEY);
                    Data roleData = (Data) data.get(ROLE_KEY);

                    if (guildId == null || roleData == null)
                        throw new InvalidDataException(data, "", null, GUILD_ID_KEY, ROLE_KEY);

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
                        break;
                    }

                }
                break;

                case GUILD_ROLE_UPDATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(GUILD_ID_KEY);
                    Data roleData = (Data) data.get(ROLE_KEY);

                    if (guildId == null || roleData == null)
                        throw new InvalidDataException(data, "", null, GUILD_ID_KEY, ROLE_KEY);

                    if (guildManager == null) {
                        Update<Role, Role> role = new Update<Role, Role>(null, Role.fromData(lApi, roleData));
                        transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                        break;
                    }

                    CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                    if (guild == null) {
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
                        break;
                    }

                    RoleManager roleManager = guild.getRoleManager();
                    if (roleManager == null) {
                        //RoleManagerImpl may be null, if CACHE_ROLES is disabled.
                        Update<Role, Role> role = new Update<Role, Role>(null, Role.fromData(lApi, roleData));
                        transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                        break;
                    }

                    Update<Role, Role> role = roleManager.updateRole(roleData);
                    if (role == null) {
                        //RoleManagerImpl didn't contain this role...
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_ROLE, null)));
                        break;
                    }

                    transmitter.onGuildRoleUpdate(lApi, new GuildRoleUpdateEvent(lApi, payload, Snowflake.fromString(guildId), role));
                }

                break;

                case GUILD_ROLE_DELETE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(GUILD_ID_KEY);
                    String roleId = (String) data.get(ROLE_ID_KEY);

                    if (guildId == null || roleId == null)
                        throw new InvalidDataException(data, "", null, GUILD_ID_KEY, ROLE_ID_KEY);

                    if (guildManager == null) {
                        transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, roleId));
                        break;
                    }

                    CachedGuildImpl guild = guildManager.getUpdatableGuildById(guildId);
                    if (guild == null) {
                        transmitter.onLApiError(lApi, new LApiErrorEvent(lApi, payload, type,
                                new LApiError(LApiError.ErrorCode.UNKNOWN_GUILD, null)));
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
                        break;
                    }
                    transmitter.onGuildRoleDelete(lApi, new GuildRoleDeleteEvent(lApi, payload, guildId, role));

                }
                break;

                case GUILD_SCHEDULED_EVENT_CREATE:
                    break;

                case GUILD_SCHEDULED_EVENT_UPDATE:
                    break;

                case GUILD_SCHEDULED_EVENT_DELETE:
                    break;

                case GUILD_SCHEDULED_EVENT_USER_ADD:
                    break;

                case GUILD_SCHEDULED_EVENT_USER_REMOVE:
                    break;

                case INTEGRATION_CREATE:
                    break;

                case INTEGRATION_UPDATE:
                    break;

                case INTEGRATION_DELETE:
                    break;

                case INTERACTION_CREATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    Interaction interaction = Interaction.fromData(lApi, data);
                    InteractionCreateEvent event = new InteractionCreateEvent(lApi, payload, interaction.getGuildIdAsSnowflake(), interaction);
                    transmitter.onInteractionCreate(lApi, event);
                }
                break;

                case INVITE_CREATE:
                    break;

                case INVITE_DELETE:
                    break;

                case MESSAGE_CREATE:
                    MessageImplementation msg = Message.fromData(lApi, innerPayload);
                    transmitter.onMessageCreate(lApi, new MessageCreateEvent(lApi, payload, msg));
                    break;

                case MESSAGE_UPDATE:
                    break;

                case MESSAGE_DELETE:
                    break;

                case MESSAGE_DELETE_BULK:
                    break;

                case MESSAGE_REACTION_ADD:
                    break;

                case MESSAGE_REACTION_REMOVE:
                    break;

                case MESSAGE_REACTION_REMOVE_ALL:
                    break;

                case MESSAGE_REACTION_REMOVE_EMOJI:
                    break;

                case PRESENCE_UPDATE:
                    break;

                case STAGE_INSTANCE_CREATE:
                    break;

                case STAGE_INSTANCE_DELETE:
                    break;

                case STAGE_INSTANCE_UPDATE:
                    break;

                case TYPING_START:
                    break;

                case USER_UPDATE:
                    break;

                case VOICE_STATE_UPDATE: {
                    Data data = (Data) payload.getPayloadData();
                    if (data == null)
                        throw new InvalidDataException(null, "Data is missing in GatewayPayload where data is required!");

                    String guildId = (String) data.get(VoiceState.GUILD_ID_KEY);
                    String userId = (String) data.get(VoiceState.USER_ID_KEY);

                    if (userId == null) {
                        throw new InvalidDataException(data, null, null, VoiceState.GUILD_ID_KEY, VoiceState.USER_ID_KEY);
                    }

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
                    break;

                case WEBHOOKS_UPDATE:
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
     * <br> Events are then transmitted to {@link #handleReceivedEvent(GatewayEvent, Data, GatewayPayloadAbstract)}
     */
    protected void handleReceivedPayload(@NotNull GatewayPayloadAbstract payload) throws Throwable {
        Long seq = payload.getSequence();
        if (seq != null) this.lastReceivedSequence.set(seq);
        GatewayOpcode opcode = payload.getOpcode();

        if(Logger.DEBUG_LOG) logger.debug(String.format("Received Payload. Opcode: %s, Sequence: %d, Event: %s", opcode, seq, payload.getType()));
        if(Logger.DEBUG_LOG) logger.debugData("received payload: " + payload.toJsonString(), "payloads");

        if (opcode == GatewayOpcode.DISPATCH) {
            if (payload.getType() == GatewayEvent.READY) {
                if(Logger.DEBUG_LOG) logger.debug("Received " + payload.getType() + " event");
                //ready event. we need to save the session id
                if(payload.getPayloadData() == null)
                    throw new InvalidDataException(null, "READY event data was null!");
                ReadyEvent event = ReadyEvent.fromData(lApi, payload, (Data) payload.getPayloadData());

                this.sessionId = event.getSessionId();
                this.canResume.set(true);
                this.pendingConnects.set(0);

                if(lApi.getGuildManager() != null) lApi.getGuildManager().onReady(event);
                transmitter.onReady(lApi, event);
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

            handleReceivedEvent(payload.getType(), (Data) payload.getPayloadData(), payload);

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

            Number heartbeatInterval = (Number) ((Data) data).get(HEARTBEAT_INTERVAL_KEY);
            if (heartbeatInterval == null) {
                disconnect("No " + HEARTBEAT_INTERVAL_KEY + " received");
                return;
            }

            this.heartbeatInterval = heartbeatInterval.longValue();

            this.heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(
                    this::sendHeartbeat, this.heartbeatInterval, this.heartbeatInterval, TimeUnit.MILLISECONDS);



            //check if this is an old session. if so, we should resume
            if(canResume.get()){
                Resume resume = new Resume(token, sessionId, lastReceivedSequence.get());

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
     * {@link #heartbeatInterval}, {@link #lastReceivedSequence}
     *
     * @param sendClose whether to send a close, if the web socket is still open
     */
    public void reconnect(boolean sendClose){
        logger.debug("reconnecting...");
        heartbeatFuture.cancel(true);

        if(!webSocket.isOutputClosed() && sendClose){
            disconnect(null).whenComplete((webSocket, throwable) -> {
                webSocket.abort();
            });
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
     * This is useful, if you want to call {@link #resume(Data)} later
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
    public void resume(Data data) throws InvalidDataException {
        if(webSocket != null) throw new UnsupportedOperationException("resume(Data) is exclusive to start()");

        String sessionId = (String) data.get(SESSION_ID_KEY);
        Boolean canResume = (Boolean) data.get(CAN_RESUME_KEY);
        Number lastReceivedSeq = (Number) data.get(LAST_RECEIVED_SEQUENCE_KEY);
        Number heartbeatsSent = (Number) data.get(HEARTBEATS_SENT_KEY);
        Number heartbeatsAcksReceived = (Number) data.get(HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY);
        Number genMillis = (Number) data.get(DATA_GENERATED_TIME_MILLIS_KEY);

        if(sessionId == null || canResume == null ||lastReceivedSeq == null || heartbeatsSent == null ||
        heartbeatsAcksReceived == null || genMillis == null){
            InvalidDataException.throwException(data, null, GatewayWebSocket.class,
                    new Object[]{sessionId, canResume, lastReceivedSeq, heartbeatsSent, heartbeatsAcksReceived, genMillis},
                    new String[]{SESSION_ID_KEY, CAN_RESUME_KEY, LAST_RECEIVED_SEQUENCE_KEY, HEARTBEATS_SENT_KEY, HEARTBEAT_ACKNOWLEDGEMENTS_RECEIVED_KEY, DATA_GENERATED_TIME_MILLIS_KEY});
        }

        long timePasted = System.currentTimeMillis() - genMillis.longValue();

        logger.debug("going to resume with a " + timePasted / 1000 + " seconds old GatewayResumeData");

        this.sessionId = sessionId;
        this.canResume.set(canResume);
        this.lastReceivedSequence.set(lastReceivedSeq.longValue());
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

        lastReceivedSequence.set(-1);

        heartbeatsSent.set(0);
        heartbeatAcknowledgementsReceived.set(0);
    }

    /**
     * sends a Heartbeat to discord
     */
    protected void sendHeartbeat() {

        Long sequence = lastReceivedSequence.get();
        if (sequence == -1) sequence = null;

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
     * Triggerd periodically, when a Heartbeat ACK is received.<br>
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
     * @param data the data of the command, most like to be a {@link Data}
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
    public void onOpen(WebSocket webSocket) {
        logger.debug("onOpen");
        this.webSocket = webSocket;
        WebSocket.Listener.super.onOpen(webSocket);
    }

    /**
     *
     * transmits the raw input to {@link #handleReceivedPayload(GatewayPayloadAbstract)}
     */
    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence text, boolean last) {
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
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer bytes, boolean last) {
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
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        if(webSocket != this.webSocket) return null;
        logger.warning("Received a ping... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        if(webSocket != this.webSocket) return null;
        logger.warning("Received a pong... Why? message: " + new String(message.array()));
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
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
    public Data getData() {
        Data data = new Data(6);

        data.add(SESSION_ID_KEY, sessionId);
        data.add(CAN_RESUME_KEY, canResume.get());
        data.add(LAST_RECEIVED_SEQUENCE_KEY, lastReceivedSequence.get());
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
         * Anyways, the {@link GatewayWebSocket} will <b>not</b> automatically reconnect
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
