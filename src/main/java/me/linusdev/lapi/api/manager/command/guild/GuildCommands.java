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

package me.linusdev.lapi.api.manager.command.guild;

import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuildCommands {

    private volatile boolean started;
    private volatile boolean initialized;

    private volatile @Nullable List<ApplicationCommand> commands;


    public GuildCommands() {
        this.started = false;
        this.initialized = false;
        this.commands = null;
    }

    public synchronized void setStarted(boolean started) {
        this.started = started;
    }

    public synchronized void initialized() {
        this.initialized = true;
        this.notifyAll();
    }

    public synchronized void setCommands(List<ApplicationCommand> commands) {
        this.commands = commands;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * {@link #wait() waits} until this {@link GuildCommands} is initialized.
     * @throws InterruptedException if interrupted
     */
    public synchronized void awaitInit() throws InterruptedException {
        if(initialized) return;
        wait();
    }

    public List<ApplicationCommand> getCommands() {
        return commands;
    }
}
