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

package me.linusdev.lapi.log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public void warningAlign(String warningLog){
        Logger.log(Logger.Type.INFO, source, null, warningLog, true);
    }

    public void error(String errorLog){
        Logger.log(Logger.Type.ERROR, source, null, errorLog, false);
    }

    public void errorAlign(String errorLog){
        Logger.log(Logger.Type.ERROR, source, null, errorLog, true);
    }

    public void errorAlign(String errorLog, @Nullable String name){
        Logger.log(Logger.Type.ERROR, source, name, errorLog, true);
    }

    public void debug(String debugLog){
        Logger.log(Logger.Type.DEBUG, source, null, debugLog, false);
    }

    public void debugAlign(String debugLog){
        Logger.log(Logger.Type.DEBUG, source, null, debugLog, true);
    }

    public void debugData(String debugLog, String subSource){
        Logger.log(Logger.Type.DEBUG_DATA, source + "-" + subSource, null, debugLog, true);
    }

    public void debugData(String debugLog){
        Logger.log(Logger.Type.DEBUG_DATA, source, null, debugLog, true);
    }

    public void debugAlign(String debugLog, String name){
        Logger.log(Logger.Type.DEBUG, source, name, debugLog, true);
    }


    /**
     * will log the message and the stacktrace. Will be aligned.
     * @param throwable to log
     */
    public void error(Throwable throwable){
        StringBuilder err = new StringBuilder();
        err.append(throwable.getMessage()).append(":\n");

        for(StackTraceElement e : throwable.getStackTrace())
            err.append(e.toString()).append("\n");

        errorAlign(err.toString(), throwable.getClass().getSimpleName());
    }
}
