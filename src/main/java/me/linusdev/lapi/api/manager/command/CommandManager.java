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
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.management.ReflectionException;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager implements Manager {

    private final @NotNull LApiImpl lApi;
    private AtomicBoolean initialized = new AtomicBoolean(false);

    public CommandManager(@NotNull LApiImpl lApi) {
        this.lApi = lApi;
        ServiceLoader<BaseCommand> loader = ServiceLoader.load(BaseCommand.class);

        for(BaseCommand command : loader) {
            System.out.println("found: " + command.getClass().getSimpleName());
        }

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
