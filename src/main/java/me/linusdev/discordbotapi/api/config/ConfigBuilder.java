package me.linusdev.discordbotapi.api.config;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class ConfigBuilder implements Datable {

    public final static String TOKEN_KEY = "token";
    public final static String FLAGS_KEY = "flags";

    public final static long DEFAULT_FLAGS = 0L;

    private String token = null;
    private long flags = 0;
    private Supplier<Queue<Future<?>>> queueSupplier = null;

    public ConfigBuilder(){

    }

    public ConfigBuilder(Path configFile) throws IOException, ParseException {
        readFromFile(configFile);
    }

    public ConfigBuilder enableLoadVoiceRegionsOnStartup(){
        flags = flags | Config.LOAD_VOICE_REGIONS_ON_STARTUP;
        return this;
    }

    public ConfigBuilder disableLoadVoiceRegionsOnStartup(){
        flags = flags & ~Config.LOAD_VOICE_REGIONS_ON_STARTUP;
        return this;
    }

    public ConfigBuilder readFromData(@NotNull Data data){
        token = (String) data.get(TOKEN_KEY);
        flags = ((Number) data.getOrDefault(FLAGS_KEY, DEFAULT_FLAGS)).longValue();
        return this;
    }

    public ConfigBuilder readFromFile(@NotNull Path configFile) throws IOException, ParseException {
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

    public ConfigBuilder setFlags(long flags) {
        this.flags = flags;
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
                token
        );
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
        data.add(FLAGS_KEY, flags);

        return data;
    }
}
