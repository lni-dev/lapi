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

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.lapiandqueue.Future;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.lapiandqueue.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager implements Manager {

    private final @NotNull LApiImpl lApi;
    private final @NotNull LogInstance log = Logger.getLogger(this);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final ArrayList<BaseCommand> allCommands;
    private final ArrayList<BaseCommand> globalCommands;
    private final ArrayList<BaseCommand> guildCommands;

    public CommandManager(@NotNull LApiImpl lApi) throws IOException {
        this.lApi = lApi;
        this.allCommands = new ArrayList<>();
        this.guildCommands = new ArrayList<>();
        this.globalCommands = new ArrayList<>();



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
        if(isInitialized()) return;

        globalCommands.clear();
        guildCommands.clear();

        Future<ArrayList<ApplicationCommand>> request = this.lApi.getRequestFactory().getGlobalApplicationCommands(true).queue();

        log.debug("loading commands with a ServiceLoader");
        ServiceLoader<BaseCommand> commands = ServiceLoader.load(BaseCommand.class);

        Iterator<BaseCommand> it = commands.iterator();
        while (true){
            try {
                if(!it.hasNext()) break;
                BaseCommand command = it.next();
                command.setlApi(lApi);

                log.debug(String.format("command '%s' loaded: %s %s %s",
                        command.getClass().getCanonicalName(), command.getScope(), command.getType(), command.getName()));

                if(command.getScope() == CommandScope.GLOBAL)
                    globalCommands.add(command);
                else if(command.getScope() == CommandScope.GUILD)
                    guildCommands.add(command);
                else {
                    log.error(String.format("command '%s' has unknown scope: %s", command.getClass().getCanonicalName(), command.getScope()));
                    throw new LApiIllegalStateException("command has unknown scope: " + command.getScope());
                }


            } catch (Throwable t) {
                log.error("Exception while trying to load a command.");
                log.error(t);
                //break the loop, so that we do not have an infinite loop
                return;
            }
        }

        //Sort the array, so that we will first iterate over commands, that specified an id.
        //Then commands that specified a template.
        //And commands that specified scope, type and name last.
        this.globalCommands.sort((o1, o2) -> {
            if(o1.getId() != null && o2.getId() != null) return 0;
            if(o1.getId() != null && o2.getId() == null) return 2;
            if(o2.getId() != null) return -2;
            if(o1.getTemplate() != null && o2.getTemplate() != null) return 0;
            if(o1.getTemplate() != null && o2.getTemplate() == null) return 1;
            if(o2.getTemplate() != null) return -1;
            return 0;
        });

        try {
            ArrayList<ApplicationCommand> discordCommands = request.get();

            for(BaseCommand command : this.globalCommands) {
                try {
                    if(command.getId() != null) {
                        //match by id

                    } else if(command.getTemplate() != null) {
                        //match by template

                    } else if(command.getType() != null && command.getName() != null) {
                        //match by scope, type and name

                    } else {
                        //cannot match
                        command.onError(new LApiIllegalStateException("Your command does not meet the requirements."));
                    }

                } catch (Throwable t) {
                    log.error("Exception while trying to match command");
                    log.error(t);
                    command.onError(t);
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Could not fetch discord commands, command manager cannot initialize!");
            log.error(e);
            return;
        }

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
