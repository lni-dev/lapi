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

package me.linusdev.lapi.api.config;

import me.linusdev.data.Datable;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.guild.LApiGuildManagerImpl;
import me.linusdev.lapi.api.manager.ManagerFactory;
import me.linusdev.lapi.api.manager.guild.member.MemberManager;
import me.linusdev.lapi.api.manager.guild.member.MemberManagerImpl;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManagerImpl;
import me.linusdev.lapi.api.manager.guild.thread.ThreadManager;
import me.linusdev.lapi.api.manager.guild.thread.ThreadManagerImpl;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStateManager;
import me.linusdev.lapi.api.manager.guild.voicestate.VoiceStatesManagerImpl;
import me.linusdev.lapi.api.manager.list.ListManager;
import me.linusdev.lapi.api.manager.presence.PresenceManager;
import me.linusdev.lapi.api.manager.presence.PresenceManagerImpl;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.sticker.Sticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class can build a {@link Config} or a {@link LApi}
 * @see #build()
 * @see #buildLApi()
 * @see #getDefault(String)
 */
@SuppressWarnings("UnusedReturnValue")
public class ConfigBuilder implements Datable {

    public final static String TOKEN_KEY = "token";
    public final static String FLAGS_KEY = "flags";
    public final static String GATEWAY_CONFIG_KEY = "gateway_config";

    public final static long DEFAULT_FLAGS = 0L;

    private String token = null;
    private long flags = 0;
    private Supplier<Queue<Future<?>>> queueSupplier = null;
    private @NotNull GatewayConfigBuilder gatewayConfigBuilder;
    private ManagerFactory<GuildManager> guildManagerFactory = null;
    private ManagerFactory<RoleManager> roleManagerFactory = null;
    private ManagerFactory<ListManager<EmojiObject>> emojiManagerFactory = null;
    private ManagerFactory<ListManager<Sticker>> stickerManagerFactory = null;
    private ManagerFactory<VoiceStateManager> voiceStateManagerFactory = null;
    private ManagerFactory<MemberManager> memberManagerFactory = null;
    private ManagerFactory<ListManager<Channel<?>>> channelManagerFactory = null;
    private ManagerFactory<ThreadManager> threadManagerFactory = null;
    private ManagerFactory<PresenceManager> presenceManagerFactory = null;


    /**
     * Creates a new {@link ConfigBuilder}
     */
    public ConfigBuilder(){
        this.gatewayConfigBuilder = new GatewayConfigBuilder();
    }

    /**
     * <p>
     *     Creates a new {@link ConfigBuilder} and sets the token to given token
     * </p>
     * @param token string token
     */
    public ConfigBuilder(String token){
        this();
        this.token = token;
    }

    /**
     * <p>
     *     Creates a new {@link ConfigBuilder} and calls {@link #readFromFile(Path)}
     * </p>
     * @param configFile the path to the file to read the config from. This file must exist.
     */
    public ConfigBuilder(Path configFile) throws IOException, ParseException, InvalidDataException {
        this();
        readFromFile(configFile);
    }

    /**
     * <p>
     *     Creates a new {@link ConfigBuilder} with all commonly required stuff enabled.
     *     The {@link me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket gateway} has
     *     all intents enabled and will receive all events.
     * </p>
     * <p>
     *    You can directly {@link #buildLApi() build a LApi} with the returned config builder
     * </p>
     * <p>
     *     See method source code for all details.
     * </p>
     * @param token string token
     * @return new {@link ConfigBuilder}
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ConfigBuilder getDefault(@NotNull String token){

        return new ConfigBuilder(token)
                .enable(ConfigFlag.CACHE_VOICE_REGIONS)
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .adjustGatewayConfig(gb -> {
                    gb.addIntent(GatewayIntent.ALL);
                });
    }

    /**
     * <em>Optional / Recommended</em>
     * <p>
     *     Enables given flag
     * </p>
     * @param flag the flag to enable/set
     */
    public ConfigBuilder enable(@NotNull ConfigFlag flag){
        this.flags = flag.set(flags);
        return this;
    }

