package me.linusdev.discordbotapi.log;

import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.FilePart;
import me.linusdev.discordbotapi.helper.Helper;
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
import java.util.Date;

public class Logger {

    public static final boolean ENABLE_LOG = true;
    public static final boolean DEBUG_LOG = true && ENABLE_LOG;
    public static final boolean SOUT_LOG =             true && ENABLE_LOG;

    public static Path logFolder;

    public static String logFileEnding = ".log";
    private static final DateFormat logDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");

    public static Path logFile;
    public static BufferedWriter writer;

    public static void start() throws IOException, URISyntaxException {
        if(!ENABLE_LOG) return;
        if(logFolder == null){
            logFolder = Helper.getJarPath().getParent().resolve("log");
        }

        if(!Files.exists(logFolder) || !Files.isDirectory(logFolder)){
            Files.createDirectories(logFolder);
        }

        Date date = new Date(System.currentTimeMillis());
        //logFile = logFolder.resolve(fileDateFormat.format(date) + logFileEnding);
        logFile = logFolder.resolve("LApi-log" + logFileEnding);

        //if(Files.exists(logFile)) throw new IOException(logFile.toString() + " already exists");
        if(Files.exists(logFile)) Files.delete(logFile);
        Files.createFile(logFile);

        writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        log(Type.INFO, Logger.class.getSimpleName(), null, "Log started...", false);
    }

    public static void close() throws IOException {
        if(!ENABLE_LOG) return;
        writer.close();
    }

    public static LogInstance getLogger(@NotNull String source){
        return getLogger(source, Type.INFO);
    }

    public static LogInstance getLogger(@NotNull String source, @NotNull Type defaultType){
        return new LogInstance(source, defaultType);
    }

    public static enum Type{
        DEBUG("DEBUG"),
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
        try {
            if(autoAlign){
                String pre;
                if(name == null){
                    pre = String.format("(%s - %s) %s: ", logDateFormat.format(new Date(System.currentTimeMillis())), source, type);
                }else{
                    pre = String.format("(%s - %s) %s: %s: ", logDateFormat.format(new Date(System.currentTimeMillis())), source, type, name);
                }
                toLog = toLog.replace("\n", "\n" + " ".repeat(pre.length()));
                finalLog(pre + toLog + "\n");
            }else {
                if (name == null) {
                    finalLog(String.format("(%s - %s) %s: %s\n", logDateFormat.format(new Date(System.currentTimeMillis())), source, type, toLog));
                } else {
                    finalLog(String.format("(%s - %s) %s: %s: %s\n", logDateFormat.format(new Date(System.currentTimeMillis())), source, type, name, toLog));
                }
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void finalLog(String s) throws IOException {
        if(SOUT_LOG) System.out.print(s);
        writer.append(s);
    }

    public static void setLogFolder(@Nullable Path logFolder){
        Logger.logFolder = logFolder;
    }

    public static void setLogFileEnding(@NotNull String ending){
        Logger.logFileEnding = ending;
    }
}
