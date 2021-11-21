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
        Logger.log(defaultType, source, null, toLog, false);
    }

    public void info(String infoLog){
        Logger.log(Logger.Type.INFO, source, null, infoLog, false);
    }

    public void warning(String warningLog){
        Logger.log(Logger.Type.INFO, source, null, warningLog, false);
    }

    public void error(String errorLog){
        Logger.log(Logger.Type.ERROR, source, null, errorLog, false);
    }

    public void errorAlign(String errorLog){
        Logger.log(Logger.Type.ERROR, source, null, errorLog, false);
    }

    public void debug(String debugLog){
        Logger.log(Logger.Type.DEBUG, source, null, debugLog, false);
    }

    public void debugAlign(String debugLog){
        Logger.log(Logger.Type.DEBUG, source, null, debugLog, true);
    }

    public void debugAlign(String debugLog, String name){
        Logger.log(Logger.Type.DEBUG, source, name, debugLog, true);
    }



    public void error(Throwable throwable){
        StringBuilder err = new StringBuilder();
        err.append(throwable.getMessage()).append(":\n");

        for(StackTraceElement e : throwable.getStackTrace())
            err.append(e.toString()).append("\n");

        errorAlign(err.toString());
    }
}
