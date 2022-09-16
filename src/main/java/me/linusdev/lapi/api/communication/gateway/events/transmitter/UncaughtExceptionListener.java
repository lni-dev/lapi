/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.events.transmitter;

import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;

public interface UncaughtExceptionListener {

    /**
     * This will be called, if an {@link Exception} or {@link Throwable} was thrown inside this
     * {@link EventListener EventListener's} methods.<br>
     * Note: This method should <b>never</b> throw an Exception!
     * @param uncaught {@link Throwable} that was not caught.
     */
    default void onUncaughtException(Throwable uncaught) {
        try{
            LogInstance log = Logger.getLogger(this.getClass());
            log.error("Uncaught exception in an event listener:");
            log.error(uncaught);
        }catch (Throwable printToConsole){
            printToConsole.printStackTrace();
        }
    }

}
