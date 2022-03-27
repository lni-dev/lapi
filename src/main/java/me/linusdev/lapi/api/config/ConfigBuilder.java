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

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayIntent;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.guild.GuildManager;
import me.linusdev.lapi.api.manager.guild.LApiGuildManager;
import me.linusdev.lapi.api.manager.ManagerFactory;
import me.linusdev.lapi.api.manager.guild.emoji.EmojiManager;
import me.linusdev.lapi.api.manager.guild.emoji.EmojiManagerImpl;
import me.linusdev.lapi.api.manager.guild.role.RoleManager;
import me.linusdev.lapi.api.manager.guild.role.RoleManagerImpl;
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
 * @see #buildLapi()
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
    private ManagerFactory<EmojiManager> emojiManagerFactory = null;

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
     *    You can directly {@link #buildLapi() build a LApi} with the returned config builder
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
     */
    public ConfigBuilder adjustGatewayConfig(@NotNull Consumer<GatewayConfigBuilder> setConfig) {
        setConfig.accept(gatewayConfigBuilder);
        return this;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -&gt; new LApiGuildManager(lApi)}
     * <p>
     *     Factory for the {@link me.linusdev.lapi.api.manager.guild.GuildManager GuildManager} used by {@link LApiImpl LApi}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param guildManagerFactory the {@link ManagerFactory<GuildManager> ManagerFactory&lt;GuildManager&gt;}
     */
    public void setGuildManagerFactory(ManagerFactory<GuildManager> guildManagerFactory) {
        this.guildManagerFactory = guildManagerFactory;
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
     */
    public void setRoleManagerFactory(ManagerFactory<RoleManager> roleManagerFactory) {
        this.roleManagerFactory = roleManagerFactory;
    }

    /**
     * <em>Optional</em><br>
     * Default: {@code lApi -> new EmojiManagerImpl(lApi)}
     * <p>
     *     Factory for the {@link EmojiManager} used by {@link me.linusdev.lapi.api.objects.guild.CachedGuild CachedGuild}
     * </p>
     * <p>
     *     Set to {@code null} to reset to default
     * </p>
     * @param emojiManagerFactory the {@link ManagerFactory<EmojiManager> ManagerFactory&lt;EmojiManager&gt;}
     */
    public void setEmojiManagerFactory(ManagerFactory<EmojiManager> emojiManagerFactory) {
        this.emojiManagerFactory = emojiManagerFactory;
    }

    /**
     * <p>
     *     Adjusts this {@link ConfigBuilder} depending on given data.
     * </p>
     *
     * @param data {@link Data}
     * @return this
     * @see #getData()
     */
    @Contract("_ -> this")
    public ConfigBuilder fromData(@NotNull Data data) throws InvalidDataException {
        String token = (String) data.get(TOKEN_KEY);
        Object flags = data.getOrDefault(FLAGS_KEY, DEFAULT_FLAGS);
        Data gateway = (Data) data.get(GATEWAY_CONFIG_KEY);

        if(flags != null) {
            if (flags instanceof Data) {
                this.flags = ConfigFlag.fromData((Data) flags);
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
        fromData(parser.readDataFromReader(reader));
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
        writer.write(getData().getJsonString().toString());
        writer.close();

        return this;
    }

    /**
     * <p>
     *     builds a {@link Config}
     * </p>
     * <p>
     *     It's much easier to use {@link #buildLapi()}
     * </p>
     * @return {@link Config}
     */
    public @NotNull Config build(){

        if(token == null) throw new LApiRuntimeException("Token is null. A config always requires a token.");

        return new Config(
                flags,
                Objects.requireNonNullElseGet(queueSupplier, () -> ConcurrentLinkedQueue::new),
                token,
                gatewayConfigBuilder.build(),
                Objects.requireNonNullElse(guildManagerFactory, lApi -> new LApiGuildManager(lApi)),
                Objects.requireNonNullElse(roleManagerFactory, lApi -> new RoleManagerImpl(lApi)),
                Objects.requireNonNullElse(emojiManagerFactory, lApi -> new EmojiManagerImpl(lApi)));
    }

    /**
     * <p>
     *     builds a {@link Config} and then a {@link LApi} with this config
     * </p>
     * @return {@link LApi}
     */
    public @NotNull LApi buildLapi() throws LApiException, IOException, ParseException, InterruptedException {
        return new LApiImpl(build());
    }

    /**
     *
     * @return {@link Data} corresponding to this {@link ConfigBuilder}
     * @see #fromData(Data)
     */
    @Override
    public Data getData() {

        Data data = new Data(2);

        data.add(TOKEN_KEY, token);
        data.add(FLAGS_KEY, ConfigFlag.toData(flags));
        data.add(GATEWAY_CONFIG_KEY, gatewayConfigBuilder);

        return data;
    }
}
