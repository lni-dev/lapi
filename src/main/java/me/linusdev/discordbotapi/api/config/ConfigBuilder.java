package me.linusdev.discordbotapi.api.config;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
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

    public ConfigBuilder(){
        this.gatewayConfigBuilder = new GatewayConfigBuilder();
    }

    public ConfigBuilder(String token){
        this();
        this.token = token;
    }

    public ConfigBuilder(Path configFile) throws IOException, ParseException, InvalidDataException {
        this();
        readFromFile(configFile);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ConfigBuilder getDefault(@NotNull String token){

        return new ConfigBuilder(token)
                .enable(ConfigFlag.LOAD_VOICE_REGIONS_ON_STARTUP)
                .enable(ConfigFlag.ENABLE_GATEWAY)
                .adjustGatewayConfig(gb -> {

                });
    }

    /**
     * Enables given flag
     * @param flag the flag to enable/set
     */
    public ConfigBuilder enable(@NotNull ConfigFlag flag){
        this.flags = flag.set(flags);
        return this;
    }

    /**
     * Disables given flag
     * @param flag the flag to disable/unset
     * @see #disable(ConfigFlag)
     */
    public ConfigBuilder disable(@NotNull ConfigFlag flag){
        this.flags = flag.remove(flags);
        return this;
    }

    public ConfigBuilder readFromData(@NotNull Data data) throws InvalidDataException {
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

    public ConfigBuilder readFromFile(@NotNull Path configFile) throws IOException, ParseException, InvalidDataException {
        if(!Files.exists(configFile)) throw new FileNotFoundException(configFile + " does not exist.");
        Reader reader = Files.newBufferedReader(configFile);
        JsonParser parser = new JsonParser();
        readFromData(parser.readDataFromReader(reader));
        return this;
    }

    /**
     * Will write the current {@link ConfigBuilder} to a file, so it could be read later. Useful to create your own config file.
     * The Config builder does not have to be able to build an actual {@link Config}.
     * @param configFile the Path to the file to write the config to. This file may not exist
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

    public ConfigBuilder setToken(@NotNull String token){
        this.token = token;
        return this;
    }

    public ConfigBuilder setQueueSupplier(Supplier<Queue<Future<?>>> queueSupplier) {
        this.queueSupplier = queueSupplier;
        return this;
    }

    /**
     * overwrites the flags long
     * @param flags new flags long
     * @see #setFlag(ConfigFlag)
     * @see #removeFlag(ConfigFlag)
     */
    public ConfigBuilder setFlags(long flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Enables given flag
     * @param flag the flag to enable/set
     * @see #enable(ConfigFlag)
     */
    public ConfigBuilder setFlag(@NotNull ConfigFlag flag){
        this.flags = flag.set(this.flags);
        return this;
    }

    /**
     * Disables given flag
     * @param flag the flag to disable/unset
     * @see #disable(ConfigFlag)
     */
    public ConfigBuilder removeFlag(@NotNull ConfigFlag flag){
        this.flags = flag.remove(this.flags);
        return this;
    }

    /**
     * Disables all flags
     */
    public ConfigBuilder clearFlags(){
        this.flags = 0;
        return this;
    }


    public ConfigBuilder setGatewayConfig(@NotNull GatewayConfigBuilder gatewayConfigBuilder) {
        this.gatewayConfigBuilder = gatewayConfigBuilder;
        return this;
    }

    /**
     * The consumer lets you adjust the {@link GatewayConfigBuilder} of this {@link GatewayConfig}. This {@link GatewayConfigBuilder}
     * is already added to this {@link ConfigBuilder}
     * @param setConfig the consumer, to adjust the {@link GatewayConfigBuilder}
     */
    public ConfigBuilder adjustGatewayConfig(@NotNull Consumer<GatewayConfigBuilder> setConfig) {
        setConfig.accept(gatewayConfigBuilder);
        return this;
    }

    /**
     * builds a {@link Config}
     * @return {@link Config}
     */
    public @NotNull Config build(){

        if(token == null) throw new LApiRuntimeException("Token is null. A config always requires a token.");

        return new Config(
                flags,
                Objects.requireNonNullElseGet(queueSupplier, () -> ConcurrentLinkedQueue::new),
                token,
                gatewayConfigBuilder.build());
    }

    /**
     * builds a {@link Config} and then a {@link LApi} with this config
     * @return {@link LApi}
     */
    public @NotNull LApi buildLapi() throws LApiException, IOException, ParseException, InterruptedException {
        return new LApi(build());
    }

    @Override
    public Data getData() {

        Data data = new Data(2);

        data.add(TOKEN_KEY, token);
        data.add(FLAGS_KEY, ConfigFlag.toData(flags));
        data.add(GATEWAY_CONFIG_KEY, gatewayConfigBuilder);

        return data;
    }
}