    /**
     * <em>Optional / Recommended</em>
     * <p>
     *     Disables given flag
     * </p>
     * @param flag the flag to disable/unset
     * @see #disable(ConfigFlag)
     */
    public ConfigBuilder disable(@NotNull ConfigFlag flag){
        this.flags = flag.remove(flags);
        return this;
    }


    /**
     * <em>Optional / Not Recommended</em>
     * <p>
     *     overwrites the flags long
     * </p>
     *
     * @param flags new flags long
     * @see #enable(ConfigFlag)
     * @see #disable(ConfigFlag)
     */
    public ConfigBuilder setFlags(long flags) {
        this.flags = flags;
        return this;
    }

    /**
     * <em>Optional / Not Recommended</em>
     * <p>
     *     Disables all flags
     * </p>
     */
    public ConfigBuilder clearFlags(){
        this.flags = 0;
        return this;
    }

    /**
     * <em>Required</em>
     * <p>
     *     The token, to authenticate.<br>
     *     go to the Discord <a href="https://discord.com/developers/applications" target="_top">Developer Portal</a> and
     *     select your application (or create one if you haven't already). Then go to <b>Bot</b> and copy your Token.
     * </p>
     * @param token your bot token
     */
    public ConfigBuilder setToken(@NotNull String token){
        this.token = token;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: () -> ConcurrentLinkedQueue::new
     * <p>
     *     Supplier for the queue used by {@link LApi} for queued HttpRequests
     * </p>
     * <p>
     *      Set to {@code null} to reset to default
     * </p>
     * @param queueSupplier queue supplier
     */
    public ConfigBuilder setQueueSupplier(Supplier<Queue<Future<?>>> queueSupplier) {
        this.queueSupplier = queueSupplier;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code new GatewayConfigBuilder()}
     * <p>
     *     It's much easier if you use {@link #adjustGatewayConfig(Consumer)}
     * </p>
     * @param gatewayConfigBuilder gateway config
     * @see #adjustGatewayConfig(Consumer)
     */
    public ConfigBuilder setGatewayConfig(@NotNull GatewayConfigBuilder gatewayConfigBuilder) {
        this.gatewayConfigBuilder = gatewayConfigBuilder;
        return this;
    }

    /**
     * <em>Optional</em>
     * <p>
     *      The consumer lets you adjust the {@link GatewayConfigBuilder} of this {@link GatewayConfig}. You do <b>not</b> need to
     *      call {@link #setGatewayConfig(GatewayConfigBuilder) setGatewayConfig(...)}.
     * </p>
     *
     * @param setConfig the consumer, to adjust the {@link GatewayConfigBuilder}
     * @return this
     */
    public ConfigBuilder adjustGatewayConfig(@NotNull Consumer<GatewayConfigBuilder> setConfig) {
        setConfig.accept(gatewayConfigBuilder);
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -&gt; new LApiGuildManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link me.linusdev.lapi.api.manager.guild.GuildManager GuildManager} used by {@link LApiImpl LApi}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param guildManagerFactory the {@link ManagerFactory<GuildManager> ManagerFactory&lt;GuildManager&gt;}
     * @return this
     */
    public ConfigBuilder setGuildManagerFactory(ManagerFactory<GuildManager> guildManagerFactory) {
        this.guildManagerFactory = guildManagerFactory;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new RoleManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link RoleManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param roleManagerFactory the {@link ManagerFactory<RoleManager> ManagerFactory&lt;RoleManager&gt;}
     * @return this
     */
    public ConfigBuilder setRoleManagerFactory(ManagerFactory<RoleManager> roleManagerFactory) {
        this.roleManagerFactory = roleManagerFactory;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new ListManager<>(lApi, EmojiObject.ID_KEY, EmojiObject::fromData, lApi::isCopyOldEmojiOnUpdateEventEnabled)}
     * <p>
     *     Factory for the {@link ListManager ListManager&lt;EmojiObject&gt;} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param emojiManagerFactory the {@link ManagerFactory ManagerFactory&lt;ListManager&lt;EmojiObject&gt;&gt;}
     * @return this
     */
    public ConfigBuilder setEmojiManagerFactory(ManagerFactory<ListManager<EmojiObject>> emojiManagerFactory) {
        this.emojiManagerFactory = emojiManagerFactory;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new ListManager<>(lApi, Sticker.ID_KEY, Sticker::fromData, lApi::isCopyOldStickerOnUpdateEventEnabled)}
     * <p>
     *     Factory for the {@link ListManager ListManager&lt;Sticker&gt;} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param stickerManagerFactory the {@link ManagerFactory ManagerFactory&lt;ListManager&lt;Sticker&gt;&gt;}
     * @return this
     */
    public ConfigBuilder setStickerManagerFactory(ManagerFactory<ListManager<Sticker>> stickerManagerFactory) {
        this.stickerManagerFactory = stickerManagerFactory;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new VoiceStatesManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link VoiceStateManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param voiceStateManagerFactory the {@link ManagerFactory ManagerFactory&lt;VoiceStateManager&gt;}
     * @return this
     */
    public ConfigBuilder setVoiceStateManagerFactory(ManagerFactory<VoiceStateManager> voiceStateManagerFactory) {
        this.voiceStateManagerFactory = voiceStateManagerFactory;
        return this;

    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new MemberManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link MemberManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param memberManagerFactory the {@link ManagerFactory ManagerFactory&lt;MemberManager&gt;}
     * @return this
     */
    public ConfigBuilder setMemberManagerFactory(ManagerFactory<MemberManager> memberManagerFactory) {
        this.memberManagerFactory = memberManagerFactory;
        return this;
    }


    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new ListManager<>(lApi, Channel.ID_KEY, Channel::fromData, lApi::isCopyOldChannelOnUpdateEventEnabled)}
     * <p>
     *     Factory for the {@link ListManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild} to cache channels
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param channelManagerFactory the {@link ManagerFactory ManagerFactory&lt;ListManager&lt;Channel&lt;?&gt;&gt;&gt;}
     * @return this
     */
    public ConfigBuilder setChannelManagerFactory(ManagerFactory<ListManager<Channel<?>>> channelManagerFactory) {
        this.channelManagerFactory = channelManagerFactory;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new ThreadManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link ThreadManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild} to cache threads.
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param threadManagerFactory the {@link ManagerFactory ManagerFactory&lt;ThreadManager&gt;}
     * @return this
     */
    public ConfigBuilder setThreadManagerFactory(ManagerFactory<ThreadManager> threadManagerFactory) {
        this.threadManagerFactory = threadManagerFactory;
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new PresenceManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link PresenceManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild} to cache presences.
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param presenceManagerFactory the {@link ManagerFactory ManagerFactory&lt;PresenceManager&gt;}
     * @return this
     */
    public ConfigBuilder setPresenceManagerFactory(ManagerFactory<PresenceManager> presenceManagerFactory) {
        this.presenceManagerFactory = presenceManagerFactory;
        return this;
    }

    /**
     * <p>
     *     Adjusts this {@link ConfigBuilder} depending on given data.
     * </p>
     *
     * @param data {@link SOData}
     * @return this
     * @see #getData()
     */
    @Contract("_ -> this")
    public ConfigBuilder fromData(@NotNull SOData data) throws InvalidDataException {
        String token = (String) data.get(TOKEN_KEY);
        Object flags = data.getOrDefault(FLAGS_KEY, DEFAULT_FLAGS);
        SOData gateway = (SOData) data.get(GATEWAY_CONFIG_KEY);

        if(flags != null) {
            if (flags instanceof SOData) {
                this.flags = ConfigFlag.fromData((SOData) flags);
            } else if (flags instanceof Number) {
                this.flags = ((Number) flags).longValue();
            }
        }

        this.token = token == null ? this.token : token;

        if(gateway != null) gatewayConfigBuilder.fromData(gateway);
        return this;
    }

    /**
     * <p>
     *     Will read the {@link ConfigBuilder} saved to this file and adjust this {@link ConfigBuilder} accordingly.
     * </p>
     * @param configFile the path to the file to read the config from. This file must exist.
     * @return this
     * @see #writeToFile(Path, boolean)
     */
    @Contract("_ -> this")
    public ConfigBuilder readFromFile(@NotNull Path configFile) throws IOException, ParseException, InvalidDataException {
        if(!Files.exists(configFile)) throw new FileNotFoundException(configFile + " does not exist.");
        Reader reader = Files.newBufferedReader(configFile);
        JsonParser parser = new JsonParser();
        fromData(parser.parseReader(reader));
        return this;
    }

    /**
     * <p>
     * Will write the current {@link ConfigBuilder} to a file, so it could be read later. Useful to create your own config file.
     * </p>
     * <p>
     *     The Config builder does not have to be able to build an actual {@link Config}. For example a token can still be missing.
     * </p>
     *
     * @param configFile the Path to the file to write the config to. This file does not have to exist, but the folder containing the file must exist.
     * @see #readFromFile(Path)
     */
    public ConfigBuilder writeToFile(@NotNull Path configFile, boolean overwriteIfExists) throws IOException {
        if(!overwriteIfExists && Files.exists(configFile)) throw new FileAlreadyExistsException(configFile + " already exists");
        if(Files.exists(configFile)) Files.delete(configFile);
        Files.createFile(configFile);
        Writer writer = Files.newBufferedWriter(configFile);
        writer.write(getData().toJsonString().toString());
        writer.close();

        return this;
    }

    /**
     * <p>
     *     builds a {@link Config}
     * </p>
     * <p>
     *     It's much easier to use {@link #buildLApi()}
     * </p>
     * @return {@link Config}
     */
    @SuppressWarnings("Convert2MethodRef")
    public @NotNull Config build(){

        if(token == null) throw new LApiRuntimeException("Token is null. A config always requires a token.");

        return new Config(
                flags,
                Objects.requireNonNullElseGet(queueSupplier, () -> ConcurrentLinkedQueue::new),
                token,
                gatewayConfigBuilder.build(),
                Objects.requireNonNullElse(guildManagerFactory, lApi -> new LApiGuildManagerImpl(lApi)),
                Objects.requireNonNullElse(roleManagerFactory, lApi -> new RoleManagerImpl(lApi)),
                Objects.requireNonNullElse(emojiManagerFactory, lApi -> new ListManager<>(lApi, EmojiObject.ID_KEY, EmojiObject::fromData, lApi::isCopyOldEmojiOnUpdateEventEnabled)),
                Objects.requireNonNullElse(stickerManagerFactory, lApi -> new ListManager<>(lApi, Sticker.ID_KEY, Sticker::fromData, lApi::isCopyOldStickerOnUpdateEventEnabled)),
                Objects.requireNonNullElse(voiceStateManagerFactory, lApi -> new VoiceStatesManagerImpl(lApi)),
                Objects.requireNonNullElse(memberManagerFactory, lApi -> new MemberManagerImpl(lApi)),
                Objects.requireNonNullElse(channelManagerFactory, lApi -> new ListManager<>(lApi, Channel.ID_KEY, Channel::fromData, lApi::isCopyOldChannelOnUpdateEventEnabled)),
                Objects.requireNonNullElse(threadManagerFactory, lApi -> new ThreadManagerImpl(lApi)),
                Objects.requireNonNullElse(presenceManagerFactory, lApi -> new PresenceManagerImpl(lApi))
        );
    }

    /**
     * <p>
     *     builds a {@link Config} and then a {@link LApi} with this config
     * </p>
     * @return {@link LApi}
     */
    public @NotNull LApi buildLApi() throws LApiException, IOException, ParseException, InterruptedException {
        return new LApiImpl(build());
    }

    /**
     *
     * @return {@link SOData} corresponding to this {@link ConfigBuilder}
     * @see #fromData(SOData)
     */
    @Override
    public SOData getData() {

        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add(TOKEN_KEY, token);
        data.add(FLAGS_KEY, ConfigFlag.toData(flags));
        data.add(GATEWAY_CONFIG_KEY, gatewayConfigBuilder);

        return data;
    }
}
