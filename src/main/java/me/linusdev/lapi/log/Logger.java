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

package me.linusdev.lapi.log;

import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.helper.Helper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {

    public static final boolean ENABLE_LOG =           true;
    public static final boolean DEBUG_LOG =            true && ENABLE_LOG;
    public static final boolean DEBUG_DATA_LOG =       false && ENABLE_LOG && DEBUG_LOG;
    public static final boolean SOUT_LOG =             true && ENABLE_LOG;
    public static final boolean SERR_LOG =             true && ENABLE_LOG;

    public static ArrayList<String> allowedSources = new ArrayList<>();
    static {
        allowedSources.add(GatewayWebSocket.class.getSimpleName());
        /**
         * @see GatewayWebSocket#handleReceivedPayload(GatewayPayloadAbstract)
         */
        //allowedSources.add(GatewayWebSocket.class.getSimpleName() + "-payloads");

        allowedSources = null;
    }

    public static Path logFolder;

    public static String logFileEnding = ".log";
    private static final DateFormat logDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");

    public static Path logFile;
    public static BufferedWriter writer;

    /**
     *
     * @param override if this is {@code true} and a log file with the same name already exits, it will be overwritten
     * @param dateInFileName current date will be added to the log file name
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void start(boolean override, boolean dateInFileName) throws IOException, URISyntaxException {
        if(!ENABLE_LOG || writer != null) return;
        if(logFolder == null){
            logFolder = Helper.getJarPath().getParent().resolve("log");
        }

        if(!Files.exists(logFolder) || !Files.isDirectory(logFolder)){
            Files.createDirectories(logFolder);
        }

        Date date = new Date(System.currentTimeMillis());
        if(dateInFileName) logFile = logFolder.resolve("lapi-" + fileDateFormat.format(date) + "-log" + logFileEnding);
        else logFile = logFolder.resolve("lapi-log" + logFileEnding);

        if(!override && Files.exists(logFile)) throw new IOException(logFile.toString() + " already exists");
        if(Files.exists(logFile)) Files.delete(logFile);
        Files.createFile(logFile);

        writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        log(Type.INFO, Logger.class.getSimpleName(), null, "Log started...", false);
        log(Type.INFO, Logger.class.getSimpleName(), null, "writing to " + logFile.toString(), false);
    }

    public static void close() throws IOException {
        if(writer != null) {
            //noinspection SynchronizeOnNonFinalField: Field never changes after calling start() once
            synchronized (writer) {
                if(!ENABLE_LOG) return;
                if(writer != null) writer.close();
                writer = null;
            }
        }


    }

    public static LogInstance getLogger(@NotNull String source){
        return getLogger(source, Type.INFO);
    }

    public static LogInstance getLogger(@NotNull Object source){
        return getLogger(source.getClass().getSimpleName(), Type.INFO);
    }

    public static LogInstance getLogger(@NotNull String source, @NotNull Type defaultType){
        return new LogInstance(source, defaultType);
    }

    public enum Type{
        DEBUG("DEBUG"),
        DEBUG_DATA("DEBUG-DATA"),
        INFO("INFO"),
        WARNING("WARNING"),
        ERROR("ERROR"),
        ;
        private final String name;
        Type(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void log(Type type, String source, @Nullable String name, String toLog, boolean autoAlign){
        if(!ENABLE_LOG) return;
        if(type == Type.DEBUG && !DEBUG_LOG) return;
        if(type == Type.DEBUG_DATA && !DEBUG_DATA_LOG) return;
        if(allowedSources != null && !allowedSources.contains(source)) return;
        try {
            if(autoAlign){
                String pre;
                if(name == null){
                    pre = String.format("(%s - %s) %s: ", logDateFormat.format(new Date(System.currentTimeMillis())), source, type);
                }else{
                    pre = String.format("(%s - %s) %s: %s: ", logDateFormat.format(new Date(System.currentTimeMillis())), source, type, name);
                }
                toLog = toLog.replace("\n", "\n" + " ".repeat(pre.length()));
                finalLog(type, pre + toLog + "\n");
            }else {
                if (name == null) {
                    finalLog(type, String.format("(%s - %s) %s: %s\n", logDateFormat.format(new Date(System.currentTimeMillis())), source, type, toLog));
                } else {
                    finalLog(type, String.format("(%s - %s) %s: %s: %s\n", logDateFormat.format(new Date(System.currentTimeMillis())), source, type, name, toLog));
                }
            }
            if(writer != null) writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void finalLog(Type type, String s) throws IOException {
        if(SERR_LOG && type == Type.ERROR) System.err.println(s);
        else if(SOUT_LOG) System.out.print(s);
        if(writer != null) writer.append(s);
    }

    public static void setLogFolder(@Nullable Path logFolder){
        Logger.logFolder = logFolder;
    }

    public static void setLogFileEnding(@NotNull String ending){
        Logger.logFileEnding = ending;
    }
}
