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

package me.linusdev.lapi.api.manager.command;

import me.linusdev.lapi.api.interfaces.updatable.IsUpdatable;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.management.ReflectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager implements Manager {

    private final @NotNull LApiImpl lApi;
    private final @NotNull LogInstance log = Logger.getLogger(this);

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private final ArrayList<BaseCommand> commands;

    public CommandManager(@NotNull LApiImpl lApi) throws IOException {
        this.lApi = lApi;

        log.debug("loading commands with a ServiceLoader");
        this.commands = new ArrayList<>();
        ServiceLoader<BaseCommand> commands = ServiceLoader.load(BaseCommand.class);

        for(BaseCommand command : commands) {
            command.setlApi(lApi);

            log.debug(String.format("command '%s' loaded: %s %s %s",
                    command.getClass().getCanonicalName(), command.getScope(), command.getType(), command.getName()));

            this.commands.add(command);
        }


    }

    private @NotNull List<String> readServices() {
        ArrayList<String> list = new ArrayList<>();

        try(InputStream inputStream = lApi.getCallerClass().getClassLoader().getResourceAsStream("META-INF/services/me.linusdev.lapi.api.manager.command.BaseCommand")){
            if(inputStream == null) return list;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line =reader.readLine()) != null){
                list.add(line);
            }
        }catch (IOException exception) {
            Logger.getLogger(this).error(exception);
        }

        return list;
    }

    @Override
    public void init(int initialCapacity) {
        initialized.set(true);
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}
