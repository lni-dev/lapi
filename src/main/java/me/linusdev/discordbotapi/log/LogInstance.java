package me.linusdev.discordbotapi.log;

import org.jetbrains.annotations.NotNull;

public class LogInstance {

    private @NotNull final String source;
    private @NotNull final Logger.Type defaultType;

    public LogInstance(@NotNull String source, @NotNull Logger.Type defaultType){
        this.source = source;
        this.defaultType = defaultType;
    }

    public void log(String toLog){
        Logger.log(defaultType, source, toLog);
    }

    public void info(String infoLog){
        Logger.log(Logger.Type.INFO, source, infoLog);
    }

    public void warning(String warningLog){
        Logger.log(Logger.Type.INFO, source, warningLog);
    }

    public void error(String errorLog){
        Logger.log(Logger.Type.ERROR, source, errorLog);
    }

    public void debug(String debugLog){
        Logger.log(Logger.Type.DEBUG, source, debugLog);
    }

    public void error(Throwable throwable){
        StringBuilder err = new StringBuilder();
        err.append(throwable.getMessage()).append(":\n");

        for(StackTraceElement e : throwable.getStackTrace())
            err.append("\t\t\t\t\t\t\t\t").append(e.toString()).append("\n");

        error(err.toString());
    }
}
